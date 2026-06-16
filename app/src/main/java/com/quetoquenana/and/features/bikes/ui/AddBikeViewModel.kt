package com.quetoquenana.and.features.bikes.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.R
import com.quetoquenana.and.core.utils.STRAVA_SYNC_PROVIDER
import com.quetoquenana.and.features.bikes.domain.model.BikeType
import com.quetoquenana.and.features.bikes.domain.model.CreateBikeRequest
import com.quetoquenana.and.features.bikes.domain.usecase.CreateBikeUseCase
import com.quetoquenana.and.features.bikes.domain.usecase.GetStravaBikesUseCase
import com.quetoquenana.and.features.bikes.domain.usecase.GetStravaConnectionStatusUseCase
import com.quetoquenana.and.features.bikes.domain.usecase.GetStravaConnectUrlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.roundToInt

@HiltViewModel
class AddBikeViewModel @Inject constructor(
    private val createBikeUseCase: CreateBikeUseCase,
    getStravaConnectUrlUseCase: GetStravaConnectUrlUseCase,
    getStravaConnectionStatusUseCase: GetStravaConnectionStatusUseCase,
    getStravaBikesUseCase: GetStravaBikesUseCase
) : ViewModel() {

    private var hasAppliedPrefill = false

    private val stravaImportCoordinator = StravaImportCoordinator(
        scope = viewModelScope,
        getStravaConnectUrl = { getStravaConnectUrlUseCase() },
        getStravaConnectionStatus = { getStravaConnectionStatusUseCase() },
        getStravaBikes = { getStravaBikesUseCase() }
    )

    sealed interface AddBikeEvent {
        data object NavigateBikes : AddBikeEvent
        data class OpenBrowser(val url: String) : AddBikeEvent
        data class ShowError(val message: String) : AddBikeEvent
        data class ShowErrorRes(@param:StringRes @field:StringRes val resId: Int) : AddBikeEvent
    }

    private val _uiState = MutableStateFlow(value = AddBikeUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddBikeEvent>()
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            stravaImportCoordinator.uiState.collectLatest { stravaImportState ->
                _uiState.update { it.copy(stravaImport = stravaImportState) }
            }
        }

        viewModelScope.launch {
            stravaImportCoordinator.events.collectLatest { event ->
                when (event) {
                    is StravaImportCoordinator.Event.BikeImported -> applyImportedStravaBike(
                        id = event.bike.id,
                        name = event.bike.name,
                        odometerKm = event.bike.distance?.roundToInt()?.toString().orEmpty()
                    )

                    is StravaImportCoordinator.Event.OpenBrowser -> {
                        _events.emit(value = AddBikeEvent.OpenBrowser(event.url))
                    }

                    is StravaImportCoordinator.Event.ShowError -> {
                        _events.emit(value = AddBikeEvent.ShowError(event.message))
                    }
                }
            }
        }
    }

    fun applyPrefill(
        name: String?,
        brand: String?,
        model: String?,
        notes: String?,
        odometerKm: String?,
        externalGearId: String?
    ) {
        if (hasAppliedPrefill) return
        hasAppliedPrefill = true

        _uiState.update {
            it.copy(
                name = name.orEmpty(),
                brand = brand.orEmpty(),
                model = model.orEmpty(),
                notes = notes.orEmpty(),
                odometerKm = odometerKm.orEmpty().filter(predicate = Char::isDigit),
                importedStravaBikeId = externalGearId?.takeIf(predicate = String::isNotBlank),
                importedStravaBikeName = name?.takeIf(predicate = String::isNotBlank)
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
            current.copy(year = value.filter(predicate = Char::isDigit))
        }
    }

    fun onSerialNumberChanged(value: String) {
        _uiState.update { it.copy(serialNumber = value) }
    }

    fun onNotesChanged(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

    fun onOdometerChanged(value: String) {
        _uiState.update {
            it.copy(odometerKm = value.filter(predicate = Char::isDigit))
        }
    }

    fun onIsPublicChanged(value: Boolean) {
        _uiState.update { it.copy(isPublic = value) }
    }

    fun connectToStrava() {
        stravaImportCoordinator.connectToStrava()
    }

    fun onAppResumedAfterStravaAuth() {
        stravaImportCoordinator.onAppResumedAfterAuth()
    }

    fun onStravaBikeSelected(id: String) {
        val bike = uiState.value.stravaImport.bikes.firstOrNull { it.id == id } ?: return
        stravaImportCoordinator.onStravaBikeSelected(bike)
    }

    fun saveBike() {
        viewModelScope.launch {
            val state = uiState.value
            val name = state.name.trim()
            val type = state.type
            val year = state.year.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
            val odometerKm = state.odometerKm.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
            val importedStravaBikeId = state.importedStravaBikeId

            var hadError = false
            var nameErr: Int? = null
            var typeErr: Int? = null

            if (name.isBlank()) {
                nameErr = R.string.error_bike_name_is_required
                hadError = true
            }

            if (type == null) {
                typeErr = R.string.error_bike_type_is_required
                hadError = true
            }
            if (hadError) {
                _uiState.update {
                    it.copy(
                        nameErrorRes = nameErr,
                        typeErrorRes = typeErr
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isSaving = true) }
            try {
                createBikeUseCase(
                    CreateBikeRequest(
                        name = name,
                        type = type!!.name,
                        brand = state.brand.trim().ifBlank { null },
                        model = state.model.trim().ifBlank { null },
                        year = year,
                        serialNumber = state.serialNumber.trim().ifBlank { null },
                        notes = state.notes.trim().ifBlank { null },
                        odometerKm = odometerKm,
                        usageTimeMinutes = null,
                        externalGearId = importedStravaBikeId,
                        externalSyncProvider = importedStravaBikeId?.let { STRAVA_SYNC_PROVIDER },
                        isPublic = state.isPublic,
                        isExternalSync = importedStravaBikeId != null
                    )
                )
                _events.emit(value = AddBikeEvent.NavigateBikes)
            } catch (throwable: Throwable) {
                Timber.w(t = throwable, message = "Error while creating bike")
                _events.emit(
                    value = AddBikeEvent.ShowErrorRes(resId = R.string.error_unable_to_save_bike)
                )
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun applyImportedStravaBike(
        id: String,
        name: String,
        odometerKm: String
    ) {
        _uiState.update {
            it.copy(
                name = name,
                odometerKm = odometerKm,
                importedStravaBikeId = id,
                importedStravaBikeName = name
            )
        }
    }
}
