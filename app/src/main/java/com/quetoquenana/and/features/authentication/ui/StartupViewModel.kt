package com.quetoquenana.and.features.authentication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quetoquenana.and.features.authentication.domain.model.SessionStatus
import com.quetoquenana.and.features.authentication.domain.usecase.RestoreSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class StartupViewModel @Inject constructor(
    private val restoreSessionUseCase: RestoreSessionUseCase
) : ViewModel() {

    private val _uiEvents = MutableSharedFlow<StartupUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        verifySession()
    }

    private fun verifySession() {
        viewModelScope.launch {
            when (restoreSessionUseCase()) {
                SessionStatus.Authenticated -> {
                    _uiEvents.emit(StartupUiEvent.NavigateHome)
                }
                SessionStatus.ProfileCompletionRequired -> {
                    _uiEvents.emit(StartupUiEvent.NavigateCompleteProfile)
                }
                SessionStatus.Unauthenticated -> {
                    _uiEvents.emit(StartupUiEvent.NavigateAuth)
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