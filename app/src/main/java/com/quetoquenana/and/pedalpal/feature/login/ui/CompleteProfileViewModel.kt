package com.quetoquenana.and.pedalpal.feature.login.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.pedalpal.feature.login.domain.model.BackendCreateUserRequest
import com.quetoquenana.and.pedalpal.feature.login.domain.model.BackendPerson
import com.quetoquenana.and.pedalpal.feature.login.domain.model.BackendUser
import com.quetoquenana.and.pedalpal.feature.login.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompleteProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    sealed interface CompleteProfileEvent {
        object NavigateHome : CompleteProfileEvent
        data class ShowError(val message: String) : CompleteProfileEvent
    }

    private val _nickname = MutableStateFlow("")
    val nickname = _nickname.asStateFlow()

    private val _idNumber = MutableStateFlow("")
    val idNumber = _idNumber.asStateFlow()

    private val _firstName = MutableStateFlow("")
    val firstName = _firstName.asStateFlow()

    private val _lastName = MutableStateFlow("")
    val lastName = _lastName.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

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
                if (!display.isNullOrBlank()) {
                    val parts = display.trim().split(" ", limit = 2)
                    _firstName.value = parts.getOrNull(0).orEmpty()
                    _lastName.value = parts.getOrNull(1).orEmpty()
                } else {
                    _firstName.value = ""
                    _lastName.value = ""
                }
                _nickname.value = it.email?.substringBefore("@") ?: ""
            }
        } catch (e: Exception) {
            _events.emit(CompleteProfileEvent.ShowError(e.message ?: "Failed to load profile"))
        }
    }

    fun onNicknameChanged(value: String) {
        _nickname.value = value
    }

    fun onIdNumberChanged(value: String) {
        _idNumber.value = value
    }

    fun onFirstNameChanged(value: String) {
        _firstName.value = value
    }

    fun onLastNameChanged(value: String) {
        _lastName.value = value
    }

    fun saveProfile() {
        viewModelScope.launch {
            if (_firstName.value.isBlank()) {
                _events.emit(CompleteProfileEvent.ShowError("First name is required"))
                return@launch
            }

            _isSaving.value = true
            try {
                val token = authRepository.getFirebaseIdToken(forceRefresh = true)

                // Build backend request and call create/update
                val person = BackendPerson(idNumber = _idNumber.value.trim(), name = _firstName.value.trim(), lastname = _lastName.value.trim())
                val backendUser = BackendUser(username = _nickname.value.trim(), nickname = _nickname.value.trim(), person = person)
                val request = BackendCreateUserRequest(user = backendUser, roleName = "USER")

                authRepository.createBackendUser(request = request, firebaseIdToken = token)

                _events.emit(value = CompleteProfileEvent.NavigateHome)
            } catch (e: Exception) {
                _events.emit(value = CompleteProfileEvent.ShowError(message = e.message ?: "Error saving profile"))
            } finally {
                _isSaving.value = false
            }
        }
    }
}
