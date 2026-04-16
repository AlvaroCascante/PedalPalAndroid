package com.quetoquenana.and.features.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.usecase.GetAppointmentsUseCase
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.bikes.domain.usecase.GetBikesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AppointmentsViewModel @Inject constructor(
    private val getAppointmentsUseCase: GetAppointmentsUseCase,
    private val getBikesUseCase: GetBikesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppointmentsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAppointments()
    }

    fun onBikeFilterSelected(bikeId: String?) {
        _uiState.update { it.copy(selectedBikeId = bikeId) }
    }

    fun loadAppointments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val appointments = getAppointmentsUseCase()
                val bikesById = getBikesUseCase().associateBy { it.id }
                val displayAppointments = appointments.map { appointment ->
                    appointment.copy(
                        bikeName = bikesById[appointment.bikeId]?.name
                            ?: appointment.bikeName
                            ?: appointment.bikeId.shortIdLabel()
                    )
                }
                _uiState.update {
                    it.copy(
                        appointments = displayAppointments,
                        bikeFilters = displayAppointments.toBikeFilters(bikesById),
                        isLoading = false
                    )
                }
            } catch (throwable: Throwable) {
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
        return distinctBy { it.bikeId }
            .map { appointment ->
                AppointmentBikeFilter(
                    bikeId = appointment.bikeId,
                    bikeName = bikesById[appointment.bikeId]?.name
                        ?: appointment.bikeName
                        ?: appointment.bikeId.shortIdLabel()
                )
            }
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
            .sortedBy { it.scheduledAtInstant ?: Instant.MAX }

    val pastAppointments: List<Appointment>
        get() = filteredAppointments
            .filterNot { it.isUpcoming }
            .sortedByDescending { it.scheduledAtInstant ?: Instant.MIN }

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
        val scheduledInstant = scheduledAtInstant
        return !isClosed && (scheduledInstant == null || scheduledInstant >= Instant.now())
    }

private val Appointment.scheduledAtInstant: Instant?
    get() = scheduledAt?.let { value ->
        runCatching { Instant.parse(value) }.getOrNull()
    }
