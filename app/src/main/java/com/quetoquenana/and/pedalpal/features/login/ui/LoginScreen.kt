package com.quetoquenana.and.pedalpal.features.login.ui

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.quetoquenana.and.pedalpal.core.ui.components.LogoImage
import com.quetoquenana.and.pedalpal.core.ui.theme.PedalPalTheme
import timber.log.Timber

@Composable
fun LoginRoute(
    onNavigateHome: () -> Unit,
    onNavigateCompleteProfile: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                LoginViewModel.LoginUiEvent.NavigateHome -> onNavigateHome()
                LoginViewModel.LoginUiEvent.NavigateCompleteProfile -> onNavigateCompleteProfile()
                is LoginViewModel.LoginUiEvent.ShowError ->
                    snackBarHostState.showSnackbar(event.message)
            }
        }
    }

    LoginScreen(
        uiState = uiState,
        snackBarHostState = snackBarHostState,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onContinueWithEmailSubmit = viewModel::onContinueWithEmailSubmit,
        onGoogleIntentRequested = viewModel::getGoogleSignInIntent,
        onGoogleIdTokenReceived = viewModel::onGoogleIdTokenReceived,
        onGoogleSignInFailed = viewModel::onGoogleSignInFailed,
        onCheckEmailVerified = viewModel::onCheckEmailVerified,
        onResendVerificationEmail = viewModel::onResendVerificationEmail
    )
}

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    snackBarHostState: SnackbarHostState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onContinueWithEmailSubmit: () -> Unit,
    onGoogleIntentRequested: () -> Intent,
    onGoogleIdTokenReceived: (String) -> Unit,
    onGoogleSignInFailed: (String) -> Unit,
    onCheckEmailVerified: () -> Unit,
    onResendVerificationEmail: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        LoginScreenHeader()

        LoginScreenFields(
            uiState = uiState,
            onEmailChanged = onEmailChanged,
            onPasswordChanged = onPasswordChanged,
            onSubmit = onContinueWithEmailSubmit
        )

        if (uiState.isEmailVerificationSent) {
            LoginScreenEmailSent(
                onCheckEmailVerified = onCheckEmailVerified,
                onResendVerificationEmail = onResendVerificationEmail
            )
        } else {
            LoginScreenButtons(
                uiState = uiState,
                onContinueWithEmailSubmit = onContinueWithEmailSubmit,
                onGoogleIntentRequested = onGoogleIntentRequested,
                onGoogleIdTokenReceived = onGoogleIdTokenReceived,
                onGoogleSignInFailed = onGoogleSignInFailed
            )
        }

        SnackbarHost(
            hostState = snackBarHostState
        )
    }
}

@Composable
fun LoginScreenHeader() {
    Text(
        text = "Welcome to PedalPal",
        style = MaterialTheme.typography.headlineMedium
    )

    Spacer(Modifier.height(24.dp))

    LogoImage()

    Spacer(Modifier.height(24.dp))
}

@Composable
fun LoginScreenFields(
    uiState: LoginUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val emailRequester = remember { FocusRequester() }
    val passwordRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = uiState.email,
        onValueChange = onEmailChanged,
        label = { Text("Email") },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester = emailRequester)
            .onPreviewKeyEvent { event ->
                if (event.key == Key.Tab && event.type == KeyEventType.KeyDown) {
                    passwordRequester.requestFocus()
                    true
                } else false
            },
        enabled = !uiState.isLoading,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Email
        ),
        keyboardActions = KeyboardActions(
            onNext = { passwordRequester.requestFocus() }
        )
    )

    Spacer(Modifier.height(8.dp))

    OutlinedTextField(
        value = uiState.password,
        onValueChange = onPasswordChanged,
        label = { Text(text = "Password") },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(passwordRequester)
            .onPreviewKeyEvent { event ->
                if (event.key == Key.Tab && event.type == KeyEventType.KeyDown) {
                    focusManager.clearFocus()
                    true
                } else false
            },
        enabled = !uiState.isLoading,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                onSubmit()
            }
        )
    )

    Spacer(Modifier.height(height = 16.dp))
}

@Composable
fun LoginScreenButtons(
    uiState: LoginUiState,
    onContinueWithEmailSubmit: () -> Unit,
    onGoogleIntentRequested: () -> Intent,
    onGoogleIdTokenReceived: (String) -> Unit,
    onGoogleSignInFailed: (String) -> Unit,
) {
    Button(
        onClick = onContinueWithEmailSubmit,
        modifier = Modifier.fillMaxWidth(),
        enabled = !uiState.isLoading
    ) {
        Text(text = "Sign In")
    }

    Spacer(Modifier.height(height = 8.dp))

    GoogleSignInButton(
        enabled = !uiState.isLoading,
        onIntentRequested = onGoogleIntentRequested,
        onIdTokenReceived = onGoogleIdTokenReceived,
        onFailure = onGoogleSignInFailed
    )
}

@Composable
fun LoginScreenEmailSent(
    onCheckEmailVerified: () -> Unit,
    onResendVerificationEmail: () -> Unit
) {
    Spacer(Modifier.height(height = 24.dp))

    Text(
        text = "Check your email, verify your account,\nthen come back and tap below.",
        style = MaterialTheme.typography.bodyMedium
    )

    Spacer(Modifier.height(height = 8.dp))

    Button(onClick = onCheckEmailVerified) {
        Text("I've verified my email")
    }

    Spacer(Modifier.height(height = 12.dp))

    ResendInlineText(onResend = onResendVerificationEmail)
}

@Composable
fun GoogleSignInButton(
    enabled: Boolean,
    onIntentRequested: () -> Intent,
    onIdTokenReceived: (String) -> Unit,
    onFailure: (String) -> Unit
) {
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val idToken = account.idToken
                    if (idToken != null) {
                        onIdTokenReceived(idToken)
                    } else {
                        Timber.e("No id token returned from Google account")
                        onFailure("No id token returned from Google account")
                    }
                } catch (e: ApiException) {
                    Timber.e("Google sign-in failed: $e")
                    onFailure(e.localizedMessage ?: "Google sign-in failed")
                }
            }
        }

    Button(
        onClick = { launcher.launch(onIntentRequested()) },
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled
    ) {
        Text("Continue with Google")
    }
}

@Composable
fun ResendInlineText(
    onResend: () -> Unit
) {
    val annotated = buildAnnotatedString {
        append("Didn't receive the email, resend it by clicking ")
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline)) {
            append("here")
        }
        append(".")
    }

    Text(
        text = annotated,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onResend() },
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)
    )
}

@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    PedalPalTheme {
        LoginScreen(
            uiState = LoginUiState(
                email = "",
                password = "",
                isLoading = false,
                isEmailVerificationSent = false
            ),
            snackBarHostState = SnackbarHostState(),
            onEmailChanged = {},
            onPasswordChanged = {},
            onContinueWithEmailSubmit = {},
            onGoogleIntentRequested = { Intent() },
            onGoogleIdTokenReceived = {},
            onCheckEmailVerified = {},
            onGoogleSignInFailed = {},
            onResendVerificationEmail = {}
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview_Loading() {
    PedalPalTheme {
        LoginScreen(
            uiState = LoginUiState(
                email = "",
                password = "",
                isLoading = true,
                isEmailVerificationSent = false
            ),
            snackBarHostState = SnackbarHostState(),
            onEmailChanged = {},
            onPasswordChanged = {},
            onContinueWithEmailSubmit = {},
            onGoogleIntentRequested = { Intent() },
            onGoogleIdTokenReceived = {},
            onCheckEmailVerified = {},
            onGoogleSignInFailed = {},
            onResendVerificationEmail = {}
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview_EmailSent() {
    PedalPalTheme {
        LoginScreen(
            uiState = LoginUiState(
                email = "",
                password = "",
                isLoading = false,
                isEmailVerificationSent = true
            ),
            snackBarHostState = SnackbarHostState(),
            onEmailChanged = {},
            onPasswordChanged = {},
            onContinueWithEmailSubmit = {},
            onGoogleIntentRequested = { Intent() },
            onGoogleIdTokenReceived = {},
            onCheckEmailVerified = {},
            onGoogleSignInFailed = {},
            onResendVerificationEmail = {}
        )
    }
}
