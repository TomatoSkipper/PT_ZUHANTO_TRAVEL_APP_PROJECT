package com.example.appproject.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appproject.R
import com.example.appproject.data.model.User
import com.example.appproject.data.repository.FirebaseRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ProfileScreen(
    username: String,
    firebaseRepo: FirebaseRepository,
    onProfileUpdated: (String) -> Unit = {}
) {
    var user by remember { mutableStateOf<User?>(null) }
    var editableUsername by remember { mutableStateOf(username) }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    LaunchedEffect(username) {
        user = firebaseRepo.getUser(username)
        user?.let {
            editableUsername = it.username
            email = it.email
            phone = it.phone
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                initialOffsetY = { it / 10 },
                animationSpec = tween(600, easing = EaseOutBack)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                SectionHeader(
                    title = stringResource(R.string.profile),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold

                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = editableUsername,
                            onValueChange = {
                                editableUsername = it
                                errorMessage = ""
                                successMessage = ""
                            },
                            label = { Text(stringResource(R.string.username)) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                errorMessage = ""
                                successMessage = ""
                            },
                            label = { Text(stringResource(R.string.email)) },
                            placeholder = { Text(stringResource(R.string.enter_email)) },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = {
                                phone = it
                                errorMessage = ""
                                successMessage = ""
                            },
                            label = { Text(stringResource(R.string.phone)) },
                            placeholder = { Text(stringResource(R.string.enter_phone)) },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                        ) {
                            val isVerified = user?.isPhoneVerified == true
                            Surface(
                                color = if (isVerified) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isVerified) Icons.Default.CheckCircle else Icons.Default.Info,
                                        contentDescription = null,
                                        tint = if (isVerified) Color(0xFF2E7D32) else Color(0xFFE65100),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isVerified) stringResource(R.string.phone_verified) else stringResource(R.string.phone_unverified),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isVerified) Color(0xFF2E7D32) else Color(0xFFE65100)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
                            )
                        }

                        if (successMessage.isNotEmpty()) {
                            Text(
                                text = successMessage,
                                color = Color(0xFF4CAF50),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                if (editableUsername.isBlank() || email.isBlank() || phone.isBlank()) {
                                    errorMessage = context.getString(R.string.error_fill_all_fields)
                                } else {
                                    scope.launch {
                                        try {
                                            user?.let {
                                                val oldUsername = it.username
                                                val newUsername = editableUsername.trim()
                                                val isUsernameChanged = oldUsername != newUsername
                                                val isPhoneChanged = it.phone != phone.trim()

                                                val updatedUser = it.copy(
                                                    username = newUsername,
                                                    email = email.trim(),
                                                    phone = phone.trim(),
                                                    isPhoneVerified = if (isPhoneChanged) false else it.isPhoneVerified
                                                )
                                                firebaseRepo.saveUser(updatedUser)

                                                if (isUsernameChanged) {
                                                    firebaseRepo.db?.collection("users")?.document(oldUsername.lowercase())?.delete()?.await()
                                                    val bookings = firebaseRepo.getBookingsForUser(oldUsername)
                                                    for (booking in bookings) {
                                                        val updatedBooking = booking.copy(customerUsername = newUsername, customerName = newUsername)
                                                        firebaseRepo.updateBooking(updatedBooking)
                                                    }
                                                }

                                                val preferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                                                preferences.edit().putString("signed_in_username", updatedUser.username).apply()

                                                user = updatedUser
                                                successMessage = context.getString(R.string.profile_updated)
                                                onProfileUpdated(updatedUser.username)
                                            }
                                        } catch (e: Exception) {
                                            errorMessage = e.message ?: context.getString(R.string.unable_update)
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.save_changes),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}