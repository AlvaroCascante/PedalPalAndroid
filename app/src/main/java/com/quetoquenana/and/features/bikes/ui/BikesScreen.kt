package com.quetoquenana.and.features.bikes.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.quetoquenana.and.core.ui.components.BottomBar
import com.quetoquenana.and.core.ui.navigation.Bikes
import com.quetoquenana.and.core.ui.navigation.shouldShowBottomBar
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import com.quetoquenana.and.features.bikes.domain.model.Bike

@Composable
fun BikesRoute(
    modifier: Modifier = Modifier,
    onNavigateAddBike: () -> Unit,
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
        onAddBikeClick = onNavigateAddBike
    )
}

@Composable
fun BikesScreen(
    modifier: Modifier = Modifier,
    uiState: BikesUiState,
    snackBarHostState: SnackbarHostState = SnackbarHostState(),
    onAddBikeClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Your bikes")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onAddBikeClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Add bike")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            uiState.isLoading -> {
                Text(text = "Loading bikes...")
            }

            uiState.bikes.isEmpty() -> {
                Text(text = "No bikes yet")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Create your first bike to start tracking service and usage.")
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.bikes, key = { it.id }) { bike ->
                        BikeCard(bike = bike)
                    }
                }
            }
        }

        SnackbarHost(hostState = snackBarHostState)
    }
}

@Composable
private fun BikeCard(
    bike: Bike,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = bike.name)
            Text(text = "Type: ${bike.type}")
            bike.brand?.let { Text(text = "Brand: $it") }
            bike.model?.let { Text(text = "Model: $it") }
            bike.year?.let { Text(text = "Year: $it") }
            if (bike.isPublic) {
                Text(text = "Visible to others")
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun BikesScreenContentPreview() {
    PedalPalTheme {
        val navController = rememberNavController()
        val currentRoute = Bikes.route
        val showBottomBar = shouldShowBottomBar(currentRoute)

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
            BikesScreen(
                modifier = Modifier
                    .padding(paddingValues = paddingValues)
                    .fillMaxSize(),
                uiState = BikesUiState(
                    bikes = listOf(
                        Bike(
                            id = "1",
                            name = "Trek Domane",
                            type = "Road",
                            status = "ACTIVE",
                            isPublic = false,
                            isExternalSync = false,
                            brand = "Trek",
                            model = "Domane AL 2",
                            year = 2024,
                            serialNumber = null,
                            notes = null,
                            odometerKm = 0.0,
                            usageTimeMinutes = 0,
                            externalGearId = null,
                            externalSyncProvider = ""
                        )
                    )
                )
            )
        }
    }
}