package com.quetoquenana.and.features.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.core.media.domain.model.MediaUploadRequest
import com.quetoquenana.and.features.authentication.domain.usecase.LogoutUseCase
import com.quetoquenana.and.features.profile.domain.model.Profile
import com.quetoquenana.and.features.profile.domain.usecase.GetProfileUseCase
import com.quetoquenana.and.features.profile.domain.usecase.UploadProfilePhotoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoggingOut: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val profileLoadingState: ProfileLoadingState = ProfileLoadingState.Loading
)

sealed interface ProfileLoadingState {
    data object Loading : ProfileLoadingState
    data class Success(val profile: ProfileUiModel) : ProfileLoadingState
    data class Error(val message: String) : ProfileLoadingState
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val uploadProfilePhotoUseCase: UploadProfilePhotoUseCase,
) : ViewModel() {

    sealed interface ProfileEvent {
        data object NavigateStartup : ProfileEvent
        data class ShowError(val message: String) : ProfileEvent
        data class ShowMessage(val message: String) : ProfileEvent
    }

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events = _events.asSharedFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            getProfileUseCase()
                .map<Profile, ProfileLoadingState> { profile ->
                    ProfileLoadingState.Success(
                        profile = profile.toUiModel()
                    )
                }
                .onStart {
                    emit(value = ProfileLoadingState.Loading)
                }
                .catch { throwable ->
                    emit(
                        ProfileLoadingState.Error(
                            message = throwable.message ?: "Unable to load profile"
                        )
                    )
                }
                .collect { state ->
                    _uiState.update {
                        it.copy(profileLoadingState = state)
                    }
                }
        }
    }

    fun onLogoutClicked() {
        if (_uiState.value.isLoggingOut) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true) }
            try {
                logoutUseCase()
                _events.emit(value = ProfileEvent.NavigateStartup)
            } catch (throwable: Throwable) {
                _events.emit(
                    value = ProfileEvent.ShowError(message = throwable.message ?: "Unable to log out")
                )
            } finally {
                _uiState.update {
                    it.copy(isLoggingOut = false)
                }
            }
        }
    }

    fun onProfilePhotoSelected(request: MediaUploadRequest) {
        if (_uiState.value.isLoggingOut || _uiState.value.isUploadingPhoto) return

        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingPhoto = true) }

            try {
                uploadProfilePhotoUseCase(request)
                _events.emit(
                    value = ProfileEvent.ShowMessage("Profile picture updated")
                )
            } catch (throwable: Throwable) {
                _events.emit(
                    value = ProfileEvent.ShowError(
                        message = throwable.message ?: "Unable to update profile picture"
                    )
                )
            } finally {
                _uiState.update {
                    it.copy(isUploadingPhoto = false)
                }
            }
        }
    }
}
