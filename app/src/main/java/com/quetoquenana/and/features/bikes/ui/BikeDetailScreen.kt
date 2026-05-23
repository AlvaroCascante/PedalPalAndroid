package com.quetoquenana.and.features.bikes.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.quetoquenana.and.core.media.domain.model.MediaReferenceType
import com.quetoquenana.and.core.media.domain.model.toImageMediaUploadRequest
import com.quetoquenana.and.core.ui.components.StickyBottomCta
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.Component
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val NewComponentId = "new"
private const val BikeComponentsRowTag = "bike-components-row"

@Composable
fun BikeDetailRoute(
    modifier: Modifier = Modifier,
    onNavigateHistory: (String) -> Unit,
    onNavigateBikeImages: (String) -> Unit,
    onNavigateStravaSync: () -> Unit,
    onNavigateComponentOptions: (String, String) -> Unit,
    viewModel: BikeDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        coroutineScope.launch {
            val upload = withContext(Dispatchers.IO) {
                context.toImageMediaUploadRequest(
                    uri = uri,
                    purpose = MediaReferenceType.BIKE_PROFILE,
                )
            }
            if (upload != null) {
                viewModel.uploadProfileImage(upload)
            } else {
                snackBarHostState.showSnackbar("No valid image selected")
            }
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is BikeDetailViewModel.BikeDetailEvent.ShowError -> {
                    snackBarHostState.showSnackbar(event.message)
                }

                is BikeDetailViewModel.BikeDetailEvent.ShowMessage -> {
                    snackBarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    BikeDetailScreen(
        modifier = modifier,
        uiState = uiState,
        snackBarHostState = snackBarHostState,
        onRetryClick = viewModel::loadBike,
        onHistoryClick = { bike ->
            onNavigateHistory(bike.id)
        },
        onViewImagesClick = { bike ->
            onNavigateBikeImages(bike.id)
        },
        onSetProfileImageClick = { pickerLauncher.launch("image/*") },
        onStravaSyncClick = { onNavigateStravaSync() },
        onAddComponentClick = { bike ->
            onNavigateComponentOptions(bike.id, NewComponentId)
        },
        onComponentClick = { bike, component ->
            onNavigateComponentOptions(bike.id, component.id)
        }
    )
}

@Composable
fun BikeDetailScreen(
    modifier: Modifier = Modifier,
    uiState: BikeDetailUiState,
    snackBarHostState: SnackbarHostState = SnackbarHostState(),
    onRetryClick: () -> Unit = {},
    onHistoryClick: (Bike) -> Unit = {},
    onViewImagesClick: (Bike) -> Unit = {},
    onSetProfileImageClick: () -> Unit = {},
    onStravaSyncClick: (Bike) -> Unit = {},
    onAddComponentClick: (Bike) -> Unit = {},
    onComponentClick: (Bike, Component) -> Unit = { _, _ -> }
) {
    val bike = uiState.bike
    val shouldShowStickyAddComponent = bike != null && !uiState.isLoading && uiState.errorMessage == null

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        bottomBar = {
            if (shouldShowStickyAddComponent) {
                AddComponentBottomBar(
                    onAddComponentClick = { onAddComponentClick(checkNotNull(bike)) }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                uiState.isLoading -> item {
                    Text(text = "Loading bike...")
                }

                uiState.errorMessage != null -> item {
                    ErrorCard(
                        message = uiState.errorMessage,
                        onRetryClick = onRetryClick
                    )
                }

                bike != null -> {

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        BikeHeaderCard(
                            bike = bike,
                            isUploadingProfileImage = uiState.isUploadingProfileImage,
                            onHistoryClick = { onHistoryClick(bike) },
                            onViewImagesClick = { onViewImagesClick(bike) },
                            onSetProfileImageClick = onSetProfileImageClick,
                            onStravaSyncClick = { onStravaSyncClick(bike) }
                        )
                    }

                    item {
                        Text(
                            text = "Components",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    if (bike.components.isEmpty()) {
                        item {
                            EmptyComponentsCard()
                        }
                    } else {
                        item {
                            ComponentsRow(
                                bike = bike,
                                onComponentClick = onComponentClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddComponentBottomBar(
    onAddComponentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    StickyBottomCta(
        text = "Add component",
        onClick = onAddComponentClick,
        modifier = modifier
    )
}

@Composable
private fun ComponentsRow(
    bike: Bike,
    onComponentClick: (Bike, Component) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .testTag(BikeComponentsRowTag),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(bike.components, key = { it.id }) { component ->
            BikeComponentCard(
                component = component,
                modifier = Modifier.width(280.dp),
                onClick = { onComponentClick(bike, component) }
            )
        }
    }
}

@Composable
private fun BikeHeaderCard(
    bike: Bike,
    isUploadingProfileImage: Boolean,
    onHistoryClick: () -> Unit,
    onViewImagesClick: () -> Unit,
    onSetProfileImageClick: () -> Unit,
    onStravaSyncClick: () -> Unit
) {
    val isShowingOptions = rememberSaveable { mutableStateOf(false) }
    val rotationY by animateFloatAsState(
        targetValue = if (isShowingOptions.value) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "bikeHeaderFlip"
    )
    val density = LocalDensity.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.rotationY = rotationY
                cameraDistance = with(density) { 24.dp.toPx() }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (rotationY <= 90f) {
                BikeHeaderFront(
                    bike = bike,
                    onShowOptionsClick = { isShowingOptions.value = true }
                )
            } else {
                BikeHeaderBack(
                    modifier = Modifier.graphicsLayer { this.rotationY = 180f },
                    isExternalSync = bike.isExternalSync,
                    isUploadingProfileImage = isUploadingProfileImage,
                    onHistoryClick = onHistoryClick,
                    onViewImagesClick = onViewImagesClick,
                    onSetProfileImageClick = onSetProfileImageClick,
                    onStravaSyncClick = onStravaSyncClick,
                    onShowInfoClick = { isShowingOptions.value = false }
                )
            }
        }
    }
}

@Composable
private fun BikeHeaderFront(
    bike: Bike,
    onShowOptionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = bike.name, style = MaterialTheme.typography.headlineSmall)
        Text(text = bike.type.toDisplayLabel(), style = MaterialTheme.typography.bodyLarge)
        listOfNotNull(bike.brand, bike.model, bike.year?.toString())
            .joinToString(" ")
            .takeIf { it.isNotBlank() }
            ?.let { Text(text = it) }
        Text(text = "Status: ${bike.status.toDisplayLabel()}")
        Text(text = "Usage: ${bike.odometerKm.toInt()} km · ${bike.usageTimeMinutes / 60} h tracked")
        bike.serialNumber?.takeIf { it.isNotBlank() }?.let {
            Text(text = "Serial: $it", maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onShowOptionsClick
        ) {
            Text(text = "Options")
        }
    }
}

@Composable
private fun BikeHeaderBack(
    isExternalSync: Boolean,
    isUploadingProfileImage: Boolean,
    onHistoryClick: () -> Unit,
    onViewImagesClick: () -> Unit,
    onSetProfileImageClick: () -> Unit,
    onStravaSyncClick: () -> Unit,
    onShowInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Bike options", style = MaterialTheme.typography.headlineSmall)
        Text(text = "Choose an action for this bike.")
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onHistoryClick
        ) {
            Text(text = "View bike history")
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onViewImagesClick
        ) {
            Text(text = "View images")
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSetProfileImageClick,
            enabled = !isUploadingProfileImage
        ) {
            Text(text = if (isUploadingProfileImage) "Uploading profile image..." else "Set bike profile image")
        }
        if (!isExternalSync) {
            StravaBrandedButton(
                onClick = onStravaSyncClick,
                modifier = Modifier.fillMaxWidth(),
                contentDescription = "Sync with Strava"
            )
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onShowInfoClick
        ) {
            Text(text = "Back to info")
        }
    }
}

@Composable
private fun BikeComponentCard(
    component: Component,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = component.type.toDisplayLabel())
            listOfNotNull(component.brand, component.model)
                .joinToString(" ")
                .takeIf { it.isNotBlank() }
                ?.let { Text(text = it, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            Text(text = "${component.odometerKm} km · ${component.usageTimeMinutes / 60} h")
        }
    }
}

@Composable
private fun EmptyComponentsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "No components yet", style = MaterialTheme.typography.titleMedium)
            Text(text = "Add drivetrain, brakes, tires, suspension, or any part you want to track.")
        }
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onRetryClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Unable to load bike", style = MaterialTheme.typography.titleMedium)
            Text(text = message)
            Button(onClick = onRetryClick) {
                Text(text = "Retry")
            }
        }
    }
}

private fun String.toDisplayLabel(): String {
    return lowercase()
        .split("_", "-", " ")
        .filter { it.isNotBlank() }
        .joinToString(separator = " ") { part ->
            part.replaceFirstChar { it.uppercase() }
        }
}

@Preview(showSystemUi = true)
@Composable
private fun BikeDetailScreenPreview() {
    PedalPalTheme {
        BikeDetailScreen(
            modifier = Modifier.fillMaxSize(),
            uiState = BikeDetailUiState(
                bike = Bike(
                    id = "bike-1",
                    name = "Trek Domane",
                    type = "ROAD",
                    status = "ACTIVE",
                    isPublic = false,
                    isExternalSync = false,
                    brand = "Trek",
                    model = "Domane AL 2",
                    year = 2024,
                    serialNumber = "SN-001234",
                    notes = "Endurance setup for long weekend rides.",
                    odometerKm = 4280.0,
                    usageTimeMinutes = 8160,
                    externalGearId = null,
                    externalSyncProvider = "MANUAL",
                    components = listOf(
                        Component(
                            id = "component-1",
                            type = "CHAIN",
                            name = "Dura-Ace Chain",
                            status = "ACTIVE",
                            brand = "Shimano",
                            model = "CN-HG901",
                            notes = null,
                            odometerKm = 1800,
                            usageTimeMinutes = 3240
                        ),
                        Component(
                            id = "component-2",
                            type = "TIRES",
                            name = "GP5000 S TR",
                            status = "ACTIVE",
                            brand = "Continental",
                            model = "32mm",
                            notes = null,
                            odometerKm = 950,
                            usageTimeMinutes = 1560
                        )
                    )
                )
            )
        )
    }
}

