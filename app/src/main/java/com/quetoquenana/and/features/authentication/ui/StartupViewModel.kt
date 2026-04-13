package com.quetoquenana.and.features.authentication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.authentication.domain.model.SessionStatus
import com.quetoquenana.and.features.authentication.domain.usecase.RestoreSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class StartupViewModel @Inject constructor(
    private val restoreSessionUseCase: RestoreSessionUseCase
) : ViewModel() {

    private val _uiEvents = Channel<StartupUiEvent>(capacity = Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    init {
        verifySession()
    }

    private fun verifySession() {
        Timber.d("Verifying session...")
        viewModelScope.launch {
            when (restoreSessionUseCase()) {
                SessionStatus.Authenticated -> {
                    Timber.d("Verifying session Authenticated...")
                    _uiEvents.send(StartupUiEvent.NavigateHome)
                }
                SessionStatus.ProfileCompletionRequired -> {
                    Timber.d("Verifying session ProfileCompletionRequired...")
                    _uiEvents.send(StartupUiEvent.NavigateCompleteProfile)
                }
                SessionStatus.Unauthenticated -> {
                    Timber.d("Verifying session Unauthenticated...")
                    _uiEvents.send(StartupUiEvent.NavigateAuth)
                }
            }
        }
    }

    sealed interface StartupUiEvent {
        data object NavigateHome : StartupUiEvent
        data object NavigateAuth : StartupUiEvent
        data object NavigateCompleteProfile : StartupUiEvent
    }
}