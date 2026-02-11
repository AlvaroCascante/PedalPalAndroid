package com.quetoquenana.and.features.auth.ui

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.quetoquenana.and.features.auth.domain.useCase.CheckEmailVerifiedUseCase
import com.quetoquenana.and.features.auth.domain.useCase.ReloadUserUseCase
import com.quetoquenana.and.features.auth.domain.useCase.SendVerificationEmailUseCase
import com.quetoquenana.and.features.auth.domain.useCase.SignInWithEmailUseCase
import com.quetoquenana.and.features.auth.domain.useCase.SignInWithGoogleUseCase
import com.quetoquenana.and.features.auth.domain.useCase.SignUpWithEmailUseCase
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
class AuthViewModel @Inject constructor(
    private val checkEmailVerified: com.quetoquenana.and.features.auth.domain.useCase.CheckEmailVerifiedUseCase,
    private val googleSignInClient: GoogleSignInClient,
    private val sendVerificationEmail: com.quetoquenana.and.features.auth.domain.useCase.SendVerificationEmailUseCase,
    private val signInWithEmail: com.quetoquenana.and.features.auth.domain.useCase.SignInWithEmailUseCase,
    private val signInWithGoogle: com.quetoquenana.and.features.auth.domain.useCase.SignInWithGoogleUseCase,
    private val signUpWithEmail: com.quetoquenana.and.features.auth.domain.useCase.SignUpWithEmailUseCase,
    private val reloadUser: com.quetoquenana.and.features.auth.domain.useCase.ReloadUserUseCase
) : ViewModel() {

    sealed interface AuthUiEvent {
        object NavigateHome : AuthUiEvent
        object NavigateCompleteProfile : AuthUiEvent
        data class ShowError(val message: String) : AuthUiEvent
    }

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<AuthUiEvent>()
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
                        _uiEvents.emit(value = AuthUiEvent.NavigateCompleteProfile)
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
            _uiEvents.emit(AuthUiEvent.ShowError(message))
        }
    }

    fun onGoogleIdTokenReceived(idToken: String) {
        Timber.d("Received ID token from Google sign-in: $idToken")
        viewModelScope.launch {
            setLoading(true)

            val result = signInWithGoogle(idToken)
            result.fold(
                onSuccess = {
                    _uiEvents.emit(value = AuthUiEvent.NavigateCompleteProfile)
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
                _uiEvents.emit(value = AuthUiEvent.NavigateCompleteProfile)
            } else {
                _uiEvents.emit(value = AuthUiEvent.ShowError("Email not verified yet"))
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
            value = AuthUiEvent.ShowError(
                message = throwable.message ?: "Something went wrong"
            )
        )
    }
}