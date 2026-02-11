package com.quetoquenana.and.features.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.appointments.domain.usecase.GetAppointmentsUseCase
import com.quetoquenana.and.features.landing.domain.usecase.GetLandingItemsUseCase
import com.quetoquenana.and.features.suggestions.domain.usecase.GetSuggestionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAppointments: com.quetoquenana.and.features.appointments.domain.usecase.GetAppointmentsUseCase,
    private val getSuggestions: com.quetoquenana.and.features.suggestions.domain.usecase.GetSuggestionsUseCase,
    private val getLandingItems: com.quetoquenana.and.features.landing.domain.usecase.GetLandingItemsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadAppointments()
        loadSuggestions()
        loadLandingItems()
    }

    private fun loadAppointments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val list = getAppointments()
                _uiState.update { it.copy(appointments = list, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun loadSuggestions() {
        viewModelScope.launch {
            try {
                val list = getSuggestions()
                _uiState.update { it.copy(suggestions = list) }
            } catch (_: Exception) {
                // ignore for now
            }
        }
    }

    private fun loadLandingItems() {
        viewModelScope.launch {
            try {
                val list = getLandingItems()
                _uiState.update { it.copy(landingItems = list) }
            } catch (_: Exception) {
                // ignore
            }
        }
    }
}