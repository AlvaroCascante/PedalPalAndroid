package com.quetoquenana.and.features.bikes.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.quetoquenana.and.R
import com.quetoquenana.and.core.ui.components.BasePreviewContainer
import com.quetoquenana.and.core.ui.components.BikesUiStateProvider
import com.quetoquenana.and.core.ui.components.DarkLightPreviews
import com.quetoquenana.and.core.ui.components.DefaultProgressIndicator
import com.quetoquenana.and.core.ui.components.LoadingBikesUiStateProvider
import com.quetoquenana.and.core.ui.components.NoBikesUiStateProvider
import com.quetoquenana.and.core.ui.components.StickyBottomCta
import com.quetoquenana.and.core.ui.components.defaultContainerPaddingValues
import com.quetoquenana.and.core.ui.components.defaultPaddingValues
import com.quetoquenana.and.core.ui.components.sharedCardShape
import com.quetoquenana.and.core.ui.components.sharedSectionTopShape
import com.quetoquenana.and.core.ui.components.sharedSectionTopShapeM
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.BikeType
import java.util.UUID

@Composable
fun BikesRoute(
    onNavigateAddBike: () -> Unit,
    onNavigateStravaImport: () -> Unit,
    onNavigateBikeDetail: (UUID) -> Unit,
    viewModel: BikesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is BikesViewModel.BikesEvent.ShowError -> snackBarHostState.showSnackbar(event.message)
            }
        }
    }

    BikesScreen(
        uiState = uiState,
        onTypeSelected = viewModel::onTypeSelected,
        onAddBikeClicked = onNavigateAddBike,
        onImportFromStravaClicked = onNavigateStravaImport,
        onBikeClicked = onNavigateBikeDetail,
        onRefresh = viewModel::refreshBikes
    )
}

@Composable
fun BikesScreen(
    uiState: BikesUiState,
    onTypeSelected: (BikeType?) -> Unit = {},
    onAddBikeClicked: () -> Unit = {},
    onImportFromStravaClicked: () -> Unit = {},
    onBikeClicked: (UUID) -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    val baseModifier = Modifier
        .fillMaxSize()
        .background(color = MaterialTheme.colorScheme.primary)

    val pullToRefreshState = rememberPullToRefreshState()

    when {
        uiState.isLoading -> {
            Box(
                modifier = baseModifier,
                contentAlignment = Alignment.Center
            ) {
                DefaultProgressIndicator()
            }
        }

        else -> {
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefresh,
                modifier = baseModifier,
                state = pullToRefreshState,
                indicator = {
                    Indicator(
                        modifier = Modifier.align(Alignment.TopCenter),
                        isRefreshing = uiState.isRefreshing,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        state = pullToRefreshState
                    )
                }
            ) {
                LazyColumn(
                    modifier = baseModifier,
                    contentPadding = WindowInsets.systemBars.asPaddingValues(),
                    verticalArrangement = spacedBy(16.dp)
                ) {
                    item {
                        BikesHeaderSection(
                            modifier = baseModifier
                        )
                    }
                    item {
                        BikesDataSection(
                            uiState = uiState,
                            onTypeSelected = onTypeSelected,
                            onBikeClicked = onBikeClicked,
                            onAddBikeClicked = onAddBikeClicked,
                            onImportFromStravaClicked = onImportFromStravaClicked
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BikesHeaderSection(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(defaultPaddingValues),
        verticalArrangement = spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.bikes),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = stringResource(id = R.string.bikes_header_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun BikesDataSection(
    uiState: BikesUiState,
    onTypeSelected: (BikeType?) -> Unit,
    onAddBikeClicked: () -> Unit = {},
    onBikeClicked: (UUID) -> Unit = {},
    onImportFromStravaClicked: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = sharedSectionTopShape
    ) {
        Column(
            modifier = Modifier
                .padding(defaultContainerPaddingValues)
                .fillMaxWidth(),
            verticalArrangement = spacedBy(12.dp)
        ) {
            if (uiState.bikes.isEmpty()) {
                NoBikesSection(
                    onCreateManuallyClicked = onAddBikeClicked,
                    onImportFromStravaClicked = onImportFromStravaClicked
                )
            } else {
                if (uiState.filteredBikes.isEmpty()) {
                    Text(
                        text = stringResource(
                            id = R.string.no_bikes_type_yet,
                            uiState.selectedType?.toBikeTypeDisplayName().orEmpty()
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
                BikesSection(
                    onBikeClicked = onBikeClicked,
                    onTypeSelected = onTypeSelected,
                    uiState = uiState
                )

                StickyBottomCta(
                    onClick = onAddBikeClicked,
                    text = stringResource(id = R.string.add_bike)
                )
            }
        }
    }
}

@Composable
private fun BikesSection(
    onBikeClicked: (UUID) -> Unit = {},
    onTypeSelected: (BikeType?) -> Unit,
    uiState: BikesUiState,
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = sharedSectionTopShape
    ) {
        Column(
            modifier = Modifier
                .padding(defaultContainerPaddingValues)
                .fillMaxWidth(),
            verticalArrangement = spacedBy(space = 12.dp)
        ) {
            BikeTypeChips(
                selectedType = uiState.selectedType,
                onTypeSelected = onTypeSelected
            )
            BikesRow(
                bikes = uiState.filteredBikes,
                uiState = uiState,
                onBikeClicked = onBikeClicked
            )
        }
    }
}

@Composable
private fun BikeTypeChips(
    modifier: Modifier = Modifier,
    selectedType: BikeType?,
    onTypeSelected: (BikeType?) -> Unit
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = spacedBy(space = 8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        item(key = "all") {
            FilterChip(
                selected = selectedType == null,
                onClick = { onTypeSelected(null) },
                label = {
                    Text(
                        text = stringResource(id = R.string.all),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )
        }
        items(BikeType.entries, key = { it.name }) { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.secondary
                ),
                label = {
                    Text(
                        text = type.toBikeTypeDisplayName(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )
        }
    }
}

@Composable
private fun BikesRow(
    bikes: List<Bike>,
    uiState: BikesUiState,
    onBikeClicked: (UUID) -> Unit = {},
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = spacedBy(space = 12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        items(items = bikes, key = { it.id }) { bike ->
            BikeCard(
                bike = bike,
                profileImageUrl = uiState.bikeProfileImageUrls[bike.id],
                onClick = { onBikeClicked(bike.id) }
            )
        }
    }
}

@Composable
fun NoBikesSection(
    onCreateManuallyClicked: () -> Unit,
    onImportFromStravaClicked: () -> Unit
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
                text = stringResource(id = R.string.no_bikes),
                modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = stringResource(id = R.string.create_your_first_bike),
                modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            ImportFromStravaBikeCard(
                onClick = onImportFromStravaClicked
            )

            CreateBikeManuallyCard(
                onClick = onCreateManuallyClicked,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
private fun BikeCard(
    bike: Bike,
    profileImageUrl: String?,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(width = 200.dp)
            .height(height = 250.dp)
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
                    .padding(all = 12.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = sharedSectionTopShapeM)
                    .fillMaxWidth()
                    .weight(weight = 0.50f)
            ) {
                BikeProfileImage(
                    imageUrl = profileImageUrl,
                    bikeName = bike.name
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 0.50f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = spacedBy(space = 8.dp)
            ) {
                Text(
                    text = bike.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = bike.type.toBikeDisplayType(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                listOfNotNull(bike.brand, bike.model)
                    .joinToString(separator = " ")
                    .takeIf { it.isNotBlank() }
                    ?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                bike.year?.let { year ->
                    Text(
                        text = year.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = stringResource(
                        id = R.string.bike_usage,
                        bike.odometerKm.toInt(),
                        bike.usageTimeMinutes.toTrackedUsageLabel()
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (bike.isExternalSync) {
                    Text(
                        text = stringResource(
                            id = R.string.synced_from,
                            bike.externalSyncProvider
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun BikeProfileImage(
    imageUrl: String?,
    bikeName: String
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val fallbackPainter = painterResource(id = R.drawable.mobi_bike_logo)
    val request = remember(key1 = imageUrl) {
        imageUrl?.takeIf { it.isNotBlank() }?.let { url ->
            ImageRequest.Builder(context)
                .data(url)
                .build()
        }
    }
    AsyncImage(
        model = request,
        contentDescription = "$bikeName profile image",
        modifier = Modifier.fillMaxSize().padding(all = 4.dp),
        contentScale = ContentScale.Crop,
        placeholder = fallbackPainter,
        error = fallbackPainter,
        fallback = fallbackPainter
    )
}

@Composable
private fun Int.toTrackedUsageLabel(): String = when {
    this <= 0 -> stringResource(id = R.string.no_min_tracked)
    this < 60 -> stringResource(id = R.string.bike_min_tracked, this)
    this % 60 == 0 -> stringResource(id = R.string.hours_tracked, this / 60)
    else -> stringResource(id = R.string.hours_minutes_tracked, this / 60, this % 60)
}

@DarkLightPreviews
@Composable
private fun BikesScreenPreview(
    @PreviewParameter(provider = BikesUiStateProvider::class) bikesUiState: BikesUiState
) {
    BasePreviewContainer {
        BikesScreen(
            uiState = bikesUiState
        )
    }
}

@DarkLightPreviews
@Composable
private fun NoBikesScreenPreview(
    @PreviewParameter(provider = NoBikesUiStateProvider::class) bikesUiState: BikesUiState
) {
    BasePreviewContainer {
        BikesScreen(
            uiState = bikesUiState
        )
    }
}

@DarkLightPreviews
@Composable
private fun LoadingBikesScreenPreview(
    @PreviewParameter(provider = LoadingBikesUiStateProvider::class) bikesUiState: BikesUiState
) {
    BasePreviewContainer {
        BikesScreen(
            uiState = bikesUiState
        )
    }
}