package com.quetoquenana.and.features.home.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.quetoquenana.and.R
import com.quetoquenana.and.core.ui.components.DarkLightPreviews
import com.quetoquenana.and.core.ui.components.previewAnnouncements
import com.quetoquenana.and.core.ui.components.previewAppointments
import com.quetoquenana.and.core.ui.components.previewBikes
import com.quetoquenana.and.core.ui.components.previewSuggestions
import com.quetoquenana.and.core.ui.navigation.AddAppointment
import com.quetoquenana.and.core.ui.navigation.AddBike
import com.quetoquenana.and.core.ui.navigation.AppointmentDetail
import com.quetoquenana.and.core.ui.navigation.LocalNavigator
import com.quetoquenana.and.core.ui.navigation.StravaImport
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import com.quetoquenana.and.features.announcements.domain.model.Announcement
import com.quetoquenana.and.features.announcements.domain.model.AnnouncementMedia
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.ui.AppointmentSummaryCard
import com.quetoquenana.and.features.suggestions.domain.model.Suggestion
import java.util.UUID

private val HomeTopShape = RoundedCornerShape(
    topStart = 20.dp,
    topEnd = 20.dp
)
private val HomeCardShape = RoundedCornerShape(24.dp)
private val HomeAnnouncementShape = RoundedCornerShape(10.dp)

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigator = LocalNavigator.current
    val context = LocalContext.current

    HomeScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
        uiState = uiState,
        onAppointmentClick = { id -> navigator.navigate(route = AppointmentDetail.createRoute(id)) },
        onEmptyClick = { navigator.navigate(route = AddAppointment.route) },
        onCreateBikeClick = { navigator.navigate(route = AddBike.createRoute()) },
        onStravaIntegrationClick = { navigator.navigate(route = StravaImport.createRoute()) },
        onAnnouncementClick = { announcement -> context.openAnnouncementUrl(rawUrl = announcement.url) }
    )
}

@Composable
private fun HomeScreen(
    modifier: Modifier,
    uiState: HomeUiState,
    onAppointmentClick: (UUID) -> Unit = {},
    onEmptyClick: () -> Unit = {},
    onCreateBikeClick: () -> Unit = {},
    onStravaIntegrationClick: () -> Unit = {},
    onAnnouncementClick: (Announcement) -> Unit = {}
) {
    when {
        uiState.isLoading || uiState.headerSection == HeaderSection.Loading -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        else -> {
            LazyColumn(
                modifier = modifier,
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    HomeHeaderSection(modifier = modifier)
                }
                item {
                    HomeTopSection(
                        hasBikes = uiState.headerSection is HeaderSection.Content,
                        appointments = (uiState.headerSection as? HeaderSection.Content)?.appointments.orEmpty(),
                        onAppointmentClick = onAppointmentClick,
                        onCreateAppointmentClick = onEmptyClick,
                        onCreateBikeClick = onCreateBikeClick,
                        onStravaIntegrationClick = onStravaIntegrationClick
                    )
                }

                if (uiState.suggestions.isNotEmpty()) {
                    item {
                        SuggestionsSection(
                            suggestions = uiState.suggestions,
                            onSuggestionClick = { }
                        )
                    }
                }

                if (uiState.announcements.isNotEmpty()) {
                    item {
                        AnnouncementsSection(
                            modifier = modifier.background(color = MaterialTheme.colorScheme.surfaceVariant),
                            announcements = uiState.announcements,
                            onAnnouncementClick = onAnnouncementClick
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun HomeHeaderSection(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 18.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.welcome_to_pedalpal),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun HomeTopSection(
    hasBikes: Boolean,
    appointments: List<Appointment>,
    onAppointmentClick: (UUID) -> Unit,
    onCreateAppointmentClick: () -> Unit,
    onCreateBikeClick: () -> Unit,
    onStravaIntegrationClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = HomeTopShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (hasBikes) {
                    stringResource(id = R.string.upcoming_appointments)
                } else {
                    stringResource(id = R.string.no_bikes)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (hasBikes) {
                AppointmentsStrip(
                    appointments = appointments,
                    onAppointmentClick = onAppointmentClick,
                    onCreateAppointmentClick = onCreateAppointmentClick
                )
            } else {
                BikeOnboardingStrip(
                    onCreateBikeClick = onCreateBikeClick,
                    onStravaIntegrationClick = onStravaIntegrationClick
                )
            }
        }
    }
}

@Composable
private fun BikeOnboardingStrip(
    onCreateBikeClick: () -> Unit,
    onStravaIntegrationClick: () -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        item {
            HomeActionCard(
                title = stringResource(id = R.string.add_bike),
                subtitle = stringResource(id = R.string.create_from_scratch),
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = onCreateBikeClick
            )
        }
        item {
            HomeActionCard(
                title = stringResource(id = R.string.import_from_strava),
                subtitle = stringResource(id = R.string.import_from_strava),
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                onClick = onStravaIntegrationClick
            )
        }
    }
}

@Composable
private fun HomeActionCard(
    title: String,
    subtitle: String,
    backgroundColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(210.dp)
            .height(132.dp)
            .clickable(onClick = onClick),
        shape = HomeCardShape,
        color = backgroundColor,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = contentColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AppointmentsStrip(
    appointments: List<Appointment>,
    onAppointmentClick: (UUID) -> Unit,
    onCreateAppointmentClick: () -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        if (appointments.isEmpty()) {
            item {
                HomeActionCard(
                    title = stringResource(id = R.string.no_appointments),
                    subtitle = stringResource(id = R.string.schedule_service_visit),
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = onCreateAppointmentClick
                )
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
private fun SuggestionsSection(
    suggestions: List<Suggestion>,
    onSuggestionClick: (UUID) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = stringResource(id = R.string.suggestions_for_you),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        SuggestionsRow(
            suggestions = suggestions,
            onSuggestionClick = onSuggestionClick
        )
    }
}

@Composable
private fun SuggestionsRow(
    suggestions: List<Suggestion>,
    onSuggestionClick: (UUID) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(items = suggestions, key = { it.id }) { suggestion ->
            SuggestionCard(
                suggestion = suggestion,
                onClick = { onSuggestionClick(suggestion.id) }
            )
        }
    }
}

@Composable
private fun SuggestionCard(
    suggestion: Suggestion,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(210.dp)
            .height(228.dp)
            .clickable(onClick = onClick),
        shape = HomeCardShape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        tonalElevation = 0.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.62f)
            ) {
                val imageRes = suggestion.thumbnailRes ?: R.drawable.mobi_bike_logo
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.38f)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = suggestion.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = suggestion.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AnnouncementsSection(
    modifier: Modifier,
    announcements: List<Announcement>,
    onAnnouncementClick: (Announcement) -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = stringResource(id = R.string.announcements),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        announcements.forEachIndexed { index, announcement ->
            AnnouncementCard(
                item = announcement,
                isBlue = index % 2 == 1,
                onClick = { onAnnouncementClick(announcement) }
            )
        }
    }
}

@Composable
private fun AnnouncementCard(
    item: Announcement,
    isBlue: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isBlue) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (isBlue) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = HomeAnnouncementShape,
        color = backgroundColor,
        tonalElevation = 0.dp,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnnouncementMediaCarousel(
                media = item.media,
                contentDescription = item.title
            )
            Text(text = item.title, style = MaterialTheme.typography.titleMedium, color = contentColor)
            item.subTitle?.let { subTitle ->
                Text(text = subTitle, style = MaterialTheme.typography.titleSmall, color = contentColor)
            }
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
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
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(times = media.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(size = if (index == currentImageIndex) 8.dp else 6.dp)
                            .clip(shape = MaterialTheme.shapes.extraSmall)
                            .background(
                                color = if (index == currentImageIndex) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant
                                }
                            )
                    )
                }
            }
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


// Previews
@Composable
private fun HomeComponentPreviewContainer(
    content: @Composable () -> Unit
) {
    PedalPalTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content()
        }
    }
}

@DarkLightPreviews
@Composable
private fun AnnouncementsSectionPreview() {
    HomeComponentPreviewContainer {
        AnnouncementsSection(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surfaceVariant),
            announcements = previewAnnouncements,
            onAnnouncementClick = {}
        )
    }
}

@DarkLightPreviews
@Composable
private fun SuggestionsSectionPreview() {
    HomeComponentPreviewContainer {
        SuggestionsSection(
            suggestions = previewSuggestions,
            onSuggestionClick = {}
        )
    }
}

@DarkLightPreviews
@Composable
private fun HomeTopSectionPreview() {
    HomeComponentPreviewContainer {
        HomeTopSection(
            hasBikes = true,
            appointments = previewAppointments,
            onAppointmentClick = {},
            onCreateAppointmentClick = {},
            onCreateBikeClick ={},
            onStravaIntegrationClick = {}
        )
    }
}

@DarkLightPreviews
@Preview(showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    PedalPalTheme {
        val state = HomeUiState(
            announcements = previewAnnouncements,
            headerSection = HeaderSection.Content(
                appointments = previewAppointments
            ),
            suggestions = previewSuggestions,
            bikes = previewBikes,
        )
        HomeScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface),
            uiState = state
        )
    }
}


@DarkLightPreviews
@Preview(showSystemUi = true)
@Composable
private fun HomeScreenPreviewNoBikes() {
    PedalPalTheme {
        val state = HomeUiState(
            announcements = previewAnnouncements,
            headerSection = HeaderSection.NoBikes(),
            suggestions = previewSuggestions,
            bikes = previewBikes,
        )
        HomeScreen(
            modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
            uiState = state
        )
    }
}
