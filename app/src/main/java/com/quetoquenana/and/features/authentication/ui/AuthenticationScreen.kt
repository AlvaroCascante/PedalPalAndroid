package com.quetoquenana.and.features.authentication.ui

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.quetoquenana.and.R
import com.quetoquenana.and.core.ui.components.BasePreviewContainer
import com.quetoquenana.and.core.ui.components.DarkLightPreviews
import com.quetoquenana.and.core.ui.components.DefaultOutlinedTextField
import com.quetoquenana.and.core.ui.components.DefaultProgressIndicator
import com.quetoquenana.and.core.ui.components.LoginUiStateProvider
import com.quetoquenana.and.core.ui.components.LogoImage
import com.quetoquenana.and.core.ui.components.defaultContainerPaddingValues
import com.quetoquenana.and.core.ui.components.defaultPaddingValues
import com.quetoquenana.and.core.ui.components.sharedSectionTopShape

@Composable
fun AuthenticationRoute(
    contentPadding: PaddingValues,
    onNavigateHome: () -> Unit,
    onNavigateCompleteProfile: () -> Unit,
    viewModel: AuthenticationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                AuthenticationViewModel.AuthUiEvent.NavigateHome -> onNavigateHome()
                AuthenticationViewModel.AuthUiEvent.NavigateCompleteProfile -> onNavigateCompleteProfile()
                is AuthenticationViewModel.AuthUiEvent.ShowError -> snackBarHostState.showSnackbar(event.message)
            }
        }
    }

    AuthenticationScreen(
        contentPadding = contentPadding,
        uiState = uiState,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onContinueWithEmailClicked = viewModel::onContinueWithEmailClicked,
        onGoogleIntentRequested = viewModel::getGoogleSignInIntent,
        onGoogleIdTokenReceived = viewModel::onGoogleIdTokenReceived,
        onGoogleSignInFailed = viewModel::onGoogleSignInFailed,
        onCheckEmailVerified = viewModel::onCheckEmailVerified,
        onResendVerificationEmail = viewModel::onResendVerificationEmail
    )
}

@Composable
fun AuthenticationScreen(
    contentPadding: PaddingValues = PaddingValues(all = 0.dp),
    uiState: LoginUiState,
    onEmailChanged: (String) -> Unit = {},
    onPasswordChanged: (String) -> Unit = {},
    onContinueWithEmailClicked: () -> Unit = {},
    onGoogleIntentRequested: () -> Intent,
    onGoogleIdTokenReceived: (String) -> Unit = {},
    onGoogleSignInFailed: (String) -> Unit = {},
    onCheckEmailVerified: () -> Unit = {},
    onResendVerificationEmail: () -> Unit = {}
) {
    val modifier = Modifier
        .fillMaxWidth()
        .background(color = MaterialTheme.colorScheme.primary)

    when {
        uiState.isLoading -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                DefaultProgressIndicator()
            }
        } else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                // INFO : Use systemBars.asPaddingValues() to ensure that the content is not
                // obscured by system bars on devices with gesture navigation or cutouts.
                contentPadding = contentPadding,
            ) {
                item { AuthenticationHeaderSection() }

                item {
                    GoogleSignInButton(
                        enabled = !uiState.isLoading,
                        onIntentRequested = onGoogleIntentRequested,
                        onIdTokenReceived = onGoogleIdTokenReceived,
                        onFailure = onGoogleSignInFailed
                    )
                }

                if (uiState.isEmailVerificationSent) {
                    item {
                        EmailVerificationSection(
                            isLoading = uiState.isLoading,
                            onCheckEmailVerified = onCheckEmailVerified,
                            onResendVerificationEmail = onResendVerificationEmail
                        )
                    }
                } else {
                    item {
                        LoginByEmailSection(
                            uiState = uiState,
                            onEmailChanged = onEmailChanged,
                            onPasswordChanged = onPasswordChanged,
                            onSubmit = onContinueWithEmailClicked
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AuthenticationHeaderSection() {
    Column(
        modifier = Modifier
            .padding(defaultPaddingValues)
            .fillMaxWidth(),
        verticalArrangement = spacedBy(space = 16.dp)
    ) {
        val isKeyboardVisible = WindowInsets.isImeVisible
        AnimatedVisibility(
            visible = !isKeyboardVisible
        ) {
            LogoImage()
        }
        Text(
            text = stringResource(id = R.string.welcome_to_pedalpal),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun LoginByEmailSection(
    uiState: LoginUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onContinueWithEmailClicked: () -> Unit = {},
) {
    val emailRequester = remember { FocusRequester() }
    val passwordRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(
            modifier = Modifier
                .padding(all = 24.dp)
                .fillMaxWidth(),
            verticalArrangement = spacedBy(space = 12.dp)
        ) {
            DefaultOutlinedTextField(
                text = uiState.email,
                onTextChanged = onEmailChanged,
                label = { Text(text = stringResource(id = R.string.email)) },
                placeholder = { Text(text = stringResource(id = R.string.email)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(emailRequester)
                    .onPreviewKeyEvent { event ->
                        if (event.key == Key.Tab && event.type == KeyEventType.KeyDown) {
                            passwordRequester.requestFocus()
                            true
                        } else false
                    },
                enabled = !uiState.isLoading,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email,
                    capitalization = KeyboardCapitalization.None
                ),
                keyboardActions = KeyboardActions(onNext = { passwordRequester.requestFocus() })
            )

            DefaultOutlinedTextField(
                text = uiState.password,
                onTextChanged = onPasswordChanged,
                label = { Text(text = stringResource(id = R.string.password)) },
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

            Button(
                onClick = onContinueWithEmailClicked,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                if (uiState.isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        DefaultProgressIndicator()
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(id = R.string.signing_in))
                    }
                } else {
                    Text(
                        text = stringResource(id = R.string.continue_by_email),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun GoogleSignInButton(
    onIntentRequested: () -> Intent,
    onIdTokenReceived: (String) -> Unit,
    onFailure: (String) -> Unit,
    enabled: Boolean = true
) {
    val launcher = rememberLauncherForActivityResult(
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
                    onFailure("No id token returned from Google account")
                }
            } catch (e: ApiException) {
                onFailure(e.localizedMessage ?: "Google sign-in failed")
            }
        } else {
            onFailure("Google sign-in cancelled or failed")
        }
    }
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = sharedSectionTopShape
    ) {
        Column(
            modifier = Modifier
                .padding(defaultContainerPaddingValues)
                .fillMaxWidth(),
            verticalArrangement = spacedBy(space = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .clickable(
                        enabled = enabled,
                        onClick = { launcher.launch(input = onIntentRequested()) }
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painterResource(id = R.drawable.btn_google_signin),
                    contentDescription = null
                )

            }
            OrSeparator()
        }
    }
}

@Composable
private fun OrSeparator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(color = MaterialTheme.colorScheme.outlineVariant)
        )
        Text(
            text = stringResource(id = R.string.or),
            modifier = Modifier.padding(horizontal = 12.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(color = MaterialTheme.colorScheme.outlineVariant)
        )
    }
}

@Composable
private fun EmailVerificationSection(
    isLoading: Boolean,
    onCheckEmailVerified: () -> Unit,
    onResendVerificationEmail: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.primary
    ) {
        Column (
            modifier = Modifier
                .padding(defaultContainerPaddingValues)
                .fillMaxWidth(),
            verticalArrangement = spacedBy(space = 12.dp)
        ){
            Text(
                text = "Check your email, verify your account,\nthen come back and tap below.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Button(
                onClick = onCheckEmailVerified,
                enabled = !isLoading
            ) {
                Text(text = "I've verified my email")
            }

            ResendInlineText(
                enabled = !isLoading,
                onResend = onResendVerificationEmail
            )
        }
    }
}

@Composable
fun ResendInlineText(
    enabled: Boolean = true,
    onResend: () -> Unit
) {
    val annotated = buildAnnotatedString {
        append("Didn't receive the email, resend it by clicking ")
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append("here")
        }
        append(".")
    }

    Text(
        text = annotated,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onResend() },
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
}

@DarkLightPreviews
@Composable
private fun LoadingHomeScreenPreview(
    @PreviewParameter(provider = LoginUiStateProvider::class) loginUiState: LoginUiState
) {
    BasePreviewContainer {
        AuthenticationScreen(
            uiState = loginUiState,
            onGoogleIntentRequested = { Intent() }
        )
    }
}