package com.example.appproject.ui.navigation

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.appproject.R
import com.example.appproject.data.model.ADMIN_USERNAME
import com.example.appproject.data.model.normalizeBookingStatus
import com.example.appproject.data.repository.FirebaseRepository
import com.example.appproject.ui.screens.AboutUsScreen
import com.example.appproject.ui.screens.AdminReportsScreen
import com.example.appproject.ui.screens.AdminScreen
import com.example.appproject.ui.screens.AdminUsersScreen
import com.example.appproject.ui.screens.AdminVouchersScreen
import com.example.appproject.ui.screens.DashboardHomeScreen
import com.example.appproject.ui.screens.InvoiceScreen
import com.example.appproject.ui.screens.LoginScreen
import com.example.appproject.ui.screens.MyBookingsScreen
import com.example.appproject.ui.screens.PartnerScreen
import com.example.appproject.ui.screens.PaymentScreen
import com.example.appproject.ui.screens.ProfileScreen
import com.example.appproject.ui.screens.PromoTripDetailScreen
import com.example.appproject.ui.screens.PromoTripScreen
import com.example.appproject.ui.screens.ReceiptScreen
import com.example.appproject.ui.screens.RegisterScreen
import com.example.appproject.ui.screens.SettingsScreen
import com.example.appproject.ui.screens.TravelChatScreen
import com.example.appproject.ui.screens.UserVouchersScreen
import com.example.appproject.ui.theme.NavyStart
import com.example.appproject.ui.theme.PrimaryBlue
import com.example.appproject.worker.isAdminNewBookingNotified
import com.example.appproject.worker.isBookingConfirmedNotified
import com.example.appproject.worker.markAdminNewBookingNotified
import com.example.appproject.worker.markBookingConfirmedNotified
import com.example.appproject.worker.showAdminNewBookingNotification
import com.example.appproject.worker.showBookingConfirmedNotification
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationDrawer() {
    val context = LocalContext.current
    val firebaseRepo = remember { FirebaseRepository() }
    val sessionPreferences = remember {
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
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
                FirebaseAuth.getInstance().signOut()
            }
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            Log.d("NotificationPermission", "POST_NOTIFICATIONS granted: $isGranted")
        }
        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    DisposableEffect(loggedInUser) {
        val user = loggedInUser
        if (user.isNullOrEmpty()) {
            return@DisposableEffect onDispose {}
        }

        val listenerRegistration = if (user == ADMIN_USERNAME) {
            firebaseRepo.listenToAllBookings { bookingList ->
                val prefs = context.getSharedPreferences("booking_notifications_prefs", Context.MODE_PRIVATE)
                val isInitialized = prefs.getBoolean("admin_booking_notifications_initialized", false)

                if (!isInitialized) {
                    val existingIds = bookingList.map { it.bookingId }.toSet()
                    prefs.edit()
                        .putStringSet("notified_admin_booking_ids", existingIds)
                        .putBoolean("admin_booking_notifications_initialized", true)
                        .apply()
                } else {
                    bookingList.forEach { booking ->
                        if (!isAdminNewBookingNotified(context, booking.bookingId)) {
                            showAdminNewBookingNotification(context, booking)
                            markAdminNewBookingNotified(context, booking.bookingId)
                        }
                    }
                }
            }
        } else {
            firebaseRepo.listenToBookingsForUser(user) { bookingList ->
                bookingList.forEach { booking ->
                    if (normalizeBookingStatus(booking.status) == "Confirmed") {
                        if (!isBookingConfirmedNotified(context, booking.bookingId)) {
                            showBookingConfirmedNotification(context, booking)
                            markBookingConfirmedNotified(context, booking.bookingId)
                        }
                    }
                }
            }
        }

        onDispose {
            listenerRegistration?.remove()
        }
    }

    LaunchedEffect(Unit) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.cancel(2002)
    }

    val activity = context as? AppCompatActivity
    val targetBookingId = activity?.intent?.getStringExtra("navigate_to_booking_id")
    val navigateToAdmin = activity?.intent?.getBooleanExtra("navigate_to_admin", false) ?: false
    LaunchedEffect(targetBookingId, navigateToAdmin, loggedInUser, showSplash) {
        if (!showSplash && (!targetBookingId.isNullOrEmpty() || navigateToAdmin) && loggedInUser != null) {
            try {
                if (loggedInUser == ADMIN_USERNAME || navigateToAdmin) {
                    navController.navigate("admin") {
                        launchSingleTop = true
                    }
                } else {
                    navController.navigate("bookings/$loggedInUser") {
                        launchSingleTop = true
                    }
                }
            } catch (e: Exception) {
                Log.e("Navigation", "Failed to navigate from notification: ${e.message}", e)
            } finally {
                activity?.intent?.removeExtra("navigate_to_booking_id")
                activity?.intent?.removeExtra("navigate_to_admin")
            }
        }
    }

    if (showSplash) {
        SplashWithCircularLogo(onSplashFinished = { showSplash = false })
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(250.dp),
                    drawerContainerColor = MaterialTheme.colorScheme.surface,
                    drawerShape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp),
                    windowInsets = WindowInsets(0)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                    // Modern Header Card in Navigation Drawer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(NavyStart, PrimaryBlue, Color(0xFF1E88E5))
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column {
                            Surface(
                                modifier = Modifier
                                    .size(60.dp)
                                    .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                                shape = CircleShape,
                                color = Color.White,
                                shadowElevation = 6.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = if (loggedInUser != null) "Hello, $loggedInUser" else stringResource(R.string.company_name_zuhanto),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (loggedInUser != null) stringResource(R.string.ready_for_your_next_trip) + "?" else stringResource(R.string.explore_indonesia_in_style),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Drawer Group 1: Main Exploration
                    Text(
                        text = stringResource(R.string.explore),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 4.dp)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Explore, contentDescription = null) },
                        label = {
                            Text(
                                when {
                                    isAdminLoggedIn -> stringResource(R.string.admin_dashboard)
                                    else -> stringResource(R.string.dashboard)
                                },
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        selected = if (loggedInUser != null) {
                            if (isAdminLoggedIn) currentRoute == "admin"
                            else currentRoute?.startsWith("dashboard") == true
                        } else {
                            currentRoute == "home" || currentRoute == "dashboard"
                        },
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (loggedInUser != null) {
                                val targetRoute = if (isAdminLoggedIn) "admin" else "dashboard/$loggedInUser"
                                if (currentRoute != targetRoute) {
                                    navController.navigate(targetRoute) { launchSingleTop = true }
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
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedIconColor = PrimaryBlue,
                            selectedTextColor = PrimaryBlue
                        ),
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    if (!isAdminLoggedIn) {
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Place, contentDescription = null) },
                            label = { Text(stringResource(R.string.promo_trip), fontWeight = FontWeight.SemiBold) },
                            selected = currentRoute == "promotrip",
                            onClick = {
                                navController.navigate("promotrip") { launchSingleTop = true }
                                scope.launch { drawerState.close() }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedIconColor = PrimaryBlue,
                                selectedTextColor = PrimaryBlue
                            ),
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                            label = { Text("AI Travel Assistant", fontWeight = FontWeight.SemiBold) },
                            selected = currentRoute?.startsWith("chat") == true,
                            onClick = {
                                navController.navigate("chat") { launchSingleTop = true }
                                scope.launch { drawerState.close() }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.secondary,
                                selectedTextColor = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp))

                    // Drawer Group 2: Account & Travel
                    if (loggedInUser != null && !isAdminLoggedIn) {
                        Text(
                            text = stringResource(R.string.my_account),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(start = 24.dp, bottom = 4.dp)
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Book, contentDescription = null) },
                            label = { Text(stringResource(R.string.my_bookings), fontWeight = FontWeight.SemiBold) },
                            selected = currentRoute?.startsWith("bookings") == true,
                            onClick = {
                                navController.navigate("bookings/$loggedInUser") { launchSingleTop = true }
                                scope.launch { drawerState.close() }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedIconColor = PrimaryBlue,
                                selectedTextColor = PrimaryBlue
                            ),
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Star, contentDescription = null) },
                            label = { Text(stringResource(R.string.available_vouchers), fontWeight = FontWeight.SemiBold) },
                            selected = currentRoute == "vouchers",
                            onClick = {
                                navController.navigate("vouchers") { launchSingleTop = true }
                                scope.launch { drawerState.close() }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedIconColor = PrimaryBlue,
                                selectedTextColor = PrimaryBlue
                            ),
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Person, contentDescription = null) },
                            label = { Text(stringResource(R.string.profile_lowercase), fontWeight = FontWeight.SemiBold) },
                            selected = currentRoute?.startsWith("profile") == true,
                            onClick = {
                                navController.navigate("profile/$loggedInUser") { launchSingleTop = true }
                                scope.launch { drawerState.close() }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedIconColor = PrimaryBlue,
                                selectedTextColor = PrimaryBlue
                            ),
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp))
                    }

                    if (isAdminLoggedIn) {
                        Text(
                            text = stringResource(R.string.admin_controls),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(start = 24.dp, bottom = 4.dp)
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Person, contentDescription = null) },
                            label = { Text(stringResource(R.string.user_account), fontWeight = FontWeight.SemiBold) },
                            selected = currentRoute == "admin-users",
                            onClick = {
                                navController.navigate("admin-users") { launchSingleTop = true }
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
                            label = { Text(stringResource(R.string.user_reports), fontWeight = FontWeight.SemiBold) },
                            selected = currentRoute == "admin-reports",
                            onClick = {
                                navController.navigate("admin-reports") { launchSingleTop = true }
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Star, contentDescription = null) },
                            label = { Text(stringResource(R.string.manage_vouchers), fontWeight = FontWeight.SemiBold) },
                            selected = currentRoute == "admin-vouchers",
                            onClick = {
                                navController.navigate("admin-vouchers") { launchSingleTop = true }
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp))
                    }

                    // Drawer Group 3: App Information
                    Text(
                        text = stringResource(R.string.info_and_settings),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 24.dp, bottom = 4.dp)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Handshake, contentDescription = null) },
                        label = { Text(stringResource(R.string.partner), fontWeight = FontWeight.Medium) },
                        selected = currentRoute == "partner",
                        onClick = {
                            navController.navigate("partner") { launchSingleTop = true }
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        label = { Text(stringResource(R.string.settings), fontWeight = FontWeight.Medium) },
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
                        label = { Text(stringResource(R.string.about), fontWeight = FontWeight.Medium) },
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
                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))

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
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                loggedInUser = null
                                sessionPreferences.edit().remove("signed_in_username").apply()
                                FirebaseAuth.getInstance().signOut()
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
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    shadowElevation = 3.dp,
                                    color = Color.White,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Image(
                                            painter = painterResource(id = R.drawable.zti_logo),
                                            contentDescription = "PT Zuhanto Travel Logo",
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = stringResource(R.string.company_name_zuhanto),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontSize = 15.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlue
                                    )
                                    Text(
                                        text = "INDONESIA",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE11D48)
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
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PrimaryBlue,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(stringResource(R.string.login), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            } else if (!isAdminLoggedIn) {
                                IconButton(
                                    onClick = {
                                        navController.navigate("profile/$loggedInUser") {
                                            launchSingleTop = true
                                        }
                                    }
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = loggedInUser?.take(1)?.uppercase() ?: "U",
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryBlue,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = stringResource(R.string.open_drawer),
                                    tint = PrimaryBlue
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
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(350, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(350))
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(350, easing = FastOutSlowInEasing)
                        ) + fadeOut(animationSpec = tween(350))
                    },
                    popEnterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(350, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(350))
                    },
                    popExitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(350, easing = FastOutSlowInEasing)
                        ) + fadeOut(animationSpec = tween(350))
                    }
                ) {
                    composable("home") {
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
                            }
                        )
                    }
                    composable("dashboard/{username}") { backStackEntry ->
                        val username = backStackEntry.arguments?.getString("username") ?: ""
                        DashboardHomeScreen(
                            username = username,
                            firebaseRepo = firebaseRepo,
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
                            firebaseRepo = firebaseRepo,
                            onBookNow = {
                                navController.navigate("promotrip") {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable("admin") {
                        AdminScreen(
                            firebaseRepo = firebaseRepo
                        )
                    }
                    composable("admin-vouchers") {
                        if (isAdminLoggedIn) {
                            AdminVouchersScreen(firebaseRepo = firebaseRepo)
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
                                onSignUpClick = { navController.navigate("register") }
                            )
                        }
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
                                onSignUpClick = { navController.navigate("register") }
                            )
                        }
                    }
                    composable("admin-reports") {
                        if (isAdminLoggedIn) {
                            AdminReportsScreen(firebaseRepo = firebaseRepo)
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
                                onSignUpClick = { navController.navigate("register") }
                            )
                        }
                    }
                    composable("promotrip") {
                        PromoTripScreen(
                            username = loggedInUser ?: "",
                            firebaseRepo = firebaseRepo,
                            onViewTripDetail = { trip ->
                                navController.navigate("trip-detail/${Uri.encode(trip.name)}") {
                                    launchSingleTop = true
                                }
                            },
                            onRequireLogin = {
                                navController.navigate("home") {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable("trip-detail/{tripName}") { backStackEntry ->
                        val encodedName = backStackEntry.arguments?.getString("tripName") ?: ""
                        val tripName = Uri.decode(encodedName)
                        PromoTripDetailScreen(
                            tripName = tripName,
                            firebaseRepo = firebaseRepo,
                            loggedInUser = loggedInUser ?: "",
                            onBack = { navController.popBackStack() },
                            onRequireLogin = {
                                navController.navigate("home") {
                                    launchSingleTop = true
                                }
                            },
                            onNavigateToChat = { destination ->
                                navController.navigate("chat/${Uri.encode(destination)}") {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable("chat") {
                        TravelChatScreen(
                            destination = "General Travel",
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("chat/{destination}") { backStackEntry ->
                        val encodedDest = backStackEntry.arguments?.getString("destination") ?: ""
                        val destination = Uri.decode(encodedDest)
                        TravelChatScreen(
                            destination = destination,
                            onBack = { navController.popBackStack() }
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
                                }
                            )
                        } else {
                            MyBookingsScreen(
                                username = username,
                                firebaseRepo = firebaseRepo,
                                onViewReceipt = { bId ->
                                    navController.navigate("receipt/$bId")
                                },
                                onViewInvoice = { bId ->
                                    navController.navigate("invoice/$bId")
                                },
                                onPayNow = { bId ->
                                    navController.navigate("payment/$bId")
                                }
                            )
                        }
                    }
                    composable("payment/{bookingId}") { backStackEntry ->
                        val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
                        PaymentScreen(
                            bookingId = bookingId,
                            firebaseRepo = firebaseRepo,
                            loggedInUser = loggedInUser,
                            onBack = { navController.popBackStack() },
                            onRedirectToBookings = {
                                navController.navigate("bookings/$loggedInUser") {
                                    popUpTo("bookings/$loggedInUser") { inclusive = false }
                                }
                            },
                            onViewReceipt = { bId ->
                                navController.navigate("receipt/$bId") {
                                    popUpTo("payment/$bId") { inclusive = true }
                                }
                            },
                            onViewInvoice = { bId ->
                                navController.navigate("invoice/$bId") {
                                    popUpTo("payment/$bId") { inclusive = true }
                                }
                            }
                        )
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
                    composable("invoice/{bookingId}") { backStackEntry ->
                        val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
                        InvoiceScreen(
                            bookingId = bookingId,
                            firebaseRepo = firebaseRepo,
                            loggedInUser = loggedInUser,
                            onBack = { navController.popBackStack() },
                            onViewReceipt = { bId ->
                                navController.navigate("receipt/$bId") {
                                    popUpTo("invoice/$bId") { inclusive = true }
                                }
                            }
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
                        if (usernameArg.equals(ADMIN_USERNAME, ignoreCase = true)) {
                            LaunchedEffect(Unit) {
                                navController.navigate("admin") {
                                    popUpTo("admin") { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        } else {
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
                    }
                    composable("settings") {
                        SettingsScreen(
                            onNavigateToAdminReports = {
                                navController.navigate("admin-reports") {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable("about") {
                        AboutUsScreen()
                    }
                    composable("register") {
                        RegisterScreen(
                            firebaseRepo = firebaseRepo,
                            onRegisterSuccess = { newUsername ->
                                loggedInUser = newUsername
                                val destination = if (newUsername == ADMIN_USERNAME) "admin" else "dashboard/$newUsername"
                                navController.navigate(destination) {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onBackToLogin = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SplashWithCircularLogo(onSplashFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500)
        onSplashFinished()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NavyStart, PrimaryBlue, Color(0xFF041C32))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier
                    .size(110.dp)
                    .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.zti_logo),
                        contentDescription = null,
                        modifier = Modifier.size(85.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "PT ZUHANTO TRAVEL",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "INDONESIA",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
    }
}
