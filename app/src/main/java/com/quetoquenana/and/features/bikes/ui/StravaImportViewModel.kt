package com.quetoquenana.and.features.bikes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.bikes.domain.model.StravaBike
import com.quetoquenana.and.features.bikes.domain.usecase.GetStravaBikesUseCase
import com.quetoquenana.and.features.bikes.domain.usecase.GetStravaConnectionStatusUseCase
import com.quetoquenana.and.features.bikes.domain.usecase.GetStravaConnectUrlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class StravaImportViewModel @Inject constructor(
    private val getStravaConnectUrlUseCase: GetStravaConnectUrlUseCase,
    private val getStravaConnectionStatusUseCase: GetStravaConnectionStatusUseCase,
    private val getStravaBikesUseCase: GetStravaBikesUseCase
) : ViewModel() {

    sealed interface StravaImportEvent {
        data class OpenBrowser(val url: String) : StravaImportEvent
        data class NavigateToCreateBike(val bike: StravaBike) : StravaImportEvent
        data class ShowError(val message: String) : StravaImportEvent
    }

    private val coordinator = StravaImportCoordinator(
        scope = viewModelScope,
        getStravaConnectUrl = { getStravaConnectUrlUseCase() },
        getStravaConnectionStatus = { getStravaConnectionStatusUseCase() },
        getStravaBikes = { getStravaBikesUseCase() }
    )

    val uiState = coordinator.uiState

    private val _events = MutableSharedFlow<StravaImportEvent>()
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            coordinator.events.collectLatest { event ->
                when (event) {
                    is StravaImportCoordinator.Event.BikeImported -> {
                        _events.emit(StravaImportEvent.NavigateToCreateBike(event.bike))
                    }

                    is StravaImportCoordinator.Event.OpenBrowser -> {
                        _events.emit(StravaImportEvent.OpenBrowser(event.url))
                    }

                    is StravaImportCoordinator.Event.ShowError -> {
                        _events.emit(StravaImportEvent.ShowError(event.message))
                    }
                }
            }
        }
    }

    fun startConnectionIfNeeded() {
        coordinator.startConnectionIfNeeded()
    }

    fun connectToStrava() {
        coordinator.connectToStrava()
    }

    fun onAppResumedAfterAuth() {
        coordinator.onAppResumedAfterAuth()
    }

    fun loadStravaBikes() {
        coordinator.loadStravaBikes()
    }

    fun onStravaBikeSelected(bike: StravaBike) {
        coordinator.onStravaBikeSelected(bike)
    }
}

