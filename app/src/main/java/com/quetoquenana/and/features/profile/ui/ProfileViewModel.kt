package com.quetoquenana.and.features.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.authentication.domain.usecase.LogoutUseCase
import com.quetoquenana.and.features.profile.domain.model.Profile
import com.quetoquenana.and.features.profile.domain.model.ProfilePhotoUploadRequest
import com.quetoquenana.and.features.profile.domain.usecase.GetProfileUseCase
import com.quetoquenana.and.features.profile.domain.usecase.UploadProfilePhotoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
            _uiState.update { it.copy(isLoading = true) }
            runCatching { getProfileUseCase() }
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            profile = profile.toUiModel()
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(
                        ProfileEvent.ShowError(
                            message = throwable.message ?: "Unable to load profile"
                        )
                    )
                }
        }
    }

    fun onLogoutClicked() {
        if (_uiState.value.isLoggingOut || _uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true) }
            runCatching { logoutUseCase() }
                .onSuccess {
                    _events.emit(ProfileEvent.NavigateStartup)
                }
                .onFailure { throwable ->
                    _events.emit(
                        ProfileEvent.ShowError(
                            message = throwable.message ?: "Unable to log out"
                        )
                    )
                }
            _uiState.update { it.copy(isLoggingOut = false) }
        }
    }

    fun onProfilePhotoSelected(request: ProfilePhotoUploadRequest) {
        if (_uiState.value.isLoading || _uiState.value.isUploadingPhoto) return

        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingPhoto = true) }
            runCatching { uploadProfilePhotoUseCase(request = request) }
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            isUploadingPhoto = false,
                            profile = profile.toUiModel()
                        )
                    }
                    _events.emit(ProfileEvent.ShowMessage("Profile picture updated"))
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isUploadingPhoto = false) }
                    _events.emit(
                        ProfileEvent.ShowError(
                            message = throwable.message ?: "Unable to update profile picture"
                        )
                    )
                }
        }
    }
}

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isLoggingOut: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val profile: ProfileUiModel? = null,
)

data class ProfileUiModel(
    val id: String,
    val name: String,
    val lastname: String,
    val idNumber: String,
    val username: String,
    val externalId: String,
    val provider: String,
    val nickname: String,
    val userStatus: String,
    val photoUrl: String?,
    val profileMediaId: String?,
)

private fun Profile.toUiModel(): ProfileUiModel {
    return ProfileUiModel(
        id = id,
        name = name,
        lastname = lastname.orEmpty(),
        idNumber = idNumber.orEmpty(),
        username = username.orEmpty(),
        externalId = externalId.orEmpty(),
        provider = provider.orEmpty(),
        nickname = nickname.orEmpty(),
        userStatus = userStatus.orEmpty(),
        photoUrl = photoUrl,
        profileMediaId = profileMediaId
    )
}

