package com.quetoquenana.and.features.bikes.ui

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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.BikeComponent

private const val NewComponentId = "new"
private const val BikeComponentsRowTag = "bike-components-row"
private val StravaOrange = Color(0xFFFC4C02)

@Composable
fun BikeDetailRoute(
    modifier: Modifier = Modifier,
    onNavigateHistory: (String) -> Unit,
    onNavigateBikeImages: (String) -> Unit,
    onNavigateStravaSync: () -> Unit,
    onNavigateComponentOptions: (String, String) -> Unit,
    viewModel: BikeDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    BikeDetailScreen(
        modifier = modifier,
        uiState = uiState,
        onRetryClick = viewModel::loadBike,
        onHistoryClick = { bike ->
            onNavigateHistory(bike.id)
        },
        onViewImagesClick = { bike ->
            onNavigateBikeImages(bike.id)
        },
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
    onRetryClick: () -> Unit = {},
    onHistoryClick: (Bike) -> Unit = {},
    onViewImagesClick: (Bike) -> Unit = {},
    onStravaSyncClick: (Bike) -> Unit = {},
    onAddComponentClick: (Bike) -> Unit = {},
    onComponentClick: (Bike, BikeComponent) -> Unit = { _, _ -> }
) {
    Scaffold(modifier = modifier) { paddingValues ->
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

                uiState.bike != null -> {
                    val bike = uiState.bike

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        BikeHeaderCard(
                            bike = bike,
                            onHistoryClick = { onHistoryClick(bike) },
                            onViewImagesClick = { onViewImagesClick(bike) },
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
                            EmptyComponentsCard(onAddComponentClick = { onAddComponentClick(bike) })
                        }
                    } else {
                        item {
                            ComponentsRow(
                                bike = bike,
                                onComponentClick = onComponentClick
                            )
                        }
                        item {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { onAddComponentClick(bike) }
                            ) {
                                Text(text = "Add component")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ComponentsRow(
    bike: Bike,
    onComponentClick: (Bike, BikeComponent) -> Unit,
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
    onHistoryClick: () -> Unit,
    onViewImagesClick: () -> Unit,
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
                    onHistoryClick = onHistoryClick,
                    onViewImagesClick = onViewImagesClick,
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
        Text(text = "${bike.odometerKm.toInt()} km · ${bike.usageTimeMinutes / 60} h tracked")
        bike.serialNumber?.takeIf { it.isNotBlank() }?.let {
            Text(text = "Serial: $it", maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        bike.notes?.takeIf { it.isNotBlank() }?.let {
            Text(text = it)
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
    onHistoryClick: () -> Unit,
    onViewImagesClick: () -> Unit,
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
        if (!isExternalSync) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StravaOrange,
                    contentColor = Color.White
                ),
                onClick = onStravaSyncClick
            ) {
                Text(text = "Sync with Strava")
            }
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
    component: BikeComponent,
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
            Text(text = component.name, style = MaterialTheme.typography.titleMedium)
            Text(text = component.type.toDisplayLabel())
            listOfNotNull(component.brand, component.model)
                .joinToString(" ")
                .takeIf { it.isNotBlank() }
                ?.let { Text(text = it, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            Text(text = "Status: ${component.status.toDisplayLabel()}")
            Text(text = "${component.odometerKm} km · ${component.usageTimeMinutes / 60} h")
        }
    }
}

@Composable
private fun EmptyComponentsCard(onAddComponentClick: () -> Unit) {
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
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onAddComponentClick
            ) {
                Text(text = "Add first component")
            }
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
