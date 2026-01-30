package com.quetoquenana.and.pedalpal.feature.login.ui

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.quetoquenana.and.pedalpal.R
import com.quetoquenana.and.pedalpal.core.ui.theme.PedalPalTheme

@Composable
fun LoginRoute(
    onNavigateHome: () -> Unit,
    onNavigateCompleteProfile: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
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
        state = state,
        snackBarHostState = snackBarHostState,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onEmailSubmit = viewModel::onEmailSubmit,
        onGoogleIntentRequested = viewModel::getGoogleSignInIntent,
        onGoogleIdTokenReceived = viewModel::onGoogleIdToken,
        onCheckEmailVerified = viewModel::onCheckEmailVerified
    )
}

@Composable
fun LoginScreen(
    state: LoginUiState,
    snackBarHostState: SnackbarHostState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onEmailSubmit: () -> Unit,
    onGoogleIntentRequested: () -> Intent,
    onGoogleIdTokenReceived: (String) -> Unit,
    onCheckEmailVerified: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to PedalPal",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(24.dp))

        Image(
            painter = painterResource(R.drawable.mobi_bike_logo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .size(160.dp),
            alignment = Alignment.Center
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChanged,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChanged,
            label = { Text(text = "Password") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(height = 16.dp))

        Button(
            onClick = onEmailSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            Text(text = "Sign In")
        }

        Spacer(Modifier.height(height = 8.dp))

        GoogleSignInButton(
            enabled = !state.isLoading,
            onIntentRequested = onGoogleIntentRequested,
            onIdTokenReceived = onGoogleIdTokenReceived
        )

        if (state.isEmailVerificationSent) {
            Spacer(Modifier.height(height = 24.dp))

            Text(
                text = "Check your email, verify your account,\nthen come back and tap below.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(height = 8.dp))

            Button(onClick = onCheckEmailVerified) {
                Text("I've verified my email")
            }
        }

        SnackbarHost(
            hostState = snackBarHostState
        )
    }
}

@Composable
fun GoogleSignInButton(
    enabled: Boolean,
    onIntentRequested: () -> Intent,
    onIdTokenReceived: (String) -> Unit
) {
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let(onIdTokenReceived)
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

@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    PedalPalTheme {
        LoginScreen(
            state = LoginUiState(
                email = "",
                password = "",
                isLoading = false,
                isEmailVerificationSent = false
            ),
            snackBarHostState = SnackbarHostState(),
            onEmailChanged = {},
            onPasswordChanged = {},
            onEmailSubmit = {},
            onGoogleIntentRequested = { Intent() },
            onGoogleIdTokenReceived = {},
            onCheckEmailVerified = {}
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview_Loading() {
    PedalPalTheme {
        LoginScreen(
            state = LoginUiState(
                email = "",
                password = "",
                isLoading = true,
                isEmailVerificationSent = false
            ),
            snackBarHostState = SnackbarHostState(),
            onEmailChanged = {},
            onPasswordChanged = {},
            onEmailSubmit = {},
            onGoogleIntentRequested = { Intent() },
            onGoogleIdTokenReceived = {},
            onCheckEmailVerified = {}
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview_EmailSent() {
    PedalPalTheme {
        LoginScreen(
            state = LoginUiState(
                email = "",
                password = "",
                isLoading = false,
                isEmailVerificationSent = true
            ),
            snackBarHostState = SnackbarHostState(),
            onEmailChanged = {},
            onPasswordChanged = {},
            onEmailSubmit = {},
            onGoogleIntentRequested = { Intent() },
            onGoogleIdTokenReceived = {},
            onCheckEmailVerified = {}
        )
    }
}
