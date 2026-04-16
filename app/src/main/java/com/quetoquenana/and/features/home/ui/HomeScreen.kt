package com.quetoquenana.and.features.home.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.quetoquenana.and.R
import com.quetoquenana.and.core.ui.components.AnimatedColoredShadows
import com.quetoquenana.and.core.ui.components.BottomBar
import com.quetoquenana.and.core.ui.components.LogoImage
import com.quetoquenana.and.core.ui.components.previewAppointments
import com.quetoquenana.and.core.ui.components.previewAnnouncements
import com.quetoquenana.and.core.ui.navigation.AddBike
import com.quetoquenana.and.core.ui.navigation.AddAppointment
import com.quetoquenana.and.core.ui.navigation.AppointmentDetail
import com.quetoquenana.and.core.ui.navigation.Home
import com.quetoquenana.and.core.ui.navigation.LocalNavigator
import com.quetoquenana.and.core.ui.navigation.StravaImport
import com.quetoquenana.and.core.ui.navigation.shouldShowBottomBar
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.announcements.domain.model.Announcement
import com.quetoquenana.and.features.suggestions.domain.model.Suggestion

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val navigator = LocalNavigator.current
    val context = LocalContext.current

    HomeScreen(
        modifier = modifier,
        uiState = uiState,
        onAppointmentClick = { id -> navigator.navigate(route = AppointmentDetail.createRoute(id)) },
        onEmptyClick = { navigator.navigate(AddAppointment.route) },
        onCreateBikeClick = { navigator.navigate(AddBike.createRoute()) },
        onStravaIntegrationClick = { navigator.navigate(StravaImport.route) },
        onAnnouncementClick = { announcement -> context.openAnnouncementUrl(announcement.url) }
    )

    SnackbarHost(
        hostState = snackBarHostState
    )
}

@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onAppointmentClick: (String) -> Unit = {},
    onEmptyClick: () -> Unit = {},
    onCreateBikeClick: () -> Unit = {},
    onStravaIntegrationClick: () -> Unit = {},
    onAnnouncementClick: (Announcement) -> Unit = {}
) {
    // Use LazyColumn for vertical scroll
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        content = {
            item {
                when(uiState.headerSection) {
                    HeaderSection.Loading -> Text(text = "Loading...", style = MaterialTheme.typography.titleMedium)
                    is HeaderSection.Content -> AppointmentsItem(
                        appointments = uiState.headerSection.appointments,
                        onAppointmentClick = onAppointmentClick,
                        onCreateAppointmentClick = onEmptyClick
                    )
                    is HeaderSection.NoBikes -> NoBikesItem(
                        onCreateBikeClick = onCreateBikeClick,
                        onStravaIntegrationClick = onStravaIntegrationClick
                    )
                }
            }

            item {
                when(uiState.headerSection) {
                    is HeaderSection.Content ->
                    SuggestionsItem(
                        suggestions = uiState.headerSection.suggestions,
                        onSuggestionClick = { /* placeholder - navigate */ }
                    )
                    else -> {} // Nothing to show in other states
                }
            }

            item {
                Spacer(modifier = Modifier.height(height = 20.dp))
                Text(text = "Explore Mobi Bike World", style = MaterialTheme.typography.titleMedium)
            }

            items(uiState.announcements, key = { it.id }) { announcement ->
                AnnouncementCard(item = announcement, onClick = { onAnnouncementClick(announcement) })
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(height = 20.dp))
                LogoImage()
            }
        }
    )
}


@Composable
fun NoBikesItem(
    modifier: Modifier = Modifier,
    onCreateBikeClick: () -> Unit = {},
    onStravaIntegrationClick: () -> Unit = {}
) {
    Text(text = "Create your first bike", style = MaterialTheme.typography.titleMedium)
    Row(
        modifier = modifier
            .size(width = 320.dp, height = 100.dp)
            .padding(all = 12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoImage(
                modifier = Modifier
                    .size(size = 48.dp)
                    .clickable(onClick = onStravaIntegrationClick),
                imageId = R.drawable.ic_strava
            )
            Text(
                text = "From Strava",
                style = MaterialTheme.typography.titleSmall
            )
        }

        Text(
            text = "Or",
            style = MaterialTheme.typography.titleSmall
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoImage(
                modifier = Modifier
                    .size(size = 48.dp)
                    .clickable(onClick = onCreateBikeClick)
            )
            Text(
                text = "New Bike",
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
fun AppointmentsItem(
    appointments: List<Appointment>,
    onAppointmentClick: (String) -> Unit = {},
    onCreateAppointmentClick: () -> Unit = {}
) {
    Text(text = "Upcoming Appointments", style = MaterialTheme.typography.titleMedium)
    AppointmentsRow(
        appointments = appointments,
        onAppointmentClick = onAppointmentClick,
        onCreateAppointmentClick = onCreateAppointmentClick
    )
}

@Composable
fun SuggestionsItem(
    suggestions: List<Suggestion>,
    onSuggestionClick: (String) -> Unit = {}
) {
    Text(text = "Suggestions for you", style = MaterialTheme.typography.titleMedium)
    SuggestionsRow(
        suggestions = suggestions,
        onSuggestionClick = onSuggestionClick
    )
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    AnimatedColoredShadows(
        content = {
            Column(
                modifier = modifier
                    .size(width = 160.dp, height = 100.dp)
                    .clickable(onClick = onClick)
                    .padding(all = 12.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start

            ) {
                val res = appointment.thumbnailRes ?: R.drawable.mobi_bike_logo
                Image(
                    painter = painterResource(id = res),
                    contentDescription = null,
                    modifier = Modifier.size(size = 40.dp)
                )

                Text(
                    text = appointment.dateText,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = appointment.bikeName ?: appointment.bikeId,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    )
}

@Composable
fun AppointmentsRow(
    modifier: Modifier = Modifier,
    appointments: List<Appointment>,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    onAppointmentClick: (String) -> Unit = {},
    onCreateAppointmentClick: () -> Unit = {}
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
        contentPadding = contentPadding
    ) {
        if (appointments.isEmpty()) {
            item(key = "create-appointment") {
                CreateAppointmentCard(onClick = onCreateAppointmentClick)
            }
        } else {
            items(items = appointments, key = { it.id }) { appointment ->
                AppointmentCard(
                    appointment = appointment,
                    onClick = { onAppointmentClick(appointment.id) }
                )
            }
        }
    }
}

@Composable
fun CreateAppointmentCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .size(width = 160.dp, height = 100.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(all = 12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = "No appointments", style = MaterialTheme.typography.titleSmall)
            Text(
                text = "Schedule a service visit",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SuggestionCard(
    suggestion: Suggestion,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .size(width = 160.dp, height = 100.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .padding(all = 12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            val res = suggestion.thumbnailRes ?: R.drawable.mobi_bike_logo
            Image(
                painter = painterResource(id = res),
                contentDescription = null,
                modifier = Modifier.size(size = 36.dp)
            )

            Text(
                text = suggestion.title,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = suggestion.subtitle,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SuggestionsRow(
    modifier: Modifier = Modifier,
    suggestions: List<Suggestion>,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    onSuggestionClick: (String) -> Unit = {}
) {
    if (!suggestions.isEmpty()) {
        LazyRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
            contentPadding = contentPadding
        ) {
            items(items = suggestions, key = { it.id }) { suggestion ->
                SuggestionCard(suggestion = suggestion, onClick = { onSuggestionClick(suggestion.id) })
            }
        }
    }
}

@Composable
fun AnnouncementCard(
    item: Announcement,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = item.title, style = MaterialTheme.typography.titleMedium)
            item.subTitle?.let { subTitle ->
                Text(text = subTitle, style = MaterialTheme.typography.titleSmall)
            }
            Text(text = item.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private fun Context.openAnnouncementUrl(rawUrl: String?) {
    val uri = rawUrl?.trim()?.takeIf { it.isNotBlank() }?.toActionUri() ?: return
    val intent = when {
        uri.scheme.equals("mailto", ignoreCase = true) -> Intent(Intent.ACTION_SENDTO, uri)
        uri.scheme.equals("tel", ignoreCase = true) -> Intent(Intent.ACTION_DIAL, uri)
        uri.scheme.equals("sms", ignoreCase = true) || uri.scheme.equals("smsto", ignoreCase = true) -> {
            Intent(Intent.ACTION_SENDTO, uri)
        }
        uri.isWhatsAppUri() -> Intent(Intent.ACTION_VIEW, uri)
        uri.scheme.equals("http", ignoreCase = true) || uri.scheme.equals("https", ignoreCase = true) -> {
            Intent(Intent.ACTION_VIEW, uri)
        }
        else -> Intent(Intent.ACTION_VIEW, uri)
    }

    try {
        startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(this, "No app available to open this announcement", Toast.LENGTH_SHORT).show()
    }
}

private fun String.toActionUri(): Uri {
    val value = trim()
    val parsed = Uri.parse(value)

    return when {
        parsed.scheme != null -> parsed
        value.contains("@") -> Uri.parse("mailto:$value")
        value.isLikelyPhoneNumber() -> Uri.parse("tel:$value")
        else -> Uri.parse("https://$value")
    }
}

private fun String.isLikelyPhoneNumber(): Boolean {
    return matches(Regex("""^\+?[0-9][0-9\s().-]{6,}$"""))
}

private fun Uri.isWhatsAppUri(): Boolean {
    val hostValue = host.orEmpty().lowercase()
    return scheme.equals("whatsapp", ignoreCase = true) ||
        hostValue == "wa.me" ||
        hostValue.endsWith(".whatsapp.com") ||
        hostValue == "whatsapp.com"
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenContentPreview() {
    PedalPalTheme {
        val navController = rememberNavController()
        val currentRoute = Home.route
        val showBottomBar = shouldShowBottomBar(currentRoute)
        val state = HomeUiState(
            headerSection = HeaderSection.Content(
                appointments = previewAppointments
            )
        )
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomBar) {
                    BottomBar(
                        navController = navController,
                        appointmentsBadgeCount = 1
                    )
                }
            }
        ) { paddingValues ->
            HomeScreen(
                modifier = Modifier
                    .padding(paddingValues = paddingValues)
                    .fillMaxSize(),
                uiState = state
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenContentPreview_Empty() {
    PedalPalTheme {
        val navController = rememberNavController()
        val currentRoute = Home.route
        val showBottomBar = shouldShowBottomBar(currentRoute)

        val state = HomeUiState(
            announcements = previewAnnouncements,
            headerSection = HeaderSection.NoBikes(
                createBikeOption = true
            )
        )
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomBar) {
                    BottomBar(
                        navController = navController,
                        appointmentsBadgeCount = 1
                    )
                }
            }
        ) { paddingValues ->
            HomeScreen(
                modifier = Modifier
                    .padding(paddingValues = paddingValues)
                    .fillMaxSize(),
                uiState = state
            )
        }
    }
}
