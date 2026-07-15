package com.quetoquenana.and.features.home.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.quetoquenana.and.R
import com.quetoquenana.and.core.ui.components.BasePreviewContainer
import com.quetoquenana.and.core.ui.components.BikesHomeUiStateProvider
import com.quetoquenana.and.core.ui.components.DarkLightPreviews
import com.quetoquenana.and.core.ui.components.DefaultProgressIndicator
import com.quetoquenana.and.core.ui.components.LoadingHomeUiStateProvider
import com.quetoquenana.and.core.ui.components.NoAppointmentsHomeUiStateProvider
import com.quetoquenana.and.core.ui.components.NoBikesHomeUiStateProvider
import com.quetoquenana.and.core.ui.components.defaultContainerPaddingValues
import com.quetoquenana.and.core.ui.components.defaultPaddingValues
import com.quetoquenana.and.core.ui.components.previewAnnouncements
import com.quetoquenana.and.core.ui.components.sharedCardShape
import com.quetoquenana.and.core.ui.components.sharedSectionTopShape
import com.quetoquenana.and.core.ui.navigation.AddAppointment
import com.quetoquenana.and.core.ui.navigation.AddBike
import com.quetoquenana.and.core.ui.navigation.AppointmentDetail
import com.quetoquenana.and.core.ui.navigation.LocalNavigator
import com.quetoquenana.and.core.ui.navigation.StravaImport
import com.quetoquenana.and.core.ui.theme.stravaOrange
import com.quetoquenana.and.core.ui.theme.stravaText
import com.quetoquenana.and.features.announcements.domain.model.Announcement
import com.quetoquenana.and.features.announcements.domain.model.AnnouncementMedia
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.ui.AppointmentSummaryCard
import com.quetoquenana.and.features.suggestions.domain.model.Suggestion
import java.util.UUID

val HomeAnnouncementShape = RoundedCornerShape(size = 12.dp)

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    name: String
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigator = LocalNavigator.current
    val context = LocalContext.current

    HomeScreen(
        name = name,
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
    name: String,
    uiState: HomeUiState,
    onAppointmentClick: (UUID) -> Unit = {},
    onEmptyClick: () -> Unit = {},
    onCreateBikeClick: () -> Unit = {},
    onStravaIntegrationClick: () -> Unit = {},
    onAnnouncementClick: (Announcement) -> Unit = {}
) {
    // Default initial modifier
    val modifier = Modifier
        .fillMaxSize()
        .background(color = MaterialTheme.colorScheme.primary)

    when {
        uiState.isLoading || uiState.headerSection == HeaderSection.Loading -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                DefaultProgressIndicator()
            }
        }

        else -> {
            LazyColumn(
                modifier = modifier,
                // INFO : Use systemBars.asPaddingValues() to ensure that the content is not
                // obscured by system bars on devices with gesture navigation or cutouts.
                contentPadding = WindowInsets.systemBars.asPaddingValues()
            ) {
                item {
                    HomeHeaderSection(
                        modifier = modifier,
                        name = name
                    )
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
    modifier: Modifier = Modifier,
    name: String
) {
    Column(
        modifier = modifier.padding(defaultPaddingValues),
        verticalArrangement = spacedBy(space = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.hello, name),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Text(
            text = stringResource(id = R.string.welcome_to_pedalpal),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary
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
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = sharedSectionTopShape
    ) {
        Column (
            modifier = Modifier
                .padding(defaultContainerPaddingValues)
                .fillMaxWidth(),
            verticalArrangement = spacedBy(space = 12.dp)
        ){
            Text(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                text = if (hasBikes) {
                    stringResource(id = R.string.upcoming_appointments)
                } else {
                    stringResource(id = R.string.no_bikes)
                },
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            if (hasBikes) {
                // INFO : Use spacedBy to add spacing between items in the LazyRow
                AppointmentsData(
                    appointments = appointments,
                    horizontalArrangement = spacedBy(space = 12.dp),
                    onAppointmentClick = onAppointmentClick,
                    onCreateAppointmentClick = onCreateAppointmentClick
                )
            } else {
                NoBikesData(
                    onCreateBikeClick = onCreateBikeClick,
                    onStravaIntegrationClick = onStravaIntegrationClick
                )
            }
        }
    }
}

@Composable
private fun NoBikesData(
    onCreateBikeClick: () -> Unit,
    onStravaIntegrationClick: () -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        item {
            HomeActionCard(
                title = stringResource(id = R.string.add_bike),
                subtitle = stringResource(id = R.string.create_from_scratch),
                onClick = onCreateBikeClick
            )
        }
        item {
            HomeActionCard(
                title = stringResource(id = R.string.strava),
                subtitle = stringResource(id = R.string.import_from_strava),
                backgroundColor = stravaOrange,
                contentColor = stravaText,
                onClick = onStravaIntegrationClick
            )
        }
    }
}

@Composable
private fun HomeActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = sharedCardShape,
        color = backgroundColor,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.secondary
        )
    ) {
        Column(
            modifier = Modifier.padding(all = 12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = contentColor
            )
            Spacer(modifier = Modifier.height(12.dp))
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
private fun AppointmentsData(
    appointments: List<Appointment>,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    onAppointmentClick: (UUID) -> Unit,
    onCreateAppointmentClick: () -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangement,
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        if (appointments.isEmpty()) {
            item {
                HomeActionCard(
                    title = stringResource(id = R.string.no_appointments),
                    subtitle = stringResource(id = R.string.schedule_service_visit),
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
    Surface(
        color = MaterialTheme.colorScheme.primary
    ) {
        Column(
            modifier = Modifier
                .padding(defaultContainerPaddingValues)
                .fillMaxWidth(),
            verticalArrangement = spacedBy(space = 12.dp)
        ) {
            Text(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                text = stringResource(id = R.string.suggestions_for_you),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            SuggestionsRow(
                suggestions = suggestions,
                onSuggestionClick = onSuggestionClick
            )
        }
    }
}

@Composable
private fun SuggestionsRow(
    suggestions: List<Suggestion>,
    onSuggestionClick: (UUID) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = spacedBy(space = 12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
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
            .width(200.dp)
            .height(200.dp)
            .clickable(onClick = onClick),
        shape = sharedCardShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.secondary
        )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.60f)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.secondary
                    )

            ) {
                val imageRes = suggestion.thumbnailRes ?: R.drawable.mobi_bike_logo
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.40f)
                    .padding(all = 12.dp),
                verticalArrangement = spacedBy(8.dp)
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
    announcements: List<Announcement>,
    onAnnouncementClick: (Announcement) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier
                .padding(defaultContainerPaddingValues)
                .fillMaxWidth(),
            verticalArrangement = spacedBy(space = 12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.announcements),
                modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            announcements.forEachIndexed { index, announcement ->
                AnnouncementCard(
                    item = announcement,
                    indexIndicator = index % 2 == 1,
                    onClick = { onAnnouncementClick(announcement) }
                )
            }
      }
    }
}

@Composable
private fun AnnouncementCard(
    item: Announcement,
    indexIndicator: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (indexIndicator) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }
    val contentColor = if (indexIndicator) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    Surface(
        modifier = Modifier
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = HomeAnnouncementShape,
        color = backgroundColor,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.secondary
        )
    ) {
        Column(
            modifier = Modifier.padding(all = 16.dp),
            verticalArrangement = spacedBy(space = 8.dp)
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
        verticalArrangement = spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val itemWidth = maxWidth
            val itemHeight = 150.dp
            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = spacedBy(8.dp)
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
            Row(horizontalArrangement = spacedBy(6.dp)) {
                repeat(times = media.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(size = if (index == currentImageIndex) 8.dp else 6.dp)
                            .clip(shape = MaterialTheme.shapes.extraSmall)
                            .background(
                                color = if (index == currentImageIndex) {
                                    MaterialTheme.colorScheme.secondary
                                } else {
                                    MaterialTheme.colorScheme.tertiary
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
@DarkLightPreviews
@Composable
private fun AnnouncementsSectionPreview() {
    BasePreviewContainer {
        AnnouncementsSection(
            announcements = previewAnnouncements,
            onAnnouncementClick = {}
        )
    }
}

@DarkLightPreviews
@Composable
private fun BikesHomeScreenPreview(
    @PreviewParameter(provider = BikesHomeUiStateProvider::class) homeUiState: HomeUiState
) {
    BasePreviewContainer {
        HomeScreen(
            name = "John Doe",
            uiState = homeUiState
        )
    }
}

@DarkLightPreviews
@Composable
private fun NoBikesHomeScreenPreview(
    @PreviewParameter(provider = NoBikesHomeUiStateProvider::class) homeUiState: HomeUiState
) {
    BasePreviewContainer {
        HomeScreen(
            name = "John Doe",
            uiState = homeUiState
        )
    }
}

@DarkLightPreviews
@Composable
private fun NoAppointmentsHomeScreenPreview(
    @PreviewParameter(provider = NoAppointmentsHomeUiStateProvider::class) homeUiState: HomeUiState
) {
    BasePreviewContainer {
        HomeScreen(
            name = "John Doe",
            uiState = homeUiState
        )
    }
}

@DarkLightPreviews
@Composable
private fun LoadingHomeScreenPreview(
    @PreviewParameter(provider = LoadingHomeUiStateProvider::class) homeUiState: HomeUiState
) {
    BasePreviewContainer {
        HomeScreen(
            name = "John Doe",
            uiState = homeUiState
        )
    }
}
