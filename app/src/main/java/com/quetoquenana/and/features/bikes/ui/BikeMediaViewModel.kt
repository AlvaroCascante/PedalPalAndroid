package com.quetoquenana.and.features.bikes.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.features.bikes.domain.usecase.GetBikeMediaUseCase
import com.quetoquenana.and.features.bikes.domain.usecase.UploadBikeMediaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class BikeMediaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBikeMediaUseCase: GetBikeMediaUseCase,
    private val uploadBikeMediaUseCase: UploadBikeMediaUseCase
) : ViewModel() {

    sealed interface BikeMediaEvent {
        data class ShowMessage(val message: String) : BikeMediaEvent
        data class ShowError(val message: String) : BikeMediaEvent
    }

    private val bikeId: String = savedStateHandle["id"] ?: ""

    private val _uiState = MutableStateFlow(value = BikeMediaUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<BikeMediaEvent>()
    val events = _events.asSharedFlow()

    fun loadMedia() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val media = getBikeMediaUseCase(bikeId)
                _uiState.update { it.copy(media = media, isLoading = false) }
            } catch (throwable: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load bike images"
                    )
                }
            }
        }
    }

    fun uploadMedia(uploads: List<MediaUploadRequest>) {
        viewModelScope.launch {
            if (bikeId.isBlank()) {
                _events.emit(BikeMediaEvent.ShowError("Bike id is missing"))
                return@launch
            }

            if (uploads.isEmpty()) {
                _events.emit(BikeMediaEvent.ShowError("No valid images selected"))
                return@launch
            }

            _uiState.update { it.copy(isUploading = true) }
            try {
                uploadBikeMediaUseCase(bikeId = bikeId, uploads = uploads)
                _events.emit(
                    BikeMediaEvent.ShowMessage(
                        if (uploads.size == 1) "1 image uploaded" else "${uploads.size} images uploaded"
                    )
                )
                loadMedia()
            } catch (throwable: Throwable) {
                _events.emit(
                    BikeMediaEvent.ShowError(
                        throwable.message ?: "Unable to upload selected images"
                    )
                )
            } finally {
                _uiState.update { it.copy(isUploading = false) }
            }
        }
    }
}
