package com.example.appproject.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.appproject.R
import com.example.appproject.data.model.ProblemReport
import com.example.appproject.data.model.TripPromo
import com.example.appproject.data.remote.getDefaultWebsitePromoTrips
import com.example.appproject.data.repository.FirebaseRepository
import com.example.appproject.ui.theme.ConfirmedEmerald
import com.example.appproject.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HelpScreen(
    loggedInUser: String? = null,
    displayUsername: String? = null,
    firebaseRepo: FirebaseRepository? = null,
    onBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedAboutTopic by rememberSaveable { mutableStateOf("A trip") }
    var selectedTripName by rememberSaveable { mutableStateOf("") }
    var tripDropdownExpanded by remember { mutableStateOf(false) }

    var userName by rememberSaveable { mutableStateOf("") }
    var userEmail by rememberSaveable { mutableStateOf("") }
    var userWhatsApp by rememberSaveable { mutableStateOf("") }
    var questionText by rememberSaveable { mutableStateOf("") }

    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessMessage by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    var availableTrips by remember { mutableStateOf<List<TripPromo>>(emptyList()) }

    LaunchedEffect(Unit) {
        availableTrips = getDefaultWebsitePromoTrips(context)
    }

    // Auto-fill user details if logged in
    LaunchedEffect(loggedInUser) {
        if (!loggedInUser.isNullOrBlank() && firebaseRepo != null) {
            try {
                val user = firebaseRepo.getUser(loggedInUser)
                if (user != null) {
                    if (user.name.isNotBlank()) userName = user.name else if (user.username.isNotBlank()) userName = user.username
                    if (user.email.isNotBlank()) userEmail = user.email
                    if (user.phone.isNotBlank()) userWhatsApp = user.phone
                } else if (!displayUsername.isNullOrBlank()) {
                    userName = displayUsername
                }
            } catch (_: Exception) {
                if (!displayUsername.isNullOrBlank()) {
                    userName = displayUsername
                }
            }
        } else if (!displayUsername.isNullOrBlank() && displayUsername != "Traveler") {
            userName = displayUsername
        }
    }

    val aboutOptions = listOf("A trip", "My booking", "Payment", "Something else")

    val faqList = remember {
        listOf(
            FaqData(
                id = 1,
                category = "Booking",
                question = "How many people do I need to book a trip?",
                answer = "Each trip has a minimum number of travelers, shown on its booking page. For example, the Tumpak Sewu Trip needs at least 5."
            ),
            FaqData(
                id = 2,
                category = "Payment",
                question = "When can I pay for my booking?",
                answer = "You can pay immediately after booking using Credit/Debit Card (Stripe), Bank Transfer, or local e-Wallets. Your booking is confirmed once payment is completed."
            ),
            FaqData(
                id = 3,
                category = "Vouchers",
                question = "How do I use a promo voucher?",
                answer = "On the booking checkout page, enter your voucher code in the 'Voucher Code' field and tap 'Apply' to receive instant discounts on your total price."
            ),
            FaqData(
                id = 4,
                category = "Cancelling",
                question = "Can I cancel a booking?",
                answer = "Yes, you can cancel your booking via the 'My Bookings' tab or by contacting customer support. Refund options depend on the timing of cancellation."
            )
        )
    }

    val filteredFaqs = remember(searchQuery, selectedAboutTopic) {
        faqList.filter { item ->
            val matchesQuery = searchQuery.isBlank() ||
                    item.question.contains(searchQuery, ignoreCase = true) ||
                    item.answer.contains(searchQuery, ignoreCase = true) ||
                    item.category.contains(searchQuery, ignoreCase = true)
            matchesQuery
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (onBack != null) {
                                    IconButton(onClick = onBack) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Menu",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "HELP & SUPPORT",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Main Headline
            Text(
                text = "How can we help?",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Ask about a trip, or browse the common questions below.",
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 2x2 Grid Topic Category Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TopicCategoryCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.AutoMirrored.Filled.ListAlt,
                    title = "Booking",
                    onClick = {
                        selectedAboutTopic = "My booking"
                        searchQuery = "Booking"
                    }
                )
                TopicCategoryCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CreditCard,
                    title = "Payment",
                    onClick = {
                        selectedAboutTopic = "Payment"
                        searchQuery = "Payment"
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TopicCategoryCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Cancel,
                    title = "Cancelling",
                    onClick = {
                        selectedAboutTopic = "My booking"
                        searchQuery = "Cancel"
                    }
                )
                TopicCategoryCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.LocalOffer,
                    title = "Vouchers",
                    onClick = {
                        selectedAboutTopic = "Payment"
                        searchQuery = "Voucher"
                    }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Common Questions Section
            Text(
                text = "Common questions",
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
                    if (filteredFaqs.isEmpty()) {
                        Text(
                            text = "No matching questions found.",
                            color = Color(0xFF64748B),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(20.dp)
                        )
                    } else {
                        filteredFaqs.forEachIndexed { index, faq ->
                            ExpandableFaqRow(
                                faq = faq,
                                defaultExpanded = (index == 0 && searchQuery.isBlank())
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
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Ask Us Anything Section
            Text(
                text = "Ask us anything",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Can't find your answer? Send us a question and our team will reply here.",
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Success Confirmation Banner
            AnimatedVisibility(
                visible = showSuccessMessage,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                    border = BorderStroke(1.dp, ConfirmedEmerald)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ConfirmedEmerald,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Success",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Inquiry Sent Successfully!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF065F46)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Our team will respond within 24 hours",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF047857),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { showSuccessMessage = false },
                            colors = ButtonDefaults.buttonColors(containerColor = ConfirmedEmerald),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp)
                        ) {
                            Text("Ask another question", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Inquiry Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Label: What is it about?
                    Text(
                        text = "What is it about?",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Chips
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        aboutOptions.forEach { option ->
                            val isSelected = selectedAboutTopic == option
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedAboutTopic = option },
                                label = {
                                    Text(
                                        text = option,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFEFF6FF),
                                    selectedLabelColor = PrimaryBlue,
                                    containerColor = Color.White,
                                    labelColor = Color(0xFF334155)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    selectedBorderColor = PrimaryBlue,
                                    borderColor = Color(0xFFE2E8F0),
                                    borderWidth = 1.5.dp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Label: Which trip? (optional)
                    Text(
                        text = "Which trip? (optional)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = tripDropdownExpanded,
                        onExpandedChange = { tripDropdownExpanded = !tripDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedTripName.ifBlank { "Select a trip" },
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            leadingIcon = {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryBlue)
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = if (tripDropdownExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = PrimaryBlue
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = PrimaryBlue,
                                focusedTextColor = if (selectedTripName.isBlank()) Color(0xFF64748B) else PrimaryBlue,
                                unfocusedTextColor = if (selectedTripName.isBlank()) Color(0xFF64748B) else PrimaryBlue
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = tripDropdownExpanded,
                            onDismissRequest = { tripDropdownExpanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            val isNoneSelected = selectedTripName.isBlank()
                            DropdownMenuItem(
                                text = { 
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "None / General Inquiry", 
                                            fontWeight = if (isNoneSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isNoneSelected) PrimaryBlue else Color(0xFF0F172A)
                                        )
                                        if (isNoneSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryBlue)
                                        }
                                    }
                                },
                                onClick = {
                                    selectedTripName = ""
                                    tripDropdownExpanded = false
                                },
                                modifier = Modifier.background(if (isNoneSelected) PrimaryBlue.copy(alpha = 0.1f) else Color.Transparent)
                            )
                            availableTrips.forEach { trip ->
                                val isSelected = selectedTripName == trip.name
                                DropdownMenuItem(
                                    text = { 
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = trip.name,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) PrimaryBlue else Color(0xFF0F172A)
                                            )
                                            if (isSelected) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryBlue)
                                            }
                                        }
                                    },
                                    onClick = {
                                        selectedTripName = trip.name
                                        tripDropdownExpanded = false
                                    },
                                    modifier = Modifier.background(if (isSelected) PrimaryBlue.copy(alpha = 0.1f) else Color.Transparent)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Label: Your Name
                    Text(
                        text = "Your Name",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. John Doe", color = Color(0xFF94A3B8), fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryBlue) },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Label: Email Address
                    Text(
                        text = "Email Address",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = userEmail,
                        onValueChange = { userEmail = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. john@example.com", color = Color(0xFF94A3B8), fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = PrimaryBlue) },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Label: WhatsApp Number
                    Text(
                        text = "WhatsApp Number",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = userWhatsApp,
                        onValueChange = { userWhatsApp = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. +62 812-3456-7890", color = Color(0xFF94A3B8), fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryBlue) },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Label: Your question
                    Text(
                        text = "Your question",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = questionText,
                        onValueChange = {
                            if (it.length <= 500) {
                                questionText = it
                                errorMessage = null
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        placeholder = {
                            Text(
                                text = "e.g. Is the Ijen trip suitable for beginners?",
                                color = Color(0xFF94A3B8),
                                fontSize = 14.sp
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Bottom info row (Lock icon + counter)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Only our team can see this",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        Text(
                            text = "${questionText.length} / 500",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    if (!errorMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            if (questionText.isBlank()) {
                                errorMessage = "Please enter your question."
                                return@Button
                            }

                            isSubmitting = true
                            errorMessage = null

                            scope.launch {
                                val reportId = UUID.randomUUID().toString()
                                val actualTrip = selectedTripName.ifBlank { "General Trip Inquiry" }
                                val titleText = "[$selectedAboutTopic] $actualTrip"
                                val fullDescription = "Trip: $actualTrip\nCategory: $selectedAboutTopic\nName: $userName\nEmail: $userEmail\nWhatsApp: $userWhatsApp\n\nQuestion:\n$questionText"

                                val report = ProblemReport(
                                    reportId = reportId,
                                    userUid = loggedInUser ?: "",
                                    username = userName.ifBlank { displayUsername ?: "Traveler" },
                                    userEmail = userEmail,
                                    title = titleText,
                                    description = fullDescription,
                                    timestamp = System.currentTimeMillis(),
                                    status = "Pending"
                                )

                                try {
                                    firebaseRepo?.saveProblemReport(report)
                                } catch (_: Exception) {
                                    // Handle offline fallback
                                }

                                isSubmitting = false
                                showSuccessMessage = true
                                questionText = ""
                                Toast.makeText(context, "Our team will respond within 24 hours", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = !isSubmitting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1D61E7)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Send question",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Direct Customer Support
            Text(
                text = "Direct customer support",
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
                Column(modifier = Modifier.padding(18.dp)) {
                    ContactSupportRow(
                        icon = Icons.Default.Email,
                        label = "Email Support",
                        value = "support@zuhantotravel.co.id"
                    )

                    HorizontalDivider(
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    ContactSupportRow(
                        icon = Icons.Default.Phone,
                        label = "Call & WhatsApp",
                        value = "+62 812-3456-7890"
                    )

                    HorizontalDivider(
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    ContactSupportRow(
                        icon = Icons.Default.Info,
                        label = "Operating Hours",
                        value = "Mon - Sun, 08:00 - 22:00 WIB"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TopicCategoryCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEFF6FF),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
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

@Composable
fun ContactSupportRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFEFF6FF),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A)
            )
        }
    }
}
