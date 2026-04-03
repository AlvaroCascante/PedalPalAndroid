package com.quetoquenana.and.features.authentication.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun StartupRoute(
    onNavigateHome: () -> Unit,
    onNavigateAuth: () -> Unit,
    onNavigateCompleteProfile: () -> Unit,
    viewModel: StartupViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                StartupViewModel.StartupUiEvent.NavigateHome -> onNavigateHome()
                StartupViewModel.StartupUiEvent.NavigateAuth -> onNavigateAuth()
                StartupViewModel.StartupUiEvent.NavigateCompleteProfile -> onNavigateCompleteProfile()
            }
        }
    }
    StartupRouteContent()
}

@Composable
fun StartupRouteContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview(showSystemUi = true)
@Composable
fun StartupRoutePreview() {
    StartupRouteContent()
}
