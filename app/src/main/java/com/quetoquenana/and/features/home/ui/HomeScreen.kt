package com.quetoquenana.and.features.home.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.quetoquenana.and.R
import com.quetoquenana.and.core.ui.components.BottomBar
import com.quetoquenana.and.core.ui.components.DarkLightPreviews
import com.quetoquenana.and.core.ui.components.RegularProgressIndicator
import com.quetoquenana.and.core.ui.components.previewAnnouncement
import com.quetoquenana.and.core.ui.components.previewAnnouncements
import com.quetoquenana.and.core.ui.components.previewAppointments
import com.quetoquenana.and.core.ui.components.previewSuggestions
import com.quetoquenana.and.core.ui.navigation.AddAppointment
import com.quetoquenana.and.core.ui.navigation.AddBike
import com.quetoquenana.and.core.ui.navigation.AppointmentDetail
import com.quetoquenana.and.core.ui.navigation.Home
import com.quetoquenana.and.core.ui.navigation.LocalNavigator
import com.quetoquenana.and.core.ui.navigation.StravaImport
import com.quetoquenana.and.core.ui.navigation.shouldShowBottomBar
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import com.quetoquenana.and.features.announcements.domain.model.Announcement
import com.quetoquenana.and.features.announcements.domain.model.AnnouncementMedia
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.ui.AppointmentSummaryCard
import com.quetoquenana.and.features.bikes.ui.FirstBikeEmptyState
import com.quetoquenana.and.features.suggestions.domain.model.Suggestion
import java.util.UUID

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
        onEmptyClick = { navigator.navigate(route = AddAppointment.route) },
        onCreateBikeClick = { navigator.navigate(route = AddBike.createRoute()) },
        onStravaIntegrationClick = { navigator.navigate(route = StravaImport.createRoute()) },
        onAnnouncementClick = { announcement -> context.openAnnouncementUrl(rawUrl = announcement.url) }
    )
    SnackbarHost(
        hostState = snackBarHostState
    )
}

@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onAppointmentClick: (UUID) -> Unit = {},
    onEmptyClick: () -> Unit = {},
    onCreateBikeClick: () -> Unit = {},
    onStravaIntegrationClick: () -> Unit = {},
    onAnnouncementClick: (Announcement) -> Unit = {}
) {
    when  {
        uiState.headerSection == HeaderSection.Loading -> { RegularProgressIndicator() }
    else -> {
        // Use LazyColumn for vertical scroll
        LazyColumn(
            modifier = modifier.padding(top = 8.dp),
            content = {
                item {
                    when(uiState.headerSection) {
                        is HeaderSection.Content -> AppointmentsItem(
                            appointments = uiState.headerSection.appointments,
                            onAppointmentClick = onAppointmentClick,
                            onCreateAppointmentClick = onEmptyClick
                        )
                        is HeaderSection.NoBikes -> FirstBikeEmptyState(
                            onCreateManuallyClick = onCreateBikeClick,
                            onImportFromStravaClick = onStravaIntegrationClick
                        )
                    }
                }

                item {
                    if (uiState.suggestions.isNotEmpty()) {
                        SuggestionsItem(
                            suggestions = uiState.suggestions,
                            onSuggestionClick = { /* placeholder - navigate */ }
                        )
                    }
                }

                items(uiState.announcements, key = { it.id }) { announcement ->
                    AnnouncementCard(item = announcement, onClick = { onAnnouncementClick(announcement) })
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        )
    }
    }
}

@Composable
fun AppointmentsItem(
    appointments: List<Appointment>,
    onAppointmentClick: (UUID) -> Unit = {},
    onCreateAppointmentClick: () -> Unit = {}
) {
    Text(text = stringResource(id = R.string.upcoming_appointments), style = MaterialTheme.typography.titleMedium)
    AppointmentsRow(
        appointments = appointments,
        onAppointmentClick = onAppointmentClick,
        onCreateAppointmentClick = onCreateAppointmentClick
    )
}

@Composable
fun SuggestionsItem(
    suggestions: List<Suggestion>,
    onSuggestionClick: (UUID) -> Unit = {}
) {
    Text(text = stringResource(id = R.string.suggestions_for_you), style = MaterialTheme.typography.titleMedium)
    SuggestionsRow(
        suggestions = suggestions,
        onSuggestionClick = onSuggestionClick
    )
}

@Composable
fun AppointmentsRow(
    modifier: Modifier = Modifier,
    appointments: List<Appointment>,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    onAppointmentClick: (UUID) -> Unit = {},
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
                AppointmentSummaryCard(
                    appointment = appointment,
                    modifier = Modifier.width(220.dp),
                    actionHint = stringResource(id = R.string.view_selected_services),
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
            Text(text = stringResource(id = R.string.no_appointments), style = MaterialTheme.typography.titleSmall)
            Text(
                text = stringResource(id = R.string.schedule_service_visit),
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
    onSuggestionClick: (UUID) -> Unit = {}
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
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnnouncementMediaCarousel(
                media = item.media,
                contentDescription = item.title
            )
            Text(text = item.title, style = MaterialTheme.typography.titleMedium)
            item.subTitle?.let { subTitle ->
                Text(text = subTitle, style = MaterialTheme.typography.titleSmall)
            }
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AnnouncementMediaCarousel(
    media: List<AnnouncementMedia>,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    if (media.isEmpty()) return

    val listState = rememberLazyListState()
    val currentImageIndex by remember(media.size) {
        derivedStateOf {
            listState.firstVisibleItemIndex.coerceIn(0, media.lastIndex)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val itemWidth = maxWidth
            val itemHeight = 150.dp
            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = media, key = { it.mediaId }) { announcementMedia ->
                    RemoteAnnouncementImage(
                        media = announcementMedia,
                        contentDescription = contentDescription,
                        modifier = Modifier
                            .width(itemWidth)
                            .height(itemHeight)
                    )
                }
            }
        }

        if (media.size > 1) {
            AnnouncementDots(
                count = media.size,
                selectedIndex = currentImageIndex
            )
        }
    }
}

@Composable
private fun RemoteAnnouncementImage(
    media: AnnouncementMedia,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val fallbackPainter = painterResource(id = R.drawable.mobi_bike_logo)
    val request = remember(media.mediaId.toString(), media.imageUrl) {
        ImageRequest.Builder(context)
            .data(media.imageUrl)
            .memoryCacheKey(media.mediaId.toString())
            .diskCacheKey(media.mediaId.toString())
            .build()
    }

    Box(
        modifier = modifier
            .clip(shape = MaterialTheme.shapes.small)
            .background(color = MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = request,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = fallbackPainter,
            error = fallbackPainter,
            fallback = fallbackPainter
        )
    }
}

@Composable
private fun AnnouncementDots(
    count: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(times = count) { index ->
            val color = if (index == selectedIndex) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
            Box(
                modifier = Modifier
                    .size(size = if (index == selectedIndex) 8.dp else 6.dp)
                    .clip(shape = MaterialTheme.shapes.extraSmall)
                    .background(color = color)
            )
        }
    }
}

private fun Context.openAnnouncementUrl(rawUrl: String?) {
    val uri = rawUrl?.trim()?.takeIf { it.isNotBlank() }?.toActionUri() ?: return
    val intent = when {
        uri.scheme.equals(other = "mailto", ignoreCase = true) -> Intent(Intent.ACTION_SENDTO, uri)
        uri.scheme.equals(other = "tel", ignoreCase = true) -> Intent(Intent.ACTION_DIAL, uri)
        uri.scheme.equals(other = "sms", ignoreCase = true) ||
                uri.scheme.equals(other = "smsto", ignoreCase = true) -> {
            Intent(Intent.ACTION_SENDTO, uri)
        }
        uri.isWhatsAppUri() -> Intent(Intent.ACTION_VIEW, uri)
        uri.scheme.equals(other = "http", ignoreCase = true) ||
                uri.scheme.equals(other = "https", ignoreCase = true) -> {
            Intent(Intent.ACTION_VIEW, uri)
        }
        else -> Intent(Intent.ACTION_VIEW, uri)
    }

    try {
        startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(this, getString(R.string.error_open_announcement), Toast.LENGTH_SHORT).show()
    }
}

private fun String.toActionUri(): Uri {
    val value = trim()
    val parsed = value.toUri()

    return when {
        parsed.scheme != null -> parsed
        value.contains("@") -> "mailto:$value".toUri()
        value.isLikelyPhoneNumber() -> "tel:$value".toUri()
        else -> "https://$value".toUri()
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

@Composable
private fun HomeComponentPreviewContainer(
    content: @Composable () -> Unit
) {
    PedalPalTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content()
        }
    }
}

@DarkLightPreviews
@Composable
private fun AnnouncementCardPreview() {
    HomeComponentPreviewContainer {
        AnnouncementCard(item = previewAnnouncement)
    }
}

@DarkLightPreviews
@Composable
private fun SuggestionsRowPreview() {
    HomeComponentPreviewContainer {
        SuggestionsRow(
            suggestions = previewSuggestions
        )
    }
}

@DarkLightPreviews
@Composable
private fun CreateAppointmentCardPreview() {
    HomeComponentPreviewContainer {
        CreateAppointmentCard()
    }
}

@DarkLightPreviews
@Composable
private fun AppointmentsItemPreview() {
    HomeComponentPreviewContainer {
        AppointmentsItem(appointments = previewAppointments.take(3))
    }
}

@DarkLightPreviews
@Preview(showSystemUi = true)
@Composable
private fun HomeScreenContentPreview() {
    PedalPalTheme {
        val navController = rememberNavController()
        val currentRoute = Home.route
        val showBottomBar = shouldShowBottomBar(currentRoute)
        val state = HomeUiState(
            suggestions = previewSuggestions,
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

@DarkLightPreviews
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
