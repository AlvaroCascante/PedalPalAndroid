package com.quetoquenana.and.features.authentication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.quetoquenana.and.R
import com.quetoquenana.and.core.ui.components.BasePreviewContainer
import com.quetoquenana.and.core.ui.components.CompleteProfileUiStateProvider
import com.quetoquenana.and.core.ui.components.DarkLightPreviews
import com.quetoquenana.and.core.ui.components.DefaultOutlinedTextField
import com.quetoquenana.and.core.ui.components.LogoImage
import com.quetoquenana.and.core.ui.components.StickyBottomCta
import com.quetoquenana.and.core.ui.components.defaultFormPaddingValues
import com.quetoquenana.and.core.ui.components.defaultPaddingValues
import com.quetoquenana.and.core.ui.components.sharedSectionTopShape

@Composable
fun CompleteProfileRoute(
    name: String,
    onComplete: () -> Unit,
    viewModel: CompleteProfileViewModel = hiltViewModel()
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CompleteProfileViewModel.CompleteProfileEvent.NavigateHome -> onComplete()
                is CompleteProfileViewModel.CompleteProfileEvent.ShowError -> {
                    snackBarHostState.showSnackbar(message = event.message)
                }
            }
        }
    }

    CompleteProfileScreen(
        name = name,
        uiState = uiState,
        snackBarHostState = snackBarHostState,
        onNicknameChanged = { viewModel.onNicknameChanged(value = it) },
        onIdNumberChanged = { viewModel.onIdNumberChanged(value = it) },
        onFirstNameChanged = { viewModel.onFirstNameChanged(value = it) },
        onLastNameChanged = { viewModel.onLastNameChanged(value = it) },
        onSaveClicked = { viewModel.saveProfile() }
    )
}

@Composable
fun CompleteProfileScreen(
    name: String,
    uiState: CompleteProfileUiState,
    onNicknameChanged : (String) -> Unit = {},
    onIdNumberChanged : (String) -> Unit = {},
    onFirstNameChanged : (String) -> Unit = {},
    onLastNameChanged : (String) -> Unit = {},
    onSaveClicked : () -> Unit = {},
    snackBarHostState: SnackbarHostState = SnackbarHostState(),
) {
    // Default initial modifier
    val modifier = Modifier
        .fillMaxSize()
        .background(color = MaterialTheme.colorScheme.primary)

    LazyColumn(
        modifier = modifier,
        // INFO : Use systemBars.asPaddingValues() to ensure that the content is not
        // obscured by system bars on devices with gesture navigation or cutouts.
        contentPadding = WindowInsets.systemBars.asPaddingValues()
    ) {
        item {
            LogoImage()
        }
        item {
            CompleteProfileHeaderSection(
                modifier = modifier,
                name = name
            )
        }
        item {
            CompleteProfileTopSection(
                uiState = uiState,
                onNicknameChanged = onNicknameChanged,
                onIdNumberChanged = onIdNumberChanged,
                onFirstNameChanged = onFirstNameChanged,
                onLastNameChanged = onLastNameChanged
            )
        }
        item {
            StickyBottomCta(
                text =  if (uiState.isSaving) stringResource(R.string.saving)
                else stringResource(R.string.complete_profile),
                onClick = onSaveClicked,
                enabled = !uiState.isSaving
            )
        }
        item {
            SnackbarHost(
                hostState = snackBarHostState
            )
        }
    }
}

@Composable
private fun CompleteProfileHeaderSection(
    modifier: Modifier = Modifier,
    name: String
) {
    Column(
        modifier = modifier.padding(defaultPaddingValues),
        verticalArrangement = spacedBy(space = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.hello, name),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Text(
            text = stringResource(id = R.string.complete_profile_label),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun CompleteProfileTopSection(
    uiState: CompleteProfileUiState,
    onNicknameChanged : (String) -> Unit = {},
    onIdNumberChanged : (String) -> Unit = {},
    onFirstNameChanged : (String) -> Unit = {},
    onLastNameChanged : (String) -> Unit = {},
) {

    val focusManager = LocalFocusManager.current
    val nicknameRequester = remember { FocusRequester() }
    val idRequester = remember { FocusRequester() }
    val firstRequester = remember { FocusRequester() }
    val lastRequester = remember { FocusRequester() }

    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = sharedSectionTopShape
    ) {
        Column (
            modifier = Modifier
                .padding(defaultFormPaddingValues)
                .fillMaxWidth(),
            verticalArrangement = spacedBy(space = 12.dp)
        ){
            DefaultOutlinedTextField(
                text = uiState.nickname,
                onTextChanged = onNicknameChanged,
                label = { Text(text = stringResource(id = R.string.nickname)) },
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
                enabled = !uiState.isSaving
            )

            DefaultOutlinedTextField(
                text = uiState.idNumber,
                onTextChanged = onIdNumberChanged,
                label = { Text(stringResource(id = R.string.id_number)) },
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
                enabled = !uiState.isSaving
            )

            DefaultOutlinedTextField(
                text = uiState.firstName,
                onTextChanged = onFirstNameChanged,
                label = { Text(stringResource(id = R.string.first_name)) },
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
                enabled = !uiState.isSaving
            )

            DefaultOutlinedTextField(
                text = uiState.lastName,
                onTextChanged = onLastNameChanged,
                label = { Text(text = stringResource(id = R.string.last_name)) },
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
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); }),
                enabled = !uiState.isSaving
            )
        }
    }
}


@DarkLightPreviews
@Composable
private fun LoadingHomeScreenPreview(
    @PreviewParameter(provider = CompleteProfileUiStateProvider::class) completeProfileUiState: CompleteProfileUiState
) {
    BasePreviewContainer {
        CompleteProfileScreen(
            name = "John Doe",
            uiState = completeProfileUiState
        )
    }
}