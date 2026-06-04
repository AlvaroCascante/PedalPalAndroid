package com.quetoquenana.and.features.appointments.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.core.media.domain.model.MediaAsset
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.features.appointments.domain.model.Appointment
import com.quetoquenana.and.features.appointments.domain.usecase.GetAppointmentDetailUseCase
import com.quetoquenana.and.features.appointments.domain.usecase.ObserveAppointmentMediaUseCase
import com.quetoquenana.and.features.appointments.domain.usecase.UploadAppointmentMediaUseCase
import com.quetoquenana.and.features.stores.domain.model.Store
import com.quetoquenana.and.features.stores.domain.model.StoreLocation
import com.quetoquenana.and.features.stores.domain.usecase.GetStoresUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface AppointmentDetailUiState {
    data object Loading : AppointmentDetailUiState
    data class Content(
        val appointment: Appointment,
        val attachments: List<MediaAsset> = emptyList(),
        val isUploadingAttachment: Boolean = false,
    ) : AppointmentDetailUiState
    data class Error(val message: String) : AppointmentDetailUiState
}

sealed interface AppointmentDetailEvent {
    data class ShowMessage(val message: String) : AppointmentDetailEvent
    data class ShowError(val message: String) : AppointmentDetailEvent
}

@HiltViewModel
class AppointmentDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAppointmentDetail: GetAppointmentDetailUseCase,
    private val getStores: GetStoresUseCase,
    private val observeAppointmentMedia: ObserveAppointmentMediaUseCase,
    private val uploadAppointmentMedia: UploadAppointmentMediaUseCase,
) : ViewModel() {

    val appointmentId: UUID = checkNotNull(savedStateHandle["id"])

    private val _uiState = MutableStateFlow<AppointmentDetailUiState>(AppointmentDetailUiState.Loading)
    val uiState: StateFlow<AppointmentDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AppointmentDetailEvent>()
    val events = _events.asSharedFlow()

    private var observeAttachmentsJob: Job? = null

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        viewModelScope.launch {
            observeAttachmentsJob?.cancel()
            _uiState.value = AppointmentDetailUiState.Loading
            runCatching { getAppointmentDetail(appointmentId) }
                .onSuccess { appointment ->
                    val enrichedAppointment = runCatching { resolveLocationName(appointment) }
                        .getOrDefault(appointment)
                    _uiState.value = AppointmentDetailUiState.Content(
                        appointment = enrichedAppointment,
                    )
                    observeAttachments()
                }
                .onFailure { _uiState.value = AppointmentDetailUiState.Error(it.message ?: "Unknown error") }
        }
    }

    private fun observeAttachments() {
        observeAttachmentsJob?.cancel()
        observeAttachmentsJob = viewModelScope.launch {
            observeAppointmentMedia(id = appointmentId, refresh = true)
                .catch { throwable ->
                    _events.emit(
                        AppointmentDetailEvent.ShowError(
                            throwable.message ?: "Unable to load payment proofs"
                        )
                    )
                }
                .collect { attachments ->
                    _uiState.update { current ->
                        when (current) {
                            is AppointmentDetailUiState.Content -> current.copy(attachments = attachments)
                            else -> current
                        }
                    }
                }
        }
    }

    fun onPaymentProofsSelected(uploads: List<MediaUploadRequest>) {
        val contentState = _uiState.value as? AppointmentDetailUiState.Content ?: return
        if (uploads.isEmpty()) {
            viewModelScope.launch {
                _events.emit(AppointmentDetailEvent.ShowError("No valid image selected"))
            }
            return
        }
        if (contentState.isUploadingAttachment) return

        viewModelScope.launch {
            _uiState.update { current ->
                when (current) {
                    is AppointmentDetailUiState.Content -> current.copy(isUploadingAttachment = true)
                    else -> current
                }
            }

            runCatching { uploadAppointmentMedia(id = appointmentId, uploads = uploads) }
                .onSuccess {
                    _uiState.update { current ->
                        when (current) {
                            is AppointmentDetailUiState.Content -> current.copy(
                                isUploadingAttachment = false,
                            )
                            else -> current
                        }
                    }
                    _events.emit(
                        AppointmentDetailEvent.ShowMessage(
                            if (uploads.size == 1) {
                                "Payment proof attached"
                            } else {
                                "${uploads.size} payment proofs attached"
                            }
                        )
                    )
                }
                .onFailure { throwable ->
                    _uiState.update { current ->
                        when (current) {
                            is AppointmentDetailUiState.Content -> current.copy(isUploadingAttachment = false)
                            else -> current
                        }
                    }
                    _events.emit(
                        AppointmentDetailEvent.ShowError(
                            throwable.message ?: "Unable to attach payment proof"
                        )
                    )
                }
        }
    }

    private suspend fun resolveLocationName(appointment: Appointment): Appointment {
        val locationId = appointment.storeLocationId ?: return appointment
        val resolvedLocation = findLocation(locationId = locationId)
        val resolvedName = appointment.storeLocationName
            ?.takeUnless { it.isBlank() || it.equals(locationId) }
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

    private suspend fun findLocation(locationId: UUID): StoreLocation? {
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

    private fun List<Store>.findLocation(locationId: UUID): StoreLocation? {
        return asSequence()
            .flatMap { store -> store.locations.asSequence() }
            .firstOrNull { location -> location.id == locationId }
    }
}

