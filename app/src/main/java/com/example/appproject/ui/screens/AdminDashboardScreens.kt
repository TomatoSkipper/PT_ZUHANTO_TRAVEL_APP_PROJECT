package com.example.appproject.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.window.core.layout.WindowWidthSizeClass
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appproject.R
import com.example.appproject.data.model.Booking
import com.example.appproject.data.repository.FirebaseRepository
import com.example.appproject.data.model.StatusOption
import com.example.appproject.data.model.getBookingStatusDisplay
import com.example.appproject.data.model.normalizeBookingStatus
import com.example.appproject.data.model.normalizePaymentStatus
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

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val columnCount = when (adaptiveInfo.windowSizeClass.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> 1
        WindowWidthSizeClass.MEDIUM -> 2
        else -> 3
    }

    LaunchedEffect(Unit) {
        isLoading = true
        connectionStatus = firebaseRepo.testConnection()
        bookingList = firebaseRepo.getAllBookings()
        isLoading = false
    }

    val filteredList = bookingList.filter {
        val name = it.customerName ?: ""
        val dest = it.destination ?: ""
        val bId = it.bookingId ?: ""
        val status = it.status ?: ""
        
        val matchesSearch = searchQuery.isBlank() || 
                name.contains(searchQuery, ignoreCase = true) ||
                dest.contains(searchQuery, ignoreCase = true) ||
                bId.contains(searchQuery, ignoreCase = true)
        
        val matchesFilter = if (filterStatus == "All") true else normalizeBookingStatus(status) == filterStatus
        
        matchesSearch && matchesFilter
    }

    val totalBookings = bookingList.size
    val confirmedBookings = bookingList.count { normalizeBookingStatus(it.status) == "Confirmed" }
    val pendingPayments = bookingList.count { normalizeBookingStatus(it.status) == "Pending" }
    val cancelledBookingsCount = bookingList.count { normalizeBookingStatus(it.status) == "Cancelled" }

    Scaffold { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        SectionHeader(title = stringResource(R.string.admin_dashboard))
                        
                        Spacer(modifier = Modifier.height(8.dp))

                        val isAdminConnected = connectionStatus == "Connected"
                        val adminDisplayStatus = if (isAdminConnected) stringResource(R.string.connected) else connectionStatus
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isAdminConnected) Color.Green else Color.Red)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.sync_status, adminDisplayStatus),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isAdminConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(onClick = {
                                scope.launch {
                                    connectionStatus = context.getString(R.string.syncing)
                                    connectionStatus = firebaseRepo.testConnection()
                                    bookingList = firebaseRepo.getAllBookings()
                                }
                            }) {
                                Text(stringResource(R.string.refresh_sync))
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AdminStatCard(
                                    title = stringResource(R.string.total),
                                    value = totalBookings.toString(),
                                    icon = Icons.Default.Book,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f)
                                )
                                AdminStatCard(
                                    title = stringResource(R.string.confirmed),
                                    value = confirmedBookings.toString(),
                                    icon = Icons.Default.CheckCircle,
                                    color = Color(0xFF4CAF50),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AdminStatCard(
                                    title = stringResource(R.string.pending),
                                    value = pendingPayments.toString(),
                                    icon = Icons.Default.Payment,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.weight(1f)
                                )
                                AdminStatCard(
                                    title = stringResource(R.string.cancelled),
                                    value = cancelledBookingsCount.toString(),
                                    icon = Icons.Default.Close,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(stringResource(R.string.search_bookings_placeholder)) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val filterOptions = listOf(
                                "All" to stringResource(R.string.all),
                                "Pending" to stringResource(R.string.pending),
                                "Confirmed" to stringResource(R.string.confirmed),
                                "Cancelled" to stringResource(R.string.cancelled)
                            )
                            filterOptions.forEach { (key, label) ->
                                FilterChip(
                                    selected = filterStatus == key,
                                    onClick = { filterStatus = key },
                                    label = { Text(label, maxLines = 1) },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (filteredList.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .heightIn(min = 200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                if (isLoading) stringResource(R.string.loading_bookings)
                                else if (connectionStatus != "Connected") stringResource(R.string.sync_failed, connectionStatus)
                                else stringResource(R.string.no_bookings_found),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            } else {
                items(filteredList, key = { it.bookingId }) { booking ->
                    AdminBookingCard(
                        booking = booking,
                        onConfirm = {
                            scope.launch {
                                val confirmedBooking = booking.copy(status = "Confirmed")
                                firebaseRepo.updateBooking(confirmedBooking)
                                bookingList = bookingList.map {
                                    if (it.bookingId == confirmedBooking.bookingId) confirmedBooking else it
                                }
                            }
                        },
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
                }
            }
        )
    }

    bookingToDelete?.let { booking ->
        AlertDialog(
            onDismissRequest = { bookingToDelete = null },
            title = { Text(stringResource(R.string.delete_booking_title)) },
            text = {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.delete_booking_confirmation_msg)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.booking_id) + ": " + booking.bookingId,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.customer_name) + ": " + booking.customerName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.destination) + ": " + booking.destination,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            firebaseRepo.deleteBooking(booking.bookingId)
                            bookingList = bookingList.filterNot { it.bookingId == booking.bookingId }
                            Toast.makeText(
                                context,
                                context.getString(R.string.booking_deleted_success, booking.bookingId),
                                Toast.LENGTH_SHORT
                            ).show()
                            bookingToDelete = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { bookingToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun AdminStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = Color.Black)
        }
    }
}

@Composable
fun AdminBookingCard(
    booking: Booking,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onConfirm: (() -> Unit)? = null
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.destination,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.booking_id_format, booking.bookingId),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                PaymentStatusBadge(status = booking.paymentStatus)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(modifier = Modifier.alpha(0.3f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = booking.customerName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = booking.customerPhone, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = booking.bookingDate, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically){
                Icon(
                    Icons.Default.ConfirmationNumber,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.discount) + ": ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error
                )
                if (booking.voucherUsed.isNotBlank()) {
                    Text(
                        text = "-" + booking.discount,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                } else{
                    Text(
                        text = stringResource(R.string.no_voucher_used),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (booking.finalPrice.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Paid,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(
                            R.string.price_paid_format,
                            booking.finalPrice),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

            }

            Spacer(modifier = Modifier.height(4.dp))

            val methodText = booking.paymentMethod.ifBlank { stringResource(R.string.no_payment_method) }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Wallet,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = methodText.uppercase(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                val normStatus = normalizeBookingStatus(booking.status)
                val displayStatus = getBookingStatusDisplay(booking.status)
                val (statusIcon, statusColor) = when (normStatus) {
                    "Confirmed" -> Pair(Icons.Default.CheckCircle, Color(0xFF4CAF50))
                    "Cancelled" -> Pair(Icons.Default.Close, MaterialTheme.colorScheme.error)
                    else -> Pair(Icons.Default.Info, MaterialTheme.colorScheme.tertiary)
                }
                Icon(statusIcon, contentDescription = null, modifier = Modifier.size(18.dp), tint = statusColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.status_format, displayStatus),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (normalizeBookingStatus(booking.status) == "Pending" && onConfirm != null) {
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.confirm_booking),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                FilledTonalIconButton(
                    onClick = onEdit,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                FilledTonalIconButton(
                    onClick = onDelete,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
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
    var customerName by remember(booking) { mutableStateOf(booking.customerName) }
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
        StatusOption("Paid", stringResource(R.string.paid_lower))
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${stringResource(R.string.update_booking)} #${booking.bookingId}") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text(stringResource(R.string.customer_name)) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { customerPhone = it },
                    label = { Text(stringResource(R.string.phone_number)) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = destination,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.destination)) },
                    singleLine = true
                )
                val context = LocalContext.current
                OutlinedButton(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                val selectedCalendar = Calendar.getInstance().apply {
                                    set(year, month, day)
                                }
                                bookingDate = SimpleDateFormat(
                                    "dd MMM yyyy",
                                    Locale.getDefault()
                                ).format(selectedCalendar.time)
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).apply {
                            datePicker.minDate = System.currentTimeMillis() - 1000
                        }.show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("${stringResource(R.string.booking_date)} $bookingDate")
                }
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
            TextButton(
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
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
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

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("$label: $currentDisplay")
                Text("▼")
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
