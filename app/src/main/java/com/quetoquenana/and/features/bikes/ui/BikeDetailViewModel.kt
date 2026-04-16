package com.quetoquenana.and.features.bikes.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.bikes.domain.usecase.GetBikeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class BikeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBikeUseCase: GetBikeUseCase
) : ViewModel() {

    private val bikeId: String = savedStateHandle["id"] ?: ""

    private val _uiState = MutableStateFlow(BikeDetailUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadBike()
    }

    fun loadBike() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val bike = getBikeUseCase(bikeId)
                _uiState.update { it.copy(bike = bike, isLoading = false) }
            } catch (throwable: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load bike"
                    )
                }
            }
        }
    }
}
