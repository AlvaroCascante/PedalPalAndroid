package com.quetoquenana.and.features.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.usecase.GetAppointmentsUseCase
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.usecase.ObserveBikesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AppointmentsViewModel @Inject constructor(
    private val getAppointmentsUseCase: GetAppointmentsUseCase,
    private val observeBikesUseCase: ObserveBikesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppointmentsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        collectAppointments()
        refreshAppointments()
    }

    fun onBikeFilterSelected(bikeId: String?) {
        _uiState.update { it.copy(selectedBikeId = bikeId) }
    }

    private fun collectAppointments() {
        viewModelScope.launch {
            combine(
                getAppointmentsUseCase.observeAppointments(),
                observeBikesUseCase()
            ) { appointments, bikes ->
                val bikesById = bikes.associateBy { it.id }
                val displayAppointments = appointments.map { appointment ->
                    appointment.copy(
                        bikeName = bikesById[appointment.bikeId]?.name
                            ?: appointment.bikeName
                            ?: appointment.bikeId.shortIdLabel()
                    )
                }
                displayAppointments to bikesById
            }
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Unable to load appointments"
                        )
                    }
                }
                .collect { (displayAppointments, bikesById) ->
                _uiState.update {
                    it.copy(
                        appointments = displayAppointments,
                        bikeFilters = displayAppointments.toBikeFilters(bikesById),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
        }
    }

    private fun refreshAppointments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { getAppointmentsUseCase() }
                .onSuccess {
                    _uiState.update { state -> state.copy(isLoading = false) }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Unable to load appointments"
                        )
                    }
                }
        }
    }

    private fun List<Appointment>.toBikeFilters(bikesById: Map<String, Bike>): List<AppointmentBikeFilter> {
        val allBikeFilters = bikesById.values.map { bike ->
            AppointmentBikeFilter(
                bikeId = bike.id,
                bikeName = bike.name
            )
        }
        val appointmentOnlyFilters = distinctBy { it.bikeId }
            .filterNot { appointment -> bikesById.containsKey(appointment.bikeId) }
            .map { appointment ->
                AppointmentBikeFilter(
                    bikeId = appointment.bikeId,
                    bikeName = appointment.bikeName
                        ?: appointment.bikeId.shortIdLabel()
                )
            }

        return (allBikeFilters + appointmentOnlyFilters)
            .sortedBy { it.bikeName.lowercase() }
    }

    private fun String.shortIdLabel(): String {
        return "Bike ${take(8)}"
    }
}

data class AppointmentsUiState(
    val isLoading: Boolean = false,
    val appointments: List<Appointment> = emptyList(),
    val bikeFilters: List<AppointmentBikeFilter> = emptyList(),
    val selectedBikeId: String? = null,
    val errorMessage: String? = null
) {
    val filteredAppointments: List<Appointment>
        get() = selectedBikeId?.let { bikeId ->
            appointments.filter { it.bikeId == bikeId }
        } ?: appointments

    val upcomingAppointments: List<Appointment>
        get() = filteredAppointments
            .filter { it.isUpcoming }
            .sortedBy { it.scheduledAtMillis ?: Long.MAX_VALUE }

    val pastAppointments: List<Appointment>
        get() = filteredAppointments
            .filterNot { it.isUpcoming }
            .sortedByDescending { it.scheduledAtMillis ?: Long.MIN_VALUE }

    val selectedBikeName: String?
        get() = bikeFilters.firstOrNull { it.bikeId == selectedBikeId }?.bikeName
}

data class AppointmentBikeFilter(
    val bikeId: String,
    val bikeName: String
)

private val Appointment.isUpcoming: Boolean
    get() {
        val normalizedStatus = status.orEmpty().uppercase()
        val isClosed = normalizedStatus in setOf(
            "COMPLETED",
            "CANCELLED",
            "CANCELED",
            "NO_SHOW",
            "CLOSED"
        )
        val scheduledTimeMillis = scheduledAtMillis
        return !isClosed && (scheduledTimeMillis == null || scheduledTimeMillis >= System.currentTimeMillis())
    }

private val Appointment.scheduledAtMillis: Long?
    get() = scheduledAt?.let { value ->
        parseUtcMillis(value)
    }

private fun parseUtcMillis(value: String): Long? {
    return listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSX",
        "yyyy-MM-dd'T'HH:mm:ssX"
    ).firstNotNullOfOrNull { pattern ->
        runCatching {
            SimpleDateFormat(pattern, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
                isLenient = false
            }.parse(value)?.time
        }.getOrNull()
    }
}
