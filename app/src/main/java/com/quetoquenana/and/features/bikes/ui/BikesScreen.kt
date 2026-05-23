package com.quetoquenana.and.features.bikes.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.quetoquenana.and.R
import com.quetoquenana.and.core.ui.components.DarkLightPreviews
import com.quetoquenana.and.core.ui.components.FonsScalePreviews
import com.quetoquenana.and.core.ui.components.StickyBottomCta
import com.quetoquenana.and.core.ui.components.previewBike
import com.quetoquenana.and.core.ui.components.previewBikes
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.BikeType

@Composable
fun BikesRoute(
    modifier: Modifier = Modifier,
    onNavigateAddBike: () -> Unit,
    onNavigateStravaImport: () -> Unit,
    onNavigateBikeDetail: (String) -> Unit,
    viewModel: BikesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
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
        onBikeClick = onNavigateBikeDetail
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
    onBikeClick: (String) -> Unit = {}
) {
    val shouldShowStickyAddBike = uiState.bikes.isNotEmpty()

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        bottomBar = {
            if (shouldShowStickyAddBike) {
                StickyBottomCta(
                    text = "Add bike",
                    onClick = onAddBikeClick
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                BikesBanner()
            }

            when {
                uiState.isLoading -> item {
                    Text(
                        text = "Loading bikes...",
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
                uiState.bikes.isEmpty() -> item {
                    FirstBikeEmptyState(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        onCreateManuallyClick = onAddBikeClick,
                        onImportFromStravaClick = onImportFromStravaClick
                    )
                }
                else -> {
                    item {
                        BikeTypeChips(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            selectedType = uiState.selectedType,
                            onTypeSelected = onTypeSelected
                        )
                    }

                    if (uiState.filteredBikes.isEmpty()) {
                        item {
                            Text(
                                text = "No ${uiState.selectedType?.toBikeTypeDisplayName().orEmpty()} bikes yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    } else {
                        item {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("bikes-row"),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(horizontal = 24.dp)
                            ) {
                                items(uiState.filteredBikes, key = { it.id }) { bike ->
                                    BikeCard(
                                        modifier = Modifier.width(320.dp),
                                        bike = bike,
                                        profileImageUrl = uiState.bikeProfileImageUrls[bike.id],
                                        onClick = { onBikeClick(bike.id) }
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

@Composable
private fun BikesBanner(modifier: Modifier = Modifier) {
    val isInPreview = androidx.compose.ui.platform.LocalInspectionMode.current

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        if (isInPreview) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Bikes banner",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Image(
                painter = painterResource(id = R.drawable.bn_bike_pop),
                contentDescription = "Bikes banner",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
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
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        item(key = "all") {
            FilterChip(
                selected = selectedType == null,
                onClick = { onTypeSelected(null) },
                label = { Text(text = "All") }
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
private fun FirstBikeEmptyState(
    onCreateManuallyClick: () -> Unit,
    onImportFromStravaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "No bikes yet", style = MaterialTheme.typography.titleMedium)
        Text(text = "Create your first bike manually or import your Strava gear to start tracking service and usage.")

        CreateBikeManuallyCard(
            onClick = onCreateManuallyClick
        )

        ImportFromStravaBikeCard(
            onClick = onImportFromStravaClick
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
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BikeProfileImage(
                imageUrl = profileImageUrl,
                bikeName = bike.name
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = bike.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = bike.type.toBikeDisplayType(), style = MaterialTheme.typography.bodyMedium)
                listOfNotNull(bike.brand, bike.model).joinToString(" ").takeIf { it.isNotBlank() }?.let {
                    Text(text = it, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                bike.year?.let { year ->
                    Text(text = year.toString(), style = MaterialTheme.typography.bodySmall)
                }
                Text(text = "${bike.odometerKm.toInt()} km · ${bike.usageTimeMinutes.toTrackedUsageLabel()}")
                if (bike.isExternalSync) {
                    Text(text = "Synced from ${bike.externalSyncProvider}")
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
    val request = remember(imageUrl) {
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
                    .aspectRatio(1f),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                placeholder = fallbackPainter,
                error = fallbackPainter,
                fallback = fallbackPainter
            )
        }
    }
}

private fun Int.toTrackedUsageLabel(): String = when {
    this <= 0 -> "0 min tracked"
    this < 60 -> "$this min tracked"
    this % 60 == 0 -> "${this / 60} h tracked"
    else -> "${this / 60} h ${this % 60} min tracked"
}

@DarkLightPreviews
@Composable
private fun BikeCardPreview() {
    PedalPalTheme {
        BikeCard(
            bike = previewBike,
            profileImageUrl = null,
            modifier = Modifier.padding(16.dp),
            onClick = {}
        )
    }
}

@FonsScalePreviews
@Composable
private fun FirstBikeEmptyStatePreview() {
    PedalPalTheme {
        FirstBikeEmptyState(
            modifier = Modifier.padding(16.dp),
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
                selectedType = BikeType.ROAD
            )
        )
    }
}

