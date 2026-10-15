package com.example.appproject.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appproject.R
import com.example.appproject.data.model.HelpRequest
import com.example.appproject.data.repository.FirebaseRepository
import com.example.appproject.ui.components.HelpScreenSkeleton
import com.example.appproject.ui.theme.PrimaryBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Suppress("UNUSED_PARAMETER")
@Composable
fun HelpScreen(
    loggedInUser: String? = null,
    displayUsername: String? = null,
    firebaseRepo: FirebaseRepository? = null,
    onBack: (() -> Unit)? = null
) {
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    
    // Form state
    var selectedCategory by rememberSaveable { mutableStateOf("") }
    var selectedTrip by rememberSaveable { mutableStateOf("") }
    var customerName by rememberSaveable { mutableStateOf(displayUsername ?: loggedInUser ?: "") }
    var customerEmail by rememberSaveable { mutableStateOf("") }
    var customerPhone by rememberSaveable { mutableStateOf("") }
    var customerQuestion by rememberSaveable { mutableStateOf("") }
    
    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var tripDropdownExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val tripOptions = listOf(
        context.getString(R.string.not_about_trip),
        context.getString(R.string.pkg_bromo_sunrise),
        context.getString(R.string.pkg_tumpak_sewu),
        context.getString(R.string.pkg_ijen_bluefire),
        context.getString(R.string.pkg_jogja_dieng),
        context.getString(R.string.pkg_mount_rinjani),
        context.getString(R.string.pkg_yogyakarta_2d1n),
        context.getString(R.string.pkg_bromo_midnight),
        context.getString(R.string.pkg_malang_batu),
        context.getString(R.string.pkg_lembang_bandung),
        context.getString(R.string.pkg_jakarta_bogor_golf),
        context.getString(R.string.mount_bromo),
        context.getString(R.string.borobudur_temple),
        context.getString(R.string.ijen_crater),
        context.getString(R.string.bali),
        context.getString(R.string.labuan_bajo),
        context.getString(R.string.raja_ampat)
    )

    val faqList = remember {
        listOf(
            FaqData(
                id = 1,
                category = "Booking",
                question = context.getString(R.string.booking_faq),
                answer = context.getString(R.string.booking_faq_answer)
            ),
            FaqData(
                id = 2,
                category = "Payment",
                question = context.getString(R.string.payment_faq),
                answer = context.getString(R.string.payment_faq_answer)
            ),
            FaqData(
                id = 3,
                category = "Vouchers",
                question = context.getString(R.string.vouchers_faq),
                answer = context.getString(R.string.vouchers_faq_answer)
            ),
            FaqData(
                id = 4,
                category = "Cancelling",
                question = context.getString(R.string.cancel_faq),
                answer = context.getString(R.string.cancel_faq_answer)
            )
        )
    }

    val filteredFaqs = remember(searchQuery) {
        faqList.filter { item ->
            searchQuery.isBlank() ||
                    item.question.contains(searchQuery, ignoreCase = true) ||
                    item.answer.contains(searchQuery, ignoreCase = true) ||
                    item.category.contains(searchQuery, ignoreCase = true)
        }
    }

    LaunchedEffect(Unit) {
        delay(600)
        isLoading = false
    }

    if (isLoading) {
        Scaffold(containerColor = Color(0xFFF8FAFC)) { innerPadding ->
            HelpScreenSkeleton(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                onBack = onBack
            )
        }
        return
    }

    if (showSuccessDialog) {
        Dialog(
            onDismissRequest = {
                showSuccessDialog = false
                customerQuestion = ""
            }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(2.dp))

                    // Green Checkmark Double Circle Icon
                    Box(contentAlignment = Alignment.Center) {
                        Surface(
                            modifier = Modifier.size(92.dp),
                            shape = CircleShape,
                            color = Color(0xFFDCFCE7)
                        ) {}
                        Surface(
                            modifier = Modifier.size(68.dp),
                            shape = CircleShape,
                            color = Color(0xFF059669)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }

                    // Title
                    Text(
                        text = stringResource(R.string.request_sent),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center
                    )

                    // Message Text
                    val annotatedMessage = buildAnnotatedString {
                        append(stringResource(R.string.thank_you_for_reach_out))
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))) {
                            append(stringResource(R.string.our_team_will_contact_you))
                        }
                        append(stringResource(R.string.by_whatsapp_or_email))
                    }

                    Text(
                        text = annotatedMessage,
                        fontSize = 14.sp,
                        color = Color(0xFF475569),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    // Expected Reply Info Container Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFEEF2FF)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Outlined.AccessTime,
                                        contentDescription = null,
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = stringResource(R.string.expected_reply),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = stringResource(R.string.within_24_hours),
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Primary Button ("Done")
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            customerQuestion = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8))
                    ) {
                        Text(
                            text = stringResource(R.string.done),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Main Headline
            Text(
                text = stringResource(R.string.help_and_support),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Common Questions Section (Displayed First)
            Text(
                text = stringResource(R.string.common_questions),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    filteredFaqs.forEachIndexed { index, faq ->
                        ExpandableFaqRow(
                            faq = faq,
                            defaultExpanded = (index == 0)
                        )
                        if (index < filteredFaqs.size - 1) {
                            HorizontalDivider(
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 2. Support Form Card (Contact Support Section)
            Text(
                text = stringResource(R.string.contact_support),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // What is it about?
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.what_is_it_about),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val categories = listOf(
                                context.getString(R.string.trip),
                                context.getString(R.string.bookings),
                                context.getString(R.string.payment),
                                context.getString(R.string.general)
                            )
                            categories.forEach { cat ->
                                val isSelected = selectedCategory == cat
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (isSelected) Color(0xFFEFF6FF) else Color.White,
                                    border = BorderStroke(1.dp, if (isSelected) PrimaryBlue else Color(0xFFE2E8F0)),
                                    modifier = Modifier.clickable { selectedCategory = cat }
                                ) {
                                    Text(
                                        text = cat,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) PrimaryBlue else Color(0xFF475569),
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Which trip? (optional)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.which_trip),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedCardButton(
                                text = if (selectedTrip.isBlank()) context.getString(R.string.not_about_trip) else selectedTrip,
                                icon = Icons.Default.LocationOn,
                                onClick = { tripDropdownExpanded = true }
                            )

                            DropdownMenu(
                                expanded = tripDropdownExpanded,
                                onDismissRequest = { tripDropdownExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .heightIn(max = 280.dp)
                                    .background(Color.White)
                            ) {
                                val scrollState = rememberScrollState()
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 280.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .verticalScroll(scrollState)
                                            .drawWithContent {
                                                drawContent()
                                                val needScrollbar = scrollState.maxValue > 0
                                                if (needScrollbar) {
                                                    val totalHeight = (scrollState.maxValue + size.height).coerceAtLeast(1f)
                                                    val scrollbarHeight = (size.height / totalHeight * size.height).coerceAtLeast(36.dp.toPx())
                                                    val scrollbarOffsetY = (scrollState.value.toFloat() / scrollState.maxValue.toFloat()) * (size.height - scrollbarHeight)

                                                    drawRoundRect(
                                                        color = Color(0xFF94A3B8),
                                                        topLeft = Offset(size.width - 6.dp.toPx(), scrollbarOffsetY),
                                                        size = Size(4.dp.toPx(), scrollbarHeight),
                                                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
                                                        alpha = 0.8f
                                                    )
                                                }
                                            }
                                    ) {
                                        tripOptions.forEach { trip ->
                                            val isSelected = (selectedTrip == trip) || (selectedTrip.isBlank() && trip == context.getString(R.string.not_about_trip))
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = trip,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (isSelected) PrimaryBlue else Color(0xFF0F172A)
                                                    )
                                                },
                                                onClick = {
                                                    selectedTrip = if (trip == context.getString(R.string.not_about_trip)) "" else trip
                                                    tripDropdownExpanded = false
                                                },
                                                modifier = Modifier.background(
                                                    if (isSelected) Color(0xFFEFF6FF) else Color.Transparent
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Your Name
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.your_name),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryBlue)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            singleLine = true,
                            placeholder = { Text("e.g: John Doe") }
                        )
                    }

                    // Email Address
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.email_address),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        OutlinedTextField(
                            value = customerEmail,
                            onValueChange = { customerEmail = it },

                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = PrimaryBlue)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            singleLine = true,
                            placeholder = { Text("e.g: test123@gmail.com") }
                        )
                    }

                    // WhatsApp Number
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.whatsapp_number),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryBlue)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            singleLine = true,
                            placeholder = { Text("e.g: +6281234567890") }
                        )
                    }

                    // Your question
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.your_question),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        OutlinedTextField(
                            value = customerQuestion,
                            onValueChange = { customerQuestion = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(16.dp),
                            placeholder = { Text(stringResource(R.string.type_your_inquiry_or_issue_here)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Submit Request Button
                    Button(
                        onClick = {
                            if (customerQuestion.isBlank()) {
                                Toast.makeText(context, R.string.please_enter_your_question, Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            scope.launch {
                                isSubmitting = true
                                try {
                                    val req = HelpRequest(
                                        id = System.currentTimeMillis().toString(),
                                        category = selectedCategory,
                                        question = customerQuestion,
                                        customerName = customerName.ifBlank { "Guest" },
                                        customerEmail = customerEmail.ifBlank { "info@ztiindo.id" },
                                        customerPhone = customerPhone.ifBlank { "+628133315225" },
                                        tripPackage = selectedTrip,
                                        timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
                                        status = "Pending",
                                        customerUid = loggedInUser ?: ""
                                    )
                                    firebaseRepo?.saveHelpRequest(req)
                                    isSubmitting = false
                                    showSuccessDialog = true
                                } catch (e: Exception) {
                                    isSubmitting = false
                                    Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        enabled = !isSubmitting
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isSubmitting) stringResource(R.string.submitting) else stringResource(R.string.submit_request),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun OutlinedCardButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = text,
                    fontSize = 15.sp,
                    color = if (text == stringResource(R.string.not_about_trip)) Color(0xFF94A3B8) else Color(0xFF0F172A),
                    fontWeight = if (text == stringResource(R.string.not_about_trip)) FontWeight.Normal else FontWeight.Medium
                )
            }
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF64748B))
        }
    }
}

data class FaqData(
    val id: Int,
    val category: String,
    val question: String,
    val answer: String
)

@Composable
fun ExpandableFaqRow(
    faq: FaqData,
    defaultExpanded: Boolean = false
) {
    var isExpanded by rememberSaveable { mutableStateOf(defaultExpanded) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = faq.question,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = Color(0xFF64748B)
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = faq.answer,
                    fontSize = 14.sp,
                    color = Color(0xFF475569),
                    lineHeight = 20.sp
                )
            }
        }
    }
}
