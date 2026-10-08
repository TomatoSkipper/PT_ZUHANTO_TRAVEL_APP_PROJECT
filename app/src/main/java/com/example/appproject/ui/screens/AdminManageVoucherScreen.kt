package com.example.appproject.ui.screens

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appproject.R
import com.example.appproject.data.model.parseBookingDate
import com.example.appproject.data.repository.FirebaseRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AdminVouchersScreen(firebaseRepo: FirebaseRepository) {
    val context = LocalContext.current
    var adminVouchers by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var inputVoucherCode by remember { mutableStateOf("") }
    var customPercentage by remember { mutableStateOf("") }
    var voucherDescription by remember { mutableStateOf("") }
    var startDateStr by remember { mutableStateOf("") }
    var endDateStr by remember { mutableStateOf("") }
    var voucherBgImageUri by remember { mutableStateOf<Uri?>(null) }
    var voucherBgImageBase64 by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val voucherBgPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            voucherBgImageUri = uri
            scope.launch {
                voucherBgImageBase64 = compressAndEncodeImage(context, uri)
                if (voucherBgImageBase64.isBlank()) {
                    Toast.makeText(context, (R.string.failed_to_process_image), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun showDatePicker(currentDateStr: String = "", onDateSelected: (String) -> Unit) {
        val cal = Calendar.getInstance()
        if (currentDateStr.isNotBlank()) {
            try {
                val parsed = SimpleDateFormat("dd-MM-yyyy", Locale.US).parse(currentDateStr.trim())
                if (parsed != null) cal.time = parsed
            } catch (_: Exception) {}
        }
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedSdf = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                val selectedCal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                onDateSelected(selectedSdf.format(selectedCal.time))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    LaunchedEffect(Unit) {
        isLoading = true
        adminVouchers = firebaseRepo.getAllVouchers()
        isLoading = false
    }

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
                Column {
                    Text(
                        text = stringResource(R.string.voucher),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.create_promo_codes_and_manage_live_offers),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Create Voucher Card
            item {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.create_voucher),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Code & Discount (%) Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1.5f)) {
                                Text(stringResource(R.string.code), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = inputVoucherCode,
                                    onValueChange = { inputVoucherCode = it.uppercase() },
                                    placeholder = { Text("NEWCOMER", color = Color(0xFFCBD5E1)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedBorderColor = Color(0xFF0284C7)
                                    )
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(stringResource(R.string.discount) + " (%)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = customPercentage,
                                    onValueChange = { customPercentage = it },
                                    placeholder = { Text("15", color = Color(0xFFCBD5E1)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedBorderColor = Color(0xFF0284C7)
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Description
                        Text(stringResource(R.string.description), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = voucherDescription,
                            onValueChange = { voucherDescription = it },
                            placeholder = { Text("Short description", color = Color(0xFFCBD5E1)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedBorderColor = Color(0xFF0284C7)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Start date & End date Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(stringResource(R.string.start_date), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = startDateStr,
                                        onValueChange = {},
                                        placeholder = { Text("DD-MM-YYYY", color = Color(0xFFCBD5E1)) },
                                        modifier = Modifier.fillMaxWidth(),
                                        readOnly = true,
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp),
                                        trailingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.DateRange,
                                                contentDescription = "Select start date",
                                                tint = Color(0xFF64748B)
                                            )
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            unfocusedBorderColor = Color(0xFFCBD5E1),
                                            focusedBorderColor = Color(0xFF0284C7)
                                        )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .clickable { showDatePicker(startDateStr) { startDateStr = it } }
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(stringResource(R.string.end_date), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = endDateStr,
                                        onValueChange = {},
                                        placeholder = { Text("DD-MM-YYYY", color = Color(0xFFCBD5E1)) },
                                        modifier = Modifier.fillMaxWidth(),
                                        readOnly = true,
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp),
                                        trailingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.DateRange,
                                                contentDescription = "Select end date",
                                                tint = Color(0xFF64748B)
                                            )
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            unfocusedBorderColor = Color(0xFFCBD5E1),
                                            focusedBorderColor = Color(0xFF0284C7)
                                        )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .clickable { showDatePicker(endDateStr) { endDateStr = it } }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Background image
                        Text(stringResource(R.string.background_image), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = { voucherBgPickerLauncher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF0284C7))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (voucherBgImageUri != null) stringResource(R.string.image_selected) else stringResource(R.string.choose_file),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0284C7)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Create Voucher Button
                        Button(
                            onClick = {
                                val code = inputVoucherCode.trim().uppercase()
                                val pct = customPercentage.toIntOrNull() ?: 0
                                if (code.isBlank() || pct <= 0) {
                                    Toast.makeText(context, (R.string.invalid_voucher_input), Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                scope.launch {
                                    firebaseRepo.saveVoucher(
                                        code = code,
                                        discountPercentage = pct,
                                        description = voucherDescription,
                                        startDate = startDateStr,
                                        endDate = endDateStr,
                                        backgroundPicture = voucherBgImageBase64
                                    )
                                    inputVoucherCode = ""
                                    customPercentage = ""
                                    voucherDescription = ""
                                    startDateStr = ""
                                    endDateStr = ""
                                    voucherBgImageUri = null
                                    voucherBgImageBase64 = ""
                                    adminVouchers = firebaseRepo.getAllVouchers()
                                    Toast.makeText(context, (R.string.voucher_created_successfully), Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0369A1))
                        ) {
                            Text(stringResource(R.string.create_voucher), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // Existing Vouchers Card
            item {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.existing_vouchers),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            modifier = Modifier.padding(16.dp)
                        )

                        if (isLoading) {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                Text(stringResource(R.string.loading_vouchers), color = Color(0xFF64748B))
                            }
                        } else if (adminVouchers.isEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                Text(stringResource(R.string.no_existing_vouchers), color = Color(0xFF64748B))
                            }
                        } else {
                            adminVouchers.forEachIndexed { index, voucher ->
                                val code = voucher[stringResource(R.string.code)]?.toString() ?: ""
                                val discount = voucher[stringResource(R.string.discount_percentage)]?.toString() ?: "0"
                                val desc = voucher[stringResource(R.string.description)]?.toString() ?: ""
                                val start = voucher[stringResource(R.string.start_date)]?.toString() ?: ""
                                val end = voucher[stringResource(R.string.end_date)]?.toString() ?: ""

                                val currentTime = System.currentTimeMillis()
                                var isEnded = false
                                if (end.isNotBlank()) {
                                    try {
                                        val endDate = parseBookingDate(end.trim())?.time ?: 0L
                                        if (currentTime > endDate + 86400000) {
                                            isEnded = true
                                        }
                                    } catch (_: Exception) {}
                                }

                                if (index > 0) {
                                    HorizontalDivider(color = Color(0xFFF1F5F9))
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = code,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                        if (desc.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(desc, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                                        }
                                        val dateText = when {
                                            start.isNotBlank() && end.isNotBlank() -> "$start to $end"
                                            end.isNotBlank() -> stringResource(R.string.valid_till) + " $end"
                                            start.isNotBlank() -> stringResource(R.string.valid_from) + " $start"
                                            else -> ""
                                        }
                                        if (dateText.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(dateText, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "$discount%",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0369A1)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(6.dp)
                                                        .clip(CircleShape)
                                                        .background(if (isEnded) Color(0xFF64748B) else Color(0xFF047857))
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = if (isEnded) stringResource(R.string.expired) else stringResource(R.string.active),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isEnded) Color(0xFF64748B) else Color(0xFF047857)
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = {
                                                scope.launch {
                                                    firebaseRepo.deleteVoucher(code)
                                                    adminVouchers = firebaseRepo.getAllVouchers()
                                                    Toast.makeText(context, (R.string.voucher_deleted), Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete voucher",
                                                tint = Color(0xFFE11D48),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private suspend fun compressAndEncodeImage(context: Context, uri: Uri): String {
    return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext ""
            val originalBitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            if (originalBitmap == null) return@withContext ""

            val maxDimension = 800
            val width = originalBitmap.width
            val height = originalBitmap.height
            val (targetW, targetH) = if (width > height) {
                if (width > maxDimension) maxDimension to (height * maxDimension / width) else width to height
            } else {
                if (height > maxDimension) (width * maxDimension / height) to maxDimension else width to height
            }

            val scaledBitmap = android.graphics.Bitmap.createScaledBitmap(originalBitmap, targetW, targetH, true)
            val outputStream = java.io.ByteArrayOutputStream()
            scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, outputStream)
            val bytes = outputStream.toByteArray()
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (_: Exception) {
            ""
        }
    }
}
