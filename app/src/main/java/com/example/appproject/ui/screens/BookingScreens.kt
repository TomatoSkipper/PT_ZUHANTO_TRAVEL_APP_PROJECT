package com.example.appproject.ui.screens

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.text.TextUtils
import android.util.Base64
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.appproject.R
import com.example.appproject.data.model.ADMIN_USERNAME
import com.example.appproject.data.model.Booking
import com.example.appproject.data.repository.FirebaseRepository
import com.example.appproject.data.model.VoucherStatus
import com.example.appproject.data.model.getBookingStatusDisplay
import com.example.appproject.data.model.getPaymentStatusDisplay
import com.example.appproject.data.model.normalizeBookingStatus
import com.example.appproject.data.model.normalizePaymentStatus
import com.example.appproject.data.model.parseBookingDate
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun MyBookingsScreen(
    username: String,
    firebaseRepo: FirebaseRepository,
    onViewReceipt: (String) -> Unit,
    onViewInvoice: (String) -> Unit,
    onPayNow: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    var bookings by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var bookingToCancel by remember { mutableStateOf<Booking?>(null) }
    var bookingToPay by remember { mutableStateOf<Booking?>(null) }
    var showRefundNotice by remember { mutableStateOf(false) }
    var connectionStatus by remember { mutableStateOf("Connected") }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(username) {
        isLoading = true
        val test = firebaseRepo.testConnection()
        if (test != "Connected") {
            connectionStatus = test
        }
        bookings = firebaseRepo.getBookingsForUser(username)
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        SectionHeader(title = stringResource(R.string.my_bookings))
        
        if (connectionStatus != "Connected") {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.sync_error, connectionStatus),
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (bookings.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.no_booking_message),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            val today = remember {
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }

            val (pastBookings, currentBookings) = bookings.partition {
                val isCancelled = normalizeBookingStatus(it.status) == "Cancelled"
                try {
                    val date = parseBookingDate(it.bookingDate)?.time ?: 0L
                    date < today || isCancelled
                } catch (_: Exception) {
                    isCancelled
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (currentBookings.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.active_booking),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(currentBookings, key = { it.bookingId }) { booking ->
                        UserBookingCard(
                            booking = booking,
                            firebaseRepo = firebaseRepo,
                            onViewReceipt = onViewReceipt,
                            onViewInvoice = onViewInvoice,
                            onCancel = { bookingToCancel = booking },
                            onPayNow = {
                                if (onPayNow != null) {
                                    onPayNow(booking.bookingId)
                                } else {
                                    bookingToPay = booking
                                }
                            }
                        )
                    }
                }

                if (pastBookings.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.booking_history),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(top = 24.dp)
                        )
                    }
                    items(pastBookings, key = { it.bookingId }) { booking ->
                        UserBookingCard(
                            booking = booking,
                            firebaseRepo = firebaseRepo,
                            onViewReceipt = onViewReceipt,
                            onViewInvoice = onViewInvoice,
                            isHistory = true
                        )
                    }
                }
            }
        }
    }

    bookingToCancel?.let { booking ->
        AlertDialog(
            onDismissRequest = { bookingToCancel = null },
            title = { Text("${stringResource(R.string.cancel_booking)}?") },
            text = {
                Text(
                    text = buildAnnotatedString {
                        append("${stringResource(R.string.confirmation_cancel_booking)} ${booking.destination}?\n\n")

                        withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                            append("${stringResource(R.string.cancel_terms)}*")
                        }
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            val cancelledBooking = booking.copy(status = "Cancelled")
                            firebaseRepo.updateBooking(cancelledBooking)
                            bookings = bookings.map { if (it.bookingId == cancelledBooking.bookingId) cancelledBooking else it }
                            bookingToCancel = null
                            showRefundNotice = true
                        }
                    }
                ) {
                    Text(stringResource(R.string.cancel_booking))
                }
            },
            dismissButton = {
                TextButton(onClick = { bookingToCancel = null }) {
                    Text(stringResource(R.string.keep_booking))
                }
            }
        )
    }

    if (showRefundNotice) {
        AlertDialog(
            onDismissRequest = { showRefundNotice = false },
            title = { Text(stringResource(R.string.booking_cancelled)) },
            text = { Text(stringResource(R.string.refund)) },
            confirmButton = {
                TextButton(onClick = { showRefundNotice = false }) {
                    Text("OKAY")
                }
            }
        )
    }

    bookingToPay?.let { booking ->
        PaymentDialog(
            booking = booking,
            onDismiss = { bookingToPay = null },
            onConfirmPayment = { paidBooking ->
                scope.launch {
                    firebaseRepo.updateBooking(paidBooking)
                    bookings = bookings.map {
                        if (it.bookingId == paidBooking.bookingId) paidBooking else it
                    }
                    bookingToPay = null
                    Toast.makeText(context, context.getString(R.string.payment_successful), Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

@Composable
fun UserBookingCard(
    booking: Booking,
    firebaseRepo: FirebaseRepository,
    onViewReceipt: (String) -> Unit = {},
    onViewInvoice: (String) -> Unit = {},
    onCancel: () -> Unit = {},
    onPayNow: () -> Unit = {},
    isHistory: Boolean = false
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isHistory) 0.7f else 1f),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
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
                        color = if (isHistory) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
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
                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = booking.bookingDate, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = booking.customerPhone, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                val normStatus = normalizeBookingStatus(booking.status)
                val displayStatus = getBookingStatusDisplay(booking.status)

                val (statusIcon, statusColor) = when {
                    normStatus == "Cancelled" -> Pair(Icons.Default.Close, MaterialTheme.colorScheme.error)
                    isHistory -> Pair(Icons.Default.CheckCircle, MaterialTheme.colorScheme.outline)
                    normStatus == "Confirmed" -> Pair(Icons.Default.CheckCircle, Color(0xFF4CAF50))
                    else -> Pair(Icons.Default.Info, MaterialTheme.colorScheme.error)
                }
                Icon(statusIcon, contentDescription = null, modifier = Modifier.size(18.dp), tint = statusColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when {
                        normStatus == "Cancelled" -> stringResource(R.string.cancelled)
                        isHistory -> stringResource(R.string.completed)
                        else -> stringResource(R.string.status_format, displayStatus)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
                )
            }

            val normStatus = normalizeBookingStatus(booking.status)
            val normPayment = normalizePaymentStatus(booking.paymentStatus)

            if (!isHistory && normPayment != "Paid" && normStatus == "Pending") {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.pay_now_notice),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (normStatus != "Cancelled" && normPayment != "Paid") {
                        OutlinedButton(
                            onClick = { onViewInvoice(booking.bookingId) },
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.invoice),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    if (normPayment == "Paid") {
                        OutlinedButton(
                            onClick = { onViewReceipt(booking.bookingId) },
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.receipt),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (isHistory) {
                            OutlinedButton(
                                onClick = { printReceipt(context, booking) },
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Print,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(R.string.print),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    if (!isHistory && normPayment != "Paid") {
                        if (normStatus == "Confirmed") {
                            Button(
                                onClick = onPayNow,
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.pay_now),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        FilledTonalIconButton(
                            onClick = onCancel,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Cancel booking",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            if (normStatus == "Confirmed") {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(modifier = Modifier.alpha(0.2f))
                RateTripSection(booking = booking, firebaseRepo = firebaseRepo, isHistory = isHistory)
            }
        }
    }
}

@Composable
fun PaymentStatusBadge(status: String, modifier: Modifier = Modifier) {
    val isPaid = normalizePaymentStatus(status) == "Paid"
    val displayStatus = getPaymentStatusDisplay(status)
    Surface(
        modifier = modifier,
        color = if (isPaid) {
            com.example.appproject.ui.theme.ConfirmedContainer
        } else {
            com.example.appproject.ui.theme.PendingContainer
        },
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = displayStatus,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (isPaid) com.example.appproject.ui.theme.ConfirmedEmerald else com.example.appproject.ui.theme.PendingAmber,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(
    bookingId: String,
    firebaseRepo: FirebaseRepository,
    loggedInUser: String?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var booking by remember { mutableStateOf<Booking?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(bookingId) {
        booking = firebaseRepo.getBooking(bookingId)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.booking_receipt)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (booking != null) {
                        IconButton(onClick = { printReceipt(context, booking!!) }) {
                            Icon(Icons.Default.Print, contentDescription = "Print")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (booking == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.receipt_not_found))
            }
        } else if (loggedInUser != booking!!.customerUsername && loggedInUser != ADMIN_USERNAME) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.access_denied))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.zti_logo),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.White).padding(8.dp)
                        )
                        Text(
                            "PT ZUHANTO TRAVEL INDONESIA",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        ReceiptRow(stringResource(R.string.booking_id), "#${booking!!.bookingId}")
                        ReceiptRow(stringResource(R.string.customer), booking!!.customerName)
                        ReceiptRow(stringResource(R.string.destination_label), booking!!.destination)
                        ReceiptRow(stringResource(R.string.date_label), booking!!.bookingDate)
                        ReceiptRow(stringResource(R.string.phone_label), booking!!.customerPhone)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))

                        ReceiptRow(stringResource(R.string.normal_price), if (booking!!.basePrice.isNotBlank()) booking!!.basePrice else booking!!.finalPrice)

                        if (booking!!.voucherUsed.isNotBlank()) {
                            ReceiptRow(
                                label = "${stringResource(R.string.discount)} (${booking!!.voucherUsed})",
                                value = "- ${booking!!.discountAmount}",
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        ReceiptRow(stringResource(R.string.total_paid), booking!!.finalPrice, isTotal = true)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            stringResource(R.string.thank_you),
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Paid",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(stringResource(R.string.paid_upper), fontWeight = FontWeight.ExtraBold, color = Color(0xFF4CAF50))
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { printReceipt(context, booking!!) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.print_receipt))
                }
            }
        }
    }
}

fun printReceipt(context: Context, booking: Booking) {
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    val jobName = "Receipt_${booking.bookingId}"
    
    val officialReceiptText = context.getString(R.string.official_receipt)
    val bookingIdText = context.getString(R.string.booking_id)
    val customerText = context.getString(R.string.customer)
    val destinationText = context.getString(R.string.destination_label)
    val dateText = context.getString(R.string.date_label)
    val phoneText = context.getString(R.string.phone_label)
    val normalPriceText = context.getString(R.string.normal_price)
    val discountText = context.getString(R.string.discount)
    val totalPaidText = context.getString(R.string.total_paid)
    val paidLabelText = context.getString(R.string.paid_label)
    val footerThankYouText = context.getString(R.string.footer_thank_you)

    val webView = WebView(context)
    val htmlContent = """
        <html>
        <body style="font-family: sans-serif; padding: 40px; color: #333;">
            <div style="text-align: center; margin-bottom: 30px;">
                <h1 style="color: #063763; margin-bottom: 5px;">PT ZUHANTO TRAVEL INDONESIA</h1>
                <p style="margin: 0; color: #666;">$officialReceiptText</p>
            </div>
            <div style="border: 1px solid #eee; padding: 20px; border-radius: 10px;">
                <table style="width: 100%; border-collapse: collapse; margin-bottom: 20px;">
                    <tr><td style="padding: 8px 0; border-bottom: 1px solid #f9f9f9;"><b>$bookingIdText</b></td><td style="text-align: right;">#${TextUtils.htmlEncode(booking.bookingId)}</td></tr>
                    <tr><td style="padding: 8px 0; border-bottom: 1px solid #f9f9f9;"><b>$customerText</b></td><td style="text-align: right;">${TextUtils.htmlEncode(booking.customerName)}</td></tr>
                    <tr><td style="padding: 8px 0; border-bottom: 1px solid #f9f9f9;"><b>$destinationText</b></td><td style="text-align: right;">${TextUtils.htmlEncode(booking.destination)}</td></tr>
                    <tr><td style="padding: 8px 0; border-bottom: 1px solid #f9f9f9;"><b>$dateText</b></td><td style="text-align: right;">${TextUtils.htmlEncode(booking.bookingDate)}</td></tr>
                    <tr><td style="padding: 8px 0;"><b>$phoneText</b></td><td style="text-align: right;">${TextUtils.htmlEncode(booking.customerPhone)}</td></tr>
                </table>
                
                <div style="background: #fcfcfc; padding: 15px; border-radius: 5px;">
                    <table style="width: 100%;">
                        <tr><td style="padding: 5px 0;">$normalPriceText</td><td style="text-align: right;">${TextUtils.htmlEncode(if (booking.basePrice.isNotBlank()) booking.basePrice else booking.finalPrice)}</td></tr>
                        ${if (booking.voucherUsed.isNotBlank()) "<tr><td style='padding: 5px 0;'>$discountText (${TextUtils.htmlEncode(booking.voucherUsed)})</td><td style='text-align: right; color: red;'>- ${TextUtils.htmlEncode(booking.discountAmount)}</td></tr>" else ""}
                        <tr style="font-size: 1.3em; font-weight: bold; color: #063763;">
                            <td style="padding-top: 10px;">$totalPaidText</td><td style="text-align: right; padding-top: 10px;">${TextUtils.htmlEncode(booking.finalPrice)}</td>
                        </tr>
                    </table>
                </div>
            </div>
            
            <div style="text-align: center; margin-top: 40px;">
                <div style="display: inline-block; padding: 10px 20px; border: 2px solid #4CAF50; color: #4CAF50; font-weight: bold; border-radius: 5px;">
                    $paidLabelText
                </div>
                <p style="margin-top: 20px; color: #888;"><i>$footerThankYouText</i></p>
            </div>
        </body>
        </html>
    """.trimIndent()
    
    webView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            val printAdapter = webView.createPrintDocumentAdapter(jobName)
            printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
        }
    }
    webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null)
}

@Composable
fun ReceiptRow(label: String, value: String, isTotal: Boolean = false, color: Color? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(0.42f, fill = false),
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            modifier = Modifier.weight(0.58f, fill = false),
            textAlign = TextAlign.End,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.SemiBold,
            color = color ?: if (isTotal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun PaymentDialog(
    booking: Booking,
    onDismiss: () -> Unit,
    onConfirmPayment: (Booking) -> Unit
) {
    val context = LocalContext.current
    var selectedMethod by remember { mutableStateOf("qris") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.payment_method)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "${stringResource(R.string.destination_label)}: ${booking.destination}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.payment_amount, booking.finalPrice),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )

                HorizontalDivider()

                val methods = listOf(
                    "card" to stringResource(R.string.credit_card),
                    "bank" to stringResource(R.string.bank_transfer),
                    "ewallet" to stringResource(R.string.ewallet)
                )

                methods.forEach { (key, label) ->
                    Surface(
                        onClick = { selectedMethod = key },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedMethod == key) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (selectedMethod == key) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedMethod == key,
                                onClick = { selectedMethod = key }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedBooking = booking.copy(
                        paymentStatus = "Paid",
                        paymentMethod = selectedMethod
                    )
                    onConfirmPayment(updatedBooking)
                }
            ) {
                Text(stringResource(R.string.confirm_payment))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceScreen(
    bookingId: String,
    firebaseRepo: FirebaseRepository,
    loggedInUser: String?,
    onBack: () -> Unit,
    onViewReceipt: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    var booking by remember { mutableStateOf<Booking?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(bookingId) {
        booking = firebaseRepo.getBooking(bookingId)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.booking_invoice)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (booking != null && normalizePaymentStatus(booking!!.paymentStatus) != "Paid") {
                        IconButton(onClick = { printInvoice(context, booking!!) }) {
                            Icon(Icons.Default.Print, contentDescription = "Print")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (booking == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.invoice_not_found))
            }
        } else if (loggedInUser != booking!!.customerUsername && loggedInUser != ADMIN_USERNAME) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.access_denied))
            }
        } else if (normalizePaymentStatus(booking!!.paymentStatus) == "Paid") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.zti_logo),
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.booking_paid_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.paid_booking_notice),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { onViewReceipt?.invoke(booking!!.bookingId) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.view_receipt))
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.zti_logo),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.White).padding(8.dp)
                        )
                        Text(
                            "PT ZUHANTO TRAVEL INDONESIA",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            stringResource(R.string.official_invoice),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))

                        ReceiptRow(stringResource(R.string.booking_id), "#${booking!!.bookingId}")
                        ReceiptRow(stringResource(R.string.customer), booking!!.customerName)
                        ReceiptRow(stringResource(R.string.destination_label), booking!!.destination)
                        ReceiptRow(stringResource(R.string.date_label), booking!!.bookingDate)
                        ReceiptRow(stringResource(R.string.phone_label), booking!!.customerPhone)
                        ReceiptRow(stringResource(R.string.booking_status),
                            getBookingStatusDisplay(booking!!.status)
                        )
                        ReceiptRow(stringResource(R.string.payment_status),
                            getPaymentStatusDisplay(booking!!.paymentStatus)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))

                        ReceiptRow(stringResource(R.string.normal_price), if (booking!!.basePrice.isNotBlank()) booking!!.basePrice else booking!!.finalPrice)

                        if (booking!!.voucherUsed.isNotBlank()) {
                            ReceiptRow(
                                label = "${stringResource(R.string.discount)} (${booking!!.voucherUsed})",
                                value = "- ${booking!!.discountAmount}",
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        ReceiptRow(stringResource(R.string.total_paid), booking!!.finalPrice, isTotal = true)

                        Spacer(modifier = Modifier.height(24.dp))

                        val isConfirmed = normalizeBookingStatus(booking!!.status) == "Confirmed"
                        val isPaid = normalizePaymentStatus(booking!!.paymentStatus) == "Paid"

                        Surface(
                            color = when {
                                isPaid -> Color(0xFFDDF5E5)
                                isConfirmed -> Color(0xFFE3F2FD)
                                else -> MaterialTheme.colorScheme.errorContainer
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = when {
                                    isPaid -> stringResource(R.string.paid_upper)
                                    isConfirmed -> stringResource(R.string.confirmed_unpaid)
                                    else -> stringResource(R.string.awaiting_confirmation)
                                },
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    isPaid -> Color(0xFF176B36)
                                    isConfirmed -> Color(0xFF0D47A1)
                                    else -> MaterialTheme.colorScheme.onErrorContainer
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { printInvoice(context, booking!!) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.print_invoice))
                }
            }
        }
    }
}

fun printInvoice(context: Context, booking: Booking) {
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    val jobName = "Invoice_${booking.bookingId}"

    val officialInvoiceText = context.getString(R.string.official_invoice)
    val bookingIdText = context.getString(R.string.booking_id)
    val customerText = context.getString(R.string.customer)
    val destinationText = context.getString(R.string.destination_label)
    val dateText = context.getString(R.string.date_label)
    val phoneText = context.getString(R.string.phone_label)
    val statusText = context.getString(R.string.booking_status)
    val paymentStatusText = context.getString(R.string.payment_status)
    val statusDisplay = getBookingStatusDisplay(booking.status, context)
    val paymentStatusDisplay = getPaymentStatusDisplay(booking.paymentStatus, context)
    val normalPriceText = context.getString(R.string.normal_price)
    val discountText = context.getString(R.string.discount)
    val totalPaidText = context.getString(R.string.total_paid)
    val footerThankYouText = context.getString(R.string.footer_thank_you)

    val webView = WebView(context)
    val htmlContent = """
        <html>
        <body style="font-family: sans-serif; padding: 40px; color: #333;">
            <div style="text-align: center; margin-bottom: 30px;">
                <h1 style="color: #063763; margin-bottom: 5px;">PT ZUHANTO TRAVEL INDONESIA</h1>
                <p style="margin: 0; color: #666;">$officialInvoiceText</p>
            </div>
            <div style="border: 1px solid #eee; padding: 20px; border-radius: 10px;">
                <table style="width: 100%; border-collapse: collapse; margin-bottom: 20px;">
                    <tr><td style="padding: 8px 0; border-bottom: 1px solid #f9f9f9;"><b>$bookingIdText</b></td><td style="text-align: right;">#${TextUtils.htmlEncode(booking.bookingId)}</td></tr>
                    <tr><td style="padding: 8px 0; border-bottom: 1px solid #f9f9f9;"><b>$customerText</b></td><td style="text-align: right;">${TextUtils.htmlEncode(booking.customerName)}</td></tr>
                    <tr><td style="padding: 8px 0; border-bottom: 1px solid #f9f9f9;"><b>$destinationText</b></td><td style="text-align: right;">${TextUtils.htmlEncode(booking.destination)}</td></tr>
                    <tr><td style="padding: 8px 0; border-bottom: 1px solid #f9f9f9;"><b>$dateText</b></td><td style="text-align: right;">${TextUtils.htmlEncode(booking.bookingDate)}</td></tr>
                    <tr><td style="padding: 8px 0; border-bottom: 1px solid #f9f9f9;"><b>$phoneText</b></td><td style="text-align: right;">${TextUtils.htmlEncode(booking.customerPhone)}</td></tr>
                    <tr><td style="padding: 8px 0; border-bottom: 1px solid #f9f9f9;"><b>$statusText</b></td><td style="text-align: right;">${TextUtils.htmlEncode(statusDisplay)}</td></tr>
                    <tr><td style="padding: 8px 0;"><b>$paymentStatusText</b></td><td style="text-align: right;">${TextUtils.htmlEncode(paymentStatusDisplay)}</td></tr>
                </table>
                <div style="background: #fcfcfc; padding: 15px; border-radius: 5px;">
                    <table style="width: 100%;">
                        <tr><td style="padding: 5px 0;">$normalPriceText</td><td style="text-align: right;">${TextUtils.htmlEncode(if (booking.basePrice.isNotBlank()) booking.basePrice else booking.finalPrice)}</td></tr>
                        ${if (booking.voucherUsed.isNotBlank()) "<tr><td style='padding: 5px 0;'>$discountText (${TextUtils.htmlEncode(booking.voucherUsed)})</td><td style='text-align: right; color: red;'>- ${TextUtils.htmlEncode(booking.discountAmount)}</td></tr>" else ""}
                        <tr style="font-size: 1.3em; font-weight: bold; color: #063763;">
                            <td style="padding-top: 10px;">$totalPaidText</td><td style="text-align: right; padding-top: 10px;">${TextUtils.htmlEncode(booking.finalPrice)}</td>
                        </tr>
                    </table>
                </div>
            </div>
            <div style="text-align: center; margin-top: 30px; color: #888;">
                <p><i>$footerThankYouText</i></p>
            </div>
        </body>
        </html>
    """.trimIndent()

    webView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            val printAdapter = webView.createPrintDocumentAdapter(jobName)
            printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
        }
    }
    webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null)
}

@Composable
fun StarRatingBar(
    rating: Int,
    onRatingChanged: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in 1..5) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Star $i",
                tint = if (i <= rating) Color(0xFFFFA51E) else Color.LightGray,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(enabled = onRatingChanged != null) {
                        onRatingChanged?.invoke(i)
                    }
            )
        }
    }
}

@Composable
fun RateTripSection(
    booking: Booking,
    firebaseRepo: FirebaseRepository,
    isHistory: Boolean
) {
    var currentRating by remember(booking.userRating) { mutableIntStateOf(booking.userRating) }
    var reviewText by remember(booking.userReview) { mutableStateOf(booking.userReview) }
    var isSubmitted by remember { mutableStateOf(booking.userRating > 0 || booking.userReview.isNotBlank()) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    if (normalizeBookingStatus(booking.status) == "Confirmed" && isHistory) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isSubmitted) {
                    Text(
                        text = stringResource(R.string.thank_you_message),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StarRatingBar(rating = currentRating)
                    if (reviewText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "\"$reviewText\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Text(
                        text = stringResource(R.string.rate_and_review_completed),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = booking.destination,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    StarRatingBar(
                        rating = currentRating,
                        onRatingChanged = { newRating ->
                            currentRating = newRating
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        placeholder = { Text(stringResource(R.string.write_review)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (currentRating == 0) {
                                Toast.makeText(context, context.getString(R.string.please_select_one_star), Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            scope.launch {
                                val updatedBooking = booking.copy(
                                    userRating = currentRating,
                                    userReview = reviewText.trim()
                                )
                                firebaseRepo.updateBooking(updatedBooking)
                                isSubmitted = true
                                Toast.makeText(context, context.getString(R.string.review_submitted_successfully), Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.submit_rating_and_review))
                    }
                }
            }
        }
    }
}


