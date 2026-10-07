package com.example.appproject.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.appproject.R
import com.example.appproject.data.model.ADMIN_USERNAME
import com.example.appproject.data.model.LanguageManager
import com.example.appproject.data.model.LanguageOption
import com.example.appproject.data.model.ProblemReport
import com.example.appproject.data.model.ValueProp
import com.example.appproject.data.repository.FirebaseRepository
import com.example.appproject.ui.theme.NavyEnd
import com.example.appproject.ui.theme.NavyStart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun SettingsScreen(
    onNavigateToAdminReports: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val firebaseRepo = remember { FirebaseRepository() }
    val preferences = remember { context.getSharedPreferences("user_session", Context.MODE_PRIVATE) }

    val currentUsername = remember {
        preferences.getString("signed_in_username", null)
            .takeIf { !it.isNullOrBlank() }
            ?: preferences.getString("logged_in_user", null)
                .takeIf { !it.isNullOrBlank() }
            ?: "Guest"
    }

    val isGuest = currentUsername.equals("Guest", ignoreCase = true) || currentUsername.isBlank()
    val isAdmin = !isGuest && (currentUsername == ADMIN_USERNAME || currentUsername.equals("Admin123", ignoreCase = true))

    var selectedTabIndex by remember { mutableStateOf(0) }

    // Preferences states
    var notificationsEnabled by remember {
        mutableStateOf(preferences.getBoolean("notifications_enabled", true))
    }
    var highQualityImages by remember {
        mutableStateOf(preferences.getBoolean("high_quality_images", true))
    }

    // Problem reporting states
    var problemCategory by remember { mutableStateOf("Booking Issue") }
    var problemTitle by remember { mutableStateOf("") }
    var problemDesc by remember { mutableStateOf("") }
    var attachedImageUri by remember { mutableStateOf<Uri?>(null) }
    var attachedImageBase64 by remember { mutableStateOf("") }
    var userReports by remember { mutableStateOf<List<ProblemReport>>(emptyList()) }
    var isLoadingReports by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var activeReportFilter by remember { mutableStateOf("All") }
    var selectedPhotoForZoom by remember { mutableStateOf<Pair<String, String>?>(null) }

    var seenReportKeys by remember(currentUsername) {
        mutableStateOf(
            preferences.getStringSet("seen_report_keys_$currentUsername", emptySet()) ?: emptySet()
        )
    }

    val unreadReportCount = remember(userReports, seenReportKeys, selectedTabIndex) {
        if (selectedTabIndex == 1) {
            0
        } else {
            userReports.count { report ->
                val key = "${report.reportId}:${report.status}:${report.adminFeedback}"
                !seenReportKeys.contains(key)
            }
        }
    }

    LaunchedEffect(selectedTabIndex, userReports) {
        if (selectedTabIndex == 1 && userReports.isNotEmpty()) {
            val updatedKeys = seenReportKeys.toMutableSet()
            userReports.forEach { report ->
                val key = "${report.reportId}:${report.status}:${report.adminFeedback}"
                updatedKeys.add(key)
            }
            if (updatedKeys != seenReportKeys) {
                seenReportKeys = updatedKeys
                preferences.edit().putStringSet("seen_report_keys_$currentUsername", HashSet(updatedKeys)).apply()
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            attachedImageUri = uri
            scope.launch {
                attachedImageBase64 = compressAndEncodeImage(context, uri)
                if (attachedImageBase64.isBlank()) {
                    Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(currentUsername) {
        if (!isGuest && currentUsername.isNotBlank()) {
            isLoadingReports = true
            userReports = firebaseRepo.getProblemReportsForUser(currentUsername)
            isLoadingReports = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header Profile Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(NavyStart, NavyEnd)
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isAdmin) Color(0xFFFFD700) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (isAdmin) Color.Black else Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = currentUsername,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Surface(
                                color = if (isAdmin) Color(0xFFFFD700).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (isAdmin) stringResource(R.string.adminstrator_upper) else if (isGuest) "Guest Account" else stringResource(R.string.member_account),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isAdmin) Color(0xFFFFD700) else Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Tabs Header (Only show tabs for non-admin registered members)
        if (!isGuest && !isAdmin) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text(stringResource(R.string.app_preferences), fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = {
                        selectedTabIndex = 1
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.support_and_reports), fontWeight = FontWeight.Bold)
                            if (unreadReportCount > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Badge {
                                    Text("$unreadReportCount")
                                }
                            }
                        }
                    },
                    icon = { Icon(Icons.Default.ReportProblem, contentDescription = null) }
                )
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (isGuest || isAdmin || selectedTabIndex == 0) {
                // TAB 0: APP PREFERENCES & ADMIN PORTAL
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Admin Control Center Section (If Admin)
                    if (isAdmin) {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.AdminPanelSettings,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = stringResource(R.string.admin_operation_center),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.admin_operation_center_subtext),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { onNavigateToAdminReports() },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.SupportAgent, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(stringResource(R.string.manage_all_user_reports))
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                                }
                            }
                        }
                    }

                    // Language Selection Card
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.elevatedCardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Language,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = stringResource(R.string.change_language),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            LanguageListBox(modifier = Modifier.fillMaxWidth())
                        }
                    }

                    // App Settings & Preferences
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.elevatedCardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.system_preferences),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Notifications toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(stringResource(R.string.push_notification), fontWeight = FontWeight.SemiBold)
                                    }
                                }
                                Switch(
                                    checked = notificationsEnabled,
                                    onCheckedChange = {
                                        notificationsEnabled = it
                                        preferences.edit().putBoolean("notifications_enabled", it).apply()
                                        Toast.makeText(context, if (it) context.getString(R.string.notification_enabled) else context.getString(R.string.notification_disabled), Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            // High quality images toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Image, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(stringResource(R.string.high_quality_image_upload), fontWeight = FontWeight.SemiBold)
                                    }
                                }
                                Switch(
                                    checked = highQualityImages,
                                    onCheckedChange = {
                                        highQualityImages = it
                                        preferences.edit().putBoolean("high_quality_images", it).apply()
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                // TAB 1: USER SUPPORT & PROBLEM REPORTS
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Create New Problem Report Form
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.elevatedCardElevation(3.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.SupportAgent,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.submit_a_problem),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = stringResource(R.string.describe_issue),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Category Selector
                            Text("Issue Category", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            val categories = listOf(context.getString(R.string.booking_issue), context.getString(R.string.app_bug), context.getString(R.string.payment), context.getString(R.string.general_inquiry), context.getString(R.string.other))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                categories.take(3).forEach { cat ->
                                    FilterChip(
                                        selected = problemCategory == cat,
                                        onClick = { problemCategory = cat },
                                        label = { Text(cat, style = MaterialTheme.typography.labelSmall) },
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                categories.drop(3).forEach { cat ->
                                    FilterChip(
                                        selected = problemCategory == cat,
                                        onClick = { problemCategory = cat },
                                        label = { Text(cat, style = MaterialTheme.typography.labelSmall) },
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = problemTitle,
                                onValueChange = { problemTitle = it },
                                label = { Text(stringResource(R.string.problem_subject_short_title)) },
                                placeholder = { Text(stringResource(R.string.problem_subject_example)) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                leadingIcon = { Icon(Icons.Default.ReportProblem, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = problemDesc,
                                onValueChange = { problemDesc = it },
                                label = { Text(stringResource(R.string.detail_description)) },
                                placeholder = { Text(stringResource(R.string.detail_description_subtext)) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Photo Attachment Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { imagePickerLauncher.launch("image/*") },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (attachedImageUri == null) stringResource(R.string.attach_screenshot) else stringResource(R.string.change_photo))
                                }

                                if (attachedImageUri != null) {
                                    TextButton(
                                        onClick = {
                                            attachedImageUri = null
                                            attachedImageBase64 = ""
                                        }
                                    ) {
                                        Text(stringResource(R.string.remove_photo), color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }

                            if (attachedImageUri != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box {
                                    AsyncImage(
                                        model = attachedImageUri,
                                        contentDescription = stringResource(R.string.attached_preview),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(160.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (problemTitle.isBlank() || problemDesc.isBlank()) {
                                        Toast.makeText(context, context.getString(R.string.please_enter_title), Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    isSubmitting = true
                                    scope.launch {
                                        val fullTitle = "[$problemCategory] ${problemTitle.trim()}"
                                        val report = ProblemReport(
                                            reportId = UUID.randomUUID().toString(),
                                            username = currentUsername,
                                            title = fullTitle,
                                            description = problemDesc.trim(),
                                            photoBase64 = attachedImageBase64,
                                            timestamp = System.currentTimeMillis(),
                                            status = context.getString(R.string.pending),
                                            adminFeedback = ""
                                        )
                                        val success = firebaseRepo.saveProblemReport(report)
                                        if (success) {
                                            Toast.makeText(context, context.getString(R.string.report_submit_success), Toast.LENGTH_LONG).show()
                                            problemTitle = ""
                                            problemDesc = ""
                                            attachedImageUri = null
                                            attachedImageBase64 = ""
                                            userReports = firebaseRepo.getProblemReportsForUser(currentUsername)
                                        } else {
                                            Toast.makeText(context, context.getString(R.string.fail_submit_report), Toast.LENGTH_LONG).show()
                                        }
                                        isSubmitting = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isSubmitting,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (isSubmitting) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(stringResource(R.string.submitting_report))
                                } else {
                                    Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(stringResource(R.string.submit_problem_report))
                                }
                            }
                        }
                    }

                    // My Reports History Section
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.my_submitted_reports),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        isLoadingReports = true
                                        userReports = firebaseRepo.getProblemReportsForUser(currentUsername)
                                        isLoadingReports = false
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh reports")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Filter Chips for User History
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val filters = listOf(context.getString(R.string.all), context.getString(R.string.pending), context.getString(R.string.in_progress), context.getString(R.string.resolved))
                            filters.forEach { filter ->
                                FilterChip(
                                    selected = activeReportFilter == filter,
                                    onClick = { activeReportFilter = filter },
                                    label = { Text(filter) },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val filteredUserReports = userReports.filter { report ->
                            when (activeReportFilter) {
                                context.getString(R.string.pending) -> report.status.equals(context.getString(R.string.pending), ignoreCase = true)
                                context.getString(R.string.in_progress) -> report.status.equals(context.getString(R.string.in_progress), ignoreCase = true)
                                context.getString(R.string.resolved) -> report.status.equals(context.getString(R.string.resolved), ignoreCase = true)
                                else -> true
                            }
                        }

                        if (isLoadingReports) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else if (filteredUserReports.isEmpty()) {
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (userReports.isEmpty()) stringResource(R.string.no_problem_report_submitted) else stringResource(R.string.no_report_found) + " '$activeReportFilter'",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            filteredUserReports.forEach { report ->
                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.elevatedCardElevation(2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = report.title,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            val resolvedStr = context.getString(R.string.resolved).lowercase()
                                            val inProgressStr = context.getString(R.string.in_progress).lowercase()
                                            val statusLower = report.status.trim().lowercase()
                                            val (statusColor, containerColor) = when {
                                                statusLower == resolvedStr || statusLower == "resolved" ->
                                                    Pair(Color(0xFF4CAF50), Color(0xFF4CAF50).copy(alpha = 0.2f))
                                                statusLower == inProgressStr || statusLower == "in progress" ->
                                                    Pair(Color(0xFFFF9800), Color(0xFFFF9800).copy(alpha = 0.2f))
                                                else ->
                                                    Pair(Color(0xFFE53935), Color(0xFFE53935).copy(alpha = 0.2f))
                                            }
                                            Surface(
                                                color = containerColor,
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = report.status,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = statusColor,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        val dateStr = remember(report.timestamp) {
                                            if (report.timestamp > 0) {
                                                val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
                                                sdf.format(Date(report.timestamp))
                                            } else context.getString(R.string.recently)
                                        }

                                        Text(
                                            text = stringResource(R.string.submitted) + ": $dateStr",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = report.description,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        if (report.photoBase64.isNotBlank()) {
                                            val imageBytes = remember(report.photoBase64) {
                                                try {
                                                    val clean = report.photoBase64.replace("\n", "").replace("\r", "").trim()
                                                    if (clean.isNotBlank()) Base64.decode(clean, Base64.DEFAULT) else null
                                                } catch (e: Exception) {
                                                    null
                                                }
                                            }
                                            if (imageBytes != null) {
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            selectedPhotoForZoom = Pair(report.title, report.photoBase64)
                                                        }
                                                ) {
                                                    AsyncImage(
                                                        model = imageBytes,
                                                        contentDescription = "Attached photo",
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(160.dp)
                                                            .clip(RoundedCornerShape(12.dp)),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                    Surface(
                                                        color = Color.Black.copy(alpha = 0.6f),
                                                        shape = RoundedCornerShape(8.dp),
                                                        modifier = Modifier
                                                            .align(Alignment.BottomEnd)
                                                            .padding(8.dp)
                                                    ) {
                                                        Text(
                                                            text = stringResource(R.string.tap_to_enlarge),
                                                            color = Color.White,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        if (report.adminFeedback.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Surface(
                                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(12.dp)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            Icons.Default.SupportAgent,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(18.dp),
                                                            tint = MaterialTheme.colorScheme.primary
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = stringResource(R.string.admin_response) + ":",
                                                            style = MaterialTheme.typography.labelMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = report.adminFeedback,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer
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
    }

    // Photo Zoom Dialog
    if (selectedPhotoForZoom != null) {
        val (title, photoBase64) = selectedPhotoForZoom!!
        FullScreenImageDialog(
            photoBase64 = photoBase64,
            title = title,
            onDismiss = { selectedPhotoForZoom = null }
        )
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

@Composable
fun FullScreenImageDialog(
    photoBase64: String,
    title: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                val imageBytes = remember(photoBase64) {
                    try {
                        val clean = photoBase64.replace("\n", "").replace("\r", "").trim()
                        if (clean.isNotBlank()) Base64.decode(clean, Base64.DEFAULT) else null
                    } catch (e: Exception) {
                        null
                    }
                }
                if (imageBytes != null) {
                    AsyncImage(
                        model = imageBytes,
                        contentDescription = "Enlarged report photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 450.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageListBox(modifier: Modifier = Modifier) {
    val languages = remember {
        listOf(
            LanguageOption("en", "English", "English"),
            LanguageOption("ms", "Malay", "Bahasa Melayu")
        )
    }

    var currentLanguageCode by remember {
        mutableStateOf(AppCompatDelegate.getApplicationLocales().get(0)?.language ?: "en")
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .selectableGroup()
                .padding(vertical = 8.dp)
        ) {
            languages.forEach { language ->
                val isSelected = language.code == currentLanguageCode

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = isSelected,
                            onClick = {
                                currentLanguageCode = language.code
                                LanguageManager.setLanguage(language.code)
                            }
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = null
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = language.displayName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = language.nativeName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ValuePropCard(prop: ValueProp, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.heightIn(min = 120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = prop.icon,
                contentDescription = prop.title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = prop.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = prop.description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}