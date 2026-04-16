package com.quetoquenana.and.features.bikes.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.bikes.domain.usecase.GetBikeHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class BikeHistoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBikeHistoryUseCase: GetBikeHistoryUseCase
) : ViewModel() {

    private val bikeId: String = savedStateHandle["id"] ?: ""

    private val _uiState = MutableStateFlow(BikeHistoryUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val history = getBikeHistoryUseCase(bikeId)
                _uiState.update { it.copy(history = history, isLoading = false) }
            } catch (throwable: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load bike history"
                    )
                }
            }
        }
    }
}
