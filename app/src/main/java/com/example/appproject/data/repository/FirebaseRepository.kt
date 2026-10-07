package com.example.appproject.data.repository

import android.util.Log
import com.example.appproject.data.model.Booking
import com.example.appproject.data.model.ProblemReport
import com.example.appproject.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Locale

class FirebaseRepository {
    private val TAG = "FirebaseRepository"

    val db by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firestore: ${e.message}")
            null
        }
    }

    private val usersCollection by lazy { db?.collection("users") }
    private val bookingsCollection by lazy { db?.collection("bookings") }
    private val vouchersCollection by lazy { db?.collection("vouchers") }
    private val usedVouchersCollection by lazy { db?.collection("usedVouchers") }
    private val reportsCollection by lazy { db?.collection("reports") }

    suspend fun testConnection(): String {
        if (db == null) return "Firestore is not initialized. Check your google-services.json and Firebase configuration."
        return try {
            db!!.collection("bookings").limit(1).get().await()
            "Connected"
        } catch (e: Exception) {
            Log.e(TAG, "Connection test failed: ${e.message}")
            e.message ?: "Unknown Error"
        }
    }

    suspend fun saveUser(user: User) {
        try {
            val uid = user.uid.ifBlank { com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "" }
            val docId = if (uid.isNotBlank()) uid else user.username.normalizedUsername()
            if (docId.isNotBlank()) {
                usersCollection?.document(docId)?.set(user)?.await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving user: ${e.message}")
        }
    }

    suspend fun getUser(query: String): User? {
        if (query.isBlank()) return null
        val cleanQuery = query.trim()
        val normalized = cleanQuery.lowercase(Locale.ROOT)
        return try {
            // 1. Direct document lookup by query (UID or normalized username)
            val snapshot = usersCollection?.document(cleanQuery)?.get()?.await()
            var user = snapshot?.toObject(User::class.java)

            if (user == null && cleanQuery != normalized) {
                val normSnapshot = usersCollection?.document(normalized)?.get()?.await()
                user = normSnapshot?.toObject(User::class.java)
            }

            // 2. Query by uid field
            if (user == null) {
                val uidDocs = usersCollection?.whereEqualTo("uid", cleanQuery)?.get()?.await()
                user = uidDocs?.documents?.firstOrNull()?.toObject(User::class.java)
            }

            // 3. Query by email
            if (user == null && cleanQuery.contains("@")) {
                val emailDocs = usersCollection?.whereEqualTo("email", cleanQuery)?.get()?.await()
                user = emailDocs?.documents?.firstOrNull()?.toObject(User::class.java)
                if (user == null && cleanQuery != normalized) {
                    val lowerEmailDocs = usersCollection?.whereEqualTo("email", normalized)?.get()?.await()
                    user = lowerEmailDocs?.documents?.firstOrNull()?.toObject(User::class.java)
                }
            }

            // 4. Query by phone
            if (user == null) {
                val phoneDocs = usersCollection?.whereEqualTo("phone", cleanQuery)?.get()?.await()
                user = phoneDocs?.documents?.firstOrNull()?.toObject(User::class.java)
                if (user == null && cleanQuery.startsWith("+")) {
                    val rawDigits = cleanQuery.removePrefix("+")
                    val rawDocs = usersCollection?.whereEqualTo("phone", rawDigits)?.get()?.await()
                    user = rawDocs?.documents?.firstOrNull()?.toObject(User::class.java)
                    if (user == null && rawDigits.length > 2) {
                        val localDigits = "0" + rawDigits.substring(2)
                        val localDocs = usersCollection?.whereEqualTo("phone", localDigits)?.get()?.await()
                        user = localDocs?.documents?.firstOrNull()?.toObject(User::class.java)
                    }
                }
            }

            // 5. Query by username property
            if (user == null) {
                val usernameDocs = usersCollection?.whereEqualTo("username", normalized)?.get()?.await()
                user = usernameDocs?.documents?.firstOrNull()?.toObject(User::class.java)
                if (user == null) {
                    val exactUsernameDocs = usersCollection?.whereEqualTo("username", cleanQuery)?.get()?.await()
                    user = exactUsernameDocs?.documents?.firstOrNull()?.toObject(User::class.java)
                }
            }

            user
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user: ${e.message}")
            null
        }
    }

    suspend fun isEmailRegistered(email: String): Boolean {
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank()) return false
        return getUser(cleanEmail) != null
    }

    suspend fun isPhoneRegistered(phone: String): Boolean {
        val cleanPhone = phone.trim()
        if (cleanPhone.isBlank()) return false
        return getUser(cleanPhone) != null
    }

    suspend fun isUsernameRegistered(username: String): Boolean {
        val cleanUsername = username.trim()
        if (cleanUsername.isBlank()) return false
        return try {
            val docs = usersCollection?.whereEqualTo("username", cleanUsername)?.get()?.await()
            val lowerDocs = usersCollection?.whereEqualTo("username", cleanUsername.lowercase(Locale.ROOT))?.get()?.await()
            (docs?.isEmpty == false) || (lowerDocs?.isEmpty == false)
        } catch (e: Exception) {
            false
        }
    }

    suspend fun saveBooking(booking: Booking) {
        try {
            val uid = booking.customerUid.ifBlank { com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "" }
            val finalBooking = if (uid.isNotBlank() && booking.customerUid.isBlank()) booking.copy(customerUid = uid) else booking
            bookingsCollection?.document(finalBooking.bookingId)?.set(finalBooking)?.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving booking: ${e.message}")
        }
    }

    suspend fun getAllBookings(): List<Booking> {
        return try {
            bookingsCollection?.orderBy("bookingDate")?.get()?.await()?.toObjects(Booking::class.java) ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all bookings with order: ${e.message}")
            // Fallback: try without order by if index is missing
            try {
                bookingsCollection?.get()?.await()?.toObjects(Booking::class.java) ?: emptyList()
            } catch (e2: Exception) {
                Log.e(TAG, "Error getting all bookings fallback: ${e2.message}")
                emptyList()
            }
        }
    }

    suspend fun getBookingsForUser(username: String): List<Booking> {
        return try {
            val user = getUser(username)
            val uid = user?.uid ?: ""
            val bookings = mutableListOf<Booking>()
            if (uid.isNotBlank()) {
                val uidDocs = bookingsCollection?.whereEqualTo("customerUid", uid)?.get()?.await()?.toObjects(Booking::class.java)
                if (uidDocs != null) bookings.addAll(uidDocs)
            }
            val usernameDocs = bookingsCollection?.whereEqualTo("customerUsername", username)?.get()?.await()?.toObjects(Booking::class.java)
            if (usernameDocs != null) {
                for (b in usernameDocs) {
                    if (bookings.none { it.bookingId == b.bookingId }) {
                        bookings.add(b)
                    }
                }
            }
            bookings.sortedBy { it.bookingDate }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting bookings for user: ${e.message}")
            emptyList()
        }
    }

    fun listenToBookingsForUser(username: String, onUpdate: (List<Booking>) -> Unit): ListenerRegistration? {
        if (username.isBlank()) return null
        return try {
            bookingsCollection
                ?.whereEqualTo("customerUsername", username)
                ?.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Listen failed: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val bookings = snapshot.toObjects(Booking::class.java)
                        onUpdate(bookings)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error listening to bookings for user: ${e.message}")
            null
        }
    }

    fun listenToAllBookings(onUpdate: (List<Booking>) -> Unit): ListenerRegistration? {
        return try {
            bookingsCollection
                ?.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Listen to all bookings failed: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val bookings = snapshot.toObjects(Booking::class.java)
                        onUpdate(bookings)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error listening to all bookings: ${e.message}")
            null
        }
    }

    suspend fun updateBooking(booking: Booking) {
        try {
            bookingsCollection?.document(booking.bookingId)?.set(booking)?.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating booking: ${e.message}")
        }
    }

    suspend fun getBooking(bookingId: String): Booking? {
        return try {
            val snapshot = bookingsCollection?.document(bookingId)?.get()?.await()
            snapshot?.toObject(Booking::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting booking: ${e.message}")
            null
        }
    }

    suspend fun deleteBooking(bookingId: String) {
        try {
            bookingsCollection?.document(bookingId)?.delete()?.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting booking: ${e.message}")
        }
    }

    suspend fun getAllUsers(): List<User> {
        return try {
            usersCollection?.orderBy("username")?.get()?.await()?.toObjects(User::class.java) ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all users with order: ${e.message}")
            try {
                usersCollection?.get()?.await()?.toObjects(User::class.java) ?: emptyList()
            } catch (e2: Exception) {
                Log.e(TAG, "Error getting all users fallback: ${e2.message}")
                emptyList()
            }
        }
    }

    suspend fun updateUser(user: User) {
        try {
            val docId = user.username.ifBlank { user.email.ifBlank { user.phone } }.normalizedUsername()
            if (docId.isNotBlank()) {
                usersCollection?.document(docId)?.set(user)?.await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user: ${e.message}")
        }
    }

    suspend fun deleteUser(username: String) {
        try {
            // 1. Delete all bookings associated with this user account
            val userBookingsSnapshot = bookingsCollection?.whereEqualTo("customerUsername", username)?.get()?.await()
            userBookingsSnapshot?.documents?.forEach { document ->
                document.reference.delete().await()
            }

            // 2. Delete the user document itself
            usersCollection?.document(username.normalizedUsername())?.delete()?.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting user and their bookings: ${e.message}")
        }
    }

    suspend fun saveVoucher(code: String, discountPercentage: Int, description: String = "", startDate: String = "", endDate: String = "", backgroundPicture: String = "") {
        try {
            val voucherData = mapOf(
                "code" to code.trim().uppercase(),
                "discountPercentage" to discountPercentage,
                "description" to description.trim(),
                "startDate" to startDate.trim(),
                "endDate" to endDate.trim(),
                "backgroundPicture" to backgroundPicture.trim()
            )
            vouchersCollection?.document(code.trim().uppercase())?.set(voucherData)?.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving voucher: ${e.message}")
        }
    }

    suspend fun deleteVoucher(code: String) {
        try {
            vouchersCollection?.document(code.trim().uppercase())?.delete()?.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting voucher: ${e.message}")
        }
    }

    suspend fun getVoucherData(code: String): Map<String, Any>? {
        return try {
            val snapshot = vouchersCollection?.document(code.trim().uppercase())?.get()?.await()
            if (snapshot != null && snapshot.exists()) {
                snapshot.data
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error getting voucher: ${e.message}")
            null
        }
    }

    suspend fun getVoucherDiscount(code: String): Int {
        val data = getVoucherData(code)
        return data?.get("discountPercentage")?.toString()?.toIntOrNull() ?: 0
    }

    suspend fun recordVoucherUsage(code: String, username: String) {
        try {
            val usageId = "${username.normalizedUsername()}_${code.trim().uppercase()}"
            val usageData = mapOf(
                "username" to username.normalizedUsername(),
                "voucherCode" to code.trim().uppercase(),
                "usedAt" to System.currentTimeMillis()
            )
            usedVouchersCollection?.document(usageId)?.set(usageData)?.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error recording voucher usage: ${e.message}")
        }
    }

    suspend fun isVoucherUsedByUser(code: String, username: String): Boolean {
        return try {
            val usageId = "${username.normalizedUsername()}_${code.trim().uppercase()}"
            val snapshot = usedVouchersCollection?.document(usageId)?.get()?.await()
            if (snapshot != null && snapshot.exists()) return true

            // Check bookings collection as a fallback for legacy data
            val querySnapshot = bookingsCollection
                ?.whereEqualTo("customerUsername", username)
                ?.whereEqualTo("voucherUsed", code.trim().uppercase())
                ?.get()
                ?.await()
            querySnapshot != null && !querySnapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getAllVouchers(): List<Map<String, Any>> {
        return try {
            vouchersCollection?.get()?.await()?.documents?.mapNotNull { it.data } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveProblemReport(report: ProblemReport): Boolean {
        return try {
            if (db == null) {
                Log.e(TAG, "Firestore db is null")
                return false
            }
            reportsCollection?.document(report.reportId)?.set(report)?.await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving problem report: ${e.message}", e)
            false
        }
    }

    suspend fun getAllProblemReports(): List<ProblemReport> {
        return try {
            val snapshot = reportsCollection?.get()?.await()
            snapshot?.toObjects(ProblemReport::class.java)?.sortedByDescending { it.timestamp } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all problem reports: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getProblemReportsForUser(username: String): List<ProblemReport> {
        if (username.isBlank()) return emptyList()
        return try {
            val allReports = getAllProblemReports()
            allReports.filter { it.username.equals(username.trim(), ignoreCase = true) }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting problem reports for user: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun updateProblemReportFeedback(reportId: String, adminFeedback: String, status: String) {
        try {
            val updates = mapOf(
                "adminFeedback" to adminFeedback.trim(),
                "status" to status.trim()
            )
            reportsCollection?.document(reportId)?.update(updates)?.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating problem report feedback: ${e.message}")
        }
    }

    private fun String.normalizedUsername(): String = trim().lowercase()

}