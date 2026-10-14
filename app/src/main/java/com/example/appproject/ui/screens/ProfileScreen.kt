package com.example.appproject.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appproject.R
import com.example.appproject.data.model.User
import com.example.appproject.data.repository.FirebaseRepository
import com.example.appproject.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    username: String,
    firebaseRepo: FirebaseRepository,
    onBack: () -> Unit = {},
    onProfileUpdated: (String) -> Unit = {}
) {
    var user by remember { mutableStateOf<User?>(null) }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    LaunchedEffect(username) {
        user = firebaseRepo.getUser(username)
        user?.let {
            fullName = it.name.ifBlank { it.username }
            email = it.email
            phone = it.phone
            gender = if (it.gender.equals("Female", ignoreCase = true)) "Female" else "Male"
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    SectionHeader(
                        title = stringResource(R.string.my_information),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

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
                            value = fullName,
                            onValueChange = {
                                fullName = it
                                errorMessage = ""
                                successMessage = ""
                            },
                            label = { Text(stringResource(R.string.full_name)) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
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

                        Spacer(modifier = Modifier.height(20.dp))

                        // Gender Selector
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Wc,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.gender),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                FilterChip(
                                    selected = gender == "Male",
                                    onClick = {
                                        gender = "Male"
                                        errorMessage = ""
                                        successMessage = ""
                                    },
                                    label = { Text(stringResource(R.string.male), fontWeight = FontWeight.Bold) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Male,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryBlue.copy(alpha = 0.15f),
                                        selectedLabelColor = PrimaryBlue,
                                        selectedLeadingIconColor = PrimaryBlue
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                FilterChip(
                                    selected = gender == "Female",
                                    onClick = {
                                        gender = "Female"
                                        errorMessage = ""
                                        successMessage = ""
                                    },
                                    label = { Text(stringResource(R.string.female), fontWeight = FontWeight.Bold) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Female,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryBlue.copy(alpha = 0.15f),
                                        selectedLabelColor = PrimaryBlue,
                                        selectedLeadingIconColor = PrimaryBlue
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
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

                        Spacer(modifier = Modifier.height(28.dp))

                        Button(
                            onClick = {
                                if (fullName.isBlank() || email.isBlank() || phone.isBlank()) {
                                    errorMessage = context.getString(R.string.error_fill_all_fields)
                                } else {
                                    scope.launch {
                                        try {
                                            user?.let {
                                                val cleanFullName = fullName.trim()
                                                val derivedUsername = cleanFullName.split("\\s+".toRegex()).firstOrNull()?.ifBlank { "Traveler" } ?: "Traveler"
                                                val newPhone = phone.trim()
                                                val isPhoneChanged = it.phone != newPhone

                                                if (isPhoneChanged && firebaseRepo.isPhoneRegistered(newPhone, it.uid)) {
                                                    errorMessage = "This phone number is already registered to another account"
                                                    return@launch
                                                }

                                                val updatedUser = it.copy(
                                                    name = cleanFullName,
                                                    username = derivedUsername,
                                                    email = email.trim(),
                                                    phone = newPhone,
                                                    gender = gender,
                                                    isPhoneVerified = if (isPhoneChanged) false else it.isPhoneVerified
                                                )
                                                firebaseRepo.saveUser(updatedUser)

                                                user = updatedUser
                                                successMessage = context.getString(R.string.profile_updated)
                                                onProfileUpdated(updatedUser.uid)
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
