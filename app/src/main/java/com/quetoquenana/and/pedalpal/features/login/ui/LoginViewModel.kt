package com.quetoquenana.and.pedalpal.features.login.ui

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.quetoquenana.and.pedalpal.features.login.domain.useCase.CheckEmailVerifiedUseCase
import com.quetoquenana.and.pedalpal.features.login.domain.useCase.ReloadUserUseCase
import com.quetoquenana.and.pedalpal.features.login.domain.useCase.SendVerificationEmailUseCase
import com.quetoquenana.and.pedalpal.features.login.domain.useCase.SignInWithEmailUseCase
import com.quetoquenana.and.pedalpal.features.login.domain.useCase.SignInWithGoogleUseCase
import com.quetoquenana.and.pedalpal.features.login.domain.useCase.SignUpWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val checkEmailVerified: CheckEmailVerifiedUseCase,
    private val googleSignInClient: GoogleSignInClient,
    private val sendVerificationEmail: SendVerificationEmailUseCase,
    private val signInWithGoogle: SignInWithGoogleUseCase,
    private val signInWithEmail: SignInWithEmailUseCase,
    private val signUpWithEmail: SignUpWithEmailUseCase,
    private val reloadUser: ReloadUserUseCase
) : ViewModel() {

    sealed interface LoginUiEvent {
        object NavigateHome : LoginUiEvent
        object NavigateCompleteProfile : LoginUiEvent
        data class ShowError(val message: String) : LoginUiEvent
    }

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<LoginUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun onContinueWithEmailSubmit() {
        viewModelScope.launch {
            setLoading(true)

            val result = signInWithEmail(
                email = uiState.value.email,
                password = uiState.value.password
            )

            result.fold(
                onSuccess = { user ->
                    if (user.isEmailVerified) {
                        _uiEvents.emit(value = LoginUiEvent.NavigateCompleteProfile)
                    } else {
                        onSendVerificationEmail()
                        _uiState.update {
                            it.copy(isEmailVerificationSent = true)
                        }
                    }
                },
                onFailure = {
                    Timber.w("Sign in with email failed: ${it.message}")
                    signUpWithEmail()
                }
            )
            setLoading(false)
        }
    }

    fun getGoogleSignInIntent(): Intent =
        googleSignInClient.signInIntent

    fun onGoogleSignInFailed(message: String) {
        viewModelScope.launch {
            _uiEvents.emit(LoginUiEvent.ShowError(message))
        }
    }

    fun onGoogleIdTokenReceived(idToken: String) {
        Timber.d("Received ID token from Google sign-in: $idToken")
        viewModelScope.launch {
            setLoading(true)

            val result = signInWithGoogle(idToken)
            result.fold(
                onSuccess = {
                    _uiEvents.emit(value = LoginUiEvent.NavigateCompleteProfile)
                },
                onFailure = {
                    showError(it)
                }
            )
            setLoading(false)
        }
    }

    fun onCheckEmailVerified() {
        viewModelScope.launch {
            reloadUser()

            if (checkEmailVerified()) {
                _uiEvents.emit(value = LoginUiEvent.NavigateCompleteProfile)
            } else {
                _uiEvents.emit(value = LoginUiEvent.ShowError("Email not verified yet"))
            }
        }
    }

    fun onResendVerificationEmail() {
        viewModelScope.launch {
            setLoading(true)
            onSendVerificationEmail()
            setLoading(false)
        }
    }

    private suspend fun signUpWithEmail() {
        Timber.d(message = "Entering sign up flow")
        val result = signUpWithEmail(
            email = uiState.value.email,
            password = uiState.value.password
        )

        result.fold(
            onSuccess = { user ->
                Timber.d(message = "Success result obtained from sign up $user")
                onSendVerificationEmail()
                _uiState.update {
                    it.copy(isEmailVerificationSent = true)
                }
            },
            onFailure = {
                Timber.e(message = "Failure result obtained from sign up $it")
                showError(it)
            }
        )
    }

    private suspend fun onSendVerificationEmail() {
        Timber.d(message = "Entering sign up flow")
        sendVerificationEmail()
    }

    private fun setLoading(value: Boolean) {
        _uiState.update { it.copy(isLoading = value) }
    }

    private suspend fun showError(throwable: Throwable) {
        _uiEvents.emit(
            value = LoginUiEvent.ShowError(
                message = throwable.message ?: "Something went wrong"
            )
        )
    }
}