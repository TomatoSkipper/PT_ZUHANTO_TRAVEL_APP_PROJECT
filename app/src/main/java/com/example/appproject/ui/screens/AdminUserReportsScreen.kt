package com.example.appproject.ui.screens

import android.util.Base64
import android.widget.Toast
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import coil.compose.AsyncImage
import com.example.appproject.R
import com.example.appproject.data.model.ProblemReport
import com.example.appproject.data.repository.FirebaseRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminReportsScreen(firebaseRepo: FirebaseRepository) {
    val context = LocalContext.current
    var reports by remember { mutableStateOf<List<ProblemReport>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf("All") }
    var editingReport by remember { mutableStateOf<ProblemReport?>(null) }
    var feedbackInput by remember { mutableStateOf("") }
    var statusInput by remember { mutableStateOf("In Progress") }
    var selectedPhotoForZoom by remember { mutableStateOf<Pair<String, String>?>(null) }
    val scope = rememberCoroutineScope()

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val columnCount = when (adaptiveInfo.windowSizeClass.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> 1
        WindowWidthSizeClass.MEDIUM -> 2
        else -> 3
    }

    LaunchedEffect(Unit) {
        isLoading = true
        reports = firebaseRepo.getAllProblemReports()
        isLoading = false
    }

    val pendingCount = reports.count { it.status.equals("Pending", ignoreCase = true) }
    val inProgressCount = reports.count { it.status.equals("In Progress", ignoreCase = true) }
    val resolvedCount = reports.count { it.status.equals("Resolved", ignoreCase = true) }

    val filteredReports = reports.filter { report ->
        val q = searchQuery.trim()
        val matchesQuery = q.isEmpty() ||
                report.title.contains(q, ignoreCase = true) ||
                report.username.contains(q, ignoreCase = true) ||
                report.description.contains(q, ignoreCase = true)

        val matchesFilter = when (activeFilter) {
            "Pending" -> report.status.equals("Pending", ignoreCase = true)
            "In Progress" -> report.status.equals("In Progress", ignoreCase = true)
            "Resolved" -> report.status.equals("Resolved", ignoreCase = true)
            else -> true
        }

        matchesQuery && matchesFilter
    }

    Scaffold { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header Title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.user_problem_reports),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.user_problem_reports_subtext),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = {
                                scope.launch {
                                    isLoading = true
                                    reports = firebaseRepo.getAllProblemReports()
                                    isLoading = false
                                }
                            }
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh reports")
                        }
                    }

                    // Stat KPI Dashboard Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AdminStatCard(
                            title = stringResource(R.string.pending),
                            value = "$pendingCount",
                            icon = Icons.Default.ReportProblem,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.weight(1f),
                            onClick = { activeFilter = context.getString(R.string.pending) }
                        )
                        AdminStatCard(
                            title = stringResource(R.string.in_progress),
                            value = "$inProgressCount",
                            icon = Icons.Default.Refresh,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.weight(1f),
                            onClick = { activeFilter = context.getString(R.string.in_progress) }
                        )
                        AdminStatCard(
                            title = stringResource(R.string.resolved),
                            value = "$resolvedCount",
                            icon = Icons.Default.CheckCircle,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f),
                            onClick = { activeFilter = context.getString(R.string.resolved) }
                        )
                    }

                    // Search & Filter Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.search_by_user_problem)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear search")
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    // Filter Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val filters = listOf(context.getString(R.string.all), context.getString(R.string.pending), context.getString(R.string.in_progress), context.getString(R.string.resolved))
                        filters.forEach { opt ->
                            FilterChip(
                                selected = activeFilter == opt,
                                onClick = { activeFilter = opt },
                                label = { Text(opt) },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.loading_user_problem))
                    }
                }
            } else if (filteredReports.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.no_user_problem), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(filteredReports, key = { it.reportId }) { report ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.elevatedCardElevation(3.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = report.title,
                                    style = MaterialTheme.typography.titleMedium,
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

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.reported_by) + ": ${report.username}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            val dateStr = remember(report.timestamp) {
                                if (report.timestamp > 0) {
                                    val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
                                    sdf.format(Date(report.timestamp))
                                } else context.getString(R.string.recently)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.date) + ": $dateStr",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = report.description,
                                style = MaterialTheme.typography.bodyMedium
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
                                            contentDescription = "User attached photo",
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
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = stringResource(R.string.current_admin_feedback),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = report.adminFeedback,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        editingReport = report
                                        feedbackInput = report.adminFeedback
                                        statusInput = report.status.ifBlank { context.getString(R.string.in_progress) }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(stringResource(R.string.respond_or_edit))
                                }

                                if (!report.status.equals(stringResource(R.string.resolved), ignoreCase = true)) {
                                    OutlinedButton(
                                        onClick = {
                                            scope.launch {
                                                firebaseRepo.updateProblemReportFeedback(
                                                    report.reportId,
                                                    report.adminFeedback.ifBlank { context.getString(R.string.issue_mark_by_admin) },
                                                    context.getString(R.string.resolved)
                                                )
                                                Toast.makeText(context, context.getString(R.string.report_mark_as_resolved), Toast.LENGTH_SHORT).show()
                                                reports = firebaseRepo.getAllProblemReports()
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(stringResource(R.string.quick_resolved))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Photo Zoom Dialog for Admin
    if (selectedPhotoForZoom != null) {
        val (title, photoBase64) = selectedPhotoForZoom!!
        FullScreenImageDialog(
            photoBase64 = photoBase64,
            title = title,
            onDismiss = { selectedPhotoForZoom = null }
        )
    }

    // Response Dialog
    if (editingReport != null) {
        val targetReport = editingReport!!
        AlertDialog(
            onDismissRequest = { editingReport = null },
            title = { Text(stringResource(R.string.provide_admin_feedback)) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(stringResource(R.string.report) + ": ${targetReport.title}", fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.user) + ": ${targetReport.username}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    OutlinedTextField(
                        value = feedbackInput,
                        onValueChange = { feedbackInput = it },
                        label = { Text(stringResource(R.string.admin_feedback)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )

                    val statusOptions = listOf(context.getString(R.string.pending), context.getString(R.string.in_progress), context.getString(R.string.resolved))
                    Text(stringResource(R.string.status_update), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),

                        ) {
                        statusOptions.forEach { opt ->
                            FilterChip(
                                selected = statusInput == opt,
                                onClick = { statusInput = opt },
                                label = {
                                    Text (
                                        text = opt,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            firebaseRepo.updateProblemReportFeedback(targetReport.reportId, feedbackInput, statusInput)
                            Toast.makeText(context, context.getString(R.string.feedback_saved) +" !", Toast.LENGTH_SHORT).show()
                            reports = firebaseRepo.getAllProblemReports()
                            editingReport = null
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.save_response))
                }
            },
            dismissButton = {
                TextButton(onClick = { editingReport = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}