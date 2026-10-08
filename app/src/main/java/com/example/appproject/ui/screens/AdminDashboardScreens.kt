package com.example.appproject.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.appproject.R
import com.example.appproject.data.model.Booking
import com.example.appproject.data.model.StatusOption
import com.example.appproject.data.model.getBookingStatusDisplay
import com.example.appproject.data.model.getPaymentStatusDisplay
import com.example.appproject.data.model.normalizeBookingStatus
import com.example.appproject.data.model.normalizePaymentStatus
import com.example.appproject.data.repository.FirebaseRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AdminScreen(
    firebaseRepo: FirebaseRepository
) {
    val context = LocalContext.current
    var bookingList by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var bookingBeingEdited by remember { mutableStateOf<Booking?>(null) }
    var bookingToDelete by remember { mutableStateOf<Booking?>(null) }
    var filterStatus by rememberSaveable { mutableStateOf("All") }
    var connectionStatus by remember { mutableStateOf(context.getString(R.string.testing)) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isLoading = true
        connectionStatus = firebaseRepo.testConnection()
        bookingList = firebaseRepo.getAllBookings()
        isLoading = false
    }

    val tabs = listOf(
        Pair("All", stringResource(R.string.all)),
        Pair("Pending", stringResource(R.string.pending)),
        Pair("Confirmed", stringResource(R.string.confirmed)),
        Pair("Cancelled", stringResource(R.string.cancelled))
    )

    val filteredList = bookingList.filter {
        val name = it.customerName.ifBlank { it.customerUsername }
        val dest = it.destination ?: ""
        val bId = it.bookingId ?: ""
        val status = it.status ?: ""

        val matchesSearch = searchQuery.isBlank() ||
                name.contains(searchQuery, ignoreCase = true) ||
                dest.contains(searchQuery, ignoreCase = true) ||
                bId.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (filterStatus) {
            "All" -> true
            else -> normalizeBookingStatus(status) == filterStatus
        }

        matchesSearch && matchesFilter
    }

    val totalBookings = bookingList.size
    val confirmedBookings = bookingList.count { normalizeBookingStatus(it.status) == "Confirmed" }
    val pendingPayments = bookingList.count { normalizeBookingStatus(it.status) == "Pending" }
    val cancelledBookingsCount = bookingList.count { normalizeBookingStatus(it.status) == "Cancelled" }

    Scaffold(
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.admin_dashboard),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (connectionStatus == "Connected") stringResource(R.string.synced_and_connected) else connectionStatus,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                    OutlinedCard(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                connectionStatus = firebaseRepo.testConnection()
                                bookingList = firebaseRepo.getAllBookings()
                                isLoading = false
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(44.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color(0xFF334155))
                        }
                    }
                }
            }

            // 2x2 KPI Summary Card
            item {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Row 1: Total bookings | Confirmed
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Column(modifier = Modifier.padding(start = 16.dp)) {
                                    Text(stringResource(R.string.total_bookings), style = MaterialTheme.typography.labelMedium, color = Color(0xFF64748B))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("$totalBookings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                }
                            }
                            Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color(0xFFE2E8F0)))
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Column(modifier = Modifier.padding(start = 16.dp)) {
                                    Text(stringResource(R.string.confirmed), style = MaterialTheme.typography.labelMedium, color = Color(0xFF64748B))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("$confirmedBookings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                                }
                            }
                        }
                        HorizontalDivider(color = Color(0xFFE2E8F0))
                        // Row 2: Pending | Cancelled
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Column(modifier = Modifier.padding(start = 16.dp)) {
                                    Text(stringResource(R.string.pending), style = MaterialTheme.typography.labelMedium, color = Color(0xFF64748B))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("$pendingPayments", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                                }
                            }
                            Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color(0xFFE2E8F0)))
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Column(modifier = Modifier.padding(start = 16.dp)) {
                                    Text(stringResource(R.string.cancelled), style = MaterialTheme.typography.labelMedium, color = Color(0xFF64748B))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("$cancelledBookingsCount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFFB91C1C))
                                }
                            }
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.search_by_name_destination_or_id), color = Color(0xFF94A3B8)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search", tint = Color(0xFF64748B))
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedBorderColor = Color(0xFF0284C7)
                    )
                )
            }

            // Tabs Row
            item {
                val selectedTabIndex = tabs.indexOfFirst { it.first == filterStatus }.coerceAtLeast(0)
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF0284C7),
                    indicator = { tabPositions ->
                        if (selectedTabIndex < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = Color(0xFF0284C7),
                                height = 2.dp
                            )
                        }
                    },
                    divider = {}
                ) {
                    tabs.forEach { (canonicalKey, tabName) ->
                        val isSelected = filterStatus == canonicalKey
                        val defaultStyle = MaterialTheme.typography.labelLarge
                        Tab(
                            selected = isSelected,
                            onClick = { filterStatus = canonicalKey },
                            text = {
                                var textStyle by remember(tabName) { mutableStateOf(defaultStyle) }
                                var readyToDraw by remember(tabName) { mutableStateOf(false) }

                                Text(
                                    text = tabName,
                                    style = textStyle,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color(0xFF0284C7) else Color(0xFF64748B),
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Clip,
                                    modifier = Modifier.drawWithContent {
                                        if (readyToDraw) {
                                            drawContent()
                                        }
                                    },
                                    onTextLayout = { textLayoutResult ->
                                        if (textLayoutResult.didOverflowWidth) {
                                            textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9f)
                                        } else {
                                            readyToDraw = true
                                        }
                                    }
                                )
                            }
                        )
                    }
                }
            }

            // Bookings List
            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.loading_bookings), color = Color(0xFF64748B))
                    }
                }
            } else if (filteredList.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.no_bookings_found), color = Color(0xFF64748B))
                    }
                }
            } else {
                items(filteredList, key = { it.bookingId }) { booking ->
                    AdminBookingCard(
                        booking = booking,
                        onEdit = { bookingBeingEdited = booking },
                        onDelete = { bookingToDelete = booking }
                    )
                }
            }
        }
    }

    bookingBeingEdited?.let { booking ->
        BookingEditDialog(
            booking = booking,
            onDismiss = { bookingBeingEdited = null },
            onSave = { updatedBooking ->
                scope.launch {
                    firebaseRepo.updateBooking(updatedBooking)
                    bookingList = bookingList.map {
                        if (it.bookingId == updatedBooking.bookingId) updatedBooking else it
                    }
                    bookingBeingEdited = null
                    Toast.makeText(context, (R.string.booking_updated_successfully), Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    bookingToDelete?.let { booking ->
        DeleteBookingDialog(
            booking = booking,
            onDismiss = { bookingToDelete = null },
            onConfirmDelete = {
                scope.launch {
                    firebaseRepo.deleteBooking(booking.bookingId)
                    bookingList = bookingList.filterNot { it.bookingId == booking.bookingId }
                    Toast.makeText(context, (R.string.booking_deleted_successfully), Toast.LENGTH_SHORT).show()
                    bookingToDelete = null
                }
            }
        )
    }
}

@Composable
fun AdminBookingCard(
    booking: Booking,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val normStatus = normalizeBookingStatus(booking.status)
    val statusText = getBookingStatusDisplay(booking.status, context)
    val statusColor = when (normStatus) {
        "Confirmed" -> Color(0xFF047857)
        "Cancelled" -> Color(0xFFB91C1C)
        else -> Color(0xFFD97706)
    }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Destination & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.destination.ifBlank { stringResource(R.string.tour_package) },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.booking_id) + " #${booking.bookingId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelLarge,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(12.dp))

            // Details Table Rows
            BookingDetailRow(
                label = stringResource(R.string.customer),
                value = booking.customerName.ifBlank { booking.customerUsername.ifBlank { "User" } }
            )
            if (booking.customerPhone.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                BookingDetailRow(label = stringResource(R.string.phone), value = booking.customerPhone)
            }
            if (booking.bookingDate.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                BookingDetailRow(label = stringResource(R.string.date), value = booking.bookingDate)
            }
            if (booking.voucherUsed.isNotBlank() && booking.discountAmount.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                BookingDetailRow(
                    label = stringResource(R.string.discount),
                    value = "-${booking.discountAmount}",
                    valueColor = Color(0xFFB91C1C)
                )
            }
            if (booking.finalPrice.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                BookingDetailRow(label = stringResource(R.string.price_paid), value = booking.finalPrice)
            }
            if (booking.paymentStatus.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                val normPayment = normalizePaymentStatus(booking.paymentStatus)
                val paymentDisplay = getPaymentStatusDisplay(booking.paymentStatus, context)
                val paymentColor = if (normPayment == "Paid") Color(0xFF047857) else Color(0xFFD97706)
                BookingDetailRow(
                    label = stringResource(R.string.payment_status),
                    value = paymentDisplay,
                    valueColor = paymentColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF0284C7)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        stringResource(R.string.edit),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0284C7)
                    )
                }
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFFFF1F2))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFFE11D48)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        stringResource(R.string.delete),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE11D48)
                    )
                }
            }
        }
    }
}

@Composable
fun BookingDetailRow(
    label: String,
    value: String,
    valueColor: Color = Color(0xFF0F172A)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF64748B),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.5f)
        )
    }
}

@Composable
fun DeleteBookingDialog(
    booking: Booking,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    val context = LocalContext.current
    val normStatus = normalizeBookingStatus(booking.status)
    val statusText = getBookingStatusDisplay(booking.status, context)
    val statusColor = when (normStatus) {
        "Confirmed" -> Color(0xFF047857)
        "Cancelled" -> Color(0xFFB91C1C)
        else -> Color(0xFFD97706)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_booking), fontWeight = FontWeight.Bold, color = Color(0xFF0F172A)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = stringResource(R.string.delete_booking_confirmation),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF475569)
                )

                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFFF8FAFC)),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BookingDetailRow(label = stringResource(R.string.booking_id), value = "#${booking.bookingId}")
                        HorizontalDivider(color = Color(0xFFE2E8F0))
                        BookingDetailRow(label = stringResource(R.string.customer_name), value = booking.customerName.ifBlank { booking.customerUsername.ifBlank { "User" } })
                        if (booking.customerPhone.isNotBlank()) {
                            BookingDetailRow(label = stringResource(R.string.phone), value = booking.customerPhone)
                        }
                        HorizontalDivider(color = Color(0xFFE2E8F0))
                        BookingDetailRow(label = stringResource(R.string.destination), value = booking.destination.ifBlank { stringResource(R.string.tour_package) })
                        if (booking.bookingDate.isNotBlank()) {
                            BookingDetailRow(label = stringResource(R.string.date), value = booking.bookingDate)
                        }
                        if (booking.finalPrice.isNotBlank()) {
                            BookingDetailRow(label = stringResource(R.string.price_paid), value = booking.finalPrice)
                        }
                        BookingDetailRow(label = stringResource(R.string.status), value = statusText, valueColor = statusColor)
                        val normPayment = normalizePaymentStatus(booking.paymentStatus)
                        val paymentDisplay = getPaymentStatusDisplay(booking.paymentStatus, context)
                        val paymentColor = if (normPayment == "Paid") Color(0xFF047857) else Color(0xFFD97706)
                        BookingDetailRow(
                            label = stringResource(R.string.payment_status),
                            value = paymentDisplay,
                            valueColor = paymentColor
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C))
            ) {
                Text(stringResource(R.string.delete), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
            ) {
                Text(stringResource(R.string.cancel), color = Color(0xFF334155))
            }
        }
    )
}

@Composable
fun BookingEditDialog(
    booking: Booking,
    onDismiss: () -> Unit,
    onSave: (Booking) -> Unit
) {
    var customerName by remember(booking) { mutableStateOf(booking.customerName.ifBlank { booking.customerUsername }) }
    var customerPhone by remember(booking) { mutableStateOf(booking.customerPhone) }
    var destination by remember(booking) { mutableStateOf(booking.destination) }
    var bookingDate by remember(booking) { mutableStateOf(booking.bookingDate) }
    var bookingStatus by remember(booking) { mutableStateOf(normalizeBookingStatus(booking.status)) }
    var paymentStatus by remember(booking) { mutableStateOf(normalizePaymentStatus(booking.paymentStatus)) }

    val bookingStatusOptions = listOf(
        StatusOption("Pending", stringResource(R.string.pending)),
        StatusOption("Confirmed", stringResource(R.string.confirmed)),
        StatusOption("Cancelled", stringResource(R.string.cancelled))
    )

    val paymentStatusOptions = listOf(
        StatusOption("Pending", stringResource(R.string.pending)),
        StatusOption("Paid", stringResource(R.string.paid)),
        StatusOption("Cancelled", stringResource(R.string.cancelled))
    )

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Update booking", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(2.dp))
                Text("Booking #${booking.bookingId}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(stringResource(R.string.customer_name), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedBorderColor = Color(0xFF0284C7)
                    )
                )

                Text(stringResource(R.string.phone_number), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { customerPhone = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedBorderColor = Color(0xFF0284C7)
                    )
                )

                Text(stringResource(R.string.destination), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedBorderColor = Color(0xFF0284C7)
                    )
                )

                Text(stringResource(R.string.booking_date), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                OutlinedTextField(
                    value = bookingDate,
                    onValueChange = { bookingDate = it },
                    modifier = Modifier.fillMaxWidth().clickable {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                val selectedCalendar = Calendar.getInstance().apply {
                                    set(year, month, day)
                                }
                                bookingDate = SimpleDateFormat("dd MMM yyyy", Locale.US).format(selectedCalendar.time)
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    readOnly = true,
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedBorderColor = Color(0xFF0284C7)
                    )
                )

                BookingDropdown(
                    label = stringResource(R.string.booking_status),
                    selectedCanonical = bookingStatus,
                    options = bookingStatusOptions,
                    onValueSelected = { bookingStatus = it }
                )

                BookingDropdown(
                    label = stringResource(R.string.payment_status),
                    selectedCanonical = paymentStatus,
                    options = paymentStatusOptions,
                    onValueSelected = { paymentStatus = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        booking.copy(
                            customerName = customerName,
                            customerPhone = customerPhone,
                            destination = destination,
                            bookingDate = bookingDate,
                            status = bookingStatus,
                            paymentStatus = paymentStatus
                        )
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0369A1))
            ) {
                Text(stringResource(R.string.save_changes), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
            ) {
                Text(stringResource(R.string.cancel), color = Color(0xFF334155))
            }
        }
    )
}

@Composable
fun BookingDropdown(
    label: String,
    selectedCanonical: String,
    options: List<StatusOption>,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currentDisplay = options.find { it.canonical == selectedCanonical }?.display
        ?: getBookingStatusDisplay(selectedCanonical)

    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
        Spacer(modifier = Modifier.height(4.dp))
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(currentDisplay, color = Color(0xFF0F172A), style = MaterialTheme.typography.bodyMedium)
                    Text("▼", color = Color(0xFF64748B), style = MaterialTheme.typography.labelSmall)
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.display) },
                        onClick = {
                            onValueSelected(option.canonical)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
