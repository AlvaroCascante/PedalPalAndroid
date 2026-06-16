package com.quetoquenana.and.features.bikes.ui

import android.content.Intent
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.quetoquenana.and.R
import com.quetoquenana.and.core.ui.components.stravaImportUiState
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import com.quetoquenana.and.features.bikes.domain.model.StravaBike

@Composable
fun StravaImportRoute(
    modifier: Modifier = Modifier,
    onNavigateToCreateBike: (StravaBike) -> Unit,
    viewModel: StravaImportViewModel = hiltViewModel(),
    fromDeepLink: Boolean = false
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(fromDeepLink) {
        if (fromDeepLink) {
            // Opened via App Link / deep link — try to load bikes directly
            viewModel.loadStravaBikes()
        } else {
            viewModel.startConnectionIfNeeded()
        }
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
        onBikeClicked = viewModel::onStravaBikeSelected
    )
}

@Composable
fun StravaImportScreen(
    modifier: Modifier = Modifier,
    uiState: StravaImportUiState,
    snackBarHostState: SnackbarHostState = SnackbarHostState(),
    onBikeClicked: (StravaBike) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(all = 24.dp),
        verticalArrangement = Arrangement.Top
    ) {

        if (uiState.isWaitingForAuthorization) {
            Spacer(modifier = Modifier.height(height = 12.dp))
            Text(text = stringResource(id = R.string.finish_strava_authorization))
        }

        if (uiState.bikes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(height = 16.dp))
            Text(text = stringResource(id = R.string.choose_strava_bike))
            Spacer(modifier = Modifier.height(height = 8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(space = 12.dp)) {
                items(uiState.bikes, key = { it.id }) { bike ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onBikeClicked(bike) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(all = 16.dp)) {
                            Text(text = bike.name)
                            bike.nickname?.let { Text(text = stringResource(id = R.string.bike_nickname, it)) }
                            Text(text = stringResource(
                                id = R.string.bike_primary,
                                if (bike.primary) "Yes" else "No"
                            ))
                            Text(text = stringResource(
                                id = R.string.bike_retired,
                                if (bike.retired) "Yes" else "No"
                            ))
                            bike.distance?.let { Text(text = stringResource(id = R.string.bike_distance, it)) }
                            bike.brandName?.let { Text(text = stringResource(id = R.string.bike_brand, it)) }
                            bike.modelName?.let { Text(text = stringResource(id = R.string.bike_model, it)) }
                            bike.description?.let { Text(text = stringResource(id = R.string.bike_description, it)) }
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
            uiState = stravaImportUiState
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun StravaImportScreenPreview1() {
    PedalPalTheme {
        StravaImportScreen(
            uiState = StravaImportUiState(
                isWaitingForAuthorization = true
            )
        )
    }
}
