package com.example.appproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.app.DatePickerDialog
import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.math.absoluteValue
import androidx.compose.ui.util.lerp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Festival
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.SafetyCheck
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.brands.Facebook
import compose.icons.fontawesomeicons.brands.Instagram
import compose.icons.fontawesomeicons.brands.Tiktok

// Shared palette based on the Guest Stories section.
private val GuestNavy = Color(0xFF063763)
private val GuestCyan = Color(0xFF00A9C5)
private val GuestGold = Color(0xFFFFA51E)


//Color Scheme For The App
private val GuestColorScheme = lightColorScheme(
    primary = GuestNavy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8EFF5),
    onPrimaryContainer = GuestNavy,
    secondary = GuestCyan,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD8F4F8),
    onSecondaryContainer = Color(0xFF003640),
    tertiary = GuestGold,
    onTertiary = Color(0xFF3A2700),
    tertiaryContainer = Color(0xFFFFE4B5),
    onTertiaryContainer = Color(0xFF402B00),
    background = Color(0xFFF8FBFD),
    onBackground = Color(0xFF132B3A),
    surface = Color.White,
    onSurface = Color(0xFF132B3A),
    surfaceVariant = Color(0xFFE0EEF2),
    onSurfaceVariant = Color(0xFF40535C),
    outline = Color(0xFF70838B),
    error = Color(0xFFB3261E),
    onError = Color.White
)



@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = GuestColorScheme, content = content)
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Firebase is initialized in TravelApplication
        
        // Enable Edge-to-Edge support for modern Android look
        enableEdgeToEdge()
        setContent {
            AppTheme {
                AppNavigationDrawer()
            }
        }
    }
}

//Set up the language change between Malay and English
object LanguageManager {
    fun setLanguage(code: String) {
        val local = Locale(code)
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(local))
    }
}



data class LanguageOption(
    val code: String,
    val displayName: String,
    val nativeName: String
)

data class TripPromo(
    val name: String,
    val description: String,
    val price: String,
    val imageRes: Int
)

private data class GuestStory(
    val quote: String,
    val guestName: String,
    val country: String
)

data class Partner(
    val name: String,
    val description: String,
    val imageRes: Int,
    val highlights: String,
    val websiteUrl: String = "",
    val contactUrl: String = ""
)

data class ValueProp(
    val title: String,
    val description: String,
    val icon: ImageVector = Icons.Default.Info
)

data class CoreValue(
    val title: String,
    val description: String
)

data class ManagementMember(
    val name: String,
    val role: String
)

private const val ADMIN_USERNAME = "Admin123"
private const val ADMIN_PASSWORD = "Admin123"

private val UsernamePattern = Regex("^[A-Za-z0-9_]{3,20}$")
private val PasswordPattern = Regex("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")
private val PhonePattern = Regex("^\\+?[0-9][0-9 ()-]{6,18}[0-9]$")

private fun isValidEmail(email: String): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

private fun isValidPhone(phone: String): Boolean {
    val digits = phone.filter(Char::isDigit)
    return digits.length in 8..15 && PhonePattern.matches(phone)
}

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey val bookingId: String = "",
    val customerUsername: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val destination: String = "",
    val bookingDate: String = "",
    val status: String = "",
    val paymentStatus: String = "",
    val finalPrice: String = "",
    val basePrice: String = "",
    val discountAmount: String = "",
    val voucherUsed: String = ""
)

data class Feature(
    val title: String,
    val description: String,
    val icon: ImageVector = Icons.Default.Info
)

@Composable
fun SplashWithCircularLogo(
    onSplashFinished: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    // Animated entrance scale (zoom-in effect)
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "LogoScale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Circular Logo Icon
            Image(
                painter = painterResource(id = R.drawable.zti_logo),
                contentDescription = "PT Zuhanto Travel Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(140.dp)
                    .scale(logoScale)
                    .clip(CircleShape)
                    .border(2.dp, Color.LightGray.copy(alpha = 0.5f), CircleShape)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.company_name_zuhanto),
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = stringResource(R.string.company_name_indonesia),
                    color = Color.Red,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationDrawer() {
    val context = LocalContext.current
    val firebaseRepo = remember { FirebaseRepository() }
    val sessionPreferences = remember {
        context.getSharedPreferences("user_session", android.content.Context.MODE_PRIVATE)
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showSplash by remember { mutableStateOf(true) }

    var loggedInUser by rememberSaveable { mutableStateOf<String?>(null) }
    val isAdminLoggedIn = loggedInUser == ADMIN_USERNAME

    LaunchedEffect(Unit) {
        val savedUsername = sessionPreferences.getString("signed_in_username", null)
        if (savedUsername != null) {
            val restoredUsername = if (savedUsername == ADMIN_USERNAME) {
                ADMIN_USERNAME
            } else {
                try {
                    firebaseRepo.getUser(savedUsername)
                        ?.takeUnless { it.isBanned }
                        ?.username
                } catch (_: Exception) {
                    null
                }
            }

            if (restoredUsername != null) {
                loggedInUser = restoredUsername
            } else {
                sessionPreferences.edit().remove("signed_in_username").apply()
            }
        }
    }

    if (showSplash) {
        SplashWithCircularLogo(onSplashFinished = { showSplash = false })
    } else {
        ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(260.dp)) {
                Text(
                    text = stringResource(R.string.menu),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                HorizontalDivider()

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = {
                        Text(
                            when {
                                isAdminLoggedIn -> "Admin"
                                loggedInUser != null -> stringResource(R.string.dashboard)
                                else -> stringResource(R.string.dashboard)
                            }
                        )
                    },
                    selected = if (loggedInUser != null) {
                        if (isAdminLoggedIn) currentRoute == "admin"
                        else currentRoute?.startsWith("dashboard") == true
                    } else {
                        currentRoute == "home"
                    },
                    onClick = {
                        scope.launch { drawerState.close() }
                        if (loggedInUser != null) {
                            val targetRoute = if (isAdminLoggedIn) "admin" else "dashboard/$loggedInUser"
                            if (currentRoute != targetRoute) {
                                navController.navigate(targetRoute) {
                                    launchSingleTop = true
                                }
                            }
                        } else {
                            if (currentRoute != "dashboard") {
                                navController.navigate("dashboard") {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }

                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                if (loggedInUser != null && !isAdminLoggedIn) {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Book, contentDescription = null) },
                        label = { Text(stringResource(R.string.my_bookings)) },
                        selected = currentRoute == "bookings/{username}",
                        onClick = {
                            navController.navigate("bookings/$loggedInUser") {
                                launchSingleTop = true
                            }
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Place, contentDescription = null) }, // Using Info as placeholder
                        label = { Text(stringResource(R.string.promo_trip)) },
                        selected = currentRoute == "promotrip",
                        onClick = {
                            navController.navigate("promotrip") {
                                launchSingleTop = true
                            }
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Star, contentDescription = null) },
                        label = { Text(stringResource(R.string.available_vouchers)) },
                        selected = currentRoute == "vouchers",
                        onClick = {
                            navController.navigate("vouchers") {
                                launchSingleTop = true
                            }
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text(stringResource(R.string.profile)) },
                        selected = currentRoute == "profile/$loggedInUser",
                        onClick = {
                            navController.navigate("profile/$loggedInUser") {
                                launchSingleTop = true
                            }
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                }

                if (isAdminLoggedIn) {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text(stringResource(R.string.user_account)) },
                        selected = currentRoute == "admin-users",
                        onClick = {
                            navController.navigate("admin-users") {
                                launchSingleTop = true
                            }
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Handshake, contentDescription = null) },
                    label = { Text(stringResource(R.string.partner)) },
                    selected = currentRoute == "partner",
                    onClick = {
                        navController.navigate("partner") {
                            launchSingleTop = true
                        }
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text(stringResource(R.string.settings)) },
                    selected = currentRoute == "settings",
                    onClick = {
                        navController.navigate("settings") {
                            launchSingleTop = true
                            restoreState = true
                        }
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text(stringResource(R.string.about)) },
                    selected = currentRoute == "about",
                    onClick = {
                        navController.navigate("about") {
                            launchSingleTop = true
                            restoreState = true
                        }
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                if (loggedInUser != null) {
                    Spacer(modifier = Modifier.weight(1f))
                    HorizontalDivider()

                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        label = {
                            Text(
                                stringResource(R.string.logout),
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            loggedInUser = null
                            sessionPreferences.edit().remove("signed_in_username").apply()
                            navController.navigate("dashboard") {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Circular Top Logo Icon
                        Image(
                            painter = painterResource(id = R.drawable.zti_logo),
                            contentDescription = "PT Zuhanto Travel Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                        Column {
                            Text(
                                text = stringResource(R.string.company_name_zuhanto),
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(R.string.company_name_indonesia),
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                        }
                    }
                    },
                    actions = {
                        if (loggedInUser == null) {
                            Button(
                                onClick = {
                                    navController.navigate("home") {
                                        launchSingleTop = true
                                    }
                                },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(stringResource(R.string.login))
                            }
                        }
                    },
                    //Menu Sidebar Navigation Icon
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(R.string.open_drawer)
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = when {
                    isAdminLoggedIn -> "admin"
                    loggedInUser != null -> "dashboard/$loggedInUser"
                    else -> "dashboard"
                },
                modifier = Modifier.padding(innerPadding),

                //Animated transitions for navigation
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(400))
                },
                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(400))
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(400))
                }
            ) {
                composable("home") {
                    LoginScreen(
                        firebaseRepo = firebaseRepo,
                        onLoginSuccess = { user ->
                            loggedInUser = user
                            val destination = if (user == ADMIN_USERNAME) "admin" else "dashboard/$user"
                            navController.navigate(destination) {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        onSignUpClick = {
                            navController.navigate("register")
                        },
                        onForgotPasswordClick = {
                            navController.navigate("forgot-password")
                        }
                    )
                }
                composable("dashboard/{username}") { backStackEntry ->
                    val username = backStackEntry.arguments?.getString("username") ?: ""
                    DashboardHomeScreen(
                        username = username,
                        onBookNow = {
                            navController.navigate("promotrip") {
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable("dashboard") {
                    DashboardHomeScreen(
                        username = "Traveler",
                        onBookNow = {
                            navController.navigate("home") {
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable("admin") {
                    AdminScreen(firebaseRepo = firebaseRepo)
                }
                composable("admin-users") {
                    if (isAdminLoggedIn) {
                        AdminUsersScreen(firebaseRepo = firebaseRepo)
                    } else {
                        LoginScreen(
                            firebaseRepo = firebaseRepo,
                            onLoginSuccess = { user ->
                                loggedInUser = user
                                val destination =
                                    if (user == ADMIN_USERNAME) "admin" else "dashboard/$user"
                                navController.navigate(destination) {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onSignUpClick = { navController.navigate("register") },
                            onForgotPasswordClick = { navController.navigate("forgot-password") }
                        )
                    }
                }
                composable("promotrip") {
                    PromoTripScreen(
                        username = loggedInUser ?: "",
                        firebaseRepo = firebaseRepo
                    )
                }
                composable("bookings/{username}") { backStackEntry ->
                    val username = backStackEntry.arguments?.getString("username") ?: ""
                    if (loggedInUser == null || loggedInUser != username || isAdminLoggedIn) {
                        LoginScreen(
                            firebaseRepo = firebaseRepo,
                            onLoginSuccess = { user ->
                                loggedInUser = user
                                val destination =
                                    if (user == ADMIN_USERNAME) "admin" else "dashboard/$user"
                                navController.navigate(destination) {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onSignUpClick = {
                                navController.navigate("register")
                            },
                            onForgotPasswordClick = {
                                navController.navigate("forgot-password")
                            }
                        )
                    } else {
                        MyBookingsScreen(
                            username = username,
                            firebaseRepo = firebaseRepo,
                            onViewReceipt = { bId ->
                                navController.navigate("receipt/$bId")
                            }
                        )
                    }
                }
                composable("receipt/{bookingId}") { backStackEntry ->
                    val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
                    ReceiptScreen(
                        bookingId = bookingId,
                        firebaseRepo = firebaseRepo,
                        loggedInUser = loggedInUser,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("vouchers") {
                    UserVouchersScreen(
                        firebaseRepo = firebaseRepo,
                        username = loggedInUser ?: ""
                    )
                }
                composable("partner") {
                    PartnerScreen()
                }
                composable("profile/{username}") { backStackEntry ->
                    val usernameArg = backStackEntry.arguments?.getString("username") ?: ""
                    ProfileScreen(
                        username = usernameArg,
                        firebaseRepo = firebaseRepo,
                        onProfileUpdated = { newUsername ->
                            if (newUsername != loggedInUser) {
                                loggedInUser = newUsername
                                navController.navigate("profile/$newUsername") {
                                    popUpTo("profile/$usernameArg") { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                }
                composable("settings") {
                    SettingsScreen()
                }
                composable("about") {
                    AboutUsScreen()
                }
                composable("register") {
                    RegisterScreen(
                        firebaseRepo = firebaseRepo,
                        onRegisterSuccess = {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        onBackToLogin = {
                            navController.popBackStack()
                        }
                    )
                }
                composable("forgot-password") {
                    ForgotPasswordScreen(
                        firebaseRepo = firebaseRepo,
                        onBackToLogin = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

}


@Composable
fun LoginScreen(
    firebaseRepo: FirebaseRepository,
    onLoginSuccess: (String) -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var staySignedIn by rememberSaveable { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(modifier = Modifier.fillMaxSize()) {
        // ... (rest of the UI remains the same)
        // I'll only replace the onClick logic for the login button
        // Background Image with a slight overlay for better text readability
        Image(
            painter = painterResource(id = R.drawable.backgroundmountainlogin),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Dark overlay to make the card pop
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
            // App Title or Logo Placeholder
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
                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            errorMessage = ""
                        },
                        label = { Text(stringResource(R.string.username)) },
                        placeholder = { Text(stringResource(R.string.enter_username)) },
                        leadingIcon = { 
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) 
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        isError = errorMessage == context.getString(R.string.error_empty_username) ||
                                errorMessage == context.getString(R.string.error_invalid_credentials)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = ""
                        },
                        label = { Text(stringResource(R.string.password)) },
                        placeholder = { Text(stringResource(R.string.enter_password)) },
                        leadingIcon = { 
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) 
                        },
                        trailingIcon = {
                            val icon = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                            val description = if (isPasswordVisible) stringResource(R.string.hide_password) else stringResource(R.string.show_password)
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(imageVector = icon, contentDescription = description)
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = errorMessage == context.getString(R.string.error_empty_password) ||
                                errorMessage == context.getString(R.string.error_invalid_credentials)
                    )

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

                    TextButton(onClick = onForgotPasswordClick) {
                        Text(stringResource(R.string.forgot_password_q))
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    Button(
                        onClick = {
                            if (username.isBlank()) {
                                errorMessage = context.getString(R.string.error_empty_username)
                            } else if (password.isBlank()) {
                                errorMessage = context.getString(R.string.error_empty_password)
                            } else {
                                scope.launch {
                                    val isAdminLogin =
                                        username == ADMIN_USERNAME && password == ADMIN_PASSWORD
                                    
                                    if (isAdminLogin) {
                                        val preferences = context.getSharedPreferences(
                                            "user_session",
                                            android.content.Context.MODE_PRIVATE
                                        )
                                        if (staySignedIn) {
                                            preferences.edit()
                                                .putString("signed_in_username", ADMIN_USERNAME)
                                                .apply()
                                        } else {
                                            preferences.edit().remove("signed_in_username").apply()
                                        }
                                        onLoginSuccess(ADMIN_USERNAME)
                                        return@launch
                                    }

                                    try {
                                        val searchInput = username.trim().lowercase(Locale.ROOT)
                                        var storedUser = firebaseRepo.getUser(searchInput)
                                        
                                        // If not found by username, let's look through all users to see if they typed their email
                                        if (storedUser == null) {
                                            val allUsers = firebaseRepo.getAllUsers()
                                            storedUser = allUsers.find { it.email.equals(username.trim(), ignoreCase = true) }
                                        }

                                        if (storedUser == null) {
                                            errorMessage = context.getString(R.string.error_invalid_credentials)
                                            return@launch
                                        }
                                        
                                        if (storedUser.isBanned) {
                                            errorMessage = context.getString(R.string.error_account_banned)
                                            return@launch
                                        }

                                        auth.signInWithEmailAndPassword(storedUser.email, password).await()
                                        
                                        val signedInUsername = storedUser.username
                                        val preferences = context.getSharedPreferences(
                                            "user_session",
                                            Context.MODE_PRIVATE
                                        )
                                        if (staySignedIn) {
                                            preferences.edit()
                                                .putString("signed_in_username", signedInUsername)
                                                .apply()
                                        } else {
                                            preferences.edit().remove("signed_in_username").apply()
                                        }
                                        onLoginSuccess(signedInUsername)
                                    } catch (e: Exception) {
                                        errorMessage = context.getString(R.string.error_invalid_credentials)
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.login),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
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


@Preview(showBackground = true)
@Composable
fun AboutUsScreenPreview() {
    AppTheme {
        AboutUsScreen()
    }
}


@Composable
fun AboutUsScreen() {
    val context = LocalContext.current
    val whatsappUrl = "https://wa.me/qr/TV6WKVO3MT4CK1"
    
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val coreValues = listOf(
        CoreValue(stringResource(R.string.integrity), stringResource(R.string.integrity_desc)),
        CoreValue(stringResource(R.string.customer_satisfaction), stringResource(R.string.customer_satisfaction_desc)),
        CoreValue(stringResource(R.string.innovation), stringResource(R.string.innovation_desc)),
        CoreValue(stringResource(R.string.professionalism), stringResource(R.string.professionalism_desc)),
        CoreValue(stringResource(R.string.sustainable_growth), stringResource(R.string.sustainable_growth_desc))
    )

    val managementTeam = listOf(
        ManagementMember("Dr. Moch. Zuhan", stringResource(R.string.role_chairman)),
        ManagementMember("Sumanto", stringResource(R.string.role_ceo)),
        ManagementMember("Hengky Melkior Sopaheluwaken", stringResource(R.string.role_cmo)),
        ManagementMember("Muhammad Sholihuddin", stringResource(R.string.role_coo)),
        ManagementMember("Riski Diah Meylani", stringResource(R.string.role_marketing_exec)),
        ManagementMember("Ria Ayu Nova Aprillia", stringResource(R.string.role_admin_officer))
    )

    val whyChooseUs = listOf(
        Feature(
            stringResource(R.string.prof_expertise_experience),
            stringResource(R.string.prof_expertise_experience_desc),
            Icons.Default.WorkspacePremium
        ),
        Feature(
            stringResource(R.string.pricing_transparency),
            stringResource(R.string.pricing_transparency_desc),
            Icons.Default.AttachMoney
        ),
        Feature(
            stringResource(R.string.customer_support_24_7),
            stringResource(R.string.customer_support_desc),
            Icons.Default.SupportAgent
        ),
        Feature(
            stringResource(R.string.safety_commitment),
            stringResource(R.string.safety_commitment_desc),
            Icons.Default.Security
        ),
        Feature(
            stringResource(R.string.customized_solutions),
            stringResource(R.string.customized_solutions_desc),
            Icons.Default.Tune
        ),
        Feature(
            stringResource(R.string.trusted_by_corporate),
            stringResource(R.string.trusted_by_corporate_desc),
            Icons.Default.Business
        )
    )

    val serviceGuarantee = listOf(
        Feature(stringResource(R.string.quality_assurance), stringResource(R.string.quality_assurance_desc), Icons.Default.CheckCircle),
        Feature(stringResource(R.string.full_transparency), stringResource(R.string.full_transparency_desc), Icons.Default.CheckCircle),
        Feature(stringResource(R.string.prof_service_standard), stringResource(R.string.prof_service_standard_desc), Icons.Default.CheckCircle)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))
                    context.startActivity(intent)
                },
                containerColor = Color(0xFF25D366),
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Phone, contentDescription = null) },
                text = { Text(text = stringResource(R.string.contact_us), fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                initialOffsetY = { 40 },
                animationSpec = tween(600, easing = EaseOutQuad)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .verticalScroll(rememberScrollState())
            ) {
                // Hero Section
                Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.backgroudmountain),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.7f
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.5f),
                                    Color.Black.copy(alpha = 0.9f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Card(
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.size(100.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.zti_logo),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.about_company),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = stringResource(R.string.company_slogan),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.6f),
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        ),
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Description Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.company_description_short),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.company_description_full),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Justify
                )
            }

            // Mission & Vision Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MissionVisionCard(
                    title = stringResource(R.string.our_mission),
                    description = stringResource(R.string.mission_description),
                    modifier = Modifier.weight(1f)
                )
                MissionVisionCard(
                    title = stringResource(R.string.our_vision),
                    description = stringResource(R.string.vision_description),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Management Team Section
            SectionHeader(title = stringResource(R.string.management_team), modifier = Modifier.padding(horizontal = 24.dp))
            Spacer(modifier = Modifier.height(16.dp))
            managementTeam.forEach { member ->
                ManagementMemberItem(member = member)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Core Values Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(vertical = 40.dp, horizontal = 24.dp)
            ) {
                SectionHeader(title = stringResource(R.string.core_values))
                Spacer(modifier = Modifier.height(24.dp))
                coreValues.forEach { value ->
                    CoreValueItem(value = value)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Why Choose Us Section
            SectionHeader(title = stringResource(R.string.why_choose_us), modifier = Modifier.padding(horizontal = 24.dp))
            Spacer(modifier = Modifier.height(16.dp))
            whyChooseUs.forEach { feature ->
                FeatureItem(feature = feature)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Service Guarantee Section
            SectionHeader(title = stringResource(R.string.service_guarantee), modifier = Modifier.padding(horizontal = 24.dp))
            Spacer(modifier = Modifier.height(16.dp))
            serviceGuarantee.forEach { feature ->
                FeatureItem(feature = feature)
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Spacer(modifier = Modifier.height(100.dp)) // Extra space for FAB
        }
    }
}

}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = horizontalAlignment
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.secondary)
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}


@Composable
fun ManagementMemberItem(member: ManagementMember) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = member.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = member.role,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


@Composable
fun FeatureItem(feature: Feature) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = feature.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = feature.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
fun MissionVisionCard(title: String, description: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.heightIn(min = 180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (title.contains("Mission")) Icons.Default.TrackChanges else Icons.Default.Visibility,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
fun CoreValueItem(value: CoreValue) {
    val icon = when (value.title) {
        stringResource(R.string.integrity) -> Icons.Default.VerifiedUser
        stringResource(R.string.customer_satisfaction) -> Icons.Default.SentimentVerySatisfied
        stringResource(R.string.innovation) -> Icons.Default.Lightbulb
        stringResource(R.string.professionalism) -> Icons.Default.WorkspacePremium
        stringResource(R.string.sustainable_growth) -> Icons.Default.TrendingUp
        else -> Icons.Default.Info
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = value.title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = value.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = value.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.change_language),
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        LanguageListBox(modifier = Modifier.padding(horizontal = 16.dp))
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
private fun ForgotPasswordScreen(
    firebaseRepo: FirebaseRepository,
    onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val auth = remember { FirebaseAuth.getInstance() }
    
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.backgroundmountainlogin),
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
                    text = stringResource(R.string.reset_password),
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
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            stringResource(R.string.reset_email_text),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it; message = "" },
                            label = { Text(stringResource(R.string.email_address)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                if (email.isBlank() || !isValidEmail(email)) {
                                    message = context.getString(R.string.invalid_email)
                                    return@Button
                                }
                                scope.launch {
                                    try {
                                        val allUsers = firebaseRepo.getAllUsers()
                                        val emailExists = allUsers.any { it.email.equals(email.trim(), ignoreCase = true) }
                                        
                                        if (!emailExists) {
                                            message = context.getString(R.string.invalid_email) // Or a specific error like "Email not found in our database"
                                            return@launch
                                        }

                                        auth.sendPasswordResetEmail(email.trim()).await()
                                        message = context.getString(R.string.reset_email_sent)
                                    } catch (e: Exception) {
                                        message = e.message ?: context.getString(R.string.reset_email_failed)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) { Text(stringResource(R.string.send_reset_email)) }

                        if (message.isNotEmpty()) {
                            val isSuccess = message == context.getString(R.string.reset_email_sent)
                            Text(
                                message,
                                color = if (isSuccess) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        TextButton(onClick = onBackToLogin) {
                            Text(stringResource(R.string.back_to_login), fontWeight = FontWeight.Bold)
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
    onRegisterSuccess: () -> Unit = {},
    onBackToLogin: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showRegistrationSuccess by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.backgroundmountainlogin),
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
                        value = username,
                        onValueChange = {
                            username = it
                            errorMessage = ""
                        },
                        label = { Text(stringResource(R.string.username)) },
                        placeholder = { Text(stringResource(R.string.enter_username)) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        isError = errorMessage == context.getString(R.string.error_username_exists)
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
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            errorMessage = ""
                        },
                        label = { Text(stringResource(R.string.phone)) },
                        placeholder = { Text(stringResource(R.string.enter_phone)) },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = ""
                        },
                        label = { Text(stringResource(R.string.password)) },
                        placeholder = { Text(stringResource(R.string.enter_password)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            val icon = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                            val description = if (isPasswordVisible) stringResource(R.string.hide_password) else stringResource(R.string.show_password)
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(imageVector = icon, contentDescription = description)
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = errorMessage == context.getString(R.string.error_passwords_mismatch)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            errorMessage = ""
                        },
                        label = { Text(stringResource(R.string.confirm_password)) },
                        placeholder = { Text(stringResource(R.string.enter_confirm_password)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            val icon = if (isConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                            val description = if (isConfirmPasswordVisible) stringResource(R.string.hide_password) else stringResource(R.string.show_password)
                            IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                                Icon(imageVector = icon, contentDescription = description)
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = errorMessage == context.getString(R.string.error_passwords_mismatch)
                    )

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
                            if (username.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                                errorMessage = context.getString(R.string.error_fill_all_fields)
                            } else if (!UsernamePattern.matches(username)) {
                                errorMessage = context.getString(R.string.error_invalid_username)
                            } else if (username.equals(ADMIN_USERNAME, ignoreCase = true)) {
                                errorMessage = context.getString(R.string.error_username_exists)
                            } else if (!isValidEmail(email.trim())) {
                                errorMessage = context.getString(R.string.invalid_email)
                            } else if (!isValidPhone(phone.trim())) {
                                errorMessage = context.getString(R.string.error_invalid_phone)
                            } else if (!PasswordPattern.matches(password)) {
                                errorMessage = context.getString(R.string.error_weak_password)
                            } else if (password != confirmPassword) {
                                errorMessage = context.getString(R.string.error_passwords_mismatch)
                            } else {
                                scope.launch {
                                    val normalizedUsername = username.trim().lowercase(Locale.ROOT)
                                    try {
                                        val existingUser = firebaseRepo.getUser(normalizedUsername)
                                        if (existingUser != null) {
                                            errorMessage = context.getString(R.string.error_username_exists)
                                        } else {
                                            // Create user in Firebase Auth
                                            auth.createUserWithEmailAndPassword(email.trim(), password).await()
                                            
                                            val newUser = User(
                                                username = normalizedUsername,
                                                email = email.trim(),
                                                phone = phone.trim(),
                                                password = "" // We don't store plain/hashed password in Firestore anymore, handled by Firebase Auth
                                            )
                                            firebaseRepo.saveUser(newUser)
                                            showRegistrationSuccess = true
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = e.message ?: context.getString(R.string.registration_failed)
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
                            text = stringResource(R.string.register),
                            style = MaterialTheme.typography.titleMedium
                        )
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

        if (showRegistrationSuccess) {
            AlertDialog(
                onDismissRequest = {
                    showRegistrationSuccess = false
                    onRegisterSuccess()
                },
                title = { Text(stringResource(R.string.registration_successful)) },
                text = {
                    Text(stringResource(R.string.account_created_message))
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showRegistrationSuccess = false
                            onRegisterSuccess()
                        }
                    ) {
                        Text(stringResource(R.string.continue_to_login))
                    }
                }
            )
        }
    }
}

}


@Preview(showBackground = true)
@Composable
fun DashboardHomeScreenPreview() {
    AppTheme {
        DashboardHomeScreen(username = "Traveler")
    }
}


@Composable
fun DashboardHomeScreen(
    username: String,
    onBookNow: () -> Unit = {}
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val columnCount = when (adaptiveInfo.windowSizeClass.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> 1
        WindowWidthSizeClass.MEDIUM -> 2
        WindowWidthSizeClass.EXPANDED -> 3
        else -> 1
    }
    
    val scrollState = rememberScrollState()
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(1000)) + slideInVertically(
                initialOffsetY = { 40 },
                animationSpec = tween(800, easing = EaseOutQuad)
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Immersive Hero Header
                Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (columnCount > 1) 400.dp else 280.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.background_dashboard),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.85f)
                            ),
                            startY = 0f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (columnCount > 1) 48.dp else 24.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = stringResource(R.string.welcome_user, username),
                    style = if (columnCount > 1) MaterialTheme.typography.displayMedium else MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = stringResource(R.string.company_slogan),
                    style = (if (columnCount > 1) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.bodyLarge).copy(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.6f),
                            offset = Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    ),
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Categories Row - Adapt spacing and size
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (columnCount > 1) 32.dp else 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val iconSize = if (columnCount > 1) 80.dp else 56.dp
            CategoryItem(icon = Icons.Default.Hiking, label = stringResource(R.string.category_adventure), iconSize = iconSize)
            CategoryItem(icon = Icons.Default.BeachAccess, label = stringResource(R.string.category_beaches), iconSize = iconSize)
            CategoryItem(icon = Icons.Default.Festival, label = stringResource(R.string.category_cultural), iconSize = iconSize)
            CategoryItem(icon = Icons.Default.Forest, label = stringResource(R.string.category_nature), iconSize = iconSize)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Explore Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (columnCount > 1) 48.dp else 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            SectionHeader(title = stringResource(R.string.explore_indonesia))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Hero Carousel - Wider on tablets
        val images: List<Int> = listOf(
            R.drawable.borobudur_temple_explore,
            R.drawable.mountbromo,
            R.drawable.bali_explore,
            R.drawable.ijen_crater_explore,
            R.drawable.labuan_bajo_explore,
            R.drawable.raja_ampat_explore
        )
        val autoScrollDelayMs: Long = 4000L
        val pagerState = rememberPagerState(pageCount = { images.size })

        LaunchedEffect(pagerState) {
            while (true) {
                delay(autoScrollDelayMs)
                if (!pagerState.isScrollInProgress) {
                    val nextPage = (pagerState.currentPage + 1) % images.size
                    pagerState.animateScrollToPage(nextPage)
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (columnCount > 1) 400.dp else 220.dp),
            contentPadding = PaddingValues(horizontal = if (columnCount > 1) 120.dp else 16.dp),
            pageSpacing = 16.dp
        ) { page ->
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val pageOffset = (
                            (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                        ).absoluteValue
                        alpha = lerp(
                            start = 0f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                        scaleX = lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                        scaleY = scaleX
                    },
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = images[page]),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Pager Indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(images.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Introduction Section (Re-styled for tablet)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (columnCount > 1) 48.dp else 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ) {
            if (columnCount > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(36.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.redefining_travel),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(R.string.affordable_price),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Spacer(modifier = Modifier.width(48.dp))
                    Text(
                        text = stringResource(R.string.company_intro),
                        modifier = Modifier.weight(1.8f),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 28.sp
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = stringResource(R.string.redefining_travel),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.affordable_price),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.company_intro),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        val featuredTrips: List<Triple<String, Int, Int>> = listOf(
            Triple(stringResource(R.string.mount_bromo), R.drawable.mountbromo, R.string.mount_bromo_price),
            Triple(stringResource(R.string.bali), R.drawable.bali, R.string.bali_price),
            Triple(stringResource(R.string.ijen_crater), R.drawable.ijencrater, R.string.ijen_crater_price),
            Triple(stringResource(R.string.labuan_bajo), R.drawable.labuanbajo, R.string.labuan_bajo_price),
            Triple(stringResource(R.string.raja_ampat), R.drawable.rajaampat, R.string.raja_ampat_price)
        )

        SectionHeader(
            title = stringResource(R.string.featured_destinations),
            modifier = Modifier.padding(horizontal = if (columnCount > 1) 48.dp else 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (columnCount > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                featuredTrips.forEach { trip ->
                    val (name, imageRes, priceRes) = trip
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(300.dp),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        FeaturedTripContent(name, imageRes, priceRes, onBookNow)
                    }
                }
            }
        } else {
            val featuredPagerState = rememberPagerState(pageCount = { featuredTrips.size })
            LaunchedEffect(featuredPagerState) {
                while (true) {
                    delay(4500L)
                    if (!featuredPagerState.isScrollInProgress) {
                        featuredPagerState.animateScrollToPage(
                            (featuredPagerState.currentPage + 1) % featuredTrips.size
                        )
                    }
                }
            }

            HorizontalPager(
                state = featuredPagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(horizontal = 16.dp)
            ) { page ->
                val (name, imageRes, priceRes) = featuredTrips[page]
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val pageOffset = (
                                (featuredPagerState.currentPage - page) + featuredPagerState.currentPageOffsetFraction
                            ).absoluteValue
                            alpha = lerp(
                                start = 0f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                            scaleX = lerp(
                                start = 0.85f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                            scaleY = scaleX
                        },
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    FeaturedTripContent(name, imageRes, priceRes, onBookNow)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(featuredTrips.size) { index ->
                    val color = if (featuredPagerState.currentPage == index) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    }
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Leadership Section - Bigger on tablet
        SectionHeader(
            title = stringResource(R.string.leadership_team),
            modifier = Modifier.padding(horizontal = if (columnCount > 1) 48.dp else 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        val leadershipImages = listOf(
            R.drawable.leadership_profile,
            R.drawable.leadership_profile2
        )
        val leadershipPagerState = rememberPagerState(pageCount = { leadershipImages.size })

        LaunchedEffect(leadershipPagerState) {
            while (leadershipImages.size > 1) {
                delay(4000L)
                if (!leadershipPagerState.isScrollInProgress) {
                    leadershipPagerState.animateScrollToPage(
                        (leadershipPagerState.currentPage + 1) % leadershipImages.size
                    )
                }
            }
        }
        if (columnCount > 1) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
            ) {
                HorizontalPager(
                    state = leadershipPagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 48.dp),
                    contentPadding = PaddingValues(horizontal = 80.dp),
                    pageSpacing = 16.dp
                ) { page ->
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                val pageOffset = (
                                    (leadershipPagerState.currentPage - page) + leadershipPagerState.currentPageOffsetFraction
                                ).absoluteValue
                                alpha = lerp(
                                    start = 0f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )
                                scaleX = lerp(
                                    start = 0.85f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )
                                scaleY = scaleX
                            },
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = leadershipImages[page]),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(leadershipImages.size) { index ->
                        val color = if (leadershipPagerState.currentPage == index) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.White.copy(alpha = 0.5f)
                        }
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }
            }
        } else {
            HorizontalPager(
                state = leadershipPagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(horizontal = 16.dp)
            ) { page ->
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val pageOffset = (
                                (leadershipPagerState.currentPage - page) + leadershipPagerState.currentPageOffsetFraction
                            ).absoluteValue
                            alpha = lerp(
                                start = 0f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                            scaleX = lerp(
                                start = 0.85f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                            scaleY = scaleX
                        },
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Image(
                        painter = painterResource(id = leadershipImages[page]),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(leadershipImages.size) { index ->
                    val color = if (leadershipPagerState.currentPage == index) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    }
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Private Services Section - Adaptive grid
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                .padding(vertical = 40.dp, horizontal = if (columnCount > 1) 48.dp else 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            SectionHeader(title = stringResource(R.string.our_private_services))
            
            Text(
                text = stringResource(R.string.private_services_desc),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            val services = listOf(
                Feature(
                    stringResource(R.string.bromo_tour),
                    stringResource(R.string.bromo_tour_desc),
                    Icons.Default.Hiking
                ),
                Feature(stringResource(R.string.pricing_transparency), stringResource(R.string.pricing_transparency_desc),
                    Icons.Default.AttachMoney),
                Feature(stringResource(R.string.customer_support_24_7), stringResource(R.string.customer_support_desc),
                    Icons.Default.SupportAgent),
                Feature(stringResource(R.string.safety_commitment), stringResource(R.string.safety_commitment_desc),
                    Icons.Default.SafetyCheck),
                Feature(stringResource(R.string.customized_solutions), stringResource(R.string.customized_solutions_desc),
                    Icons.Default.Settings),
                Feature(stringResource(R.string.trusted_by_corporate), stringResource(R.string.trusted_by_corporate_desc),
                    Icons.Default.Business)
            )

            val serviceCols = if (columnCount == 1) 2 else 3
            services.chunked(serviceCols).forEach { rowServices ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowServices.forEach { service ->
                        ServiceItem(
                            title = service.title,
                            description = service.description,
                            icon = service.icon,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill empty space if row is not full
                    repeat(serviceCols - rowServices.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (columnCount > 1) 48.dp else 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.Start
                ){
                    SectionHeader(title = stringResource(R.string.travel_galery))
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    val galleryImages = listOf(
                        R.drawable.traveler_gallery1,
                        R.drawable.traveler_gallery2,
                        R.drawable.traveler_gallery3,
                        R.drawable.traveler_gallery4,
                        R.drawable.traveler_gallery5,
                        R.drawable.traveler_gallery6
                    )

                    TravelGalleryCarousel(
                        images = galleryImages,
                        height = if (columnCount > 1) 500.dp else 220.dp,
                        contentScale = ContentScale.Crop,
                        horizontalPadding = if (columnCount > 1) 48.dp else 16.dp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))

        GuestStoriesCarousel(isWideLayout = columnCount > 1)
        Spacer(modifier = Modifier.height(40.dp))
        ContactAndFooter(isWideLayout = columnCount > 1)
    }
}

@Composable
private fun FeaturedTripContent(
    name: String,
    imageRes: Int,
    priceRes: Int,
    onBookNow: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.92f)
        ) {
            Text(
                text = stringResource(priceRes),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onBookNow) {
                Text(stringResource(R.string.book_now))
            }
        }
    }
}



@Composable
private fun TravelGalleryCarousel(
    images: List<Int>,
    height: Dp,
    contentScale: ContentScale,
    horizontalPadding: Dp
) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    LaunchedEffect(pagerState) {
        while (images.size > 1) {
            delay(4000L)
            if (!pagerState.isScrollInProgress) {
                pagerState.animateScrollToPage((pagerState.currentPage + 1) % images.size)
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            pageSpacing = 16.dp
        ) { page ->
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val pageOffset = (
                            (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                        ).absoluteValue
                        alpha = lerp(
                            start = 0f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                        scaleX = lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                        scaleY = scaleX
                    },
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = images[page]),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(images.size) { index ->
                val color = if (pagerState.currentPage == index) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                }
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}


@Composable
private fun GuestStoriesCarousel(isWideLayout: Boolean = false) {
    val stories = listOf(
        GuestStory(
            quote = (stringResource(R.string.johndoe_review)),
            guestName = (stringResource(R.string.johndoe_name)),
            country = (stringResource(R.string.johndoe_country))
        ),
        GuestStory(
            quote = (stringResource(R.string.emilysmith_review)),
            guestName = (stringResource(R.string.emilysmith_name)),
            country = (stringResource(R.string.emilysmith_country))
        ),
        GuestStory(
            quote = (stringResource(R.string.davidsarah_review)),
            guestName = (stringResource(R.string.davidsarah_name)),
            country = (stringResource(R.string.davidsarah_country))
        )
    )
    val pagerState = rememberPagerState(pageCount = { stories.size })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(4500L)
            if (!pagerState.isScrollInProgress) {
                pagerState.animateScrollToPage((pagerState.currentPage + 1) % stories.size)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        SectionHeader(title = stringResource(R.string.guest_stories))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isWideLayout) 400.dp else 360.dp),
            contentPadding = PaddingValues(horizontal = if (isWideLayout) 80.dp else 0.dp),
            pageSpacing = if (isWideLayout) 24.dp else 0.dp
        ) { page ->
            val story = stories[page]
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val pageOffset = (
                            (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                        ).absoluteValue
                        alpha = lerp(
                            start = 0f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                        scaleX = lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                        scaleY = scaleX
                    },
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Surface(
                        modifier = Modifier
                            .padding(top = 0.dp)
                            .size(88.dp),
                        shape = CircleShape,
                        color = Color(0xFF063763),
                        shadowElevation = 8.dp
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = null,
                            modifier = Modifier.padding(24.dp),
                            tint = Color(0xFFFFAD21)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 28.dp, vertical = 96.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row {
                                repeat(5) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFA51E)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(28.dp))
                            Text(
                                text = "\"${story.quote}\"",
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = FontFamily.Serif,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF063763)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = story.guestName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF063763)
                            )
                            Text(
                                text = story.country,
                                color = Color(0xFF00A9C5)
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(stories.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(10.dp)
                        .width(if (pagerState.currentPage == index) 60.dp else 10.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) Color(0xFF063763)
                            else Color(0xFFE1E5E9)
                        )
                )
            }
        }
    }
}


@Composable
private fun ContactAndFooter(isWideLayout: Boolean) {
    val navy = Color(0xFF063763)
    val accent = Color(0xFFFFA51E)
    val cyan = Color(0xFF00A9C5)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(navy)
            .padding(horizontal = if (isWideLayout) 64.dp else 24.dp, vertical = 48.dp)
    ) {
        if (isWideLayout) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ContactDetails(modifier = Modifier.weight(1f), accent = accent, cyan = cyan)
                ContactPhoto(modifier = Modifier.weight(1f))
            }
        } else {
            ContactDetails(accent = accent, cyan = cyan)
            Spacer(modifier = Modifier.height(28.dp))
            ContactPhoto()
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 48.dp, bottom = 28.dp),
            color = accent.copy(alpha = 0.8f)
        )

        if (isWideLayout) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                FooterBrand(modifier = Modifier.weight(1.3f), accent = accent, cyan = cyan)
                FooterContact(modifier = Modifier.weight(1.1f), accent = accent, cyan = cyan)
                FooterSocials(modifier = Modifier.weight(0.8f), accent = accent)
            }
        } else {
            FooterBrand(accent = accent, cyan = cyan)
            Spacer(modifier = Modifier.height(24.dp))
            FooterContact(accent = accent, cyan = cyan)
            Spacer(modifier = Modifier.height(24.dp))
            FooterSocials(accent = accent)
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 32.dp, bottom = 16.dp),
            color = Color.White.copy(alpha = 0.18f)
        )
        Text(
            text = stringResource(R.string.copyright_text),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.65f)
        )
    }
}


@Composable
private fun ContactDetails(modifier: Modifier = Modifier, accent: Color, cyan: Color) {
    Column(modifier = modifier) {
        Text(stringResource(R.string.start_your), style = MaterialTheme.typography.headlineSmall, fontFamily = FontFamily.Serif, color = Color.White)
        Text(stringResource(R.string.dream_journey  ), style = MaterialTheme.typography.headlineSmall, fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic, color = accent)
        Box(modifier = Modifier.padding(vertical = 12.dp).width(64.dp).height(2.dp).background(cyan))
        Text(stringResource(R.string.contact_desc), color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(20.dp))
        FooterLine(Icons.Default.Place, stringResource(R.string.head_office), stringResource(R.string.head_office_location), cyan)
        FooterLine(Icons.Default.Phone, stringResource(R.string.phone_transalate), "+62 813 3315 225", cyan)
        FooterLine(Icons.Default.Email, stringResource(R.string.email_transalate), "info@ztiindo.id", cyan)
        FooterLine(Icons.Default.Language, stringResource(R.string.website_transalate), "www.ztiindo.id", cyan)
    }
}


@Composable
private fun ContactPhoto(modifier: Modifier = Modifier) {
    Card(modifier = modifier.height(270.dp), shape = RoundedCornerShape(12.dp)) {
        Image(painter = painterResource(R.drawable.officeimage), contentDescription = "Zuhanto Travel office", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
    }
}


@Composable
private fun FooterBrand(modifier: Modifier = Modifier, accent: Color, cyan: Color) {
    Column(modifier = modifier) {
        Image(painter = painterResource(R.drawable.zti_logo), contentDescription = "Zuhanto Travel logo", modifier = Modifier.size(42.dp) .background(Color.White,CircleShape) .padding(6.dp),contentScale = ContentScale.Crop)
        Spacer(modifier = Modifier.height(10.dp))
        Text("PT ZUHANTO TRAVEL\nINDONESIA", fontWeight = FontWeight.Bold, color = accent, style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(stringResource(R.string.pt_zuhanto_desc), color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
    }
}


@Composable
private fun FooterContact(modifier: Modifier = Modifier, accent: Color, cyan: Color) {
    Column(modifier = modifier) {
        Text(stringResource(R.string.contact_us_transalate), color = accent, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        FooterLine(Icons.Default.Place, "", stringResource(R.string.place), cyan)
        FooterLine(Icons.Default.Phone, "", "+62 812 333 951–165", cyan)
        FooterLine(Icons.Default.Email, "", "info@ztiindo.id", cyan)
    }
}


@Composable
private fun FooterSocials(modifier: Modifier = Modifier, accent: Color) {
    val context = LocalContext.current
    Column(modifier = modifier) {
        Text(stringResource(R.string.follow_us), color = accent, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val socialLinks = listOf(
                Triple("Instagram", FontAwesomeIcons.Brands.Instagram, "https://www.instagram.com/zuhantotravelindo"),
                Triple("Facebook", FontAwesomeIcons.Brands.Facebook, "https://www.facebook.com/share/1MH9Q8RGc8/"),
                Triple("TikTok", FontAwesomeIcons.Brands.Tiktok, "https://www.tiktok.com/@zuhantotravelindo")
            )
            socialLinks.forEach { (name, icon, url) ->
                Surface(
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        },
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.12f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Open $name",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun FooterLine(icon: ImageVector, label: String, value: String, tint: Color) {
    Row(modifier = Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = tint)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            if (label.isNotEmpty()) Text(label, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
            Text(value, color = Color.White.copy(alpha = 0.75f), style = MaterialTheme.typography.bodySmall)
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CategoryItem(icon: ImageVector, label: String, iconSize: Dp = 56.dp) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { /* Action */ }
            )
            .motionEventSpy { event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> isPressed = true
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> isPressed = false
                }
            }
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(iconSize)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(iconSize / 2)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FeaturedDestinationItem(name: String, imageRes: Int, modifier: Modifier = Modifier) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    Card(
        modifier = modifier
            .height(150.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { /* Navigate */ }
            )
            .motionEventSpy { event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> isPressed = true
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> isPressed = false
                }
            },
        shape = RoundedCornerShape(20.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                            startY = 100f
                        )
                    )
            )
            Text(
                text = name,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ServiceItem(
    title: String,
    description: String,
    icon: ImageVector = Icons.Default.Info,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { /* Action */ }
            )
            .motionEventSpy { event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> isPressed = true
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> isPressed = false
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                lineHeight = 16.sp
            )
        }
    }
}


@Composable
fun PromoTripScreen(
    username: String,
    firebaseRepo: FirebaseRepository
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isExpanded = adaptiveInfo.windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT
    var selectedTrip by remember { mutableStateOf<TripPromo?>(null) }
    
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val trips = listOf(
// ...
        TripPromo(
            stringResource(R.string.mount_bromo),
            stringResource(R.string.mount_bromo_desc),
            stringResource(R.string.mount_bromo_price),
            R.drawable.mountbromo
        ),
        TripPromo(
            stringResource(R.string.borobudur_temple),
            stringResource(R.string.borobudur_temple_desc),
            stringResource(R.string.borobudur_temple_price),
            R.drawable.borobudurtemple
        ),
        TripPromo(
            stringResource(R.string.ijen_crater),
            stringResource(R.string.ijen_crater_desc),
            stringResource(R.string.ijen_crater_price),
            R.drawable.ijencrater
        ),
        TripPromo(
            stringResource(R.string.bali),
            stringResource(R.string.bali_desc),
            stringResource(R.string.bali_price),
            R.drawable.bali
        ),
        TripPromo(
            stringResource(R.string.labuan_bajo),
            stringResource(R.string.labuan_bajo_desc),
            stringResource(R.string.labuan_bajo_price),
            R.drawable.labuanbajo
        ),
        TripPromo(
            stringResource(R.string.raja_ampat),
            stringResource(R.string.raja_ampat_desc),
            stringResource(R.string.raja_ampat_price),
            R.drawable.rajaampat
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                initialOffsetY = { 40 },
                animationSpec = tween(600, easing = EaseOutQuad)
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
            Image(
                painter = painterResource(id = R.drawable.backgroudmountain),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.7f
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.promo_trip),
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(
            title = stringResource(R.string.indonesian_travel_icons),
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Text(
            text = stringResource(R.string.promo_trip_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        val columns = if (isExpanded) 2 else 1
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            trips.chunked(columns).forEach { rowTrips ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    rowTrips.forEach { trip ->
                        TripCard(
                            trip = trip,
                            modifier = Modifier.weight(1f),
                            onBook = { selectedTrip = trip }
                        )
                    }
                    if (rowTrips.size < columns) {
                        repeat(columns - rowTrips.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        selectedTrip?.let { trip ->
            BookingDialog(
                trip = trip,
                username = username,
                firebaseRepo = firebaseRepo,
                onDismiss = { selectedTrip = null }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

}
}

@Composable
private fun BookingDialog(
    trip: TripPromo,
    username: String,
    firebaseRepo: FirebaseRepository,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var bookingDate by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf(false) }
    
    var voucherCode by remember { mutableStateOf("") }
    var appliedDiscount by remember { mutableStateOf(0) }
    var voucherStatusMessage by remember { mutableStateOf("") }

    // Parse base price integer digits out of string like "IDR 1,850,000"
    val basePriceInt = remember(trip.price) {
        trip.price.filter { it.isDigit() }.toIntOrNull() ?: 0
    }
    
    val finalPriceString = remember(basePriceInt, appliedDiscount) {
        if (appliedDiscount > 0) {
            val discountedPrice = (basePriceInt * (100 - appliedDiscount)) / 100
            // Format back cleanly with thousands separators
            String.format(Locale.US, "IDR %,d", discountedPrice).replace(',', '.')
        } else {
            trip.price
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.book_trip_name, trip.name)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.choose_your_booking_date))
                OutlinedButton(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                val selectedCalendar = Calendar.getInstance().apply {
                                    set(year, month, day)
                                }
                                bookingDate = SimpleDateFormat(
                                    "dd MMM yyyy",
                                    Locale.getDefault()
                                ).format(selectedCalendar.time)
                                dateError = false
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).apply {
                            datePicker.minDate = System.currentTimeMillis() - 1000
                        }.show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (bookingDate.isBlank()) stringResource(R.string.select_date) else bookingDate)
                }
                if (dateError) {
                    Text(
                        text = stringResource(R.string.please_select_a_booking_date_),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                
                Text(stringResource(R.string.promo_voucher_code))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = voucherCode,
                        onValueChange = { voucherCode = it },
                        placeholder = { Text(stringResource(R.string.voucher_placeholder)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Button(
                        onClick = {
                            if (voucherCode.isNotBlank()) {
                                scope.launch {
                                    val isUsed = firebaseRepo.isVoucherUsedByUser(voucherCode, username)
                                    if (isUsed) {
                                        appliedDiscount = 0
                                        voucherStatusMessage = context.getString(R.string.used_voucher_message)
                                    } else {
                                        val vData = firebaseRepo.getVoucherData(voucherCode)
                                        val pct = vData?.get("discountPercentage")?.toString()?.toIntOrNull() ?: 0
                                        
                                        if (pct > 0) {
                                            if (bookingDate.isNotBlank()) {
                                                val startStr = vData?.get("startDate")?.toString() ?: ""
                                                val endStr = vData?.get("endDate")?.toString() ?: ""
                                                
                                                try {
                                                    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                                                    val paramSdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                                    val currentBookingTime = sdf.parse(bookingDate)?.time ?: 0L
                                                    
                                                    var isDateValid = true
                                                    if (startStr.isNotBlank()) {
                                                        val startTime = paramSdf.parse(startStr.trim())?.time ?: 0L
                                                        if (currentBookingTime < startTime) isDateValid = false
                                                    }
                                                    if (endStr.isNotBlank()) {
                                                        val endTime = paramSdf.parse(endStr.trim())?.time ?: 0L
                                                        if (currentBookingTime > endTime) isDateValid = false
                                                    }
                                                    
                                                    if (!isDateValid) {
                                                        appliedDiscount = 0
                                                        voucherStatusMessage = context.getString(R.string.invalid_date_voucher)
                                                    } else {
                                                        appliedDiscount = pct
                                                        voucherStatusMessage = context.getString(R.string.discount_applied_success, pct)
                                                    }
                                                } catch (e: Exception) {
                                                    appliedDiscount = pct
                                                    voucherStatusMessage = context.getString(R.string.discount_applied_success, pct)
                                                }
                                            } else {
                                                appliedDiscount = pct
                                                voucherStatusMessage = context.getString(R.string.discount_applied_confirm, pct)
                                            }
                                        } else {
                                            appliedDiscount = 0
                                            voucherStatusMessage = context.getString(R.string.invalid_voucher)
                                        }
                                    }
                                }
                            }
                        }
                    ) {
                        Text(stringResource(R.string.apply))
                    }
                }
                if (voucherStatusMessage.isNotEmpty()) {
                    Text(
                        text = voucherStatusMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (appliedDiscount > 0) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                    )
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Text(
                    text = stringResource(R.string.payment_amount, finalPriceString) + if (appliedDiscount > 0) stringResource(R.string.voucher_applied_suffix) else "",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(
                    onClick = {
                        if (bookingDate.isBlank()) {
                            dateError = true
                        } else {
                            scope.launch {
                                val customer = firebaseRepo.getUser(username)
                                if (appliedDiscount > 0) {
                                    firebaseRepo.recordVoucherUsage(voucherCode, username)
                                }
                                firebaseRepo.saveBooking(
                                    Booking(
                                        bookingId = System.currentTimeMillis().toString(),
                                        customerUsername = username,
                                        customerName = customer?.username ?: username,
                                        customerPhone = customer?.phone.orEmpty(),
                                        destination = trip.name,
                                        bookingDate = bookingDate,
                                        status = "Pending",
                                        paymentStatus = "Pending",
                                        finalPrice = finalPriceString,
                                        basePrice = trip.price,
                                        discountAmount = if (appliedDiscount > 0) {
                                            String.format(Locale.US, "IDR %,d", (basePriceInt * appliedDiscount) / 100).replace(',', '.')
                                        } else "IDR 0",
                                        voucherUsed = if (appliedDiscount > 0) voucherCode.trim().uppercase() else ""
                                    )
                                )
                                onDismiss()
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.pay_later))
                }
                Button(
                    onClick = {
                        if (bookingDate.isBlank()) {
                            dateError = true
                        } else {
                            scope.launch {
                                val customer = firebaseRepo.getUser(username)
                                if (appliedDiscount > 0) {
                                    firebaseRepo.recordVoucherUsage(voucherCode, username)
                                }
                                firebaseRepo.saveBooking(
                                    Booking(
                                        bookingId = System.currentTimeMillis().toString(),
                                        customerUsername = username,
                                        customerName = customer?.username ?: username,
                                        customerPhone = customer?.phone.orEmpty(),
                                        destination = trip.name,
                                        bookingDate = bookingDate,
                                        status = "Confirmed",
                                        paymentStatus = "Paid",
                                        finalPrice = finalPriceString,
                                        basePrice = trip.price,
                                        discountAmount = if (appliedDiscount > 0) {
                                            String.format(Locale.US, "IDR %,d", (basePriceInt * appliedDiscount) / 100).replace(',', '.')
                                        } else "IDR 0",
                                        voucherUsed = if (appliedDiscount > 0) voucherCode.trim().uppercase() else ""
                                    )
                                )
                                onDismiss()
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.pay_now))
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TripCard(
    trip: TripPromo,
    modifier: Modifier = Modifier,
    onBook: () -> Unit = {}
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { /* Navigate */ }
            )
            .motionEventSpy { event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> isPressed = true
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> isPressed = false
                }
            },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = trip.imageRes),
                    contentDescription = trip.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = trip.price,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = trip.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = trip.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onBook,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = stringResource(R.string.book_now))
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PartnerScreenPreview() {
    AppTheme {
        PartnerScreen()
    }
}


@Composable
fun PartnerScreen() {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isExpanded = adaptiveInfo.windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT
    
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val partners = listOf(
// ...
        Partner(
            name = stringResource(R.string.varna_culture_hotel),
            description = stringResource(R.string.varna_hotel_desc),
            imageRes = R.drawable.varnaculturehotel,
            highlights = stringResource(R.string.varna_highlights),
            websiteUrl = "https://varnaculturehotel.com/"
        ),
        Partner(
            name = stringResource(R.string.mooz_hotel),
            description = stringResource(R.string.mooz_hotel_desc),
            imageRes = R.drawable.moozhotel,
            highlights = stringResource(R.string.mooz_highlights),
            websiteUrl = "https://www.moozhotel.com/"
        ),
        Partner(
            name = stringResource(R.string.coffee_tgc),
            description = stringResource(R.string.coffee_tgc_desc),
            imageRes = R.drawable.cofeetgc,
            highlights = stringResource(R.string.coffee_tgc_highlights),
            websiteUrl = "https://coffeetgc.com/"
        )
    )

    val valueProps = listOf(
        ValueProp(
            stringResource(R.string.assured_quality),
            stringResource(R.string.assured_quality_desc),
            Icons.Default.CheckCircle
        ),
        ValueProp(
            stringResource(R.string.exclusive_service),
            stringResource(R.string.exclusive_service_desc),
            Icons.Default.WorkspacePremium
        ),
        ValueProp(
            stringResource(R.string.best_pricing),
            stringResource(R.string.best_pricing_desc),
            Icons.Default.AttachMoney
        ),
        ValueProp(
            stringResource(R.string.trusted_security),
            stringResource(R.string.trusted_security_desc),
            Icons.Default.Security
        ),
        ValueProp(
            stringResource(R.string.maximum_comfort),
            stringResource(R.string.maximum_comfort_desc),
            Icons.Default.Home
        ),
        ValueProp(
            stringResource(R.string.customer_focus),
            stringResource(R.string.customer_focus_desc),
            Icons.Default.Person
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                initialOffsetY = { 40 },
                animationSpec = tween(600, easing = EaseOutQuad)
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Hero Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
            Image(
                painter = painterResource(id = R.drawable.backgroudmountain),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.6f
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.official_partners_content),
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.strategic_partnership_desc_short),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Intro Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            SectionHeader(title = stringResource(R.string.strategic_partnership))

            Text(
                text = stringResource(R.string.strategic_partnership_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Partner Cards
        val columns = if (isExpanded) 2 else 1
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            partners.chunked(columns).forEach { rowPartners ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    rowPartners.forEach { partner ->
                        PartnerCard(partner = partner, modifier = Modifier.weight(1f))
                    }
                    if (rowPartners.size < columns) {
                        repeat(columns - rowPartners.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Why ZTI Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                .padding(vertical = 40.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            SectionHeader(title = stringResource(R.string.why_zti_chooses_partners))

            Spacer(modifier = Modifier.height(24.dp))

            // Two-column grid for value props
            valueProps.chunked(2).forEach { rowProps ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowProps.forEach { prop ->
                        ValuePropCard(prop = prop, modifier = Modifier.weight(1f))
                    }
                    if (rowProps.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

}
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PartnerCard(partner: Partner, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    if (partner.websiteUrl.isNotBlank()) {
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(partner.websiteUrl)))
                        } catch (_: Exception) {}
                    }
                }
            )
            .motionEventSpy { event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> isPressed = true
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> isPressed = false
                }
            },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            Image(
                painter = painterResource(id = partner.imageRes),
                contentDescription = partner.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = partner.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = partner.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        partner.highlights.split("\n").forEach { highlight ->
                            if (highlight.isNotBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = highlight.trim(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)

                ) {
                    OutlinedButton(
                        onClick = {
                            if (partner.websiteUrl.isNotBlank()) {
                                try {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(partner.websiteUrl)))
                                } catch (_: Exception) {}
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(text = stringResource(R.string.website))
                    }
                    Button(
                        onClick = {
                            val contactLink = if (partner.contactUrl.isNotBlank()) partner.contactUrl else "https://wa.me/qr/TV6WKVO3MT4CK1"
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(contactLink)))
                            } catch (_: Exception) {}
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(text = stringResource(R.string.contact))
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
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    LaunchedEffect(username) {
        user = firebaseRepo.getUser(username)
        user?.let {
            editableUsername = it.username
            email = it.email
            phone = it.phone
            password = ""
            confirmPassword = ""
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.backgroundmountainlogin),
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
            SectionHeader(title = stringResource(R.string.profile))

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

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                errorMessage = ""
                                successMessage = ""
                            },
                            label = { Text(stringResource(R.string.password)) },
                            placeholder = { Text(stringResource(R.string.enter_password)) },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                val icon =
                                    if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                                val description =
                                    if (isPasswordVisible) stringResource(R.string.hide_password) else stringResource(
                                        R.string.show_password
                                    )
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(imageVector = icon, contentDescription = description)
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                errorMessage = ""
                                successMessage = ""
                            },
                            label = { Text(stringResource(R.string.confirm_password)) },
                            placeholder = { Text(stringResource(R.string.enter_confirm_password)) },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
                        )

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
                                } else if (password.isNotBlank() && password != confirmPassword) {
                                    errorMessage =
                                        context.getString(R.string.error_passwords_mismatch)
                                } else {
                                    scope.launch {
                                        try {
                                            if (editableUsername != username) {
                                                val existingUser =
                                                    firebaseRepo.getUser(editableUsername)
                                                if (existingUser != null) {
                                                    errorMessage =
                                                        context.getString(R.string.error_username_exists)
                                                    return@launch
                                                }
                                            }

                                            user?.let {
                                                val updatedUser = it.copy(
                                                    username = editableUsername.trim(),
                                                    email = email.trim(),
                                                    phone = phone.trim(),
                                                    password = ""
                                                )
                                                firebaseRepo.updateUser(updatedUser)
                                                auth.currentUser?.let { firebaseUser ->
                                                    if (firebaseUser.email != updatedUser.email) {
                                                        firebaseUser.updateEmail(updatedUser.email).await()
                                                    }
                                                    if (password.isNotBlank()) {
                                                        firebaseUser.updatePassword(password).await()
                                                    }
                                                }
                                                user = updatedUser
                                                successMessage =
                                                    context.getString(R.string.profile_updated)
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
                        
                        // Connection Status
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
                                    users = users.map { if (it.id == updatedUser.id) updatedUser else it }
                                }
                                "delete" -> {
                                    firebaseRepo.deleteUser(user.username)
                                    users = users.filterNot { it.id == user.id }
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
private fun AdminUserAccountCard(
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
                    Text("Account ID: ${user.id}", style = MaterialTheme.typography.bodySmall,
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
                    text = if (user.isBanned) "Status: Banned" else "Status: Active",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (user.isBanned) MaterialTheme.colorScheme.error else Color(0xFF4CAF50)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalIconButton(
                        onClick = onBanOrUnban,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = if (user.isBanned) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = if (user.isBanned) "Unban user" else "Ban user",
                            tint = if (user.isBanned) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error
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
private fun AdminAccountDetail(label: String, value: String) {
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


@Composable
fun AdminScreen(firebaseRepo: FirebaseRepository) {
    val context = LocalContext.current
    var bookingList by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var adminVouchers by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var bookingBeingEdited by remember { mutableStateOf<Booking?>(null) }
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
        bookingList = firebaseRepo.getAllBookings()
        adminVouchers = firebaseRepo.getAllVouchers()
        isLoading = false
    }

    val filteredList = bookingList.filter {
        val name = it.customerName ?: ""
        val dest = it.destination ?: ""
        val bId = it.bookingId ?: ""
        name.contains(searchQuery, ignoreCase = true) ||
                dest.contains(searchQuery, ignoreCase = true) ||
                bId.contains(searchQuery, ignoreCase = true)
    }

    val totalBookings = bookingList.size
    val confirmedBookings = bookingList.count { it.status == "Confirmed" }
    val pendingPayments = bookingList.count { it.paymentStatus == "Pending" }

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
                        SectionHeader(title = stringResource(R.string.admin_dashboard))
                        
                        Spacer(modifier = Modifier.height(8.dp))

                        // Connection Status
                        val isAdminConnected = connectionStatus == "Connected"
                        val adminDisplayStatus = if (isAdminConnected) stringResource(R.string.connected) else connectionStatus
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isAdminConnected) Color.Green else Color.Red)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.sync_status, adminDisplayStatus),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isAdminConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(onClick = {
                                scope.launch {
                                    connectionStatus = context.getString(R.string.syncing)
                                    connectionStatus = firebaseRepo.testConnection()
                                    bookingList = firebaseRepo.getAllBookings()
                                }
                            }) {
                                Text(stringResource(R.string.refresh_sync))
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Stats Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AdminStatCard(
                                title = stringResource(R.string.total),
                                value = totalBookings.toString(),
                                icon = Icons.Default.Book,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                            AdminStatCard(
                                title = stringResource(R.string.confirmed),
                                value = confirmedBookings.toString(),
                                icon = Icons.Default.CheckCircle,
                                color = Color(0xFF4CAF50),
                                modifier = Modifier.weight(1f)
                            )
                            AdminStatCard(
                                title = stringResource(R.string.pending),
                                value = pendingPayments.toString(),
                                icon = Icons.Default.Payment,
                                color = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Search Bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(stringResource(R.string.search_bookings_placeholder)) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Quick Admin Voucher Management Row
                        var inputVoucherCode by remember { mutableStateOf("") }
                        var customPercentage by remember { mutableStateOf("10") }
                        var voucherDescription by remember { mutableStateOf("") }
                        var startDateStr by remember { mutableStateOf("") }
                        var endDateStr by remember { mutableStateOf("") }
                        var voucherSavedStatus by remember { mutableStateOf("") }
                        var isVoucherSectionExpanded by remember { mutableStateOf(false) }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { isVoucherSectionExpanded = !isVoucherSectionExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(R.string.create_promo_voucher), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { isVoucherSectionExpanded = !isVoucherSectionExpanded }) {
                                Icon(
                                    imageVector = if (isVoucherSectionExpanded) Icons.Default.Lock else Icons.Default.Search,
                                    contentDescription = if (isVoucherSectionExpanded) stringResource(R.string.collapse) else stringResource(R.string.expand)
                                )
                            }
                        }
                        
                        AnimatedVisibility(visible = isVoucherSectionExpanded) {
                            Column {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = inputVoucherCode,
                                        onValueChange = { inputVoucherCode = it },
                                        placeholder = { Text(stringResource(R.string.code_upper)) },
                                        modifier = Modifier.weight(1.5f),
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    OutlinedTextField(
                                        value = customPercentage,
                                        onValueChange = { customPercentage = it.filter { c -> c.isDigit() } },
                                        placeholder = { Text(stringResource(R.string.percent_sign)) },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = voucherDescription,
                                    onValueChange = { voucherDescription = it },
                                    placeholder = { Text(stringResource(R.string.voucher_details_placeholder)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val context = LocalContext.current
                                    OutlinedButton(
                                        onClick = {
                                            val calendar = Calendar.getInstance()
                                            DatePickerDialog(
                                                context,
                                                { _, year, month, day ->
                                                    val selectedCalendar = Calendar.getInstance().apply {
                                                        set(year, month, day)
                                                    }
                                                    startDateStr = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedCalendar.time)
                                                },
                                                calendar.get(Calendar.YEAR),
                                                calendar.get(Calendar.MONTH),
                                                calendar.get(Calendar.DAY_OF_MONTH)
                                            ).apply {
                                                datePicker.minDate = System.currentTimeMillis() - 1000
                                            }.show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(if (startDateStr.isBlank()) stringResource(R.string.start_date) else startDateStr, style = MaterialTheme.typography.bodySmall)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            val calendar = Calendar.getInstance()
                                            val datePickerDialog = DatePickerDialog(
                                                context,
                                                { _, year, month, day ->
                                                    val selectedCalendar = Calendar.getInstance().apply {
                                                        set(year, month, day)
                                                    }
                                                    endDateStr = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedCalendar.time)
                                                },
                                                calendar.get(Calendar.YEAR),
                                                calendar.get(Calendar.MONTH),
                                                calendar.get(Calendar.DAY_OF_MONTH)
                                            )
                                            
                                            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                            val minDate = if (startDateStr.isNotBlank()) {
                                                try {
                                                    sdf.parse(startDateStr)?.time ?: System.currentTimeMillis()
                                                } catch (e: Exception) {
                                                    System.currentTimeMillis()
                                                }
                                            } else {
                                                System.currentTimeMillis()
                                            }
                                            
                                            datePickerDialog.datePicker.minDate = minDate - 1000
                                            datePickerDialog.show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(if (endDateStr.isBlank()) stringResource(R.string.end_date) else endDateStr, style = MaterialTheme.typography.bodySmall)
                                    }
                                    Button(
                                        onClick = {
                                            if (inputVoucherCode.isNotBlank()) {
                                                scope.launch {
                                                    val pct = customPercentage.toIntOrNull() ?: 10
                                                    firebaseRepo.saveVoucher(
                                                        code = inputVoucherCode,
                                                        discountPercentage = pct,
                                                        description = voucherDescription,
                                                        startDate = startDateStr,
                                                        endDate = endDateStr
                                                    )
                                                    adminVouchers = firebaseRepo.getAllVouchers()
                                                    voucherSavedStatus = "${context.getString(R.string.voucher)} '${inputVoucherCode.trim().uppercase()}' ${context.getString(R.string.successfully_created)}!"
                                                    inputVoucherCode = ""
                                                    voucherDescription = ""
                                                    startDateStr = ""
                                                    endDateStr = ""
                                                }
                                            }
                                        }
                                    ) {
                                        Text(stringResource(R.string.create))
                                    }
                                }
                                if (voucherSavedStatus.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(voucherSavedStatus, style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        var isManageVouchersExpanded by remember { mutableStateOf(false) }
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { isManageVouchersExpanded = !isManageVouchersExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(R.string.manage_existing_vouchers), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { isManageVouchersExpanded = !isManageVouchersExpanded }) {
                                Icon(
                                    imageVector = if (isManageVouchersExpanded) Icons.Default.Lock else Icons.Default.Search,
                                    contentDescription = if (isManageVouchersExpanded) stringResource(R.string.collapse) else stringResource(R.string.expand)
                                )
                            }
                        }

                        AnimatedVisibility(visible = isManageVouchersExpanded) {
                            Column {
                                if (adminVouchers.isEmpty()) {
                                    Text(stringResource(R.string.no_voucher_created), style = MaterialTheme.typography.bodySmall)
                                } else {
                                    adminVouchers.forEach { voucher ->
                                        val code = voucher["code"]?.toString() ?: ""
                                        val discount = voucher["discountPercentage"]?.toString() ?: "0"
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(code, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                                Text("${stringResource(R.string.discount)}: $discount%", style = MaterialTheme.typography.bodySmall)
                                            }
                                            IconButton(onClick = {
                                                scope.launch {
                                                    firebaseRepo.deleteVoucher(code)
                                                    adminVouchers = firebaseRepo.getAllVouchers()
                                                }
                                            }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (filteredList.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .heightIn(min = 200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                if (isLoading) stringResource(R.string.loading_bookings) 
                                else if (connectionStatus != "Connected") stringResource(R.string.sync_failed, connectionStatus)
                                else stringResource(R.string.no_bookings_found),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            } else {
                items(filteredList, key = { it.bookingId }) { booking ->
                    AdminBookingCard(
                        booking = booking,
                        onEdit = { bookingBeingEdited = booking },
                        onDelete = {
                            scope.launch {
                                firebaseRepo.deleteBooking(booking.bookingId)
                                bookingList = bookingList.filterNot { it.bookingId == booking.bookingId }
                            }
                        }
                    )
                }
            }
        }
    }

    bookingBeingEdited?.let { booking ->
        BookingEditDialog(
            booking = booking,
            onDismiss = { bookingBeingEdited = null },
            onSave = { updatedBooking ->
                scope.launch {
                    firebaseRepo.updateBooking(updatedBooking)
                    bookingList = bookingList.map {
                        if (it.bookingId == updatedBooking.bookingId) updatedBooking else it
                    }
                    bookingBeingEdited = null
                }
            }
        )
    }
}


@Composable
fun AdminStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color)
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = color.copy(alpha = 0.8f))
        }
    }
}


@Composable
fun AdminBookingCard(
    booking: Booking,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.destination,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.booking_id_format, booking.bookingId),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                PaymentStatusBadge(status = booking.paymentStatus)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(modifier = Modifier.alpha(0.3f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = booking.customerName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = booking.customerPhone, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = booking.bookingDate, style = MaterialTheme.typography.bodyMedium)
            }

            if (booking.finalPrice.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF4CAF50))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.price_paid_format, booking.finalPrice) + if (booking.voucherUsed.isNotBlank()) stringResource(R.string.via_voucher_format, booking.voucherUsed) else "",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.status_format, booking.status),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (booking.status == "Confirmed") Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(end = 16.dp)
                )
                
                FilledTonalIconButton(
                    onClick = onEdit,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                FilledTonalIconButton(
                    onClick = onDelete,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}



@Composable
fun UserVouchersScreen(firebaseRepo: FirebaseRepository, username: String) {
    var vouchersWithStatus by remember { mutableStateOf<List<VoucherStatus>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val allVouchers = firebaseRepo.getAllVouchers()
        val currentTime = System.currentTimeMillis()
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        vouchersWithStatus = allVouchers.map { vData ->
            val code = vData["code"]?.toString() ?: ""
            val endDateStr = vData["endDate"]?.toString() ?: ""
            
            val isUsed = firebaseRepo.isVoucherUsedByUser(code, username)
            
            var isExpired = false
            if (endDateStr.isNotBlank()) {
                try {
                    val endDate = sdf.parse(endDateStr.trim())?.time ?: 0L
                    // If current time is after the end of that day
                    if (currentTime > endDate + 86400000) { // +1 day to be safe or just check date
                        isExpired = true
                    }
                } catch (_: Exception) {}
            }
            
            VoucherStatus(vData, isUsed, isExpired)
        }
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        SectionHeader(title = stringResource(R.string.available_promo_vouchers))
        
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (vouchersWithStatus.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.no_active_voucher),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(vouchersWithStatus) { status ->
                    val voucher = status.data
                    val code = voucher["code"]?.toString() ?: "UNKNOWN"
                    val discount = voucher["discountPercentage"]?.toString() ?: "0"
                    val isUsed = status.isUsed
                    val isExpired = status.isExpired
                    val isDisabled = isUsed || isExpired
                    
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth().alpha(if (isDisabled) 0.6f else 1f),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = code,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isDisabled) Color.Gray else MaterialTheme.colorScheme.primary
                                )
                                val desc = voucher["description"]?.toString() ?: ""
                                val start = voucher["startDate"]?.toString() ?: ""
                                val end = voucher["endDate"]?.toString() ?: ""
                                
                                Text(
                                    text = if (desc.isNotBlank()) desc else "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (start.isNotBlank() || end.isNotBlank()) {
                                    Text(
                                        text = "Validity: $start to $end",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isDisabled) Color.Gray else MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            
                            Column(horizontalAlignment = Alignment.End) {
                                Surface(
                                    color = when {
                                        isUsed -> Color.Gray.copy(alpha = 0.1f)
                                        isExpired -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                                        else -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(
                                        1.dp, 
                                        when {
                                            isUsed -> Color.Gray.copy(alpha = 0.3f)
                                            isExpired -> MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                                            else -> Color(0xFF4CAF50).copy(alpha = 0.3f)
                                        }
                                    )
                                ) {
                                    Text(
                                        text = when {
                                            isUsed -> stringResource(R.string.used)
                                            isExpired -> stringResource(R.string.expired)
                                            else -> "$discount% OFF"
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            isUsed -> Color.Gray
                                            isExpired -> MaterialTheme.colorScheme.error
                                            else -> Color(0xFF4CAF50)
                                        }
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


data class VoucherStatus(
    val data: Map<String, Any>,
    val isUsed: Boolean,
    val isExpired: Boolean
)

@Composable
private fun UserBookingCard(
    booking: Booking,
    onViewReceipt: (String) -> Unit = {},
    onCancel: () -> Unit = {},
    onPayNow: () -> Unit = {},
    isHistory: Boolean = false
) {
    val context = LocalContext.current
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isHistory) 0.7f else 1f),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.destination,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isHistory) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.booking_id_format, booking.bookingId),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                PaymentStatusBadge(status = booking.paymentStatus)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(modifier = Modifier.alpha(0.3f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = booking.bookingDate, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = booking.customerPhone, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isHistory) stringResource(R.string.completed) else stringResource(R.string.status_format, booking.status),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isHistory) MaterialTheme.colorScheme.outline else (if (booking.status == "Confirmed") Color(0xFF4CAF50) else MaterialTheme.colorScheme.error)
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (booking.paymentStatus.equals("Paid", ignoreCase = true)) {
                        OutlinedButton(
                            onClick = { onViewReceipt(booking.bookingId) },
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(stringResource(R.string.receipt))
                        }

                        if (isHistory) {
                            OutlinedButton(
                                onClick = { printReceipt(context, booking) },
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Print,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(stringResource(R.string.print))
                            }
                        }
                    }

                    if (!isHistory) {
                        if (booking.paymentStatus.equals("Pending", ignoreCase = true)) {
                            Button(
                                onClick = onPayNow,
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(stringResource(R.string.pay_now))
                            }
                        }

                        FilledTonalIconButton(
                            onClick = onCancel,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Cancel booking",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyBookingsScreen(
    username: String,
    firebaseRepo: FirebaseRepository,
    onViewReceipt: (String) -> Unit
) {
    val context = LocalContext.current
    var bookings by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var bookingToCancel by remember { mutableStateOf<Booking?>(null) }
    var showRefundNotice by remember { mutableStateOf(false) }
    var connectionStatus by remember { mutableStateOf("Connected") }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(username) {
        isLoading = true
        val test = firebaseRepo.testConnection()
        if (test != "Connected") {
            connectionStatus = test
        }
        bookings = firebaseRepo.getBookingsForUser(username)
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        SectionHeader(title = stringResource(R.string.my_bookings))
        
        if (connectionStatus != "Connected") {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.sync_error, connectionStatus),
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (bookings.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.no_booking_message),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            val sdf = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
            val today = remember {
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }

            val (pastBookings, currentBookings) = bookings.partition {
                try {
                    val date = sdf.parse(it.bookingDate)?.time ?: 0L
                    date < today
                } catch (_: Exception) {
                    false
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (currentBookings.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.active_booking),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(currentBookings, key = { it.bookingId }) { booking ->
                        UserBookingCard(
                            booking = booking,
                            onViewReceipt = onViewReceipt,
                            onCancel = { bookingToCancel = booking },
                            onPayNow = {
                                scope.launch {
                                    val paidBooking = booking.copy(
                                        status = "Confirmed",
                                        paymentStatus = "Paid"
                                    )
                                    firebaseRepo.updateBooking(paidBooking)
                                    bookings = bookings.map {
                                        if (it.bookingId == paidBooking.bookingId) paidBooking else it
                                    }
                                }
                            }
                        )
                    }
                }

                if (pastBookings.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.booking_history),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(top = 24.dp)
                        )
                    }
                    items(pastBookings, key = { it.bookingId }) { booking ->
                        UserBookingCard(
                            booking = booking,
                            onViewReceipt = onViewReceipt,
                            isHistory = true
                        )
                    }
                }
            }
        }
    }

    bookingToCancel?.let { booking ->
        AlertDialog(
            onDismissRequest = { bookingToCancel = null },
            title = { Text("${stringResource(R.string.cancel_booking)}?") },
            text = { Text("${stringResource(R.string.confirmation_cancel_booking)} ${booking.destination}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            firebaseRepo.deleteBooking(booking.bookingId)
                            bookings = bookings.filterNot { it.bookingId == booking.bookingId }
                            bookingToCancel = null
                            showRefundNotice = true
                        }
                    }
                ) {
                    Text(stringResource(R.string.cancel_booking))
                }
            },
            dismissButton = {
                TextButton(onClick = { bookingToCancel = null }) {
                    Text(stringResource(R.string.keep_booking))
                }
            }
        )
    }

    if (showRefundNotice) {
        AlertDialog(
            onDismissRequest = { showRefundNotice = false },
            title = { Text(stringResource(R.string.booking_cancelled)) },
            text = { Text(stringResource(R.string.refund)) },
            confirmButton = {
                TextButton(onClick = { showRefundNotice = false }) {
                    Text("OK")
                }
            }
        )
    }
}



@Composable
private fun BookingEditDialog(
    booking: Booking,
    onDismiss: () -> Unit,
    onSave: (Booking) -> Unit
) {
    var customerName by remember(booking) { mutableStateOf(booking.customerName) }
    var customerPhone by remember(booking) { mutableStateOf(booking.customerPhone) }
    var destination by remember(booking) { mutableStateOf(booking.destination) }
    var bookingDate by remember(booking) { mutableStateOf(booking.bookingDate) }
    var bookingStatus by remember(booking) { mutableStateOf(booking.status) }
    var paymentStatus by remember(booking) { mutableStateOf(booking.paymentStatus) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${stringResource(R.string.update_booking)} #${booking.bookingId}") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text(stringResource(R.string.customer_name)) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { customerPhone = it },
                    label = { Text(stringResource(R.string.phone_number)) },
                    singleLine = true
                )
                BookingDropdown(
                    label = stringResource(R.string.destination),
                    selectedValue = destination,
                    options = listOf(
                        "Mount Bromo",
                        "Borobudur Temple",
                        "Ijen Crater",
                        "Bali",
                        "Labuan Bajo",
                        "Raja Ampat"
                    ),
                    onValueSelected = { destination = it }
                )
                val context = LocalContext.current
                OutlinedButton(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                val selectedCalendar = Calendar.getInstance().apply {
                                    set(year, month, day)
                                }
                                bookingDate = SimpleDateFormat(
                                    "dd MMM yyyy",
                                    Locale.getDefault()
                                ).format(selectedCalendar.time)
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).apply {
                            datePicker.minDate = System.currentTimeMillis() - 1000
                        }.show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("${stringResource(R.string.booking_date)} $bookingDate")
                }
                BookingDropdown(
                    label = stringResource(R.string.booking_status),
                    selectedValue = bookingStatus,
                    options = listOf(stringResource(R.string.pending), stringResource(R.string.confirmed), stringResource(R.string.cancelled)),
                    onValueSelected = { bookingStatus = it }
                )
                BookingDropdown(
                    label = stringResource(R.string.payment_status),
                    selectedValue = paymentStatus,
                    options = listOf(stringResource(R.string.pending), stringResource(R.string.paid_lower)),
                    onValueSelected = { paymentStatus = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        booking.copy(
                            customerName = customerName,
                            customerPhone = customerPhone,
                            destination = destination,
                            bookingDate = bookingDate,
                            status = bookingStatus,
                            paymentStatus = paymentStatus
                        )
                    )
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun BookingDropdown(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("$label: $selectedValue")
                Text("▼")
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
private fun PaymentStatusBadge(status: String, modifier: Modifier = Modifier) {
    val isPaid = status.equals("Paid", ignoreCase = true)
    Surface(
        modifier = modifier,
        color = if (isPaid) {
            Color(0xFFDDF5E5)
        } else {
            MaterialTheme.colorScheme.errorContainer
        },
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (isPaid) Color(0xFF176B36) else MaterialTheme.colorScheme.onErrorContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(
    bookingId: String,
    firebaseRepo: FirebaseRepository,
    loggedInUser: String?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var booking by remember { mutableStateOf<Booking?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(bookingId) {
        booking = firebaseRepo.getBooking(bookingId)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.booking_receipt)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (booking != null) {
                        IconButton(onClick = { printReceipt(context, booking!!) }) {
                            Icon(Icons.Default.Print, contentDescription = "Print")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (booking == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.receipt_not_found))
            }
        } else if (loggedInUser != booking!!.customerUsername && loggedInUser != ADMIN_USERNAME) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.access_denied))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.zti_logo),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.White).padding(8.dp)
                        )
                        Text(
                            "PT ZUHANTO TRAVEL INDONESIA",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        ReceiptRow(stringResource(R.string.booking_id), "#${booking!!.bookingId}")
                        ReceiptRow(stringResource(R.string.customer), booking!!.customerName)
                        ReceiptRow(stringResource(R.string.destination_label), booking!!.destination)
                        ReceiptRow(stringResource(R.string.date_label), booking!!.bookingDate)
                        ReceiptRow(stringResource(R.string.phone_label), booking!!.customerPhone)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Price Breakdown
                        ReceiptRow(stringResource(R.string.normal_price), if (booking!!.basePrice.isNotBlank()) booking!!.basePrice else booking!!.finalPrice)
                        
                        if (booking!!.voucherUsed.isNotBlank()) {
                            ReceiptRow(
                                label = "${stringResource(R.string.discount)} (${booking!!.voucherUsed})",
                                value = "- ${booking!!.discountAmount}",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        
                        ReceiptRow(stringResource(R.string.total_paid), booking!!.finalPrice, isTotal = true)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            stringResource(R.string.thank_you),
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Paid",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(stringResource(R.string.paid_upper), fontWeight = FontWeight.ExtraBold, color = Color(0xFF4CAF50))
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { printReceipt(context, booking!!) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.print_receipt))
                }
            }
        }
    }
}


fun printReceipt(context: Context, booking: Booking) {
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    val jobName = "Receipt_${booking.bookingId}"
    
    val officialReceiptText = context.getString(R.string.official_receipt)
    val bookingIdText = context.getString(R.string.booking_id)
    val customerText = context.getString(R.string.customer)
    val destinationText = context.getString(R.string.destination_label)
    val dateText = context.getString(R.string.date_label)
    val phoneText = context.getString(R.string.phone_label)
    val normalPriceText = context.getString(R.string.normal_price)
    val discountText = context.getString(R.string.discount)
    val totalPaidText = context.getString(R.string.total_paid)
    val paidLabelText = context.getString(R.string.paid_label)
    val footerThankYouText = context.getString(R.string.footer_thank_you)

    val webView = WebView(context)
    val htmlContent = """
        <html>
        <body style="font-family: sans-serif; padding: 40px; color: #333;">
            <div style="text-align: center; margin-bottom: 30px;">
                <h1 style="color: #063763; margin-bottom: 5px;">PT ZUHANTO TRAVEL INDONESIA</h1>
                <p style="margin: 0; color: #666;">$officialReceiptText</p>
            </div>
            <div style="border: 1px solid #eee; padding: 20px; border-radius: 10px;">
                <table style="width: 100%; border-collapse: collapse; margin-bottom: 20px;">
                    <tr><td style="padding: 8px 0; border-bottom: 1px solid #f9f9f9;"><b>$bookingIdText</b></td><td style="text-align: right;">#${booking.bookingId}</td></tr>
                    <tr><td style="padding: 8px 0; border-bottom: 1px solid #f9f9f9;"><b>$customerText</b></td><td style="text-align: right;">${booking.customerName}</td></tr>
                    <tr><td style="padding: 8px 0; border-bottom: 1px solid #f9f9f9;"><b>$destinationText</b></td><td style="text-align: right;">${booking.destination}</td></tr>
                    <tr><td style="padding: 8px 0; border-bottom: 1px solid #f9f9f9;"><b>$dateText</b></td><td style="text-align: right;">${booking.bookingDate}</td></tr>
                    <tr><td style="padding: 8px 0;"><b>$phoneText</b></td><td style="text-align: right;">${booking.customerPhone}</td></tr>
                </table>
                
                <div style="background: #fcfcfc; padding: 15px; border-radius: 5px;">
                    <table style="width: 100%;">
                        <tr><td style="padding: 5px 0;">$normalPriceText</td><td style="text-align: right;">${if (booking.basePrice.isNotBlank()) booking.basePrice else booking.finalPrice}</td></tr>
                        ${if (booking.voucherUsed.isNotBlank()) "<tr><td style='padding: 5px 0;'>$discountText (${booking.voucherUsed})</td><td style='text-align: right; color: red;'>- ${booking.discountAmount}</td></tr>" else ""}
                        <tr style="font-size: 1.3em; font-weight: bold; color: #063763;">
                            <td style="padding-top: 10px;">$totalPaidText</td><td style="text-align: right; padding-top: 10px;">${booking.finalPrice}</td>
                        </tr>
                    </table>
                </div>
            </div>
            
            <div style="text-align: center; margin-top: 40px;">
                <div style="display: inline-block; padding: 10px 20px; border: 2px solid #4CAF50; color: #4CAF50; font-weight: bold; border-radius: 5px;">
                    $paidLabelText
                </div>
                <p style="margin-top: 20px; color: #888;"><i>$footerThankYouText</i></p>
            </div>
        </body>
        </html>
    """.trimIndent()
    
    webView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            val printAdapter = webView.createPrintDocumentAdapter(jobName)
            printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
        }
    }
    webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null)
}

@Composable
fun ReceiptRow(label: String, value: String, isTotal: Boolean = false, color: Color? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            value,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.SemiBold,
            color = color ?: if (isTotal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
