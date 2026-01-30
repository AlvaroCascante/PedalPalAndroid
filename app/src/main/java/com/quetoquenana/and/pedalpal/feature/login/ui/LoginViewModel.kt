package com.quetoquenana.and.pedalpal.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.pedalpal.feature.auth.domain.useCase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {
    sealed interface UiEvent {
        object NavigateHome : UiEvent
        data class ShowError(val message: String) : UiEvent
    }

    private val _events = MutableSharedFlow<UiEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onUsernameChanged(value: String) {
        _state.update { it.copy(username = value, errorMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _state.update { it.copy(password = value, errorMessage = null) }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun submit() {
        val current = _state.value
        if (current.isLoading) return

        val username = current.username.trim()
        val password = current.password

        if (username.isBlank() || password.isBlank()) {
            _state.update { it.copy(errorMessage = "Username and password are required") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null, isSuccess = false) }
            try {
                // perform login (tokens/storage handled elsewhere)
                loginUseCase(username = username, password = password)
                _state.update { it.copy(isLoading = false, isSuccess = true) }
                _events.emit(UiEvent.NavigateHome)
            } catch (t: Throwable) {
                val error = "Login failed"
                try { Timber.Forest.e(t.message.toString()) } catch (_: Throwable) {}
                _state.update {
                    it.copy(isLoading = false, isSuccess = false, errorMessage = error)
                }
                _events.emit(UiEvent.ShowError(error))
            }
        }
    }
}