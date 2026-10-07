package com.example.appproject.ui.screens

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.appproject.R
import com.example.appproject.data.model.parseBookingDate
import com.example.appproject.data.repository.FirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
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
    var voucherSavedStatus by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val voucherBgPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            voucherBgImageUri = uri
            scope.launch {
                voucherBgImageBase64 = compressAndEncodeImage(context, uri)
                if (voucherBgImageBase64.isBlank()) {
                    Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        isLoading = true
        adminVouchers = firebaseRepo.getAllVouchers()
        isLoading = false
    }

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionHeader(
                    title = context.getString(R.string.manage_vouchers),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.elevatedCardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.create_promo_voucher),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = inputVoucherCode,
                                onValueChange = { inputVoucherCode = it },
                                placeholder = { Text(stringResource(R.string.code_upper)) },
                                modifier = Modifier.weight(1.5f),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(
                                value = customPercentage,
                                onValueChange = { customPercentage = it.filter { c -> c.isDigit() } },
                                placeholder = { Text(stringResource(R.string.percent_sign)) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = voucherDescription,
                            onValueChange = { voucherDescription = it },
                            placeholder = { Text(stringResource(R.string.voucher_details_placeholder)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val ctx = LocalContext.current
                            OutlinedButton(
                                onClick = {
                                    val calendar = Calendar.getInstance()
                                    DatePickerDialog(
                                        ctx,
                                        { _, year, month, day ->
                                            val selectedCalendar = Calendar.getInstance().apply {
                                                set(year, month, day)
                                            }
                                            startDateStr = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedCalendar.time)
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    ).apply {
                                        datePicker.minDate = System.currentTimeMillis() - 1000
                                    }.show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (startDateStr.isBlank()) stringResource(R.string.start_date) else startDateStr,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            OutlinedButton(
                                onClick = {
                                    val calendar = Calendar.getInstance()
                                    val datePickerDialog = DatePickerDialog(
                                        ctx,
                                        { _, year, month, day ->
                                            val selectedCalendar = Calendar.getInstance().apply {
                                                set(year, month, day)
                                            }
                                            endDateStr = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedCalendar.time)
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    )

                                    val minDate = if (startDateStr.isNotBlank()) {
                                        try {
                                            parseBookingDate(startDateStr)?.time ?: System.currentTimeMillis()
                                        } catch (_: Exception) {
                                            System.currentTimeMillis()
                                        }
                                    } else {
                                        System.currentTimeMillis()
                                    }

                                    datePickerDialog.datePicker.minDate = minDate - 1000
                                    datePickerDialog.show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (endDateStr.isBlank()) stringResource(R.string.end_date) else endDateStr,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { voucherBgPickerLauncher.launch("image/*") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (voucherBgImageUri == null) "Select Background Picture" else "Change Background",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            if (voucherBgImageUri != null) {
                                OutlinedButton(
                                    onClick = {
                                        voucherBgImageUri = null
                                        voucherBgImageBase64 = ""
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Remove", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }

                        if (voucherBgImageUri != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    model = voucherBgImageUri,
                                    contentDescription = "Background Preview",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (inputVoucherCode.isNotBlank()) {
                                    scope.launch {
                                        val pct = customPercentage.toIntOrNull() ?: 10
                                        firebaseRepo.saveVoucher(
                                            code = inputVoucherCode,
                                            discountPercentage = pct,
                                            description = voucherDescription,
                                            startDate = startDateStr,
                                            endDate = endDateStr,
                                            backgroundPicture = voucherBgImageBase64
                                        )
                                        adminVouchers = firebaseRepo.getAllVouchers()
                                        voucherSavedStatus = "${context.getString(R.string.voucher)} '${inputVoucherCode.trim().uppercase()}' ${context.getString(R.string.successfully_created)}!"
                                        inputVoucherCode = ""
                                        customPercentage = ""
                                        voucherDescription = ""
                                        startDateStr = ""
                                        endDateStr = ""
                                        voucherBgImageUri = null
                                        voucherBgImageBase64 = ""
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(stringResource(R.string.create))
                        }

                        if (voucherSavedStatus.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = voucherSavedStatus,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
            }

            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.elevatedCardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.manage_existing_vouchers),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        } else if (adminVouchers.isEmpty()) {
                            Text(
                                text = stringResource(R.string.no_voucher_created),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            adminVouchers.forEachIndexed { index, voucher ->
                                val code = voucher["code"]?.toString() ?: ""
                                val discount = voucher["discountPercentage"]?.toString() ?: "0"
                                val desc = voucher["description"]?.toString() ?: ""
                                val start = voucher["startDate"]?.toString() ?: ""
                                val end = voucher["endDate"]?.toString() ?: ""
                                val bgPic = voucher["backgroundPicture"]?.toString() ?: ""
                                val bgBytes = remember(bgPic) {
                                    try {
                                        val clean = bgPic.replace("\n", "").replace("\r", "").trim()
                                        if (clean.isNotBlank()) Base64.decode(clean, Base64.DEFAULT) else null
                                    } catch (_: Exception) {
                                        null
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (bgBytes != null) {
                                        AsyncImage(
                                            model = bgBytes,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = code,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Text(
                                            text = "${stringResource(R.string.discount)}: $discount%",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        if (desc.isNotBlank()) {
                                            Text(
                                                text = desc,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (start.isNotBlank() || end.isNotBlank()) {
                                            Text(
                                                text = "Valid: ${start.ifBlank { "Any" }} - ${end.ifBlank { "Any" }}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                firebaseRepo.deleteVoucher(code)
                                                adminVouchers = firebaseRepo.getAllVouchers()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }

                                if (index < adminVouchers.size - 1) {
                                    HorizontalDivider()
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
    return withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext ""
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            if (originalBitmap == null) return@withContext ""

            val maxDimension = 800
            val width = originalBitmap.width
            val height = originalBitmap.height
            val scaledBitmap = if (width > maxDimension || height > maxDimension) {
                val ratio = width.toFloat() / height.toFloat()
                val targetW: Int
                val targetH: Int
                if (ratio > 1) {
                    targetW = maxDimension
                    targetH = (maxDimension / ratio).toInt()
                } else {
                    targetW = (maxDimension * ratio).toInt()
                    targetH = maxDimension
                }
                Bitmap.createScaledBitmap(originalBitmap, targetW, targetH, true)
            } else {
                originalBitmap
            }

            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            val byteArray = outputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.NO_WRAP).replace("\n", "").replace("\r", "").trim()
        } catch (_: Exception) {
            ""
        }
    }
}