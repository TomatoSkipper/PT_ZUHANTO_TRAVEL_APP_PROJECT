package com.example.appproject

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

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
            usersCollection?.document(user.username.normalizedUsername())?.set(user)?.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving user: ${e.message}")
        }
    }

    suspend fun getUser(username: String): User? {
        return try {
            val snapshot = usersCollection?.document(username.normalizedUsername())?.get()?.await()
            snapshot?.toObject(User::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user: ${e.message}")
            null
        }
    }

    suspend fun saveBooking(booking: Booking) {
        try {
            bookingsCollection?.document(booking.bookingId)?.set(booking)?.await()
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
            bookingsCollection
                ?.whereEqualTo("customerUsername", username)
                ?.orderBy("bookingDate")
                ?.get()
                ?.await()
                ?.toObjects(Booking::class.java) ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting bookings for user: ${e.message}")
            emptyList()
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
            Log.e(TAG, "Error getting all users: ${e.message}")
            emptyList()
        }
    }

    suspend fun updateUser(user: User) {
        try {
            usersCollection?.document(user.username.normalizedUsername())?.set(user)?.await()
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

    suspend fun saveVoucher(code: String, discountPercentage: Int, description: String = "", startDate: String = "", endDate: String = "") {
        try {
            val voucherData = mapOf(
                "code" to code.trim().uppercase(),
                "discountPercentage" to discountPercentage,
                "description" to description.trim(),
                "startDate" to startDate.trim(),
                "endDate" to endDate.trim()
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

    private fun String.normalizedUsername(): String = trim().lowercase()
}
