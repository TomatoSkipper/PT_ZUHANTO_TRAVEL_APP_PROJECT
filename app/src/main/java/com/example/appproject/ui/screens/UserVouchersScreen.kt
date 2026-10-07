package com.example.appproject.ui.screens

import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.appproject.R
import com.example.appproject.data.model.VoucherStatus
import com.example.appproject.data.model.parseBookingDate
import com.example.appproject.data.repository.FirebaseRepository

@Composable
fun UserVouchersScreen(firebaseRepo: FirebaseRepository, username: String) {
    var vouchersWithStatus by remember { mutableStateOf<List<VoucherStatus>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("All") }

    LaunchedEffect(Unit) {
        val allVouchers = firebaseRepo.getAllVouchers()
        val currentTime = System.currentTimeMillis()

        vouchersWithStatus = allVouchers.map { vData ->
            val code = vData["code"]?.toString() ?: ""
            val endDateStr = vData["endDate"]?.toString() ?: ""

            val isUsed = firebaseRepo.isVoucherUsedByUser(code, username)

            var isExpired = false
            if (endDateStr.isNotBlank()) {
                try {
                    val endDate = parseBookingDate(endDateStr.trim())?.time ?: 0L
                    if (currentTime > endDate + 86400000) {
                        isExpired = true
                    }
                } catch (_: Exception) {}
            }

            VoucherStatus(vData, isUsed, isExpired)
        }
        isLoading = false
    }

    val filteredVouchers = remember(vouchersWithStatus, selectedCategory) {
        if (selectedCategory == "All") vouchersWithStatus
        else {
            val filtered = vouchersWithStatus.filter { status ->
                val desc = status.data["description"]?.toString() ?: ""
                val code = status.data["code"]?.toString() ?: ""
                desc.contains(selectedCategory, ignoreCase = true) || code.contains(selectedCategory, ignoreCase = true)
            }
            if (filtered.isEmpty()) vouchersWithStatus else filtered
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        SectionHeader(title = stringResource(R.string.available_promo_vouchers))

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (filteredVouchers.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.no_active_voucher),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(filteredVouchers, key = { it.data["code"]?.toString() ?: "" }) { status ->
                    val voucher = status.data
                    val code = voucher["code"]?.toString() ?: "UNKNOWN"
                    val discount = voucher["discountPercentage"]?.toString() ?: "0"
                    val isUsed = status.isUsed
                    val isExpired = status.isExpired
                    val isDisabled = isUsed || isExpired
                    val bgPic = voucher["backgroundPicture"]?.toString() ?: ""
                    val bgBytes = remember(bgPic) {
                        try {
                            val clean = bgPic.replace("\n", "").replace("\r", "").trim()
                            if (clean.isNotBlank()) Base64.decode(clean, Base64.DEFAULT) else null
                        } catch (_: Exception) {
                            null
                        }
                    }

                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth().alpha(if (isDisabled) 0.6f else 1f),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = if (bgBytes != null) Color.Transparent else Color(0xFFFFB300)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (bgBytes == null) Color(0xFFFFB300) else Color.Transparent)
                        ) {
                            if (bgBytes != null) {
                                AsyncImage(
                                    model = bgBytes,
                                    contentDescription = null,
                                    modifier = Modifier.matchParentSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(Color.Black.copy(alpha = 0.5f))
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {

                                val desc = voucher["description"]?.toString() ?: ""
                                Text(
                                    text = if (desc.isNotBlank()) desc else "Get $discount% OFF with voucher $code",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (bgBytes != null) Color.White else Color(0xFF1F1F1F)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                val start = voucher["startDate"]?.toString() ?: ""
                                val end = voucher["endDate"]?.toString() ?: ""
                                val validText = if (end.isNotBlank()) "Valid till: $end" else if (start.isNotBlank()) "Valid from: $start" else "Valid anytime"
                                Text(
                                    text = validText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (bgBytes != null) Color.LightGray else Color(0xFF424242)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Voucher code pill button
                                    Surface(
                                        color = Color.White,
                                        shape = RoundedCornerShape(24.dp),
                                        shadowElevation = 2.dp
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocalOffer,
                                                contentDescription = null,
                                                tint = Color(0xFF1F1F1F),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = code,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFF1F1F1F)
                                            )
                                        }
                                    }

                                    // Discount / Status badge
                                    Surface(
                                        color = when {
                                            isUsed -> Color.Gray.copy(alpha = 0.3f)
                                            isExpired -> MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                                            else -> (if (bgBytes != null) Color.White else Color(0xFF1F1F1F)).copy(alpha = 0.15f)
                                        },
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = when {
                                                isUsed -> stringResource(R.string.used)
                                                isExpired -> stringResource(R.string.expired)
                                                else -> "$discount% OFF"
                                            },
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = when {
                                                isUsed -> Color.DarkGray
                                                isExpired -> MaterialTheme.colorScheme.error
                                                else -> if (bgBytes != null) Color.White else Color(0xFF1F1F1F)
                                            }
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