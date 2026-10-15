package com.example.appproject.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import com.example.appproject.ui.components.AdminListItemSkeleton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import com.example.appproject.data.model.User
import com.example.appproject.data.repository.FirebaseRepository
import kotlinx.coroutines.launch

@Composable
fun AdminUsersScreen(firebaseRepo: FirebaseRepository) {
    val context = LocalContext.current
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var searchKey by rememberSaveable { mutableStateOf("") }
    var userPendingAction by remember { mutableStateOf<User?>(null) }
    var pendingAction by remember { mutableStateOf("") }
    var connectionStatus by remember { mutableStateOf(context.getString(R.string.testing)) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isLoading = true
        connectionStatus = firebaseRepo.testConnection()
        users = firebaseRepo.getAllUsers()
        isLoading = false
    }

    val filteredUsers = users.filter { user ->
        val key = searchKey.trim()
        key.isEmpty() ||
                (user.id.toString()).contains(key, ignoreCase = true) ||
                (user.username).contains(key, ignoreCase = true) ||
                (user.email).contains(key, ignoreCase = true) ||
                (user.phone).contains(key, ignoreCase = true)
    }
    val activeUsers = users.count { !it.isBanned }
    val bannedUsers = users.count { it.isBanned }

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
            // Top Header & Title
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.user_accounts),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (connectionStatus == "Connected") stringResource(R.string.synced_and_connected) else connectionStatus,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
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
                            Text(stringResource(R.string.total), style = MaterialTheme.typography.labelMedium, color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${users.size}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        }
                        Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color(0xFFE2E8F0)))
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(stringResource(R.string.active), style = MaterialTheme.typography.labelMedium, color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$activeUsers", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                        }
                        Box(modifier = Modifier.width(1.dp).height(36.dp).background(Color(0xFFE2E8F0)))
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(stringResource(R.string.banned), style = MaterialTheme.typography.labelMedium, color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$bannedUsers", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFFB91C1C))
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchKey,
                    onValueChange = { searchKey = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.search_by_id_username_email_or_phone), color = Color(0xFF94A3B8)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B)) },
                    trailingIcon = {
                        if (searchKey.isNotEmpty()) {
                            IconButton(onClick = { searchKey = "" }) {
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

            // User Cards List
            if (isLoading) {
                items(4) {
                    AdminListItemSkeleton(modifier = Modifier.padding(bottom = 10.dp))
                }
            } else if (filteredUsers.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(24.dp).heightIn(min = 200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (connectionStatus != "Connected") stringResource(R.string.sync_failed, connectionStatus)
                                else if (users.isEmpty()) stringResource(R.string.no_user_accounts_found)
                                else stringResource(R.string.no_matching_user_accounts),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            } else {
                items(filteredUsers, key = { it.uid.ifBlank { it.username } }) { user ->
                    AdminUserAccountCard(
                        user = user,
                        onBanOrUnban = {
                            userPendingAction = user
                            pendingAction = if (user.isBanned) context.getString(R.string.unban) else context.getString(R.string.ban)
                        },
                        onDelete = {
                            userPendingAction = user
                            pendingAction = context.getString(R.string.delete)
                        }
                    )
                }
            }
        }
    }

    userPendingAction?.let { user ->
        val actionLabel = when (pendingAction) {
            context.getString(R.string.ban) -> stringResource(R.string.ban)
            context.getString(R.string.unban) -> stringResource(R.string.unban)
            else -> stringResource(R.string.delete)
        }
        val safeUsername = user.username.ifBlank { "User" }
        val message = when (pendingAction) {
            context.getString(R.string.ban) -> stringResource(R.string.ban_confirmation_msg, safeUsername)
            context.getString(R.string.unban) -> stringResource(R.string.unban_confirmation_msg, safeUsername)
            else -> stringResource(R.string.delete_confirmation_msg, safeUsername)
        }
        AlertDialog(
            onDismissRequest = { userPendingAction = null },
            title = { Text(stringResource(R.string.ban_user_q, actionLabel)) },
            text = { Text(message) },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            when (pendingAction) {
                                context.getString(R.string.ban), context.getString(R.string.unban) -> {
                                    val updatedUser = user.copy(isBanned = pendingAction == context.getString(R.string.ban))
                                    firebaseRepo.updateUser(updatedUser)
                                    users = users.map { if (it.uid == updatedUser.uid) updatedUser else it }
                                }
                                context.getString(R.string.delete) -> {
                                    firebaseRepo.deleteUser(user.uid ?: "")
                                    users = users.filterNot { it.uid == user.uid }
                                }
                            }
                            userPendingAction = null
                        }
                    },
                    colors = if (pendingAction == context.getString(R.string.delete) || pendingAction == context.getString(R.string.ban)) {
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    } else ButtonDefaults.buttonColors()
                ) { Text(actionLabel) }
            },
            dismissButton = {
                TextButton(onClick = { userPendingAction = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@Composable
fun AdminUserAccountCard(
    user: User,
    onBanOrUnban: () -> Unit,
    onDelete: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = (user.username ?: "").ifBlank { stringResource(R.string.user) },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (user.isBanned) Color(0xFFDC2626) else Color(0xFF047857))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (user.isBanned) stringResource(R.string.banned) else stringResource(R.string.active),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (user.isBanned) Color(0xFFDC2626) else Color(0xFF047857)
                    )
                }
            }
            if ((user.email ?: "").isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(user.email ?: "", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
            }
            if ((user.phone ?: "").isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(user.phone ?: "", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBanOrUnban,
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        if (user.isBanned) stringResource(R.string.unban) else stringResource(R.string.ban),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF334155)
                    )
                }
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFFFF1F2))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFFE11D48)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        stringResource(R.string.delete),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE11D48)
                    )
                }
            }
        }
    }
}
