package com.quetoquenana.and.features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.auth.domain.model.CreateUserRequest
import com.quetoquenana.and.features.auth.domain.model.CreateUserUseCaseResult
import com.quetoquenana.and.features.auth.domain.usecase.CreateUserUseCase
import com.quetoquenana.and.features.auth.domain.usecase.GetFirebaseUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompleteProfileViewModel @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
    private val getFirebaseUserUseCase: GetFirebaseUserUseCase
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
            initializeFrom()
        }
    }

    private suspend fun initializeFrom() {
        val user = getFirebaseUserUseCase()
        val display = user.displayName
        val (first, last) = if (!display.isNullOrBlank()) {
            val parts = display.trim().split(" ", limit = 2)
            parts.getOrNull(0).orEmpty() to parts.getOrNull(1).orEmpty()
        } else {
            "" to ""
        }
        val nicknameFallback = user.email?.substringBefore("@") ?: ""

        _uiState.update { current ->
            current.copy(
                nickname = nicknameFallback,
                firstName = first,
                lastName = last
            )
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
                val request = CreateUserRequest(
                    idNumber = state.idNumber.trim(),
                    name = state.firstName.trim(),
                    lastname = state.lastName.trim(),
                    nickname = state.nickname.trim()
                )

                val result = createUserUseCase(request = request)

                when (result) {
                    is CreateUserUseCaseResult.Success -> _events.emit(value = CompleteProfileEvent.NavigateHome)
                    CreateUserUseCaseResult.NetworkError -> _events.emit(
                        value = CompleteProfileEvent.ShowError(
                            message = "Network error, please try again"
                        )
                    )

                    CreateUserUseCaseResult.InvalidFirebaseSession -> _events.emit(
                        value = CompleteProfileEvent.ShowError(
                            message = "Session expired, please log in again"
                        )
                    )

                    CreateUserUseCaseResult.UnknownError -> _events.emit(
                        value = CompleteProfileEvent.ShowError(
                            message = "An unknown error occurred, please try again"
                        )
                    )
                }
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}
