package com.example.appproject.ui.screens

import android.app.DatePickerDialog
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.appproject.R
import com.example.appproject.data.model.Booking
import com.example.appproject.data.remote.FetchPromoTripsFromWebsite
import com.example.appproject.data.repository.FirebaseRepository
import com.example.appproject.data.model.PricingTier
import com.example.appproject.data.model.TripPromo
import com.example.appproject.data.model.User
import com.example.appproject.data.remote.getDefaultItinerariesForTrip
import com.example.appproject.data.model.getFormattedTierPrice
import com.example.appproject.data.remote.getSavedPromoTrips
import com.example.appproject.data.model.parseBookingDate
import com.example.appproject.data.remote.resolveTripImageRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
import androidx.compose.ui.text.font.Font

val BebasNeue = FontFamily(
    Font(R.font.bebas_naue, FontWeight.Normal)
)

@Composable
fun PromoTripScreen(
    username: String,
    firebaseRepo: FirebaseRepository,
    onViewTripDetail: (TripPromo) -> Unit = {},
    onRequireLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isExpanded = adaptiveInfo.windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT

    var selectedBookingTrip by remember { mutableStateOf<TripPromo?>(null) }
    var showSignInPrompt by remember { mutableStateOf(false) }
    var trips by remember { mutableStateOf<List<TripPromo>>(emptyList()) }
    var isSyncing by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        trips = withContext(Dispatchers.IO) { getSavedPromoTrips(context) }
        isSyncing = true
        try {
            val fresh = FetchPromoTripsFromWebsite(context)
            if (fresh.isNotEmpty()) trips = fresh
        } catch (e: Exception) {
            Log.e("PromoTripScreen", "Error auto syncing with website", e)
        } finally {
            isSyncing = false
        }
    }

    val filteredTrips = remember(trips, searchQuery) {
        trips.filter { trip ->
            searchQuery.isBlank() ||
                    trip.name.contains(searchQuery, ignoreCase = true) ||
                    trip.description.contains(searchQuery, ignoreCase = true) ||
                    trip.subtext.contains(searchQuery, ignoreCase = true) ||
                    trip.price.contains(searchQuery, ignoreCase = true)
        }
    }

    val columns = if (isExpanded) 2 else 1
    val horizontalPadding = if (isExpanded) 24.dp else 16.dp

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
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
                        AsyncImage(
                            model = R.drawable.backgroudmountain,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            alpha = 0.7f
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.45f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = stringResource(R.string.promo_trip),
                                    style = MaterialTheme.typography.displayLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = BebasNeue
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.all_travel_packages_special_offers),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizontalPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SectionHeader(
                            title = stringResource(R.string.indonesian_travel_icons)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.promo_trip_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text(stringResource(R.string.search_promo_package)) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (filteredTrips.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_package_match),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }

        itemsIndexed(filteredTrips, key = { _, trip -> trip.name }) { index, trip ->
            val startPad = if (columns == 1) horizontalPadding else if (index % 2 == 0) horizontalPadding else 8.dp
            val endPad = if (columns == 1) horizontalPadding else if (index % 2 == 0) 8.dp else horizontalPadding

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = startPad, end = endPad)
            ) {
                TripCard(
                    trip = trip,
                    modifier = Modifier.fillMaxWidth(),
                    onViewDetails = { onViewTripDetail(trip) },
                    onBook = {
                        if (username.isBlank() || username == "Traveler") {
                            showSignInPrompt = true
                        } else {
                            selectedBookingTrip = trip
                        }
                    }
                )
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    selectedBookingTrip?.let { trip ->
        BookingDialog(
            trip = trip,
            username = username,
            firebaseRepo = firebaseRepo,
            onDismiss = { selectedBookingTrip = null }
        )
    }

    if (showSignInPrompt) {
        AlertDialog(
            onDismissRequest = { showSignInPrompt = false },
            title = { Text(stringResource(R.string.sign_in_required)) },
            text = { Text(stringResource(R.string.sign_in_required_desc)) },
            confirmButton = {
                Button(onClick = { showSignInPrompt = false; onRequireLogin() }) { Text(stringResource(
                    R.string.sign_in)) }
            },
            dismissButton = {
                TextButton(onClick = { showSignInPrompt = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@Composable
fun FacilityCard(
    title: String,
    items: List<String>,
    badgeText: String = "✓",
    isExcluded: Boolean = false
) {
    if (items.isEmpty()) return

    val containerColor = if (isExcluded) {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
    } else {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
    }

    val borderColor = if (isExcluded) {
        MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    }

    val titleColor = if (isExcluded) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }

    val badgeBgColor = if (isExcluded) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )
            Spacer(modifier = Modifier.height(10.dp))
            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        color = badgeBgColor,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = badgeText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun FacilitiesAndInclusionsSection(trip: TripPromo) {
    Text(
        text = stringResource(R.string.package_facilities_inclusions, "Package Facilities & Inclusions"),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(12.dp))

    if (trip.name.contains("Tumpak", ignoreCase = true)) {
        FacilityCard(
            title = stringResource(R.string.package_A, "Package A"),
            items = listOf(
                stringResource(R.string.inc_transport_ac_full, "Transportation (AC)"),
                stringResource(R.string.inc_tickets, "Tickets"),
                stringResource(R.string.inc_local_guide, "Local Guide"),
                stringResource(R.string.inc_mineral_water, "Mineral Water")
            ),
            badgeText = "✓"
        )

        FacilityCard(
            title = stringResource(R.string.package_B, "Package B"),
            items = listOf(
                stringResource(R.string.all_package_a, "All Package A inclusions"),
                stringResource(R.string.lunch_makan_siang, "Lunch"),
                stringResource(R.string.inc_snack_box, "Snack Box"),
                stringResource(R.string.inc_video_cinematic, "Video Cinematic")
            ),
            badgeText = "✓"
        )

        FacilityCard(
            title = stringResource(R.string.included, "INCLUDED"),
            items = listOf(
                stringResource(R.string.inc_transport_ac, "Transport AC"),
                stringResource(R.string.inc_driver_bbm, "Driver + BBM"),
                stringResource(R.string.inc_tickets, "Tickets"),
                stringResource(R.string.inc_local_guide, "Local Guide"),
                stringResource(R.string.inc_mineral_water, "Mineral Water"),
                stringResource(R.string.inc_lunch_pack_b, "Lunch (Pack B)"),
                stringResource(R.string.inc_documentation, "Documentation"),
                stringResource(R.string.inc_first_aid, "First Aid")
            ),
            badgeText = "✓"
        )

        if (trip.exclusions.isNotEmpty()) {
            FacilityCard(
                title = stringResource(R.string.excluded, "EXCLUDED"),
                items = trip.exclusions,
                badgeText = "✕",
                isExcluded = true
            )
        }
    } else {
        val effectiveInclusions = trip.inclusions.ifEmpty {
            emptyList()
        }

        val effectiveExclusions = trip.exclusions.ifEmpty {
            emptyList()
        }

        FacilityCard(
            title = stringResource(R.string.included, "INCLUDED"),
            items = effectiveInclusions,
            badgeText = "✓"
        )

        FacilityCard(
            title = stringResource(R.string.excluded, "EXCLUDED"),
            items = effectiveExclusions,
            badgeText = "✕",
            isExcluded = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoTripDetailScreen(
    tripName: String,
    firebaseRepo: FirebaseRepository,
    loggedInUser: String,
    onBack: () -> Unit,
    onRequireLogin: () -> Unit = {},
    onNavigateToChat: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    var trip by remember { mutableStateOf<TripPromo?>(null) }
    var tripReviews by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var selectedBookingTrip by remember { mutableStateOf<TripPromo?>(null) }
    var showSignInPrompt by remember { mutableStateOf(false) }

    LaunchedEffect(tripName) {
        val cached = withContext(Dispatchers.IO) { getSavedPromoTrips(context) }
        trip = cached.find { it.name.equals(tripName, ignoreCase = true) }
            ?: cached.find {
                it.name.contains(tripName, ignoreCase = true) ||
                        tripName.contains(it.name.substringBefore(" "), ignoreCase = true)
            }

        val live = FetchPromoTripsFromWebsite(context)
        val liveMatch = live.find { it.name.equals(tripName, ignoreCase = true) }
            ?: live.find {
                it.name.contains(tripName, ignoreCase = true) ||
                        tripName.contains(it.name.substringBefore(" "), ignoreCase = true)
            }
        if (liveMatch != null) trip = liveMatch

        try {
            val allBookings = firebaseRepo.getAllBookings()
            tripReviews = allBookings.filter { b ->
                (b.destination.contains(tripName, ignoreCase = true) || tripName.contains(b.destination.substringBefore(" "), ignoreCase = true)) &&
                (b.userRating > 0 || b.userReview.isNotBlank())
            }
        } catch (_: Exception) {}
    }

    if (trip == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.package_not_found, "Package Not Found")) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.package_details_not_found, "Package details not found."),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    } else {
        val currentTrip = trip!!
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentTrip.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            bottomBar = {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 12.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.starting_rate, "Starting Rate"),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = currentTrip.price,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Button(
                            onClick = {
                                if (loggedInUser.isBlank() || loggedInUser == "Traveler") {
                                    showSignInPrompt = true
                                } else {
                                    selectedBookingTrip = currentTrip
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.book_now),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    AsyncImage(
                        model = resolveTripImageRes(currentTrip.name, currentTrip.imageRes),
                        contentDescription = currentTrip.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = currentTrip.badge.ifEmpty { "Promo Package" },
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentTrip.name,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentTrip.price,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DetailInfoCard(
                                icon = Icons.Default.CalendarToday,
                                title = stringResource(R.string.duration_label),
                                value = currentTrip.duration,
                                modifier = Modifier.weight(1f)
                            )
                            DetailInfoCard(
                                icon = Icons.Default.TrackChanges,
                                title = "Transport",
                                value = currentTrip.transport,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DetailInfoCard(
                                icon = Icons.Default.Home,
                                title = stringResource(R.string.accomodation, "Accomodation"),
                                value = currentTrip.hotel,
                                modifier = Modifier.weight(1f)
                            )
                            DetailInfoCard(
                                icon = Icons.Default.Person,
                                title = stringResource(R.string.participants_label),
                                value = currentTrip.participant,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.package_overview, "Package Overview"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentTrip.description,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (currentTrip.subtext.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentTrip.subtext,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = stringResource(R.string.tour_itinerary_timeline, "Tour Itinerary & Timeline"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val effectiveItineraries = currentTrip.itineraries ?: getDefaultItinerariesForTrip(
                        currentTrip.name
                    )
                    if (!effectiveItineraries.isNullOrEmpty()) {
                        effectiveItineraries.forEach { day ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = day.dayTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (day.steps.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        day.steps.forEach { step ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 5.dp),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                if (step.time.isNotBlank()) {
                                                    Surface(
                                                        color = MaterialTheme.colorScheme.primary,
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Text(
                                                            text = step.time,
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                }
                                                Text(
                                                    text = step.activity,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ) {
                            Text(
                                text = stringResource(R.string.customized_daily_tour_schedule, "Customized daily tour schedule & flexible timeline provided upon booking."),
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AI Travel Assistant",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Need recommendations for best places to visit, stay, or eat in ${currentTrip.name}?",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    onNavigateToChat?.invoke(currentTrip.name)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ask AI for Places to Visit, Stay, or Eat")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(20.dp))

                    FacilitiesAndInclusionsSection(currentTrip)

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = stringResource(R.string.rating_and_reviews),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val totalReviewsCount = tripReviews.size
                    val avgRating = if (totalReviewsCount > 0) {
                        tripReviews.filter { it.userRating > 0 }.map { it.userRating }.average().toFloat()
                    } else 0f

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = if (totalReviewsCount > 0) String.format(Locale.US, "%.1f", avgRating) else "0.0",
                                        style = MaterialTheme.typography.displayMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    StarRatingBar(rating = if (totalReviewsCount > 0) avgRating.roundToInt() else 0)
                                }
                                Column {
                                    Text(
                                        text = if (totalReviewsCount > 0) "$totalReviewsCount " + stringResource(
                                            R.string.verified_reveiews) else stringResource(R.string.no_reviews),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (totalReviewsCount > 0) stringResource(R.string.rating_text) else stringResource(
                                            R.string.rating_else_text),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (tripReviews.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(modifier = Modifier.alpha(0.3f))
                                Spacer(modifier = Modifier.height(12.dp))

                                tripReviews.forEach { reviewBooking ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = reviewBooking.customerName.ifBlank { "Traveler" },
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            StarRatingBar(rating = reviewBooking.userRating)
                                        }
                                        if (reviewBooking.userReview.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "\"${reviewBooking.userReview}\"",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontStyle = FontStyle.Italic,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = reviewBooking.bookingDate,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                        HorizontalDivider(modifier = Modifier.padding(top = 8.dp).alpha(0.1f))
                                    }
                                }
                            }
                        }
                    }

                    if (currentTrip.pricingTiers.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = stringResource(R.string.pricing_tiers_label),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                currentTrip.pricingTiers.forEach { tier ->
                                    val tierPriceText = getFormattedTierPrice(tier)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = tier.paxLabel,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = tierPriceText,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    HorizontalDivider(modifier = Modifier.alpha(0.2f))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    selectedBookingTrip?.let { bTrip ->
        BookingDialog(
            trip = bTrip,
            username = loggedInUser,
            firebaseRepo = firebaseRepo,
            onDismiss = { selectedBookingTrip = null }
        )
    }

    if (showSignInPrompt) {
        AlertDialog(
            onDismissRequest = { showSignInPrompt = false },
            title = { Text(stringResource(R.string.sign_in_required, "Sign In Required")) },
            text = { Text(stringResource(R.string.sign_in_required_desc, "You need to sign in to book a trip package. Would you like to sign in now?")) },
            confirmButton = {
                Button(
                    onClick = {
                        showSignInPrompt = false
                        onRequireLogin()
                    }
                ) {
                    Text(stringResource(R.string.sign_in, "Sign In"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignInPrompt = false }) {
                    Text(stringResource(R.string.cancel, "Cancel"))
                }
            }
        )
    }
}

@Composable
fun DetailInfoCard(
    icon: ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

fun getCustomPaxMin(tripName: String): Int? {
    val lower = tripName.lowercase(Locale.ROOT)
    return when {
        lower.contains("tumpak") || lower.contains("sewu") -> 5
        lower.contains("yogyakarta") || lower.contains("jogja 2d1n") -> 30
        lower.contains("rinjani") -> 1
        lower == "mount bromo" || lower.contains("mount bromo") -> 1
        lower == "borobudur temple" || lower.contains("borobudur") -> 1
        lower == "ijen crater" || (lower.contains("ijen") && !lower.contains("blue fire")) -> 1
        lower == "bali" || lower.contains("bali") -> 1
        lower == "labuan bajo" || lower.contains("labuan") -> 1
        lower == "raja ampat" || lower.contains("raja ampat") -> 1
        else -> null
    }
}

fun isFixedPackageTrip(tripName: String): Boolean {
    return getCustomPaxMin(tripName) != null
}

@Composable
fun BookingDialog(
    trip: TripPromo,
    username: String,
    firebaseRepo: FirebaseRepository,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var bookingDate by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf(false) }

    val customPaxMin = remember(trip.name) { getCustomPaxMin(trip.name) }
    val isCustomPaxTrip = customPaxMin != null

    var customPaxCount by remember(customPaxMin) { mutableIntStateOf(customPaxMin ?: 2) }

    val availableTiers = remember(trip) {
        if (trip.pricingTiers.isNotEmpty()) {
            trip.pricingTiers
        } else {
            val digits = trip.price.filter { it.isDigit() }
            val pInt = digits.toIntOrNull() ?: 0
            val pFormatted = if (trip.price.isNotBlank()) trip.price else "IDR 0"
            listOf(PricingTier(1, "Standard Package", pInt, pFormatted))
        }
    }

    var selectedTier by remember { mutableStateOf(availableTiers.first()) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    var voucherCode by remember { mutableStateOf("") }
    var appliedDiscount by remember { mutableStateOf(0) }
    var voucherStatusMessage by remember { mutableStateOf("") }
    var showPhoneVerificationDialog by remember { mutableStateOf(false) }
    var currentUserPhone by remember { mutableStateOf("") }

    val totalBasePriceInt = remember(selectedTier, customPaxCount, isCustomPaxTrip) {
        if (isCustomPaxTrip) {
            selectedTier.pricePerPaxInt * customPaxCount
        } else {
            selectedTier.pricePerPaxInt * selectedTier.paxCount
        }
    }

    val finalPriceString = remember(totalBasePriceInt, appliedDiscount) {
        if (appliedDiscount > 0) {
            val discountedPrice = (totalBasePriceInt * (100 - appliedDiscount)) / 100
            String.format(Locale.US, "IDR %,d", discountedPrice).replace(',', '.')
        } else {
            String.format(Locale.US, "IDR %,d", totalBasePriceInt).replace(',', '.')
        }
    }

    fun completeBookingCreation(customerPhone: String) {
        scope.launch {
            val customer = firebaseRepo.getUser(username)
            val customerUid = customer?.uid?.ifBlank { null } ?: FirebaseAuth.getInstance().currentUser?.uid ?: ""
            if (appliedDiscount > 0) {
                firebaseRepo.recordVoucherUsage(voucherCode, username)
            }
            val paxLabelText = if (isCustomPaxTrip) {
                "$customPaxCount Pax" + (if (selectedTier.paxLabel.contains("Package", ignoreCase = true)) " - ${selectedTier.paxLabel}" else "")
            } else {
                selectedTier.paxLabel.ifEmpty { "${selectedTier.paxCount} Pax" }
            }
            firebaseRepo.saveBooking(
                Booking(
                    bookingId = System.currentTimeMillis().toString(),
                    customerUid = customerUid,
                    customerUsername = username,
                    customerName = customer?.username ?: username,
                    customerPhone = customerPhone,
                    destination = "${trip.name} ($paxLabelText)",
                    bookingDate = bookingDate,
                    status = "Pending",
                    paymentStatus = "Pending",
                    finalPrice = finalPriceString,
                    basePrice = "IDR " + String.format(Locale.US, "%,d", totalBasePriceInt)
                        .replace(',', '.'),
                    discountAmount = if (appliedDiscount > 0) {
                        String.format(
                            Locale.US,
                            "IDR %,d",
                            (totalBasePriceInt * appliedDiscount) / 100
                        ).replace(',', '.')
                    } else "IDR 0",
                    voucherUsed = if (appliedDiscount > 0) voucherCode.trim().uppercase() else ""
                )
            )
            Toast.makeText(context, context.getString(R.string.booking_confirmed_success), Toast.LENGTH_SHORT).show()
            onDismiss()
        }
    }

    if (showPhoneVerificationDialog) {
        PhoneVerificationBookingDialog(
            initialPhone = currentUserPhone,
            onVerificationSuccess = { verifiedPhone ->
                scope.launch {
                    val customer = firebaseRepo.getUser(username)
                    val updatedUser = (customer ?: User(username = username, name = username)).copy(
                        phone = verifiedPhone,
                        isPhoneVerified = true
                    )
                    firebaseRepo.updateUser(updatedUser)
                    showPhoneVerificationDialog = false
                    completeBookingCreation(verifiedPhone)
                }
            },
            onDismiss = { showPhoneVerificationDialog = false }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.book_trip_name, trip.name)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isCustomPaxTrip) {
                    if (availableTiers.size > 1) {
                        Text(
                            text = stringResource(R.string.select_package_options),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                onClick = { dropdownExpanded = true },
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = selectedTier.paxLabel,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = getFormattedTierPrice(selectedTier),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Select Option",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                availableTiers.forEach { tier ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    text = tier.paxLabel,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = getFormattedTierPrice(tier),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.secondary
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedTier = tier
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    val minPax = customPaxMin ?: 1
                    Text(
                        text = stringResource(R.string.customized_number_travelers) + "(Min $minPax Pax)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "$customPaxCount " + stringResource(R.string.travelers) + " / Pax",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                val rateStr = if (selectedTier.pricePerPaxInt > 0) String.format(Locale.US, "IDR %,d / pax", selectedTier.pricePerPaxInt).replace(',', '.') else ""
                                Text(
                                    text = rateStr,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedIconButton(
                                    onClick = { if (customPaxCount > minPax) customPaxCount-- },
                                    enabled = customPaxCount > minPax,
                                    modifier = Modifier.size(36.dp),
                                    shape = CircleShape
                                ) {
                                    Text("-", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "$customPaxCount",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                OutlinedIconButton(
                                    onClick = { if (customPaxCount < 100) customPaxCount++ },
                                    enabled = customPaxCount < 100,
                                    modifier = Modifier.size(36.dp),
                                    shape = CircleShape
                                ) {
                                    Text("+", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.select_number_of_pax),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            onClick = { if (availableTiers.size > 1) dropdownExpanded = true },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedTier.paxLabel.ifEmpty { "${selectedTier.paxCount} Pax" },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = getFormattedTierPrice(selectedTier),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                if (availableTiers.size > 1) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Select Option",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        if (availableTiers.size > 1) {
                            DropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                availableTiers.forEach { tier ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    text = tier.paxLabel.ifEmpty { "${tier.paxCount} Pax" },
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = getFormattedTierPrice(tier),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.secondary
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedTier = tier
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Text(
                    text = stringResource(R.string.choose_your_journey_date),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                OutlinedButton(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        if (bookingDate.isNotBlank()) {
                            try {
                                val parsed = parseBookingDate(bookingDate)
                                if (parsed != null) calendar.time = parsed
                            } catch (_: Exception) {}
                        }
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                val selectedCalendar = Calendar.getInstance().apply {
                                    set(year, month, day)
                                }
                                bookingDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(selectedCalendar.time)
                                dateError = false
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).apply {
                            datePicker.minDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)
                        }.show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (bookingDate.isBlank()) stringResource(R.string.select_date) else bookingDate,
                                color = if (bookingDate.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
                                fontWeight = if (bookingDate.isNotBlank()) FontWeight.Bold else FontWeight.Normal,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Pick Date",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
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
                                            appliedDiscount = pct
                                            voucherStatusMessage = context.getString(R.string.discount_applied_success, pct)
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

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val rawPaxLabel = selectedTier.paxLabel.ifEmpty { "${selectedTier.paxCount} " + stringResource(
                                R.string.pax) }
                            val localizedPaxLabel = if (isCustomPaxTrip) {
                                "$customPaxCount " + stringResource(R.string.pax)
                            } else {
                                rawPaxLabel.replace("Pax", stringResource(R.string.pax))
                            }
                            val baseLabelText = "${stringResource(R.string.base_price)} ($localizedPaxLabel):"
                            Text(
                                text = baseLabelText,
                                modifier = Modifier.weight(0.5f, fill = false),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "IDR " + String.format(Locale.US, "%,d", totalBasePriceInt).replace(',', '.'),
                                modifier = Modifier.weight(0.5f, fill = false),
                                textAlign = TextAlign.End,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        if (appliedDiscount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.voucher_discount) + " ($appliedDiscount%):",
                                    modifier = Modifier.weight(0.5f, fill = false),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "- IDR " + String.format(Locale.US, "%,d", (totalBasePriceInt * appliedDiscount) / 100).replace(',', '.'),
                                    modifier = Modifier.weight(0.5f, fill = false),
                                    textAlign = TextAlign.End,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.total_price_label),
                                modifier = Modifier.weight(0.45f, fill = false),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = finalPriceString,
                                modifier = Modifier.weight(0.55f, fill = false),
                                textAlign = TextAlign.End,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
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
                                currentUserPhone = customer?.phone.orEmpty()
                                if (customer != null && customer.isPhoneVerified && customer.phone.isNotBlank()) {
                                    completeBookingCreation(customer.phone)
                                } else {
                                    showPhoneVerificationDialog = true
                                }
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.book_now))
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
    onViewDetails: () -> Unit = {},
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
                onClick = onViewDetails
            ),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = resolveTripImageRes(trip.name, trip.imageRes),
                    contentDescription = trip.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp),
                    contentScale = ContentScale.Crop
                )

                // Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.5f)
                                )
                            )
                        )
                )

                // Top Floating Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = com.example.appproject.ui.theme.PrimaryBlue
                    ) {
                        Text(
                            text = trip.badge,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    if (trip.price.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Black.copy(alpha = 0.65f)
                        ) {
                            Text(
                                text = trip.price,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = com.example.appproject.ui.theme.WarmAmberGold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = trip.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = com.example.appproject.ui.theme.PrimaryBlue
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = trip.description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                )

                if (trip.subtext.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = trip.subtext,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewDetails,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, com.example.appproject.ui.theme.PrimaryBlue),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.view_itinerary),
                            style = MaterialTheme.typography.labelMedium,
                            color = com.example.appproject.ui.theme.PrimaryBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onBook,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = com.example.appproject.ui.theme.PrimaryBlue,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.book_now),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
