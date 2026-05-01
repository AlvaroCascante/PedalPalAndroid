package com.quetoquenana.and.features.bikes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.bikes.domain.model.BikeType
import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest
import com.quetoquenana.and.features.bikes.domain.usecase.CreateBikeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AddBikeViewModel @Inject constructor(
    private val createBikeUseCase: CreateBikeUseCase
) : ViewModel() {

    private var hasAppliedPrefill = false

    sealed interface AddBikeEvent {
        data object NavigateBikes : AddBikeEvent
        data class ShowError(val message: String) : AddBikeEvent
    }

    private val _uiState = MutableStateFlow(AddBikeUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddBikeEvent>()
    val events = _events.asSharedFlow()

    fun applyPrefill(
        name: String?,
        model: String?,
        notes: String?
    ) {
        if (hasAppliedPrefill) return
        hasAppliedPrefill = true

        _uiState.update {
            it.copy(
                name = name.orEmpty(),
                model = model.orEmpty(),
                notes = notes.orEmpty()
            )
        }
    }

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun onTypeChanged(value: BikeType) {
        _uiState.update { it.copy(type = value) }
    }

    fun onBrandChanged(value: String) {
        _uiState.update { it.copy(brand = value) }
    }

    fun onModelChanged(value: String) {
        _uiState.update { it.copy(model = value) }
    }

    fun onYearChanged(value: String) {
        _uiState.update { current ->
            current.copy(year = value.filter(Char::isDigit))
        }
    }

    fun onSerialNumberChanged(value: String) {
        _uiState.update { it.copy(serialNumber = value) }
    }

    fun onNotesChanged(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

    fun onIsPublicChanged(value: Boolean) {
        _uiState.update { it.copy(isPublic = value) }
    }

    fun saveBike() {
        viewModelScope.launch {
            val state = uiState.value
            val name = state.name.trim()
            val type = state.type

            if (name.isBlank()) {
                _events.emit(AddBikeEvent.ShowError("Bike name is required"))
                return@launch
            }

            if (type == null) {
                _events.emit(AddBikeEvent.ShowError("Bike type is required"))
                return@launch
            }

            val year = state.year.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
            if (state.year.isNotBlank() && year == null) {
                _events.emit(AddBikeEvent.ShowError("Year must be a valid number"))
                return@launch
            }

            _uiState.update { it.copy(isSaving = true) }
            try {
                createBikeUseCase(
                    CreateBikeRequest(
                        name = name,
                        type = type.name,
                        isPublic = state.isPublic,
                        brand = state.brand.trim().ifBlank { null },
                        model = state.model.trim().ifBlank { null },
                        year = year,
                        serialNumber = state.serialNumber.trim().ifBlank { null },
                        notes = state.notes.trim().ifBlank { null }
                    )
                )
                _events.emit(AddBikeEvent.NavigateBikes)
            } catch (throwable: Throwable) {
                _events.emit(
                    AddBikeEvent.ShowError(
                        message = throwable.message ?: "Unable to save bike"
                    )
                )
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}

