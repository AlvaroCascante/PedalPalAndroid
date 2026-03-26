package com.quetoquenana.and.features.auth.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

    Box {
        CircularProgressIndicator()
    }
}