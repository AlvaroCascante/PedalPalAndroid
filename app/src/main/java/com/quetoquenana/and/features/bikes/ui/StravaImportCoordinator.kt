package com.quetoquenana.and.features.bikes.ui

import com.quetoquenana.and.features.bikes.domain.model.StravaBike
import com.quetoquenana.and.features.bikes.domain.model.StravaConnectionStatus
import com.quetoquenana.and.features.bikes.domain.model.StravaConnectUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class StravaImportCoordinator(
    private val scope: CoroutineScope,
    private val getStravaConnectUrl: suspend () -> StravaConnectUrl,
    private val getStravaConnectionStatus: suspend () -> StravaConnectionStatus,
    private val getStravaBikes: suspend () -> List<StravaBike>
) {

    sealed interface Event {
        data class OpenBrowser(val url: String) : Event
        data class BikeImported(val bike: StravaBike) : Event
        data class ShowError(val message: String) : Event
    }

    private val _uiState = MutableStateFlow(StravaImportUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    private var hasStartedConnection = false

    fun startConnectionIfNeeded() {
        if (hasStartedConnection) return
        hasStartedConnection = true

        // Check backend connection status first. If connected, load bikes directly and
        // avoid opening the browser. If the status check fails, fall back to starting
        // the auth flow (conservative behavior).
        scope.launch {
            try {
                val status = try {
                    getStravaConnectionStatus()
                } catch (e: Throwable) {
                    // Treat status check failure as unknown -> start auth
                    Timber.w("Failed to check Strava connection status: ${e.message}")
                    null
                }

                if (status?.isConnected == true) {
                    loadStravaBikes()
                } else {
                    connectToStrava()
                }
            } catch (t: Throwable) {
                // If something unexpected happens, fall back to starting auth flow.
                Timber.w("Error checking Strava connection status: ${t.message}")
                connectToStrava()
            }
        }
    }

    fun connectToStrava() {
        scope.launch {
            _uiState.update {
                it.copy(
                    isConnecting = true,
                    isLoadingBikes = false,
                    isWaitingForAuthorization = false,
                    bikes = emptyList()
                )
            }

            try {
                // Before requesting a connect URL, check backend connection status. If
                // the backend already reports connected, simply load bikes and avoid
                // opening the browser. If status check fails, proceed with auth flow.
                val status = try {
                    getStravaConnectionStatus()
                } catch (e: Throwable) {
                    null
                }

                if (status?.isConnected == true) {
                    // Backend says we're already connected; load bikes instead of auth.
                    _uiState.update { it.copy(isConnecting = false) }
                    loadStravaBikes()
                    return@launch
                }

                val connectUrl = getStravaConnectUrl()
                _uiState.update {
                    it.copy(
                        isConnecting = false,
                        isWaitingForAuthorization = true
                    )
                }
                _events.emit(Event.OpenBrowser(connectUrl.url))
            } catch (throwable: Throwable) {
                _uiState.update { it.copy(isConnecting = false) }
                _events.emit(
                    Event.ShowError(
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
        scope.launch {
            _uiState.update {
                it.copy(
                    isLoadingBikes = true,
                    isWaitingForAuthorization = false
                )
            }

            try {
                val bikes = loadStravaBikesWithFallback()
                val shouldShowSelection = bikes.size > 1
                _uiState.update {
                    it.copy(
                        bikes = if (shouldShowSelection) bikes else emptyList(),
                        isLoadingBikes = false
                    )
                }
                if (bikes.size == 1) {
                    _events.emit(Event.BikeImported(bikes.first()))
                }
            } catch (throwable: Throwable) {
                _uiState.update { it.copy(isLoadingBikes = false) }
                _events.emit(
                    Event.ShowError(
                        throwable.message ?: "Unable to load Strava bikes"
                    )
                )
            }
        }
    }

    fun onStravaBikeSelected(bike: StravaBike) {
        scope.launch {
            _uiState.update { it.copy(bikes = emptyList()) }
            _events.emit(Event.BikeImported(bike))
        }
    }

    private suspend fun loadStravaBikesWithFallback(): List<StravaBike> {
        return try {
            getStravaBikes()
        } catch (originalThrowable: Throwable) {
            val connectionStatus = try {
                getStravaConnectionStatus()
            } catch (_: Throwable) {
                throw originalThrowable
            }

            if (!connectionStatus.isConnected) {
                throw IllegalStateException("Strava authorization is still pending")
            }

            getStravaBikes()
        }
    }
}

