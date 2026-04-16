package com.quetoquenana.and.features.bikes.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.model.BikeComponent

private const val NewComponentId = "new"

@Composable
fun BikeDetailRoute(
    modifier: Modifier = Modifier,
    onNavigateHistory: (String) -> Unit,
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
                            onHistoryClick = { onHistoryClick(bike) }
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Components",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Button(onClick = { onAddComponentClick(bike) }) {
                                Text(text = "Add")
                            }
                        }
                    }

                    if (bike.components.isEmpty()) {
                        item {
                            EmptyComponentsCard(onAddComponentClick = { onAddComponentClick(bike) })
                        }
                    } else {
                        items(bike.components, key = { it.id }) { component ->
                            BikeComponentCard(
                                component = component,
                                onClick = { onComponentClick(bike, component) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BikeHeaderCard(
    bike: Bike,
    onHistoryClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
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
                onClick = onHistoryClick
            ) {
                Text(text = "View bike history")
            }
        }
    }
}

@Composable
private fun BikeComponentCard(
    component: BikeComponent,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
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
