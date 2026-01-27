package com.quetoquenana.and.pedalpal.feature.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.quetoquenana.and.pedalpal.R
import com.quetoquenana.and.pedalpal.core.ui.theme.PedalPalTheme

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateHome: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginViewModel.UiEvent.NavigateHome -> onNavigateHome()
                is LoginViewModel.UiEvent.ShowError -> snackBarHostState.showSnackbar(event.message)
            }
        }
    }

    LoginScreenContent(
        modifier = modifier,
        state = state,
        snackBarHostState = snackBarHostState,
        onUsernameChange = viewModel::onUsernameChanged,
        onPasswordChange = viewModel::onPasswordChanged,
        onLoginClick = viewModel::submit,
        onCreateAccountClick = onCreateAccountClick,
        onForgotPasswordClick = onForgotPasswordClick,
    )
}

@Composable
private fun LoginScreenContent(
    modifier: Modifier = Modifier,
    state: LoginUiState,
    snackBarHostState: SnackbarHostState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Welcome to PedalPal",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 24.sp,
        )

        Spacer(Modifier.height(12.dp))

        Image(
            painter = painterResource(id = R.drawable.mobi_bike_logo),
            contentDescription = "MobiBike logo",
            modifier = Modifier.size(160.dp),
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = state.username,
            onValueChange = onUsernameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Username") },
            singleLine = true,
            enabled = !state.isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            singleLine = true,
            enabled = !state.isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(Modifier.size(12.dp))
            }
            Text(if (state.isLoading) "Logging in…" else "Login")
        }

        if (state.isSuccess) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Login successful",
                color = MaterialTheme.colorScheme.tertiary,
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Doesn't have an account? ")
            Text(
                text = "Create One",
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(enabled = !state.isLoading) { onCreateAccountClick() },
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Forgot Password",
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(enabled = !state.isLoading) { onForgotPasswordClick() },
        )

        // SnackBars for errors
        Spacer(Modifier.height(12.dp))
        SnackbarHost(hostState = snackBarHostState)
    }
}

@Preview(showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    PedalPalTheme {
        LoginScreenContent(
            modifier = Modifier.padding(vertical = 24.dp),
            state = LoginUiState(username = "demo", password = "password", isLoading = false),
            snackBarHostState = remember { SnackbarHostState() },
            onUsernameChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onCreateAccountClick = {},
            onForgotPasswordClick = {},
        )
    }
}


@Preview(showSystemUi = true)
@Composable
private fun LoginScreenPreviewIsLoading() {
    PedalPalTheme {
        LoginScreenContent(
            modifier = Modifier.padding(vertical = 24.dp),
            state = LoginUiState(username = "demo", password = "password", isLoading = true),
            snackBarHostState = remember { SnackbarHostState() },
            onUsernameChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onCreateAccountClick = {},
            onForgotPasswordClick = {},
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun LoginScreenPreview_WithSnackBar() {
    PedalPalTheme {
        val snackBarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            snackBarHostState.showSnackbar("Invalid username or password")
        }

        LoginScreenContent(
            modifier = Modifier.padding(vertical = 24.dp),
            state = LoginUiState(username = "demo", password = "wrong", isLoading = false),
            snackBarHostState = snackBarHostState,
            onUsernameChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onCreateAccountClick = {},
            onForgotPasswordClick = {},
        )
    }
}