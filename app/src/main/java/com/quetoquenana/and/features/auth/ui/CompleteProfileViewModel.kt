package com.quetoquenana.and.features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.auth.domain.model.BackendCreateUserRequest
import com.quetoquenana.and.features.auth.domain.model.BackendPerson
import com.quetoquenana.and.features.auth.domain.model.BackendUser
import com.quetoquenana.and.features.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.trim

@HiltViewModel
class CompleteProfileViewModel @Inject constructor(
    private val authRepository: com.quetoquenana.and.features.auth.domain.repository.AuthRepository
) : ViewModel() {

    sealed interface CompleteProfileEvent {
        object NavigateHome : CompleteProfileEvent
        data class ShowError(val message: String) : CompleteProfileEvent
    }

    private val _uiState = MutableStateFlow(CompleteProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CompleteProfileEvent>()
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            initializeFromAuth()
        }
    }

    private suspend fun initializeFromAuth() {
        try {
            val user = authRepository.getCurrentUserInfo()
            user?.let {
                val display = it.displayName
                val (first, last) = if (!display.isNullOrBlank()) {
                    val parts = display.trim().split(" ", limit = 2)
                    parts.getOrNull(0).orEmpty() to parts.getOrNull(1).orEmpty()
                } else {
                    "" to ""
                }
                val nicknameFallback = it.email?.substringBefore("@") ?: ""
                _uiState.update { current ->
                    current.copy(
                        firstName = first,
                        lastName = last,
                        nickname = nicknameFallback
                    )
                }
            }
        } catch (e: Exception) {
            _events.emit(CompleteProfileEvent.ShowError(e.message ?: "Failed to load profile"))
        }
    }

    fun onNicknameChanged(value: String) {
        _uiState.update { it.copy(nickname = value) }
    }

    fun onIdNumberChanged(value: String) {
        _uiState.update { it.copy(idNumber = value) }
    }

    fun onFirstNameChanged(value: String) {
        _uiState.update { it.copy(firstName = value) }
    }

    fun onLastNameChanged(value: String) {
        _uiState.update { it.copy(lastName = value) }
    }

    fun saveProfile() {
        viewModelScope.launch {
            val state = uiState.value
            _uiState.update { it.copy(isSaving = true) }
            try {
                val token = authRepository.getFirebaseIdToken(forceRefresh = true)

                // Build backend request and call create/update
                val person =
                    _root_ide_package_.com.quetoquenana.and.features.auth.domain.model.BackendPerson(
                        idNumber = state.idNumber.trim(),
                        name = state.firstName.trim(),
                        lastname = state.lastName.trim()
                    )
                val backendUser =
                    _root_ide_package_.com.quetoquenana.and.features.auth.domain.model.BackendUser(
                        username = state.nickname.trim(),
                        nickname = state.nickname.trim(),
                        person = person
                    )
                val request =
                    _root_ide_package_.com.quetoquenana.and.features.auth.domain.model.BackendCreateUserRequest(
                        user = backendUser,
                        roleName = "USER"
                    )
                authRepository.createBackendUser(request = request, firebaseIdToken = token)

                _events.emit(value = CompleteProfileEvent.NavigateHome)
            } catch (e: Exception) {
                _events.emit(value = CompleteProfileEvent.ShowError(message = e.message ?: "Error saving profile"))
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}
