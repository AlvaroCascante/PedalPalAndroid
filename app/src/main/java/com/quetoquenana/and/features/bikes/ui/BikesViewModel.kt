package com.quetoquenana.and.features.bikes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.bikes.domain.model.BikeType
import com.quetoquenana.and.features.bikes.domain.usecase.GetBikeProfileImageUrlUseCase
import com.quetoquenana.and.features.bikes.domain.usecase.GetBikesUseCase
import com.quetoquenana.and.features.bikes.domain.usecase.ObserveBikesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

@HiltViewModel
class BikesViewModel @Inject constructor(
    private val observeBikesUseCase: ObserveBikesUseCase,
    private val getBikesUseCase: GetBikesUseCase,
    private val getBikeProfileImageUrlUseCase: GetBikeProfileImageUrlUseCase
) : ViewModel() {

    sealed interface BikesEvent {
        data class ShowError(val message: String) : BikesEvent
    }

    private val _uiState = MutableStateFlow(BikesUiState())
    val uiState = _uiState.asStateFlow()
    private var loadBikeProfileImagesJob: Job? = null
    private var refreshJob: Job? = null

    private val _events = MutableSharedFlow<BikesEvent>()
    val events = _events.asSharedFlow()

    init {
        observeBikes()
        ensureLocalDataLoaded()
    }

    private fun ensureLocalDataLoaded() {
        viewModelScope.launch {
            // show full-screen loading only if we don't have local bikes yet
            val showLoading = _uiState.value.bikes.isEmpty()
            _uiState.update { it.copy(isLoading = showLoading) }
            try {
                // repository will fetch remote only if local is empty (refresh = false)
                getBikesUseCase()
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

    fun onTypeSelected(type: BikeType?) {
        _uiState.update { it.copy(selectedType = type) }
    }

    private fun observeBikes() {
        viewModelScope.launch {
            observeBikesUseCase()
                .catch { throwable ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(
                        BikesEvent.ShowError(
                            message = throwable.message ?: "Unable to load bikes"
                        )
                    )
                }
                .collect { bikes ->
                    _uiState.update {
                        it.copy(
                            bikes = bikes,
                            isLoading = false
                        )
                    }
                    loadBikeProfileImages(bikes.map { bike -> bike.id })
                }
        }
    }

    private fun loadBikeProfileImages(bikeIds: List<UUID>) {
        loadBikeProfileImagesJob?.cancel()
        if (bikeIds.isEmpty()) {
            _uiState.update { it.copy(bikeProfileImageUrls = emptyMap()) }
            return
        }

        loadBikeProfileImagesJob = viewModelScope.launch {
            val imageUrls = bikeIds
                .distinct()
                .map { bikeId ->
                    async {
                        bikeId to runCatching {
                            getBikeProfileImageUrlUseCase(id = bikeId)
                        }.getOrNull()
                    }
                }
                .awaitAll()
                .mapNotNull { (bikeId, imageUrl) ->
                    imageUrl?.takeIf { it.isNotBlank() }?.let { bikeId to it }
                }
                .toMap()

            _uiState.update { state ->
                state.copy(
                    bikeProfileImageUrls = imageUrls
                )
            }
        }
    }

    fun refreshBikes() {
        // avoid concurrent refreshes
        if (refreshJob?.isActive == true) return

        refreshJob = viewModelScope.launch {
            // show pull-to-refresh indicator, and full-screen loader if list is empty
            _uiState.update { it.copy(isRefreshing = true, isLoading = it.bikes.isEmpty()) }
            try {
                getBikesUseCase(refresh = true)
            } catch (throwable: Throwable) {
                _events.emit(
                    BikesEvent.ShowError(
                        message = throwable.message ?: "Unable to load bikes"
                    )
                )
            } finally {
                _uiState.update { it.copy(isRefreshing = false, isLoading = false) }
            }
        }
    }
}
