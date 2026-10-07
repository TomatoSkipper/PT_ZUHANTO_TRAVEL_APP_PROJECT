package com.example.appproject.ui.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.appproject.MainActivity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appproject.R
import com.example.appproject.data.model.ADMIN_USERNAME
import com.example.appproject.data.model.Booking
import com.example.appproject.data.repository.FirebaseRepository
import com.example.appproject.ui.theme.AppTheme
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.NumberFormat
import java.util.Locale

class PaymentScreen : ComponentActivity() {

    private val publishableKey = "pk_test_51UNOmW984JcO38MUl2fQbGvBbgnCyLVaPdPhwlUTMSR1dXkjrZzV8Gl5VV96wZKTtv2UUaVOqFcy48zCvgpr3FpH00mrOstu0x"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Stripe Configuration
        PaymentConfiguration.init(applicationContext, publishableKey)

        val bookingId = intent.getStringExtra("BOOKING_ID") ?: ""
        val loggedInUser = intent.getStringExtra("LOGGED_IN_USER") ?: ""
        val firebaseRepo = FirebaseRepository()

        setContent {
            AppTheme {
                PaymentScreen(
                    bookingId = bookingId,
                    firebaseRepo = firebaseRepo,
                    loggedInUser = loggedInUser,
                    onBack = { finish() },
                    onRedirectToBookings = {
                        val intent = Intent(this@PaymentScreen, MainActivity::class.java).apply {
                            putExtra("navigate_to_booking_id", bookingId)
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                        finish()
                    },
                    onViewReceipt = { finish() },
                    onViewInvoice = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    bookingId: String,
    firebaseRepo: FirebaseRepository,
    loggedInUser: String?,
    onBack: () -> Unit,
    onRedirectToBookings: (() -> Unit)? = null,
    onViewReceipt: ((String) -> Unit)? = null,
    onViewInvoice: ((String) -> Unit)? = null,
    stripeClientSecret: String? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var booking by remember { mutableStateOf<Booking?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var paymentSuccessDialog by remember { mutableStateOf(false) }


    // Promo / Voucher
    var voucherCodeInput by remember { mutableStateOf("") }
    var appliedVoucherCode by remember { mutableStateOf("") }
    var discountValue by remember { mutableDoubleStateOf(0.0) }
    var voucherMessage by remember { mutableStateOf<String?>(null) }
    var isVoucherValid by remember { mutableStateOf(false) }

    // Strings for callbacks & UI messages
    val paymentCancelledMsg = stringResource(R.string.payment_cancelled)
    val paymentFailedMsg = stringResource(R.string.payment_failed)
    val voucherAlreadyUsedMsg = stringResource(R.string.voucher_already_used)
    val voucherAppliedMsg = stringResource(R.string.voucher_applied)
    val invalidVoucherMsg = stringResource(R.string.invalid_voucher)

    val publishableKey = "pk_test_51UNOmW984JcO38MUl2fQbGvBbgnCyLVaPdPhwlUTMSR1dXkjrZzV8Gl5VV96wZKTtv2UUaVOqFcy48zCvgpr3FpH00mrOstu0x"
    val secretKey = "sk_test_51UNOmW984JcO38MUvHE1OJuDLgCfMFl82KWJf5aInHErmm6RyVi8O2sii0UOsP9PxzGKWpS0RqH8uFmSHLzKyiX9006Dq3Xw2b"

    // Stripe PaymentSheet integration
    val paymentSheet = rememberPaymentSheet { result ->
        when (result) {
            is PaymentSheetResult.Completed -> {
                isProcessingPayment = false
                scope.launch {
                    processPaymentCompletion(
                        booking = booking,
                        firebaseRepo = firebaseRepo,
                        methodName = "Stripe Credit/Debit Card",
                        appliedVoucher = appliedVoucherCode,
                        discountVal = discountValue,
                        onSuccess = { updated ->
                            booking = updated
                            paymentSuccessDialog = true
                        }
                    )
                }
            }
            is PaymentSheetResult.Canceled -> {
                isProcessingPayment = false
                Toast.makeText(context, paymentCancelledMsg, Toast.LENGTH_SHORT).show()
            }
            is PaymentSheetResult.Failed -> {
                isProcessingPayment = false
                Toast.makeText(
                    context,
                    "$paymentFailedMsg: ${result.error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    LaunchedEffect(bookingId) {
        PaymentConfiguration.init(context, publishableKey)
        if (bookingId.isNotBlank()) {
            val fetched = firebaseRepo.getBooking(bookingId)
            booking = fetched
            if (fetched != null) {
                if (fetched.voucherUsed.isNotBlank()) {
                    appliedVoucherCode = fetched.voucherUsed
                    discountValue = parseCurrencyToDouble(fetched.discountAmount)
                }
            }
        }
        isLoading = false
    }

    LaunchedEffect(paymentSuccessDialog) {
        if (paymentSuccessDialog) {
            Toast.makeText(context, context.getString(R.string.payment_successful), Toast.LENGTH_SHORT).show()
            delay(2500)
            if (paymentSuccessDialog) {
                paymentSuccessDialog = false
                if (onRedirectToBookings != null) {
                    onRedirectToBookings()
                } else if (onViewReceipt != null && booking != null) {
                    onViewReceipt(booking!!.bookingId)
                } else {
                    onBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.payment),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = stringResource(R.string.secure_checkout_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "SSL 256-bit",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (booking == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.receipt_not_found),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else if (loggedInUser != null && loggedInUser != booking!!.customerUsername && loggedInUser != ADMIN_USERNAME) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.access_denied),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            val currentBooking = booking!!
            val basePriceStr = currentBooking.basePrice.ifBlank { currentBooking.finalPrice }
            val rawBasePrice = parseCurrencyToDouble(basePriceStr)
            val calculatedFinalPrice = (rawBasePrice - discountValue).coerceAtLeast(0.0)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 1. Booking Summary Card
                BookingSummaryCard(booking = currentBooking)

                // 2. Voucher & Promo Code Section
                VoucherSection(
                    voucherCodeInput = voucherCodeInput,
                    onVoucherCodeChange = { voucherCodeInput = it },
                    appliedVoucherCode = appliedVoucherCode,
                    discountValue = discountValue,
                    voucherMessage = voucherMessage,
                    isVoucherValid = isVoucherValid,
                    onApplyVoucher = {
                        scope.launch {
                            val code = voucherCodeInput.trim().uppercase()
                            if (code.isBlank()) return@launch
                            val allVouchers = firebaseRepo.getAllVouchers()
                            val matched = allVouchers.find {
                                it["code"]?.toString()?.equals(code, ignoreCase = true) == true
                            }
                            if (matched != null) {
                                val isUsed = firebaseRepo.isVoucherUsedByUser(
                                    code,
                                    loggedInUser ?: currentBooking.customerUsername
                                )
                                if (isUsed) {
                                    voucherMessage = voucherAlreadyUsedMsg
                                    isVoucherValid = false
                                } else {
                                    val discPercent =
                                        matched["discount"]?.toString()?.toDoubleOrNull() ?: 10.0
                                    val calcDisc = rawBasePrice * (discPercent / 100.0)
                                    discountValue = calcDisc
                                    appliedVoucherCode = code
                                    isVoucherValid = true
                                    voucherMessage =
                                        "$voucherAppliedMsg (${discPercent.toInt()}% OFF)"
                                }
                            } else {
                                voucherMessage = invalidVoucherMsg
                                isVoucherValid = false
                            }
                        }
                    },
                    onRemoveVoucher = {
                        appliedVoucherCode = ""
                        discountValue = 0.0
                        voucherCodeInput = ""
                        voucherMessage = null
                        isVoucherValid = false
                    }
                )

                // 3. Payment Method
                PaymentMethodCard(
                    title = stringResource(R.string.credit_card),
                    subtitle = stringResource(R.string.powered_by_stripe),
                    icon = Icons.Default.CreditCard,
                    isSelected = true,
                    onClick = { }
                ) {
                    Column(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.stripe_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CardBadge(text = "VISA")
                            CardBadge(text = "Mastercard")
                            CardBadge(text = "AMEX")
                            CardBadge(text = "JCB")
                        }
                    }
                }

                // 4. Price Breakdown
                PriceBreakdownCard(
                    basePrice = rawBasePrice,
                    discount = discountValue,
                    finalPrice = calculatedFinalPrice
                )

                // 5. Confirm Payment Action Button
                Button(
                    onClick = {
                        // Handle Stripe Credit/Debit Card
                        isProcessingPayment = true
                        scope.launch {
                            val targetCurrency = "idr"
                            val (clientSecret, errorMsg) = if (!stripeClientSecret.isNullOrBlank()) {
                                Pair(stripeClientSecret, null)
                            } else {
                                createStripePaymentIntent(
                                    calculatedFinalPrice,
                                    secretKey,
                                    currency = targetCurrency
                                )
                            }

                            isProcessingPayment = false

                            if (!clientSecret.isNullOrBlank()) {
                                try {
                                    val config = PaymentSheet.Configuration(
                                        merchantDisplayName = "PT ZUHANTO TRAVEL INDONESIA"
                                    )
                                    paymentSheet.presentWithPaymentIntent(
                                        clientSecret,
                                        config
                                    )
                                } catch (e: Exception) {
                                    Log.e(
                                        "PaymentScreen",
                                        "Error launching PaymentSheet",
                                        e
                                    )
                                    Toast.makeText(
                                        context,
                                        "Error: ${e.localizedMessage}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Stripe Error: ${errorMsg ?: "Failed to generate client secret"}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isProcessingPayment,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (isProcessingPayment) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.confirm_payment),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Success Dialog
    if (paymentSuccessDialog && booking != null) {
        AlertDialog(
            onDismissRequest = { paymentSuccessDialog = false },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.payment_successful),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.payment_success_message),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Booking ID: #${booking!!.bookingId}", fontWeight = FontWeight.Bold)
                            Text("Destination: ${booking!!.destination}")
                            Text("Amount Paid: ${booking!!.finalPrice}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Text("Method: ${booking!!.paymentMethod}")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        paymentSuccessDialog = false
                        if (onRedirectToBookings != null) {
                            onRedirectToBookings()
                        } else if (onViewReceipt != null) {
                            onViewReceipt(booking!!.bookingId)
                        } else {
                            onBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Go to Bookings")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        paymentSuccessDialog = false
                        if (onViewInvoice != null) {
                            onViewInvoice(booking!!.bookingId)
                        } else {
                            onBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.view_invoice))
                }
            }
        )
    }
}

@Composable
private fun BookingSummaryCard(booking: Booking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Booking #${booking.bookingId}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = booking.status.ifBlank { "Confirmed" },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider()

            Text(
                text = booking.destination,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.date_label),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = booking.bookingDate,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.customer),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = booking.customerName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun VoucherSection(
    voucherCodeInput: String,
    onVoucherCodeChange: (String) -> Unit,
    appliedVoucherCode: String,
    discountValue: Double,
    voucherMessage: String?,
    isVoucherValid: Boolean,
    onApplyVoucher: () -> Unit,
    onRemoveVoucher: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Discount,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.promo_voucher),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            if (appliedVoucherCode.isNotBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${stringResource(R.string.applied_voucher)}: $appliedVoucherCode",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Savings: ${formatCurrency(discountValue)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        TextButton(onClick = onRemoveVoucher) {
                            Text(stringResource(R.string.remove), color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = voucherCodeInput,
                        onValueChange = onVoucherCodeChange,
                        placeholder = { Text(stringResource(R.string.enter_voucher_code)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Button(
                        onClick = onApplyVoucher,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(stringResource(R.string.apply))
                    }
                }
            }

            if (voucherMessage != null) {
                Text(
                    text = voucherMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isVoucherValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    content: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                RadioButton(
                    selected = isSelected,
                    onClick = onClick
                )
            }

            if (isSelected && content != null) {
                content()
            }
        }
    }
}

@Composable
private fun CardBadge(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun PriceBreakdownCard(
    basePrice: Double,
    discount: Double,
    finalPrice: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.price_details),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.base_price),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(text = formatCurrency(basePrice), fontWeight = FontWeight.SemiBold)
            }

            if (discount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.discount),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "-${formatCurrency(discount)}",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.taxes_and_fees),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.included),
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.total_price_label),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatCurrency(finalPrice),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

private suspend fun createStripePaymentIntent(
    amountInCurrency: Double,
    secretKey: String,
    currency: String = "idr",
    paymentMethodType: String? = null
): Pair<String?, String?> = withContext(Dispatchers.IO) {
    try {
        if (secretKey.isBlank() || !secretKey.startsWith("sk_")) {
            return@withContext Pair(null, "Secret key is invalid or blank")
        }

        val url = URL("https://api.stripe.com/v1/payment_intents")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Authorization", "Bearer $secretKey")
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        conn.connectTimeout = 10000
        conn.readTimeout = 10000
        conn.doOutput = true

        val amountInt = (amountInCurrency * 100).toLong().coerceAtLeast(100L)

        // Specify explicit payment method type if provided, otherwise use automatic
        val postData = if (!paymentMethodType.isNullOrEmpty()) {
            "amount=$amountInt&currency=$currency&payment_method_types[]=$paymentMethodType"
        } else {
            "amount=$amountInt&currency=$currency&automatic_payment_methods[enabled]=true"
        }

        conn.outputStream.use { os ->
            os.write(postData.toByteArray(Charsets.UTF_8))
        }

        val responseCode = conn.responseCode
        val inputStream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
        val responseText = inputStream?.bufferedReader()?.use { it.readText() } ?: ""

        if (responseCode in 200..299) {
            val jsonObject = JSONObject(responseText)
            val clientSecret = jsonObject.optString("client_secret", "")
            if (clientSecret.isNotBlank()) Pair(clientSecret, null) else Pair(null, "Missing client_secret")
        } else {
            Log.e("PaymentScreen", "Stripe API Error ($responseCode): $responseText")
            Pair(null, "Stripe Error ($responseCode): $responseText")
        }
    } catch (e: Exception) {
        Log.e("PaymentScreen", "Exception creating PaymentIntent", e)
        Pair(null, e.localizedMessage ?: "Network error")
    }
}

private suspend fun processPaymentCompletion(
    booking: Booking?,
    firebaseRepo: FirebaseRepository,
    methodName: String,
    appliedVoucher: String,
    discountVal: Double,
    onSuccess: (Booking) -> Unit
) {
    if (booking == null) return
    val basePriceStr = booking.basePrice.ifBlank { booking.finalPrice }
    val rawBasePrice = parseCurrencyToDouble(basePriceStr)
    val finalCalculated = (rawBasePrice - discountVal).coerceAtLeast(0.0)

    val updatedBooking = booking.copy(
        paymentStatus = "Paid",
        paymentMethod = methodName,
        voucherUsed = appliedVoucher,
        discountAmount = if (discountVal > 0) formatCurrency(discountVal) else booking.discountAmount,
        basePrice = formatCurrency(rawBasePrice),
        finalPrice = formatCurrency(finalCalculated)
    )

    firebaseRepo.updateBooking(updatedBooking)

    // Mark voucher as used if applicable
    if (appliedVoucher.isNotBlank() && booking.customerUsername.isNotBlank()) {
        try {
            firebaseRepo.recordVoucherUsage(appliedVoucher, booking.customerUsername)
        } catch (_: Exception) {}
    }

    onSuccess(updatedBooking)
}

private fun parseCurrencyToDouble(priceStr: String): Double {
    if (priceStr.isBlank()) return 0.0
    val cleaned = priceStr.replace("[^0-9]".toRegex(), "")
    return cleaned.toDoubleOrNull() ?: 0.0
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(amount).replace("Rp", "Rp ").trim()
}




