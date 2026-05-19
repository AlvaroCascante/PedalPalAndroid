package com.quetoquenana.and.features.appointments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.usecase.GetAppointmentDetailUseCase
import com.quetoquenana.and.features.stores.domain.model.Store
import com.quetoquenana.and.features.stores.domain.model.StoreLocation
import com.quetoquenana.and.features.stores.domain.usecase.GetStoresUseCase
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
    private val getAppointmentDetail: GetAppointmentDetailUseCase,
    private val getStores: GetStoresUseCase
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
                .onSuccess { appointment ->
                    val enrichedAppointment = runCatching { resolveLocationName(appointment) }
                        .getOrDefault(appointment)
                    _uiState.value = AppointmentDetailUiState.Content(enrichedAppointment)
                }
                .onFailure { _uiState.value = AppointmentDetailUiState.Error(it.message ?: "Unknown error") }
        }
    }

    private suspend fun resolveLocationName(appointment: Appointment): Appointment {
        val locationId = appointment.storeLocationId ?: return appointment
        val resolvedLocation = findLocation(locationId = locationId)
        val resolvedName = appointment.storeLocationName
            ?.takeUnless { it.isBlank() || it == locationId }
            ?: resolvedLocation?.name

        val resolvedCurrency = appointment.currency ?: resolvedLocation?.currency

        return if (resolvedName == appointment.storeLocationName && resolvedCurrency == appointment.currency) {
            appointment
        } else {
            appointment.copy(
                storeLocationName = resolvedName,
                currency = resolvedCurrency
            )
        }
    }

    private suspend fun findLocation(locationId: String): StoreLocation? {
        val cachedLocation = runCatching { getStores(refresh = false) }
            .getOrNull()
            ?.findLocation(locationId = locationId)
        if (cachedLocation != null) {
            return cachedLocation
        }

        return runCatching { getStores(refresh = true) }
            .getOrNull()
            ?.findLocation(locationId = locationId)
    }

    private fun List<Store>.findLocation(locationId: String): StoreLocation? {
        return asSequence()
            .flatMap { store -> store.locations.asSequence() }
            .firstOrNull { location -> location.id == locationId }
    }
}

