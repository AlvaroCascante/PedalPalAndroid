package com.quetoquenana.and.features.bikes.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import com.quetoquenana.and.features.bikes.domain.model.StravaBike
import androidx.core.net.toUri

@Composable
fun StravaImportRoute(
    modifier: Modifier = Modifier,
    onNavigateToCreateBike: (StravaBike) -> Unit,
    viewModel: StravaImportViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.startConnectionIfNeeded()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is StravaImportViewModel.StravaImportEvent.OpenBrowser -> {
                    context.startActivity(Intent(Intent.ACTION_VIEW, event.url.toUri()))
                }
                is StravaImportViewModel.StravaImportEvent.NavigateToCreateBike -> {
                    onNavigateToCreateBike(event.bike)
                }
                is StravaImportViewModel.StravaImportEvent.ShowError -> {
                    snackBarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onAppResumedAfterAuth()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    StravaImportScreen(
        modifier = modifier,
        uiState = uiState,
        snackBarHostState = snackBarHostState,
        onConnectClicked = viewModel::connectToStrava,
        onBikeClicked = viewModel::onStravaBikeSelected
    )
}

@Composable
fun StravaImportScreen(
    modifier: Modifier = Modifier,
    uiState: StravaImportUiState,
    snackBarHostState: SnackbarHostState = SnackbarHostState(),
    onConnectClicked: () -> Unit = {},
    onBikeClicked: (StravaBike) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Import from Strava")

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onConnectClicked,
            enabled = !uiState.isConnecting && !uiState.isLoadingBikes,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = when {
                    uiState.isConnecting -> "Opening Strava..."
                    uiState.isLoadingBikes -> "Loading bikes..."
                    else -> "Connect Strava"
                }
            )
        }

        if (uiState.isWaitingForAuthorization) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Finish authorization in the browser, then return to the app.")
        }

        if (uiState.bikes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Choose a Strava bike")
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.bikes, key = { it.id }) { bike ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onBikeClicked(bike) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = bike.name)
                            bike.nickname?.let { Text(text = "Nickname: $it") }
                            Text(text = "Primary: ${if (bike.primary) "Yes" else "No"}")
                            Text(text = "Retired: ${if (bike.retired) "Yes" else "No"}")
                            bike.distance?.let { Text(text = "Distance: $it") }
                        }
                    }
                }
            }
        }

        SnackbarHost(hostState = snackBarHostState)
    }
}

@Preview(showSystemUi = true)
@Composable
private fun StravaImportScreenPreview() {
    PedalPalTheme {
        StravaImportScreen(
            uiState = StravaImportUiState(
                bikes = listOf(
                    StravaBike(
                        id = "1",
                        name = "Strava Road Bike",
                        nickname = "Fast one",
                        primary = true,
                        retired = false,
                        distance = 1200.0
                    )
                )
            )
        )
    }
}

