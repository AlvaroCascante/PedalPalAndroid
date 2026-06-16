package com.quetoquenana.and.features.bikes.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateStartPadding
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.quetoquenana.and.R
import com.quetoquenana.and.core.ui.components.DarkLightPreviews
import com.quetoquenana.and.core.ui.components.FonsScalePreviews
import com.quetoquenana.and.core.ui.components.RegularProgressIndicator
import com.quetoquenana.and.core.ui.components.StickyBottomCta
import com.quetoquenana.and.core.ui.components.previewBike
import com.quetoquenana.and.core.ui.components.previewBikes
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.BikeType
import java.util.UUID

@Composable
fun BikesRoute(
    modifier: Modifier = Modifier,
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
        modifier = modifier,
        uiState = uiState,
        snackBarHostState = snackBarHostState,
        onTypeSelected = viewModel::onTypeSelected,
        onAddBikeClick = onNavigateAddBike,
        onImportFromStravaClick = onNavigateStravaImport,
        onBikeClick = onNavigateBikeDetail,
        onRefresh = viewModel::refreshBikes
    )
}

@Composable
fun BikesScreen(
    modifier: Modifier = Modifier,
    uiState: BikesUiState,
    snackBarHostState: SnackbarHostState = SnackbarHostState(),
    onTypeSelected: (BikeType?) -> Unit = {},
    onAddBikeClick: () -> Unit = {},
    onImportFromStravaClick: () -> Unit = {},
    onBikeClick: (UUID) -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    val shouldShowStickyAddBike = uiState.bikes.isNotEmpty()

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        bottomBar = {
            if (shouldShowStickyAddBike) {
                StickyBottomCta(
                    text = stringResource(id = R.string.add_bike),
                    onClick = onAddBikeClick
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
            .fillMaxSize()
        ) {
            BikesBanner()
            when {
                uiState.isLoading -> { RegularProgressIndicator() }

                uiState.bikes.isEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
                    ) { item  {
                            FirstBikeEmptyState(
                                modifier = Modifier.padding(horizontal = 24.dp),
                                onCreateManuallyClick = onAddBikeClick,
                                onImportFromStravaClick = onImportFromStravaClick
                            )
                        }
                    }
                } else -> {
                    BikeTypeChips(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                        selectedType = uiState.selectedType,
                        onTypeSelected = onTypeSelected
                    )
                    if (uiState.filteredBikes.isEmpty()) {
                        Text(
                            text = stringResource(
                                id = R.string.no_bikes_type_yet,
                                uiState.selectedType?.toBikeTypeDisplayName().orEmpty()
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    } else {
                        BikeList(
                            uiState = uiState,
                            paddingValues = paddingValues,
                            isRefreshing = uiState.isRefreshing,
                            onRefresh = onRefresh,
                            modifier = Modifier.padding(top = 8.dp),
                            onBikeClick = onBikeClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BikeList(
    uiState: BikesUiState,
    paddingValues: PaddingValues,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    onBikeClick: (UUID) -> Unit = {}
) {
    val state = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        state = state,
        indicator = {
            Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isRefreshing,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                state = state
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(uiState.filteredBikes, key = { it.id }) { bike ->
                BikeCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    bike = bike,
                    profileImageUrl = uiState.bikeProfileImageUrls[bike.id],
                    onClick = { onBikeClick(bike.id) }
                )
            }
        }
    }
}

@Composable
private fun BikesBanner(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.bn_bike_pop),
            contentDescription = "Bikes banner",
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 160.dp),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
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
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        item(key = "all") {
            FilterChip(
                selected = selectedType == null,
                onClick = { onTypeSelected(null) },
                label = { Text(text = stringResource(id = R.string.all)) }
            )
        }
        items(BikeType.entries, key = { it.name }) { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = { Text(text = type.toBikeTypeDisplayName()) }
            )
        }
    }
}

@Composable
fun FirstBikeEmptyState(
    onCreateManuallyClick: () -> Unit,
    onImportFromStravaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = stringResource(id = R.string.no_bikes), style = MaterialTheme.typography.titleMedium)
        Text(text = stringResource(id = R.string.create_your_first_bike))

        ImportFromStravaBikeCard(
            onClick = onImportFromStravaClick
        )

        CreateBikeManuallyCard(
            onClick = onCreateManuallyClick
        )
    }
}

@Composable
private fun BikeCard(
    bike: Bike,
    profileImageUrl: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(all = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BikeProfileImage(
                imageUrl = profileImageUrl,
                bikeName = bike.name
            )

            Column(
                modifier = Modifier.weight(weight = 1f),
                verticalArrangement = Arrangement.spacedBy(space = 4.dp)
            ) {
                Text(text = bike.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = bike.type.toBikeDisplayType(), style = MaterialTheme.typography.bodyMedium)
                listOfNotNull(bike.brand, bike.model)
                    .joinToString(separator = " ")
                    .takeIf { it.isNotBlank() }
                    ?.let {
                        Text(text = it, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                bike.year?.let { year ->
                    Text(text = year.toString(), style = MaterialTheme.typography.bodySmall)
                }
                Text(text = stringResource(
                    id = R.string.bike_usage,
                    bike.odometerKm.toInt(),
                    bike.usageTimeMinutes.toTrackedUsageLabel()
                ))
                if (bike.isExternalSync) {
                    Text(text = stringResource(
                        id = R.string.synced_from,
                        bike.externalSyncProvider
                    ))
                }
            }
        }
    }
}

@Composable
private fun BikeProfileImage(
    imageUrl: String?,
    bikeName: String,
    modifier: Modifier = Modifier
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

    Card(
        modifier = modifier.size(96.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.medium,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = request,
                contentDescription = "$bikeName profile image",
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(ratio = 1f),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                placeholder = fallbackPainter,
                error = fallbackPainter,
                fallback = fallbackPainter
            )
        }
    }
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
private fun BikeCardPreview() {
    PedalPalTheme {
        BikeCard(
            bike = previewBike,
            profileImageUrl = null,
            modifier = Modifier.padding(all = 16.dp),
            onClick = {}
        )
    }
}

@FonsScalePreviews
@Composable
private fun FirstBikeEmptyStatePreview() {
    PedalPalTheme {
        FirstBikeEmptyState(
            modifier = Modifier.padding(all = 16.dp),
            onCreateManuallyClick = {},
            onImportFromStravaClick = {}
        )
    }
}

@DarkLightPreviews
@Composable
private fun BikesScreenPreview() {
    PedalPalTheme {
        BikesScreen(
            modifier = Modifier.fillMaxSize(),
            uiState = BikesUiState(
                bikes = previewBikes,
                selectedType = null
            )
        )
    }
}

@DarkLightPreviews
@Composable
private fun BikesScreenPreviewRoad() {
    PedalPalTheme {
        BikesScreen(
            modifier = Modifier.fillMaxSize(),
            uiState = BikesUiState(
                bikes = previewBikes,
                selectedType = BikeType.ROAD
            )
        )
    }
}

@DarkLightPreviews
@Composable
private fun BikesScreenPreviewLoading() {
    PedalPalTheme {
        BikesScreen(
            modifier = Modifier.fillMaxSize(),
            uiState = BikesUiState(
                isLoading = true
            )
        )
    }
}

@DarkLightPreviews
@Composable
private fun BikesScreenPreviewEmpty() {
    PedalPalTheme {
        BikesScreen(
            modifier = Modifier.fillMaxSize(),
            uiState = BikesUiState(
                isLoading = false,
                bikes = emptyList()
            )
        )
    }
}

@DarkLightPreviews
@Composable
private fun BikesScreenPreviewFilteredEmpty() {
    PedalPalTheme {
        BikesScreen(
            modifier = Modifier.fillMaxSize(),
            uiState = BikesUiState(
                isLoading = false,
                bikes = previewBikes,
                selectedType = BikeType.MOUNTAIN
            )
        )
    }
}