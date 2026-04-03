package com.quetoquenana.and.features.bikes.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.quetoquenana.and.core.ui.theme.PedalPalTheme

@Composable
fun AddBikeRoute(
    modifier: Modifier = Modifier,
    onNavigateBikes: () -> Unit,
    prefillName: String? = null,
    prefillModel: String? = null,
    prefillNotes: String? = null,
    viewModel: AddBikeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(prefillName, prefillModel, prefillNotes) {
        viewModel.applyPrefill(
            name = prefillName,
            model = prefillModel,
            notes = prefillNotes
        )
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                AddBikeViewModel.AddBikeEvent.NavigateBikes -> onNavigateBikes()
                is AddBikeViewModel.AddBikeEvent.ShowError -> snackBarHostState.showSnackbar(event.message)
            }
        }
    }

    AddBikeScreen(
        modifier = modifier,
        uiState = uiState,
        snackBarHostState = snackBarHostState,
        onNameChanged = viewModel::onNameChanged,
        onTypeChanged = viewModel::onTypeChanged,
        onBrandChanged = viewModel::onBrandChanged,
        onModelChanged = viewModel::onModelChanged,
        onYearChanged = viewModel::onYearChanged,
        onSerialNumberChanged = viewModel::onSerialNumberChanged,
        onNotesChanged = viewModel::onNotesChanged,
        onIsPublicChanged = viewModel::onIsPublicChanged,
        onSaveClicked = viewModel::saveBike
    )
}

@Composable
fun AddBikeScreen(
    modifier: Modifier = Modifier,
    uiState: AddBikeUiState,
    snackBarHostState: SnackbarHostState = SnackbarHostState(),
    onNameChanged: (String) -> Unit = {},
    onTypeChanged: (String) -> Unit = {},
    onBrandChanged: (String) -> Unit = {},
    onModelChanged: (String) -> Unit = {},
    onYearChanged: (String) -> Unit = {},
    onSerialNumberChanged: (String) -> Unit = {},
    onNotesChanged: (String) -> Unit = {},
    onIsPublicChanged: (Boolean) -> Unit = {},
    onSaveClicked: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Add bike")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.name,
            onValueChange = onNameChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Name") },
            enabled = !uiState.isSaving
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.type,
            onValueChange = onTypeChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Type") },
            enabled = !uiState.isSaving
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.brand,
            onValueChange = onBrandChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Brand") },
            enabled = !uiState.isSaving
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.model,
            onValueChange = onModelChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Model") },
            enabled = !uiState.isSaving
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.year,
            onValueChange = onYearChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Year") },
            enabled = !uiState.isSaving,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.serialNumber,
            onValueChange = onSerialNumberChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Serial number") },
            enabled = !uiState.isSaving
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.notes,
            onValueChange = onNotesChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Notes") },
            minLines = 3,
            enabled = !uiState.isSaving
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Public profile bike")
            Switch(
                checked = uiState.isPublic,
                onCheckedChange = onIsPublicChanged,
                enabled = !uiState.isSaving
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSaveClicked,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSaving
        ) {
            Text(text = if (uiState.isSaving) "Saving..." else "Save bike")
        }

        SnackbarHost(hostState = snackBarHostState)
    }
}

@Preview(showSystemUi = true)
@Composable
private fun AddBikeScreenPreview() {
    PedalPalTheme {
        AddBikeScreen(
            uiState = AddBikeUiState(
                name = "Trek Domane",
                type = "Road",
                brand = "Trek",
                model = "AL 2",
                year = "2024",
                serialNumber = "SN-001",
                notes = "Weekend bike",
                isPublic = true
            )
        )
    }
}



