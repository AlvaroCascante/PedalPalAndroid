package com.quetoquenana.and.features.bikes.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.bikes.domain.model.AddComponentRequest
import com.quetoquenana.and.features.bikes.domain.usecase.AddBikeComponentUseCase
import com.quetoquenana.and.features.bikes.domain.usecase.GetBikeComponentTypesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class BikeComponentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addBikeComponentUseCase: AddBikeComponentUseCase,
    private val getBikeComponentTypesUseCase: GetBikeComponentTypesUseCase
) : ViewModel() {

    private val bikeId: String = savedStateHandle["bikeId"] ?: ""

    sealed interface BikeComponentEvent {
        data object NavigateBikeDetail : BikeComponentEvent
        data class ShowError(val message: String) : BikeComponentEvent
    }

    private val _uiState = MutableStateFlow(AddBikeComponentUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<BikeComponentEvent>()
    val events = _events.asSharedFlow()

    init {
        loadComponentTypes()
    }

    fun loadComponentTypes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingComponentTypes = true) }
            try {
                val componentTypes = getBikeComponentTypesUseCase()
                _uiState.update {
                    it.copy(
                        componentTypes = componentTypes,
                        isLoadingComponentTypes = false
                    )
                }
            } catch (throwable: Throwable) {
                _uiState.update { it.copy(isLoadingComponentTypes = false) }
                _events.emit(
                    BikeComponentEvent.ShowError(
                        message = throwable.message ?: "Unable to load component types"
                    )
                )
            }
        }
    }

    fun onTypeChanged(value: String) {
        _uiState.update { it.copy(type = value) }
    }

    fun onBrandChanged(value: String) {
        _uiState.update { it.copy(brand = value.take(50)) }
    }

    fun onModelChanged(value: String) {
        _uiState.update { it.copy(model = value.take(50)) }
    }

    fun onNotesChanged(value: String) {
        _uiState.update { it.copy(notes = value.take(250)) }
    }

    fun onOdometerChanged(value: String) {
        _uiState.update { it.copy(odometerKm = value.filter(Char::isDigit)) }
    }

    fun onUsageTimeChanged(value: String) {
        _uiState.update { it.copy(usageTimeMinutes = value.filter(Char::isDigit)) }
    }

    fun saveComponent() {
        viewModelScope.launch {
            val state = uiState.value
            val type = state.type.trim()

            if (bikeId.isBlank()) {
                _events.emit(BikeComponentEvent.ShowError("Bike id is missing"))
                return@launch
            }

            if (type.isBlank()) {
                _events.emit(BikeComponentEvent.ShowError("Component type is required"))
                return@launch
            }

            _uiState.update { it.copy(isSaving = true) }
            try {
                addBikeComponentUseCase(
                    bikeId = bikeId,
                    request = AddComponentRequest(
                        name = type, // No using name for now, just set it to type. We can add a separate name field later if needed
                        type = type,
                        brand = state.brand.trim().ifBlank { null },
                        model = state.model.trim().ifBlank { null },
                        notes = state.notes.trim().ifBlank { null },
                        odometerKm = state.odometerKm.toIntOrNull() ?: 0,
                        usageTimeMinutes = state.usageTimeMinutes.toIntOrNull() ?: 0
                    )
                )
                _events.emit(BikeComponentEvent.NavigateBikeDetail)
            } catch (throwable: Throwable) {
                _events.emit(
                    BikeComponentEvent.ShowError(
                        message = throwable.message ?: "Unable to save component"
                    )
                )
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}
