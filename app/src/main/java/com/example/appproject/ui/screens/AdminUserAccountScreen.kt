package com.example.appproject.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.window.core.layout.WindowWidthSizeClass
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

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val columnCount = when (adaptiveInfo.windowSizeClass.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> 1
        WindowWidthSizeClass.MEDIUM -> 2
        else -> 3
    }

    LaunchedEffect(Unit) {
        isLoading = true
        connectionStatus = firebaseRepo.testConnection()
        users = firebaseRepo.getAllUsers()
        isLoading = false
    }

    val filteredUsers = users.filter { user ->
        val key = searchKey.trim()
        key.isEmpty() ||
                (user.id?.toString() ?: "").contains(key, ignoreCase = true) ||
                (user.username ?: "").contains(key, ignoreCase = true) ||
                (user.email ?: "").contains(key, ignoreCase = true) ||
                (user.phone ?: "").contains(key, ignoreCase = true)
    }
    val activeUsers = users.count { !it.isBanned }
    val bannedUsers = users.count { it.isBanned }

    Scaffold { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        SectionHeader(title = stringResource(R.string.user_accounts))

                        Spacer(modifier = Modifier.height(8.dp))

                        val isAdminUserConnected = connectionStatus == "Connected"
                        val adminUserDisplayStatus = if (isAdminUserConnected) stringResource(R.string.connected) else connectionStatus
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isAdminUserConnected) Color.Green else Color.Red)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.sync_status, adminUserDisplayStatus),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isAdminUserConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(onClick = {
                                scope.launch {
                                    connectionStatus = context.getString(R.string.syncing)
                                    connectionStatus = firebaseRepo.testConnection()
                                    users = firebaseRepo.getAllUsers()
                                }
                            }) {
                                Text(stringResource(R.string.refresh_sync))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AdminStatCard(stringResource(R.string.total), users.size.toString(), Icons.Default.Person,
                                MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                            AdminStatCard(stringResource(R.string.active), activeUsers.toString(), Icons.Default.CheckCircle,
                                Color(0xFF4CAF50), Modifier.weight(1f))
                            AdminStatCard(stringResource(R.string.banned), bannedUsers.toString(), Icons.Default.Lock,
                                MaterialTheme.colorScheme.error, Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = searchKey,
                            onValueChange = { searchKey = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(stringResource(R.string.search_users_placeholder)) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                if (searchKey.isNotEmpty()) {
                                    IconButton(onClick = { searchKey = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear search")
                                    }
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true
                        )
                    }
                }
            }

            if (filteredUsers.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(24.dp).heightIn(min = 200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isLoading) stringResource(R.string.loading_users)
                                else if (connectionStatus != "Connected") stringResource(R.string.sync_failed, connectionStatus)
                                else if (users.isEmpty()) stringResource(R.string.no_users)
                                else stringResource(R.string.no_matching_users),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            } else {
                items(filteredUsers, key = { it.username }) { user ->
                    AdminUserAccountCard(
                        user = user,
                        onBanOrUnban = {
                            userPendingAction = user
                            pendingAction = if (user.isBanned) "unban" else "ban"
                        },
                        onDelete = {
                            userPendingAction = user
                            pendingAction = "delete"
                        }
                    )
                }
            }
        }
    }

    userPendingAction?.let { user ->
        val actionLabel = when (pendingAction) {
            "ban" -> stringResource(R.string.ban)
            "unban" -> stringResource(R.string.unban)
            else -> stringResource(R.string.delete)
        }
        val message = when (pendingAction) {
            "ban" -> stringResource(R.string.ban_confirmation_msg, user.username)
            "unban" -> stringResource(R.string.unban_confirmation_msg, user.username)
            else -> stringResource(R.string.delete_confirmation_msg, user.username)
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
                                "ban", "unban" -> {
                                    val updatedUser = user.copy(isBanned = pendingAction == "ban")
                                    firebaseRepo.updateUser(updatedUser)
                                    users = users.map { if (it.username == updatedUser.username) updatedUser else it }
                                }
                                "delete" -> {
                                    firebaseRepo.deleteUser(user.username)
                                    users = users.filterNot { it.username == user.username }
                                }
                            }
                            userPendingAction = null
                        }
                    },
                    colors = if (pendingAction == "delete" || pendingAction == "ban") {
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
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(user.username, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("Username: ${user.username}", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            AdminAccountDetail(label = stringResource(R.string.username), value = user.username)
            AdminAccountDetail(label = stringResource(R.string.email), value = user.email)
            AdminAccountDetail(label = stringResource(R.string.phone), value = user.phone)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (user.isBanned) stringResource(R.string.status_banned) else stringResource(
                        R.string.status_active),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (user.isBanned) MaterialTheme.colorScheme.error else Color(0xFF4CAF50)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalIconButton(
                        onClick = onBanOrUnban,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = if (user.isBanned) MaterialTheme.colorScheme.errorContainer
                            else MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = if (user.isBanned) "Unban user" else "Ban user",
                            tint = if (user.isBanned) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary
                        )
                    }
                    FilledTonalIconButton(
                        onClick = onDelete,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete user",
                            tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAccountDetail(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(
            text = label,
            modifier = Modifier.width(88.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}