package com.quetoquenana.and.features.bikes.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        floatingActionButton = {
            if (uiState.bikes.isNotEmpty()) {
                ExtendedFloatingActionButton(onClick = onAddBikeClick) {
                    Text(text = "Add bike")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Your bikes", style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "Filter by type, inspect components, and review each bike history.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            when {
                uiState.isLoading -> item { Text(text = "Loading bikes...") }
                uiState.bikes.isEmpty() -> item {
                    FirstBikeEmptyState(
                        onCreateManuallyClick = onAddBikeClick,
                        onImportFromStravaClick = onImportFromStravaClick
                    )
                }
                else -> {
                    item {
                        BikeTypeChips(
                            selectedType = uiState.selectedType,
                            onTypeSelected = onTypeSelected
                        )
                    }

                    if (uiState.filteredBikes.isEmpty()) {
                        item {
                            Text(
                                text = "No ${uiState.selectedType?.toDisplayName().orEmpty()} bikes yet.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        items(uiState.filteredBikes, key = { it.id }) { bike ->
                            BikeCard(
                                bike = bike,
                                onClick = { onBikeClick(bike.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BikeTypeChips(
    selectedType: BikeType?,
    onTypeSelected: (BikeType?) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
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
                label = { Text(text = type.toDisplayName()) }
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

        FirstBikeActionCard(
            title = "Create from scratch",
            description = "Add the bike details yourself. Best when this bike is not in Strava yet.",
            actionText = "Create manually",
            onClick = onCreateManuallyClick
        )

        FirstBikeActionCard(
            title = "Import from Strava",
            description = "Connect Strava, choose existing gear, then review and save it in PedalPal.",
            actionText = "Connect Strava",
            onClick = onImportFromStravaClick
        )
    }
}

@Composable
private fun FirstBikeActionCard(
    title: String,
    description: String,
    actionText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = description)
            Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
                Text(text = actionText)
            }
        }
    }
}

@Composable
private fun BikeCard(
    bike: Bike,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = bike.name, style = MaterialTheme.typography.titleMedium)
            Text(text = bike.type.toDisplayType(), style = MaterialTheme.typography.bodyMedium)
            listOfNotNull(bike.brand, bike.model).joinToString(" ").takeIf { it.isNotBlank() }?.let {
                Text(text = it, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(text = "${bike.components.size} components · ${bike.odometerKm.toInt()} km")
            if (bike.isExternalSync) {
                Text(text = "Synced from ${bike.externalSyncProvider}")
            }
        }
    }
}

fun BikeType.toDisplayName(): String {
    return name.toDisplayType()
}

private fun String.toDisplayType(): String {
    return lowercase()
        .split("_", "-", " ")
        .filter { it.isNotBlank() }
        .joinToString(separator = " ") { part ->
            part.replaceFirstChar { it.uppercase() }
        }
}

@Preview(showSystemUi = true)
@Composable
private fun BikesScreenContentPreview() {
    PedalPalTheme {
        BikesScreen(
            modifier = Modifier.fillMaxSize(),
            uiState = BikesUiState(
                bikes = listOf(
                    Bike(
                        id = "1",
                        name = "Trek Domane",
                        type = "ROAD",
                        status = "ACTIVE",
                        isPublic = false,
                        isExternalSync = false,
                        brand = "Trek",
                        model = "Domane AL 2",
                        year = 2024,
                        serialNumber = null,
                        notes = null,
                        odometerKm = 120.0,
                        usageTimeMinutes = 0,
                        externalGearId = null,
                        externalSyncProvider = ""
                    )
                )
            )
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun BikesScreenEmptyPreview() {
    PedalPalTheme {
        BikesScreen(
            modifier = Modifier.fillMaxSize(),
            uiState = BikesUiState()
        )
    }
}
