package com.quetoquenana.and.features.bikes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.bikes.domain.model.StravaBike
import com.quetoquenana.and.features.bikes.domain.usecase.GetStravaBikesUseCase
import com.quetoquenana.and.features.bikes.domain.usecase.GetStravaConnectUrlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class StravaImportViewModel @Inject constructor(
    private val getStravaConnectUrlUseCase: GetStravaConnectUrlUseCase,
    private val getStravaBikesUseCase: GetStravaBikesUseCase
) : ViewModel() {

    sealed interface StravaImportEvent {
        data class OpenBrowser(val url: String) : StravaImportEvent
        data class NavigateToCreateBike(val bike: StravaBike) : StravaImportEvent
        data class ShowError(val message: String) : StravaImportEvent
    }

    private val _uiState = MutableStateFlow(StravaImportUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<StravaImportEvent>()
    val events = _events.asSharedFlow()

    private var hasStartedConnection = false

    fun startConnectionIfNeeded() {
        if (hasStartedConnection) return
        hasStartedConnection = true
        connectToStrava()
    }

    fun connectToStrava() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isConnecting = true,
                    bikes = emptyList()
                )
            }
            try {
                val connectUrl = getStravaConnectUrlUseCase()
                _uiState.update {
                    it.copy(
                        isConnecting = false,
                        isWaitingForAuthorization = true
                    )
                }
                _events.emit(StravaImportEvent.OpenBrowser(connectUrl.url))
            } catch (throwable: Throwable) {
                _uiState.update { it.copy(isConnecting = false) }
                _events.emit(
                    StravaImportEvent.ShowError(
                        throwable.message ?: "Unable to start Strava connection"
                    )
                )
            }
        }
    }

    fun onAppResumedAfterAuth() {
        if (!uiState.value.isWaitingForAuthorization) return
        loadStravaBikes()
    }

    fun loadStravaBikes() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingBikes = true,
                    isWaitingForAuthorization = false
                )
            }
            try {
                val bikes = getStravaBikesUseCase()
                _uiState.update {
                    it.copy(
                        bikes = bikes,
                        isLoadingBikes = false
                    )
                }
            } catch (throwable: Throwable) {
                _uiState.update { it.copy(isLoadingBikes = false) }
                _events.emit(
                    StravaImportEvent.ShowError(
                        throwable.message ?: "Unable to load Strava bikes"
                    )
                )
            }
        }
    }

    fun onStravaBikeSelected(bike: StravaBike) {
        viewModelScope.launch {
            _events.emit(StravaImportEvent.NavigateToCreateBike(bike))
        }
    }
}

