package com.example.appproject.ui.screens

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import coil.compose.AsyncImage
import com.example.appproject.R
import com.example.appproject.data.model.CountryCodeOption
import com.example.appproject.data.repository.FirebaseRepository
import com.example.appproject.data.model.User
import com.example.appproject.data.model.defaultCountryCodes
import com.example.appproject.data.model.isValidEmail
import com.example.appproject.data.model.isValidPhone
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.brands.Google
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun CountryCodePhoneInput(
    phoneValue: String,
    onPhoneValueChange: (String) -> Unit,
    selectedCountry: CountryCodeOption,
    onCountrySelected: (CountryCodeOption) -> Unit,
    enabled: Boolean = true,
    isError: Boolean = false,
    label: String = stringResource(R.string.phone),
    modifier: Modifier = Modifier
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = phoneValue,
            onValueChange = onPhoneValueChange,
            enabled = enabled,
            label = { Text(label) },
            placeholder = { Text("123456789") },
            leadingIcon = {
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable(enabled = enabled) { dropdownExpanded = true }
                            .padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
                    ) {
                        Text(
                            text = "${selectedCountry.flag} ${selectedCountry.code}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = if (dropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = "Select Country Code",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        defaultCountryCodes.forEach { country ->
                            DropdownMenuItem(
                                text = {
                                    Text("${country.flag} ${country.countryName} (${country.code})")
                                },
                                onClick = {
                                    onCountrySelected(country)
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = isError,
            modifier = Modifier.fillMaxWidth().testTag("phoneInput"),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

fun getWebClientId(context: Context): String {
    return try {
        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        if (resId != 0) {
            context.getString(resId)
        } else {
            "815723458802-bfg6omi650936erccj1arsbes2fsjoeg.apps.googleusercontent.com"
        }
    } catch (_: Exception) {
        "815723458802-bfg6omi650936erccj1arsbes2fsjoeg.apps.googleusercontent.com"
    }
}

@Composable
fun LoginScreen(
    firebaseRepo: FirebaseRepository,
    onLoginSuccess: (String) -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    var selectedCountry by remember { mutableStateOf(defaultCountryCodes[0]) }
    var phoneInput by remember { mutableStateOf("") }
    var tacCode by remember { mutableStateOf("") }
    var staySignedIn by rememberSaveable { mutableStateOf(false) }

    var verificationId by remember { mutableStateOf<String?>(null) }
    var isTacSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as? Activity
    val auth = remember { FirebaseAuth.getInstance() }
    
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val webClientId = remember(context) { getWebClientId(context) }
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                scope.launch {
                    isLoading = true
                    try {
                        val authResult = auth.signInWithCredential(credential).await()
                        val firebaseUser = authResult.user

                        val email = firebaseUser?.email ?: account.email ?: ""
                        val displayName = firebaseUser?.displayName ?: account.displayName ?: "Traveler"
                        val phone = firebaseUser?.phoneNumber ?: ""
                        val username = handleGoogleSignInUser(firebaseRepo, email, displayName, phone)

                        val preferences = context.getSharedPreferences(
                            "user_session",
                            Context.MODE_PRIVATE
                        )
                        preferences.edit().putString("signed_in_username", username).apply()

                        isLoading = false
                        onLoginSuccess(username)
                    } catch (e: Exception) {
                        isLoading = false
                        errorMessage = e.localizedMessage ?: context.getString(R.string.error_invalid_credentials)
                    }
                }
            } else {
                isLoading = false
                errorMessage = "Google Sign-In failed: No ID Token"
            }
        } catch (e: Exception) {
            isLoading = false
            if (e is ApiException) {
                if (e.statusCode == 12501) {
                    return@rememberLauncherForActivityResult
                }
                val msg = e.localizedMessage ?: ""
                if (msg.contains("Long live credential", ignoreCase = true)) {
                    errorMessage = "Google credentials unavailable on this device. Please sign in with phone number."
                } else {
                    errorMessage = "Google Sign-In failed (${e.statusCode})"
                }
            } else {
                errorMessage = e.localizedMessage ?: "Google Sign-In failed"
            }
        }
    }

    fun launchGoogleSignIn() {
        isLoading = true
        errorMessage = ""
        scope.launch {
            try {
                if (activity != null) {
                    val credentialManager = CredentialManager.create(context)
                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(webClientId)
                        .setAutoSelectEnabled(false)
                        .build()

                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    val result = credentialManager.getCredential(context = activity, request = request)
                    val credential = result.credential
                    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                        val authResult = auth.signInWithCredential(authCredential).await()
                        val firebaseUser = authResult.user

                        val email = firebaseUser?.email ?: googleIdTokenCredential.id ?: ""
                        val displayName = firebaseUser?.displayName ?: googleIdTokenCredential.displayName ?: "Traveler"
                        val phone = firebaseUser?.phoneNumber ?: ""
                        val username = handleGoogleSignInUser(firebaseRepo, email, displayName, phone)

                        val preferences = context.getSharedPreferences(
                            "user_session",
                            Context.MODE_PRIVATE
                        )
                        preferences.edit().putString("signed_in_username", username).apply()

                        isLoading = false
                        onLoginSuccess(username)
                        return@launch
                    }
                }
            } catch (e: GetCredentialCancellationException) {
                isLoading = false
                return@launch
            } catch (e: Exception) {
                Log.w("GoogleSignIn", "CredentialManager failed, trying legacy GoogleSignInIntent: ${e.message}")
            }

            try {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(webClientId)
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                googleSignInClient.signOut()
                googleSignInLauncher.launch(googleSignInClient.signInIntent)
            } catch (e: Exception) {
                isLoading = false
                errorMessage = e.localizedMessage ?: "Failed to start Google Sign-In"
            }
        }
    }

    fun getFullPhoneNumber(): String {
        val trimmed = phoneInput.trim()
        return if (trimmed.startsWith("+")) {
            trimmed
        } else {
            val digits = trimmed.removePrefix("0")
            "${selectedCountry.code}$digits"
        }
    }

    fun completeLogin(credential: PhoneAuthCredential) {
        scope.launch {
            isLoading = true
            try {
                val authResult = auth.signInWithCredential(credential).await()
                val firebaseUser = authResult.user
                val userPhone = firebaseUser?.phoneNumber ?: getFullPhoneNumber()

                val tokenResult = firebaseUser?.getIdToken(true)?.await()
                val isAdmin = tokenResult?.claims?.get("admin") == true

                val targetUsername = if (isAdmin) {
                    // Set a default username for admin users
                    "Admin"
                } else {
                    val currentUser = firebaseRepo.getUser(userPhone)
                    if (currentUser == null) {
                        isLoading = false
                        errorMessage = context.getString(R.string.error_phone_not_registered)
                        return@launch
                    }

                    if (currentUser.isBanned) {
                        isLoading = false
                        errorMessage = context.getString(R.string.error_account_banned)
                        return@launch
                    }

                    val updated = currentUser.copy(isPhoneVerified = true)
                    firebaseRepo.updateUser(updated)
                    currentUser.username
                }

                val preferences = context.getSharedPreferences(
                    "user_session",
                    Context.MODE_PRIVATE
                )

                if (staySignedIn) {
                    preferences.edit()
                        .putString("signed_in_username", targetUsername)
                        .apply()
                } else {
                    preferences.edit().remove("signed_in_username").apply()
                }

                isLoading = false
                onLoginSuccess(targetUsername)
            } catch (e: Exception) {
                errorMessage = context.getString(R.string.error_invalid_credentials)
                isLoading = false
            }
        }
    }

    fun requestTacCode() {
        if (activity == null) {
            errorMessage = "Activity Reference Error"
            return
        }

        isLoading = true
        errorMessage = ""

        val fullPhone = getFullPhoneNumber()

        scope.launch {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(fullPhone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        completeLogin(credential)
                    }

                    override fun onVerificationFailed(p0: FirebaseException) {
                        isLoading = false
                        errorMessage = p0.localizedMessage ?: "Verification Failed"
                    }

                    override fun onCodeSent(
                        vId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        isLoading = false
                        isTacSent = true
                        verificationId = vId
                    }
                }).build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = R.drawable.backgroundmountainlogin,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

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
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.size(100.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.zti_logo),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.welcome_back),
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().widthIn(max = 500.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.login),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        CountryCodePhoneInput(
                            phoneValue = phoneInput,
                            onPhoneValueChange = {
                                phoneInput = it
                                errorMessage = ""
                            },
                            selectedCountry = selectedCountry,
                            onCountrySelected = { selectedCountry = it },
                            enabled = !isTacSent,
                            label = stringResource(R.string.phone),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (isTacSent) {
                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = tacCode,
                                onValueChange = {
                                    if (it.length <= 6) tacCode = it
                                    errorMessage = ""
                                },
                                label = { Text("6-Digit TAC Code") },
                                placeholder = { Text("Enter TAC") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().testTag("tacInput"),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }

                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .padding(top = 12.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = staySignedIn,
                                onCheckedChange = { staySignedIn = it }
                            )
                            Text(
                                text = stringResource(R.string.stay_signed_in),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.clickable { staySignedIn = !staySignedIn }
                            )
                        }

                        Spacer(modifier = Modifier.height(36.dp))

                        Button(
                            onClick = {
                                if (!isTacSent) {
                                    val fullPhone = getFullPhoneNumber()
                                    if (isValidPhone(fullPhone)) {
                                        requestTacCode()
                                    } else {
                                        errorMessage = context.getString(R.string.error_invalid_phone)
                                    }
                                } else {
                                    if (tacCode.length != 6) {
                                        errorMessage = "Please enter a valid 6-digit TAC code"
                                    } else if (verificationId != null) {
                                        val credential = PhoneAuthProvider.getCredential(verificationId!!, tacCode.trim())
                                        completeLogin(credential)
                                    } else {
                                        errorMessage = "Invalid TAC code"
                                    }
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    text = if (!isTacSent) "Request TAC" else stringResource(R.string.login),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (isTacSent) {
                            TextButton(
                                onClick = {
                                    isTacSent = false
                                    tacCode = ""
                                },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text(stringResource(R.string.change_phone_tac))
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                            Text(
                                text = stringResource(R.string.or_continue_with),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedButton(
                            onClick = { launchGoogleSignIn() },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = FontAwesomeIcons.Brands.Google,
                                    contentDescription = "Google Logo",
                                    tint = Color(0xFF4285F4),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = stringResource(R.string.sign_in_with_google),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F1F1F)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.no_account),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.sign_up),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.clickable { onSignUpClick() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(
    firebaseRepo: FirebaseRepository,
    onRegisterSuccess: (String) -> Unit = {},
    onBackToLogin: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf(defaultCountryCodes[0]) }
    var phoneInput by remember { mutableStateOf("") }

    var isloading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showRegistrationSuccess by remember { mutableStateOf(false) }
    var registeredUsername by remember { mutableStateOf("") }
    var isTacSent by remember { mutableStateOf(false) }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var tacCode by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as? Activity
    val auth = remember { FirebaseAuth.getInstance() }
    
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val webClientId = remember(context) { getWebClientId(context) }
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                scope.launch {
                    isloading = true
                    try {
                        val authResult = auth.signInWithCredential(credential).await()
                        val firebaseUser = authResult.user

                        val userEmail = firebaseUser?.email ?: account.email ?: ""
                        val displayName = firebaseUser?.displayName ?: account.displayName ?: "Traveler"
                        val userPhone = firebaseUser?.phoneNumber ?: ""
                        val username = handleGoogleSignInUser(firebaseRepo, userEmail, displayName, userPhone)

                        val preferences = context.getSharedPreferences(
                            "user_session",
                            Context.MODE_PRIVATE
                        )
                        preferences.edit().putString("signed_in_username", username).apply()

                        registeredUsername = username
                        isloading = false
                        showRegistrationSuccess = true
                    } catch (e: Exception) {
                        isloading = false
                        errorMessage = e.localizedMessage ?: context.getString(R.string.registration_failed)
                    }
                }
            } else {
                isloading = false
                errorMessage = "Google Sign-In failed: No ID Token"
            }
        } catch (e: Exception) {
            isloading = false
            if (e is ApiException) {
                if (e.statusCode == 12501) {
                    return@rememberLauncherForActivityResult
                }
                val msg = e.localizedMessage ?: ""
                if (msg.contains("Long live credential", ignoreCase = true)) {
                    errorMessage = "Google credentials unavailable on this device. Please register with phone number."
                } else {
                    errorMessage = "Google Sign-In failed (${e.statusCode})"
                }
            } else {
                errorMessage = e.localizedMessage ?: "Google Sign-In failed"
            }
        }
    }

    fun launchGoogleSignIn() {
        isloading = true
        errorMessage = ""
        scope.launch {
            val activity = context as? Activity
            try {
                if (activity != null) {
                    val credentialManager = CredentialManager.create(context)
                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(webClientId)
                        .setAutoSelectEnabled(false)
                        .build()

                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    val result = credentialManager.getCredential(context = activity, request = request)
                    val credential = result.credential
                    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                        val authResult = auth.signInWithCredential(authCredential).await()
                        val firebaseUser = authResult.user

                        val userEmail = firebaseUser?.email ?: googleIdTokenCredential.id ?: ""
                        val displayName = firebaseUser?.displayName ?: googleIdTokenCredential.displayName ?: "Traveler"
                        val userPhone = firebaseUser?.phoneNumber ?: ""
                        val username = handleGoogleSignInUser(firebaseRepo, userEmail, displayName, userPhone)

                        val preferences = context.getSharedPreferences(
                            "user_session",
                            Context.MODE_PRIVATE
                        )
                        preferences.edit().putString("signed_in_username", username).apply()

                        registeredUsername = username
                        isloading = false
                        showRegistrationSuccess = true
                        return@launch
                    }
                }
            } catch (e: GetCredentialCancellationException) {
                isloading = false
                return@launch
            } catch (e: Exception) {
                Log.w("GoogleSignIn", "CredentialManager failed, trying legacy GoogleSignInIntent: ${e.message}")
            }

            try {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(webClientId)
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                googleSignInClient.signOut()
                googleSignInLauncher.launch(googleSignInClient.signInIntent)
            } catch (e: Exception) {
                isloading = false
                errorMessage = e.localizedMessage ?: "Failed to start Google Sign-In"
            }
        }
    }

    fun getFullPhoneNumber(): String {
        val trimmed = phoneInput.trim()
        return if (trimmed.startsWith("+")) {
            trimmed
        } else {
            val digits = trimmed.removePrefix("0")
            "${selectedCountry.code}$digits"
        }
    }

    fun saveNewUserAndFinish(fullPhone: String, username: String, isVerified: Boolean) {
        scope.launch {
            try {
                if (firebaseRepo.isEmailRegistered(email.trim())) {
                    isloading = false
                    errorMessage = "This email is already registered to another account"
                    return@launch
                }

                if (firebaseRepo.isPhoneRegistered(fullPhone)) {
                    isloading = false
                    errorMessage = "This phone number is already registered to another account"
                    return@launch
                }

                val finalUsername = username.ifBlank { email.trim().substringBefore("@").ifBlank { fullPhone } }
                if (firebaseRepo.isUsernameRegistered(finalUsername)) {
                    isloading = false
                    errorMessage = "This username is already taken"
                    return@launch
                }

                val currentUserUid = auth.currentUser?.uid ?: ""
                val newUser = User(
                    uid = currentUserUid,
                    username = finalUsername,
                    email = email.trim(),
                    phone = fullPhone,
                    isPhoneVerified = isVerified,
                    name = username.ifBlank { finalUsername }
                )
                firebaseRepo.saveUser(newUser)

                val preferences = context.getSharedPreferences(
                    "user_session",
                    Context.MODE_PRIVATE
                )
                preferences.edit().putString("signed_in_username", finalUsername).apply()

                registeredUsername = finalUsername
                isloading = false
                showRegistrationSuccess = true
            } catch (e: Exception) {
                isloading = false
                errorMessage = e.message ?: context.getString(R.string.registration_failed)
            }
        }
    }

    fun completeRegistrationWithCredential(credential: PhoneAuthCredential) {
        scope.launch {
            isloading = true
            try {
                auth.signInWithCredential(credential).await()
                val fullPhone = getFullPhoneNumber()
                val username = name.trim()
                saveNewUserAndFinish(fullPhone, username, true)
            } catch (e: Exception) {
                isloading = false
                errorMessage = e.localizedMessage ?: context.getString(R.string.registration_failed)
            }
        }
    }

    fun verifyTacAndRegister() {
        if (tacCode.length != 6) {
            errorMessage = context.getString(R.string.error_tac_message)
            return
        }

        if (verificationId != null) {
            isloading = true
            val credential = PhoneAuthProvider.getCredential(verificationId!!, tacCode.trim())
            completeRegistrationWithCredential(credential)
        } else {
            errorMessage = context.getString(R.string.invalid_verification_request)
        }
    }

    fun requestTacCode() {
        val fullPhone = getFullPhoneNumber()
        if (name.isBlank() || email.isBlank() || phoneInput.isBlank()) {
            errorMessage = context.getString(R.string.error_fill_all_fields)
            return
        }
        if (!isValidEmail(email.trim())) {
            errorMessage = context.getString(R.string.invalid_email)
            return
        }
        if (!isValidPhone(fullPhone)) {
            errorMessage = context.getString(R.string.error_invalid_phone)
            return
        }

        isloading = true
        errorMessage = ""

        scope.launch {
            try {
                val emailTaken = firebaseRepo.isEmailRegistered(email.trim())
                if (emailTaken) {
                    isloading = false
                    errorMessage = "This email is already registered to another account"
                    return@launch
                }

                val phoneTaken = firebaseRepo.isPhoneRegistered(fullPhone)
                if (phoneTaken) {
                    isloading = false
                    errorMessage = "This phone number is already registered to another account"
                    return@launch
                }

                if (activity == null) {
                    isloading = false
                    errorMessage = "Activity Reference Error"
                    return@launch
                }

                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(fullPhone)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(activity)
                    .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                            completeRegistrationWithCredential(credential)
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            isloading = false
                            errorMessage = e.message ?: "Verification Failed"
                        }

                        override fun onCodeSent(vId: String, token: PhoneAuthProvider.ForceResendingToken) {
                            isloading = false
                            isTacSent = true
                            verificationId = vId
                        }
                    }).build()

                PhoneAuthProvider.verifyPhoneNumber(options)
            } catch (e: Exception) {
                isloading = false
                errorMessage = e.localizedMessage ?: "Registration failed"
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = R.drawable.backgroundmountainlogin,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.6f
        )

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
                Text(
                    text = stringResource(R.string.create_account),
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().widthIn(max = 500.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                errorMessage = ""
                            },
                            label = { Text(stringResource(R.string.username)) },
                            placeholder = { Text(stringResource(R.string.enter_username)) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            enabled = !isTacSent,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                errorMessage = ""
                            },
                            label = { Text(stringResource(R.string.email)) },
                            placeholder = { Text(stringResource(R.string.enter_email)) },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            singleLine = true,
                            enabled = !isTacSent,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        CountryCodePhoneInput(
                            phoneValue = phoneInput,
                            onPhoneValueChange = {
                                phoneInput = it
                                errorMessage = ""
                            },
                            selectedCountry = selectedCountry,
                            onCountrySelected = { selectedCountry = it },
                            label = stringResource(R.string.phone),
                            enabled = !isTacSent,
                            isError = errorMessage == context.getString(R.string.error_invalid_phone),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (isTacSent) {
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = tacCode,
                                onValueChange = {
                                    if (it.length <= 6) tacCode = it
                                    errorMessage = ""
                                },
                                label = { Text(stringResource(R.string.six_digit_tac)) },
                                placeholder = { Text(stringResource(R.string.enter_tac)) },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().testTag("tacInput"),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                if (!isTacSent) {
                                    requestTacCode()
                                } else {
                                    verifyTacAndRegister()
                                }
                            },
                            enabled = !isloading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            if (isloading) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    text = if (!isTacSent) stringResource(R.string.send_tac_code) else "Verify & Register",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }

                        if (isTacSent) {
                            TextButton(
                                onClick = {
                                    isTacSent = false
                                    tacCode = ""
                                },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text(stringResource(R.string.change_phone_tac))
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                            Text(
                                text = stringResource(R.string.or_continue_with),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedButton(
                            onClick = { launchGoogleSignIn() },
                            enabled = !isloading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = FontAwesomeIcons.Brands.Google,
                                    contentDescription = "Google Logo",
                                    tint = Color(0xFF4285F4),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = stringResource(R.string.sign_in_with_google),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F1F1F)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.already_have_account),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.login),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onBackToLogin() }
                            )
                        }
                    }
                }
            }
        }

        if (showRegistrationSuccess) {
            AlertDialog(
                onDismissRequest = {
                    showRegistrationSuccess = false
                    onRegisterSuccess(registeredUsername)
                },
                title = { Text(stringResource(R.string.registration_successful)) },
                text = {
                    Text(stringResource(R.string.account_created_message))
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showRegistrationSuccess = false
                            onRegisterSuccess(registeredUsername)
                        }
                    ) {
                        Text(stringResource(R.string.continue_to_login))
                    }
                }
            )
        }
    }
}

@Composable
fun PhoneVerificationBookingDialog(
    initialPhone: String,
    onVerificationSuccess: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCountry by remember { mutableStateOf(defaultCountryCodes[0]) }
    var phoneInput by remember { mutableStateOf(initialPhone.removePrefix(selectedCountry.code).removePrefix("+")) }
    var tacCode by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var isTacSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val activity = context as? Activity
    val auth = remember { FirebaseAuth.getInstance() }

    fun getFullPhoneNumber(): String {
        val trimmed = phoneInput.trim()
        return if (trimmed.startsWith("+")) trimmed else {
            val digits = trimmed.removePrefix("0")
            "${selectedCountry.code}$digits"
        }
    }

    fun requestTacCode() {
        if (activity == null) {
            errorMessage = "Activity Reference Error"
            return
        }

        isLoading = true
        errorMessage = ""
        val fullPhone = getFullPhoneNumber()

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(fullPhone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    isLoading = false
                    onVerificationSuccess(fullPhone)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    isLoading = false
                    errorMessage = e.message ?: "Verification Failed"
                }

                override fun onCodeSent(vId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    isLoading = false
                    isTacSent = true
                    verificationId = vId
                }
            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyTac() {
        val fullPhone = getFullPhoneNumber()
        if (tacCode.length != 6) {
            errorMessage = context.getString(R.string.error_tac_message)
            return
        }
        if (verificationId != null) {
            isLoading = true
            val credential = PhoneAuthProvider.getCredential(verificationId!!, tacCode.trim())
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    isLoading = false
                    if (task.isSuccessful) {
                        onVerificationSuccess(fullPhone)
                    } else {
                        errorMessage = task.exception?.message ?: context.getString(R.string.invalid_tac)
                    }
                }
        } else {
            errorMessage = context.getString(R.string.invalid_verification_request)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.phone_verification_required)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.phone_verfication_required_desc),
                    style = MaterialTheme.typography.bodyMedium
                )

                CountryCodePhoneInput(
                    phoneValue = phoneInput,
                    onPhoneValueChange = {
                        phoneInput = it
                        errorMessage = ""
                    },
                    selectedCountry = selectedCountry,
                    onCountrySelected = { selectedCountry = it },
                    enabled = !isTacSent,
                    label = stringResource(R.string.phone),
                    modifier = Modifier.fillMaxWidth()
                )

                if (isTacSent) {
                    OutlinedTextField(
                        value = tacCode,
                        onValueChange = {
                            if (it.length <= 6) tacCode = it
                            errorMessage = ""
                        },
                        label = { Text(stringResource(R.string.six_digit_tac)) },
                        placeholder = { Text(stringResource(R.string.enter_tac)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!isTacSent) {
                        val fullPhone = getFullPhoneNumber()
                        if (isValidPhone(fullPhone)) {
                            requestTacCode()
                        } else {
                            errorMessage = context.getString(R.string.error_invalid_phone)
                        }
                    } else {
                        verifyTac()
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text(if (!isTacSent) stringResource(R.string.send_tac_code) else stringResource(
                        R.string.verify_tac_complete_booking))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

private suspend fun handleGoogleSignInUser(
    firebaseRepo: FirebaseRepository,
    email: String,
    displayName: String,
    phone: String = ""
): String {
    val cleanEmail = email.trim()
    val existingUser = if (cleanEmail.isNotBlank()) firebaseRepo.getUser(cleanEmail) else null

    if (existingUser != null && existingUser.isBanned) {
        throw IllegalStateException("Your account has been banned, please contact our customer service")
    }

    val targetUsername = if (existingUser != null && existingUser.username.isNotBlank()) {
        existingUser.username
    } else {
        if (cleanEmail.contains("@")) cleanEmail.substringBefore("@") else displayName.trim().lowercase(Locale.ROOT)
    }

    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: existingUser?.uid ?: ""
    val userToSave = existingUser?.copy(
        uid = if (existingUser.uid.isBlank()) currentUserUid else existingUser.uid,
        email = if (existingUser.email.isBlank()) cleanEmail else existingUser.email,
        name = if (existingUser.name.isBlank()) displayName else existingUser.name,
        phone = if (existingUser.phone.isBlank() && phone.isNotBlank()) phone else existingUser.phone
    ) ?: User(
        uid = currentUserUid,
        username = targetUsername,
        email = cleanEmail,
        phone = phone,
        name = displayName
    )

    firebaseRepo.saveUser(userToSave)
    return targetUsername
}
