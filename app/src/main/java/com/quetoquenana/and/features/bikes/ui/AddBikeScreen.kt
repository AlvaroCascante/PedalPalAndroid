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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import com.quetoquenana.and.features.bikes.domain.model.BikeType
import java.time.Year

private const val MIN_BIKE_YEAR = 1900

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
    onTypeChanged: (BikeType) -> Unit = {},
    onBrandChanged: (String) -> Unit = {},
    onModelChanged: (String) -> Unit = {},
    onYearChanged: (String) -> Unit = {},
    onSerialNumberChanged: (String) -> Unit = {},
    onNotesChanged: (String) -> Unit = {},
    onIsPublicChanged: (Boolean) -> Unit = {},
    onSaveClicked: () -> Unit = {}
) {
    val wordsKeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
    val sentencesKeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)

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
            keyboardOptions = wordsKeyboardOptions,
            enabled = !uiState.isSaving
        )

        Spacer(modifier = Modifier.height(8.dp))

        BikeTypeDropdownSelector(
            selectedType = uiState.type,
            onTypeSelected = onTypeChanged,
            enabled = !uiState.isSaving
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.brand,
            onValueChange = onBrandChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Brand") },
            keyboardOptions = wordsKeyboardOptions,
            enabled = !uiState.isSaving
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.model,
            onValueChange = onModelChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Model") },
            keyboardOptions = wordsKeyboardOptions,
            enabled = !uiState.isSaving
        )

        Spacer(modifier = Modifier.height(8.dp))

        BikeYearDropdownSelector(
            selectedYear = uiState.year,
            onYearSelected = onYearChanged,
            enabled = !uiState.isSaving
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
            keyboardOptions = sentencesKeyboardOptions,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BikeYearDropdownSelector(
    selectedYear: String,
    onYearSelected: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val currentYear = remember { Year.now().value }
    val yearOptions = remember(currentYear) {
        ((currentYear + 1) downTo MIN_BIKE_YEAR).map(Int::toString)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedYear,
            onValueChange = {},
            modifier = Modifier
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = enabled
                )
                .fillMaxWidth(),
            readOnly = true,
            label = { Text(text = "Year") },
            placeholder = { Text(text = "Select year") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            enabled = enabled
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            yearOptions.forEach { year ->
                DropdownMenuItem(
                    text = { Text(text = year) },
                    onClick = {
                        onYearSelected(year)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BikeTypeDropdownSelector(
    selectedType: BikeType?,
    onTypeSelected: (BikeType) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedType?.toBikeTypeDisplayName().orEmpty(),
            onValueChange = {},
            modifier = Modifier
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = enabled
                )
                .fillMaxWidth(),
            readOnly = true,
            label = { Text(text = "Type") },
            placeholder = { Text(text = "Select bike type") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            enabled = enabled
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            BikeType.entries.forEach { bikeType ->
                DropdownMenuItem(
                    text = { Text(text = bikeType.toBikeTypeDisplayName()) },
                    onClick = {
                        onTypeSelected(bikeType)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun AddBikeScreenPreview() {
    PedalPalTheme {
        AddBikeScreen(
            uiState = AddBikeUiState(
                name = "Trek Domane",
                type = BikeType.ROAD,
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



