package com.quetoquenana.and.features.authentication.ui

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.quetoquenana.and.features.authentication.domain.model.CreateUserRequest
import com.quetoquenana.and.features.authentication.domain.model.CreateUserUseCaseResult
import com.quetoquenana.and.features.authentication.domain.model.FirebaseUserModel
import com.quetoquenana.and.features.authentication.domain.usecase.CheckEmailVerifiedUseCase
import com.quetoquenana.and.features.authentication.domain.usecase.CreateUserUseCase
import com.quetoquenana.and.features.authentication.domain.usecase.ReloadUserUseCase
import com.quetoquenana.and.features.authentication.domain.usecase.SendVerificationEmailUseCase
import com.quetoquenana.and.features.authentication.domain.usecase.SignInWithEmailUseCase
import com.quetoquenana.and.features.authentication.domain.usecase.SignInWithGoogleUseCase
import com.quetoquenana.and.features.authentication.domain.usecase.SignUpWithEmailUseCase
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
    private val checkEmailVerified: CheckEmailVerifiedUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val googleSignInClient: GoogleSignInClient,
    private val sendVerificationEmail: SendVerificationEmailUseCase,
    private val signInWithEmail: SignInWithEmailUseCase,
    private val signInWithGoogle: SignInWithGoogleUseCase,
    private val signUpWithEmail: SignUpWithEmailUseCase,
    private val reloadUser: ReloadUserUseCase
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
                        completeRegistration(user)
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
                onSuccess = { user ->
                    completeRegistration(user)
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
                completeRegistration(
                    user = FirebaseUserModel(
                        uid = "",
                        email = uiState.value.email,
                        displayName = null,
                        isEmailVerified = true
                    )
                )
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

    private suspend fun completeRegistration(user: FirebaseUserModel) {
        when (createUserUseCase(buildCreateUserRequest(user))) {
            is CreateUserUseCaseResult.Success -> {
                _uiEvents.emit(value = AuthUiEvent.NavigateCompleteProfile)
            }

            CreateUserUseCaseResult.InvalidFirebaseSession -> {
                _uiEvents.emit(
                    value = AuthUiEvent.ShowError("Session expired, please log in again")
                )
            }

            CreateUserUseCaseResult.NetworkError -> {
                _uiEvents.emit(
                    value = AuthUiEvent.ShowError("Network error, please try again")
                )
            }

            CreateUserUseCaseResult.UnknownError -> {
                _uiEvents.emit(
                    value = AuthUiEvent.ShowError("Something went wrong")
                )
            }
        }
    }

    private fun buildCreateUserRequest(user: FirebaseUserModel): CreateUserRequest {
        val displayName = user.displayName?.trim().orEmpty()
        val emailPrefix = user.email?.substringBefore("@")?.trim().orEmpty()
        val nameParts = displayName.split(Regex("\\s+")).filter { it.isNotBlank() }

        val firstName = nameParts.firstOrNull().orEmpty().ifBlank {
            emailPrefix.ifBlank { "PedalPal" }
        }
        val lastName = nameParts.drop(1).joinToString(separator = " ")
        val nickname = emailPrefix.ifBlank {
            displayName.replace(" ", "").ifBlank { user.uid.ifBlank { "pedalpal-user" } }
        }

        return CreateUserRequest(
            idNumber = "",
            name = firstName,
            lastname = lastName,
            nickname = nickname
        )
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
