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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.appproject.ui.components.BookingCardSkeleton
import com.example.appproject.ui.theme.PrimaryBlue
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
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.sp
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
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
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
                items(3) {
                    BookingCardSkeleton(modifier = Modifier.padding(bottom = 12.dp))
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
                        onDelete = { bookingToDelete = booking },
                        onConfirmBooking = {
                            scope.launch {
                                val updatedBooking = booking.copy(status = "Confirmed")
                                firebaseRepo.updateBooking(updatedBooking)
                                bookingList = bookingList.map {
                                    if (it.bookingId == updatedBooking.bookingId) updatedBooking else it
                                }
                                Toast.makeText(context, R.string.booking_updated_successfully, Toast.LENGTH_SHORT).show()
                            }
                        }
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
    onDelete: () -> Unit,
    onConfirmBooking: () -> Unit
) {
    val context = LocalContext.current
    val details = remember(booking.destination) { parseBookingDestinationDetails(booking.destination) }
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
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Destination Title & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = details.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A),
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${details.paxInfo}, ${stringResource(R.string.booking_id)} #${booking.bookingId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B),
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
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
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            // Customer Note Box
            if (details.noteText.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = Color(0xFF475569),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.customer_note),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = details.noteText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF0F172A)
                        )
                    }
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
                BookingDetailRow(label = stringResource(R.string.travel_date), value = booking.bookingDate)
            }
            if (booking.finalPrice.isNotBlank() || booking.basePrice.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                BookingDetailRow(
                    label = stringResource(R.string.total_price),
                    value = booking.finalPrice.ifBlank { booking.basePrice }
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            val voucherDisplay = if (booking.voucherUsed.isNotBlank()) {
                val discountText = if (booking.discountAmount.isNotBlank()) " (-${booking.discountAmount})" else ""
                "${booking.voucherUsed}$discountText"
            } else {
                stringResource(R.string.no_voucher_used)
            }
            val voucherColor = if (booking.voucherUsed.isNotBlank()) Color(0xFF0284C7) else Color(0xFF64748B)
            BookingDetailRow(
                label = stringResource(R.string.voucher),
                value = voucherDisplay,
                valueColor = voucherColor
            )
            if (booking.paymentStatus.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                val normPayment = normalizePaymentStatus(booking.paymentStatus)
                val paymentDisplay = getPaymentStatusDisplay(booking.paymentStatus, context)
                val paymentColor = if (normPayment == "Paid") Color(0xFF047857) else Color(0xFFD97706)
                BookingDetailRow(
                    label = stringResource(R.string.payment_status),
                    value = paymentDisplay,
                    valueColor = paymentColor,
                    showDot = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Action Button: Confirm Booking
            if (normStatus == "Pending") {
                Button(
                    onClick = onConfirmBooking,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D5C91))
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.confirm_booking),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Secondary Action Buttons Row: Edit & Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(10.dp),
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
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(10.dp),
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
    valueColor: Color = Color(0xFF0F172A),
    showDot: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF64748B),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            if (showDot) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(valueColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun DeleteBookingDialog(
    booking: Booking,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    val context = LocalContext.current
    var isUnderstandChecked by remember { mutableStateOf(false) }

    val normStatus = normalizeBookingStatus(booking.status)
    val normPayment = normalizePaymentStatus(booking.paymentStatus)
    val statusText = getBookingStatusDisplay(booking.status, context)
    val statusColor = when (normStatus) {
        "Confirmed" -> Color(0xFF047857)
        "Cancelled" -> Color(0xFFB91C1C)
        else -> Color(0xFFD97706)
    }

    val paymentDisplay = getPaymentStatusDisplay(booking.paymentStatus, context)
    val paymentColor = if (normPayment == "Paid") Color(0xFF047857) else Color(0xFFD97706)

    val isConfirmedOrPaid = normStatus == "Confirmed" || normPayment == "Paid"

    val warningMsg = when {
        normStatus == "Confirmed" && normPayment == "Paid" ->
            stringResource(R.string.delete_booking_warn_confirmed_paid)
        normStatus == "Confirmed" ->
            stringResource(R.string.delete_booking_warn_confirmed)
        else ->
            stringResource(R.string.delete_booking_warn_paid)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Trash icon in soft red circular badge
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFEE2E2),
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // Title & Subtitle
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.delete_this_booking),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = stringResource(R.string.this_cant_be_undone),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B)
                    )
                }

                // Details Card Box
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BookingDetailRow(
                            label = stringResource(R.string.booking_id),
                            value = "#${booking.bookingId}"
                        )
                        HorizontalDivider(color = Color(0xFFE2E8F0))
                        BookingDetailRow(
                            label = stringResource(R.string.customer_name),
                            value = booking.customerName.ifBlank { booking.customerUsername.ifBlank { "User" } }
                        )
                        if (booking.customerPhone.isNotBlank()) {
                            BookingDetailRow(
                                label = stringResource(R.string.phone),
                                value = booking.customerPhone
                            )
                        }
                        HorizontalDivider(color = Color(0xFFE2E8F0))
                        BookingDetailRow(
                            label = stringResource(R.string.package_label),
                            value = booking.destination.ifBlank { stringResource(R.string.tour_package) }
                        )
                        if (booking.bookingDate.isNotBlank()) {
                            BookingDetailRow(
                                label = stringResource(R.string.travel_date_label),
                                value = booking.bookingDate
                            )
                        }
                        if (booking.finalPrice.isNotBlank()) {
                            BookingDetailRow(
                                label = stringResource(R.string.total_label),
                                value = booking.finalPrice
                            )
                        }
                        BookingDetailRow(
                            label = stringResource(R.string.applied_voucher),
                            value = if (booking.voucherUsed.isNotBlank()) "${booking.voucherUsed}${if (booking.discountAmount.isNotBlank()) " (-${booking.discountAmount})" else ""}" else stringResource(R.string.no_voucher_used)
                        )
                        BookingDetailRow(
                            label = stringResource(R.string.booking_label),
                            value = statusText,
                            valueColor = statusColor,
                            showDot = true
                        )
                        BookingDetailRow(
                            label = stringResource(R.string.payment_label),
                            value = paymentDisplay,
                            valueColor = paymentColor,
                            showDot = true
                        )
                    }
                }

                // Warning Banner
                if (isConfirmedOrPaid) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFFFBEB),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Warning,
                                contentDescription = null,
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = warningMsg,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF92400E),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // Checkbox Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isUnderstandChecked = !isUnderstandChecked }
                ) {
                    Checkbox(
                        checked = isUnderstandChecked,
                        onCheckedChange = { isUnderstandChecked = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFFEF4444)
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.i_understand_delete),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E293B)
                    )
                }

                // Action Buttons
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onConfirmDelete,
                        enabled = isUnderstandChecked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE57373),
                            disabledContainerColor = Color(0xFFFCA5A5).copy(alpha = 0.5f),
                            contentColor = Color.White,
                            disabledContentColor = Color.White.copy(alpha = 0.7f)
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.delete_booking_button),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Text(
                            text = stringResource(R.string.keep_booking),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2563EB)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookingEditDialog(
    booking: Booking,
    onDismiss: () -> Unit,
    onSave: (Booking) -> Unit
) {
    val context = LocalContext.current
    val details = remember(booking.destination) { parseBookingDestinationDetails(booking.destination) }

    var customerName by remember(booking) { mutableStateOf(booking.customerName.ifBlank { booking.customerUsername }) }
    var customerPhone by remember(booking) { mutableStateOf(booking.customerPhone) }
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

    val openDatePicker = {
        val calendar = Calendar.getInstance()
        try {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.US)
            val parsed = sdf.parse(bookingDate)
            if (parsed != null) {
                calendar.time = parsed
            }
        } catch (_: Exception) {}

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                bookingDate = SimpleDateFormat("dd MMM yyyy", Locale.US).format(selectedCalendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Column {
                Text("Update booking", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(2.dp))
                Text("Booking #${booking.bookingId}", fontSize = 13.sp, color = Color(0xFF64748B))
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Customer Name
                Column {
                    Text(stringResource(R.string.customer_name), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedBorderColor = Color(0xFF0284C7)
                        )
                    )
                }

                // 2. Phone Number
                Column {
                    Text(stringResource(R.string.phone_number), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedBorderColor = Color(0xFF0284C7)
                        )
                    )
                }

                // 3. Package (read only)
                Column {
                    Text(stringResource(R.string.package_read_only), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = "${details.title}, ${details.paxInfo}",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF1F5F9),
                            focusedContainerColor = Color(0xFFF1F5F9),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedBorderColor = Color(0xFFE2E8F0)
                        )
                    )
                }

                // 4. Customer note (read only)
                Column {
                    Text(stringResource(R.string.customer_note_read_only), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = details.noteText.ifBlank { "" },
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF1F5F9),
                            focusedContainerColor = Color(0xFFF1F5F9),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedBorderColor = Color(0xFFE2E8F0)
                        )
                    )
                }

                // 4b. Voucher Used (read only)
                Column {
                    Text(stringResource(R.string.applied_voucher), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = if (booking.voucherUsed.isNotBlank()) "${booking.voucherUsed}${if (booking.discountAmount.isNotBlank()) " (-${booking.discountAmount})" else ""}" else stringResource(R.string.no_voucher_used),
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF1F5F9),
                            focusedContainerColor = Color(0xFFF1F5F9),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedBorderColor = Color(0xFFE2E8F0)
                        )
                    )
                }

                // 5. Travel date
                Column {
                    Text(stringResource(R.string.travel_date), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = bookingDate,
                        onValueChange = { bookingDate = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { openDatePicker() },
                        readOnly = true,
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        trailingIcon = {
                            IconButton(onClick = { openDatePicker() }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Select Date",
                                    tint = Color(0xFF2563EB)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color(0xFF64748B),
                            focusedBorderColor = Color(0xFF0284C7)
                        )
                    )
                }

                // 6 & 7. Booking Status and Payment Dropdowns (Side by Side Row)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        BookingDropdown(
                            label = stringResource(R.string.booking_status),
                            selectedCanonical = bookingStatus,
                            options = bookingStatusOptions,
                            onValueSelected = { bookingStatus = it }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        BookingDropdown(
                            label = stringResource(R.string.payment_status),
                            selectedCanonical = paymentStatus,
                            options = paymentStatusOptions,
                            onValueSelected = { paymentStatus = it }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        booking.copy(
                            customerName = customerName,
                            customerPhone = customerPhone,
                            bookingDate = bookingDate,
                            status = bookingStatus,
                            paymentStatus = paymentStatus
                        )
                    )
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D5C91)),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Text(stringResource(R.string.save_changes), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Text(stringResource(R.string.cancel), color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 14.sp)
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

    val currentStatusColor = when (normalizeBookingStatus(selectedCanonical)) {
        "Confirmed", "Paid" -> Color(0xFF047857)
        "Cancelled" -> Color(0xFFB91C1C)
        else -> Color(0xFFD97706)
    }

    Column {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
        Spacer(modifier = Modifier.height(4.dp))
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (expanded) PrimaryBlue else Color(0xFFCBD5E1)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(currentStatusColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(currentDisplay, color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color(0xFF64748B)
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.85f).background(Color.White)
            ) {
                options.forEach { option ->
                    val isSelected = option.canonical == selectedCanonical
                    val optionStatusColor = when (normalizeBookingStatus(option.canonical)) {
                        "Confirmed", "Paid" -> Color(0xFF047857)
                        "Cancelled" -> Color(0xFFB91C1C)
                        else -> Color(0xFFD97706)
                    }
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(optionStatusColor)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = option.display,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) PrimaryBlue else Color(0xFF0F172A)
                                    )
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryBlue)
                                }
                            }
                        },
                        onClick = {
                            onValueSelected(option.canonical)
                            expanded = false
                        },
                        modifier = Modifier.background(if (isSelected) PrimaryBlue.copy(alpha = 0.1f) else Color.Transparent)
                    )
                }
            }
        }
    }
}
