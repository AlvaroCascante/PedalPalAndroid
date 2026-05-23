package com.quetoquenana.and.features.bikes.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.features.bikes.domain.usecase.GetBikeUseCase
import com.quetoquenana.and.features.bikes.domain.usecase.UploadBikeProfileImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class BikeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBikeUseCase: GetBikeUseCase,
    private val uploadBikeProfileImageUseCase: UploadBikeProfileImageUseCase,
) : ViewModel() {

    sealed interface BikeDetailEvent {
        data class ShowMessage(val message: String) : BikeDetailEvent
        data class ShowError(val message: String) : BikeDetailEvent
    }

    private val bikeId: String = savedStateHandle["id"] ?: ""

    private val _uiState = MutableStateFlow(BikeDetailUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<BikeDetailEvent>()
    val events = _events.asSharedFlow()

    init {
        loadBike()
    }

    fun loadBike() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val bike = getBikeUseCase(bikeId)
                _uiState.update { it.copy(bike = bike, isLoading = false) }
            } catch (throwable: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load bike"
                    )
                }
            }
        }
    }

    fun uploadProfileImage(upload: MediaUploadRequest) {
        viewModelScope.launch {
            if (bikeId.isBlank()) {
                _events.emit(BikeDetailEvent.ShowError("Bike id is missing"))
                return@launch
            }

            _uiState.update { it.copy(isUploadingProfileImage = true) }
            try {
                uploadBikeProfileImageUseCase(bikeId = bikeId, upload = upload)
                _events.emit(BikeDetailEvent.ShowMessage("Bike profile image updated"))
            } catch (throwable: Throwable) {
                _events.emit(
                    BikeDetailEvent.ShowError(
                        throwable.message ?: "Unable to upload bike profile image"
                    )
                )
            } finally {
                _uiState.update { it.copy(isUploadingProfileImage = false) }
            }
        }
    }
}
