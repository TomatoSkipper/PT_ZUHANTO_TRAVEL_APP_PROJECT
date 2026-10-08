package com.example.appproject.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    var activeFilter by rememberSaveable { mutableStateOf("All") }
    var editingReport by remember { mutableStateOf<ProblemReport?>(null) }
    var feedbackInput by remember { mutableStateOf("") }
    var statusInput: String by remember { mutableStateOf("In Progress") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isLoading = true
        reports = firebaseRepo.getAllProblemReports()
        isLoading = false
    }

    val tabs = listOf(
        Pair("All", stringResource(R.string.all)),
        Pair("Pending", stringResource(R.string.pending)),
        Pair("In Progress", stringResource(R.string.in_progress)),
        Pair("Resolved", stringResource(R.string.resolved))
    )

    val pendingCount = reports.count { normalizeReportStatus(it.status) == "Pending" }
    val inProgressCount = reports.count { normalizeReportStatus(it.status) == "In Progress" }
    val resolvedCount = reports.count { normalizeReportStatus(it.status) == "Resolved" }

    val filteredReports = reports.filter { report ->
        val q = searchQuery.trim()
        val matchesQuery = q.isEmpty() ||
                report.title.contains(q, ignoreCase = true) ||
                report.username.contains(q, ignoreCase = true) ||
                report.description.contains(q, ignoreCase = true)

        val matchesFilter = when (activeFilter) {
            "All" -> true
            else -> normalizeReportStatus(report.status) == activeFilter
        }

        matchesQuery && matchesFilter
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.user_reports),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.review_issues),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                    OutlinedCard(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                reports = firebaseRepo.getAllProblemReports()
                                isLoading = false
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(44.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color(0xFF334155))
                        }
                    }
                }
            }

            // Stat Summary KPI Card
            item {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(stringResource(R.string.pending), style = MaterialTheme.typography.labelMedium, color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$pendingCount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFFB91C1C))
                        }
                        Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color(0xFFE2E8F0)))
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(stringResource(R.string.in_progress), style = MaterialTheme.typography.labelMedium, color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$inProgressCount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                        }
                        Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color(0xFFE2E8F0)))
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(stringResource(R.string.resolved), style = MaterialTheme.typography.labelMedium, color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$resolvedCount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                        }
                    }
                }
            }

            // Tabs Row
            item {
                val selectedTabIndex = tabs.indexOfFirst { it.first == activeFilter }.coerceAtLeast(0)
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF0284C7),
                    indicator = { tabPositions ->
                        if (selectedTabIndex < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = Color(0xFF0284C7),
                                height = 2.dp
                            )
                        }
                    },
                    divider = {}
                ) {
                    tabs.forEach { (canonicalKey, tabName) ->
                        val isSelected = activeFilter == canonicalKey
                        Tab(
                            selected = isSelected,
                            onClick = { activeFilter = canonicalKey },
                            text = {
                                Text(
                                    text = tabName,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color(0xFF0284C7) else Color(0xFF64748B)
                                )
                            }
                        )
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.search_by_user_title_or_description), color = Color(0xFF94A3B8)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search", tint = Color(0xFF64748B))
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedBorderColor = Color(0xFF0284C7)
                    )
                )
            }

            // Reports List
            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.loading_problem_reports), color = Color(0xFF64748B))
                    }
                }
            } else if (filteredReports.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.no_reports_found), color = Color(0xFF64748B))
                    }
                }
            } else {
                items(filteredReports, key = { it.reportId }) { report ->
                    val fullTitle = report.title
                    val category = if (fullTitle.startsWith("[") && fullTitle.contains("]")) {
                        fullTitle.substringAfter("[").substringBefore("]")
                    } else stringResource(R.string.general)
                    val displayTitle = if (fullTitle.startsWith("[") && fullTitle.contains("]")) {
                        fullTitle.substringAfter("]").trim()
                    } else fullTitle

                    val statusLower = report.status.trim().lowercase()
                    val (statusColor, statusText) = when {
                        statusLower.contains(stringResource(R.string.resolved)) -> Pair(Color(0xFF047857), stringResource(R.string.resolved))
                        statusLower.contains(stringResource(R.string.in_progress)) -> Pair(Color(0xFFD97706), stringResource(R.string.in_progress))
                        else -> Pair(Color(0xFFB91C1C), stringResource(R.string.pending))
                    }

                    val dateStr = remember(report.timestamp) {
                        if (report.timestamp > 0) {
                            val sdf = SimpleDateFormat("MMM d, yyyy, hh:mm a", Locale.US)
                            sdf.format(Date(report.timestamp))
                        } else (R.string.recent)
                    }

                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Category & Status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color(0xFF475569),
                                    fontWeight = FontWeight.Medium
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(statusColor)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = statusText,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = statusColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = displayTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = stringResource(R.string.reported_by) + " ${report.username}, $dateStr",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )

                            if (report.adminFeedback.isNotBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.admin_feedback),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = report.adminFeedback,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF334155)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedButton(
                                onClick = {
                                    editingReport = report
                                    feedbackInput = report.adminFeedback
                                    statusInput = if (report.status.equals(context.getString(R.string.resolved), ignoreCase = true)) context.getString(R.string.resolved)
                                                  else if (report.status.equals(context.getString(R.string.pending), ignoreCase = true)) context.getString(R.string.pending)
                                                  else context.getString(R.string.in_progress)
                                },
                                modifier = Modifier.fillMaxWidth().height(42.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFF0284C7)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.reply_or_edit_feedback),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0284C7)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Feedback Dialog
    editingReport?.let { targetReport ->
        AlertDialog(
            onDismissRequest = { editingReport = null },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White,
            title = {
                Column {
                    Text(
                        text = stringResource(R.string.reply_or_edit_feedback),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.user) + ": ${targetReport.username.ifBlank { "User" }}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B)
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Column {
                        Text(
                            text = stringResource(R.string.admin_feedback),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = feedbackInput,
                            onValueChange = { feedbackInput = it },
                            placeholder = { Text(stringResource(R.string.enter_resolution_or_response), color = Color(0xFF94A3B8)) },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedBorderColor = Color(0xFF0284C7),
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )
                    }

                    Column {
                        Text(
                            text = stringResource(R.string.status),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(stringResource(R.string.pending), stringResource(R.string.in_progress), stringResource(R.string.resolved)).forEach { st ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth().clickable { statusInput = st }
                                ) {
                                    RadioButton(
                                        selected = statusInput.equals(st, ignoreCase = true),
                                        onClick = { statusInput = st },
                                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0369A1))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = st,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            firebaseRepo.updateProblemReportFeedback(targetReport.reportId, feedbackInput, statusInput)
                            reports = firebaseRepo.getAllProblemReports()
                            editingReport = null
                            Toast.makeText(context, (R.string.feedback_updated_successfully), Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0369A1))
                ) {
                    Text(stringResource(R.string.save_feedback), fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { editingReport = null },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Text(stringResource(android.R.string.cancel), color = Color(0xFF334155), fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}

fun normalizeReportStatus(status: String?): String {
    if (status.isNullOrBlank()) return "Pending"
    val lower = status.trim().lowercase()
    return when {
        lower.contains("progress") || lower.contains("proses") || lower.contains("in_progress") -> "In Progress"
        lower.contains("resolve") || lower.contains("selesai") || lower.contains("complete") -> "Resolved"
        lower.contains("cancel") || lower.contains("batal") -> "Cancelled"
        else -> "Pending"
    }
}
