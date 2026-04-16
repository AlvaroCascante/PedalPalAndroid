package com.quetoquenana.and.features.bikes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.bikes.domain.model.BikeType
import com.quetoquenana.and.features.bikes.domain.usecase.GetBikesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class BikesViewModel @Inject constructor(
    private val getBikesUseCase: GetBikesUseCase
) : ViewModel() {

    sealed interface BikesEvent {
        data class ShowError(val message: String) : BikesEvent
    }

    private val _uiState = MutableStateFlow(BikesUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<BikesEvent>()
    val events = _events.asSharedFlow()

    init {
        loadBikes()
    }

    fun onTypeSelected(type: BikeType?) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun loadBikes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val bikes = getBikesUseCase()
                _uiState.update {
                    it.copy(
                        bikes = bikes,
                        isLoading = false
                    )
                }
            } catch (throwable: Throwable) {
                _uiState.update { it.copy(isLoading = false) }
                _events.emit(
                    BikesEvent.ShowError(
                        message = throwable.message ?: "Unable to load bikes"
                    )
                )
            }
        }
    }
}
