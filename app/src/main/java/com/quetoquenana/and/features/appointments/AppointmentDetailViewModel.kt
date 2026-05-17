package com.quetoquenana.and.features.appointments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.usecase.GetAppointmentDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AppointmentDetailUiState {
    data object Loading : AppointmentDetailUiState
    data class Content(val appointment: Appointment) : AppointmentDetailUiState
    data class Error(val message: String) : AppointmentDetailUiState
}

@HiltViewModel
class AppointmentDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAppointmentDetail: GetAppointmentDetailUseCase
) : ViewModel() {

    private val appointmentId: String = checkNotNull(savedStateHandle["id"])

    private val _uiState = MutableStateFlow<AppointmentDetailUiState>(AppointmentDetailUiState.Loading)
    val uiState: StateFlow<AppointmentDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        viewModelScope.launch {
            _uiState.value = AppointmentDetailUiState.Loading
            runCatching { getAppointmentDetail(appointmentId) }
                .onSuccess { _uiState.value = AppointmentDetailUiState.Content(it) }
                .onFailure { _uiState.value = AppointmentDetailUiState.Error(it.message ?: "Unknown error") }
        }
    }
}

