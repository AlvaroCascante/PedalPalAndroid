package com.quetoquenana.and.pedalpal.feature.login.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.quetoquenana.and.pedalpal.core.ui.components.LogoImage

/**
 * Simple profile completion screen that collects nickname and person fields.
 */
@Composable
fun CompleteProfileScreen(
    onComplete: () -> Unit,
    viewModel: CompleteProfileViewModel = hiltViewModel()
) {
    val nickname by viewModel.nickname.collectAsState()
    val idNumber by viewModel.idNumber.collectAsState()
    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    val focusManager = LocalFocusManager.current
    val nicknameRequester = remember { FocusRequester() }
    val idRequester = remember { FocusRequester() }
    val firstRequester = remember { FocusRequester() }
    val lastRequester = remember { FocusRequester() }

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CompleteProfileViewModel.CompleteProfileEvent.NavigateHome -> onComplete()
                is CompleteProfileViewModel.CompleteProfileEvent.ShowError -> {
                    snackBarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LogoImage()

        Spacer(Modifier.height(height = 8.dp))
        OutlinedTextField(
            value = nickname,
            onValueChange = viewModel::onNicknameChanged,
            label = { Text(text = "Nickname") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester = nicknameRequester)
                .onPreviewKeyEvent { event ->
                    if (event.key == Key.Tab && event.type == KeyEventType.KeyDown) {
                        idRequester.requestFocus()
                        true
                    } else false
                },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { idRequester.requestFocus() }),
            enabled = !isSaving
        )

        Spacer(Modifier.height(height = 8.dp))
        OutlinedTextField(
            value = idNumber,
            onValueChange = viewModel::onIdNumberChanged,
            label = { Text("ID Number") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester = idRequester)
                .onPreviewKeyEvent { event ->
                    if (event.key == Key.Tab && event.type == KeyEventType.KeyDown) {
                        firstRequester.requestFocus()
                        true
                    } else false
                },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { firstRequester.requestFocus() }),
            enabled = !isSaving
        )

        Spacer(Modifier.height(height = 8.dp))
        OutlinedTextField(
            value = firstName,
            onValueChange = viewModel::onFirstNameChanged,
            label = { Text("First name") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester = firstRequester)
                .onPreviewKeyEvent { event ->
                    if (event.key == Key.Tab && event.type == KeyEventType.KeyDown) {
                        lastRequester.requestFocus()
                        true
                    } else false
                },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { lastRequester.requestFocus() }),
            enabled = !isSaving
        )

        Spacer(Modifier.height(height = 8.dp))
        OutlinedTextField(
            value = lastName,
            onValueChange = viewModel::onLastNameChanged,
            label = { Text(text = "Last name") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester = lastRequester)
                .onPreviewKeyEvent { event ->
                    if (event.key == Key.Tab && event.type == KeyEventType.KeyDown) {
                        focusManager.clearFocus()
                        true
                    } else false
                },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); viewModel.saveProfile() }),
            enabled = !isSaving
        )

        Spacer(Modifier.height(height = 12.dp))
        Button(onClick = { viewModel.saveProfile() }, enabled = !isSaving) {
            Text(text = "Complete profile")
        }

        SnackbarHost(
            hostState = snackBarHostState
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun CompleteProfileScreenPreview() {
    CompleteProfileScreen(onComplete = {})
}