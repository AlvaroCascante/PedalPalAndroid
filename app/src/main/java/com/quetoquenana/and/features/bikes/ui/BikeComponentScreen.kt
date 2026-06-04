package com.quetoquenana.and.features.bikes.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.quetoquenana.and.core.ui.components.StickyBottomCta
import com.quetoquenana.and.core.ui.components.previewComponentTypes
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import com.quetoquenana.and.features.bikes.domain.model.ComponentType
import java.util.UUID

private const val NewComponentId = "new"

@Composable
fun BikeComponentRoute(
    modifier: Modifier = Modifier,
    bikeId: UUID,
    componentId: String = "",
    onComponentSaved: (UUID) -> Unit = {},
    viewModel: BikeComponentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                BikeComponentViewModel.BikeComponentEvent.NavigateBikeDetail -> {
                    onComponentSaved(bikeId)
                }
                is BikeComponentViewModel.BikeComponentEvent.ShowError -> {
                    snackBarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    BikeComponentScreen(
        modifier = modifier,
        bikeId = bikeId,
        componentId = componentId,
        uiState = uiState,
        snackBarHostState = snackBarHostState,
        onTypeChanged = viewModel::onTypeChanged,
        onBrandChanged = viewModel::onBrandChanged,
        onModelChanged = viewModel::onModelChanged,
        onNotesChanged = viewModel::onNotesChanged,
        onSaveClicked = viewModel::saveComponent
    )
}

@Composable
fun BikeComponentScreen(
    modifier: Modifier = Modifier,
    bikeId: UUID,
    componentId: String,
    uiState: AddBikeComponentUiState,
    snackBarHostState: SnackbarHostState = SnackbarHostState(),
    onTypeChanged: (String) -> Unit = {},
    onBrandChanged: (String) -> Unit = {},
    onModelChanged: (String) -> Unit = {},
    onNotesChanged: (String) -> Unit = {},
    onSaveClicked: () -> Unit = {}
) {
    val isNewComponent = componentId == NewComponentId

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        bottomBar = {
            if (isNewComponent) {
                StickyBottomCta(
                    text = if (uiState.isSaving) "Saving..." else "Save component",
                    onClick = onSaveClicked,
                    enabled = !uiState.isSaving
                )
            }
        }
    ) { paddingValues ->
        if (isNewComponent) {
            AddBikeComponentForm(
                modifier = Modifier.padding(paddingValues),
                uiState = uiState,
                onTypeChanged = onTypeChanged,
                onBrandChanged = onBrandChanged,
                onModelChanged = onModelChanged,
                onNotesChanged = onNotesChanged
            )
        } else {
            ExistingComponentOptions(
                modifier = Modifier.padding(paddingValues),
                bikeId = bikeId,
                componentId = componentId
            )
        }
    }
}

@Composable
private fun AddBikeComponentForm(
    modifier: Modifier = Modifier,
    uiState: AddBikeComponentUiState,
    onTypeChanged: (String) -> Unit,
    onBrandChanged: (String) -> Unit,
    onModelChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit
) {
    val wordsKeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
    val sentencesKeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ComponentTypeDropdownSelector(
                selectedType = uiState.componentTypes.firstOrNull { it.code == uiState.type },
                componentTypes = uiState.componentTypes,
                onTypeSelected = onTypeChanged,
                enabled = !uiState.isSaving && !uiState.isLoadingComponentTypes,
                isLoading = uiState.isLoadingComponentTypes
            )
        }

        item {
            OutlinedTextField(
                value = uiState.brand,
                onValueChange = onBrandChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Brand") },
                keyboardOptions = wordsKeyboardOptions,
                enabled = !uiState.isSaving,
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = uiState.model,
                onValueChange = onModelChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Model") },
                keyboardOptions = wordsKeyboardOptions,
                enabled = !uiState.isSaving,
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = uiState.notes,
                onValueChange = onNotesChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Notes") },
                minLines = 3,
                keyboardOptions = sentencesKeyboardOptions,
                enabled = !uiState.isSaving
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComponentTypeDropdownSelector(
    selectedType: ComponentType?,
    componentTypes: List<ComponentType>,
    onTypeSelected: (String) -> Unit,
    enabled: Boolean,
    isLoading: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            value = when {
                isLoading -> "Loading types..."
                selectedType != null -> selectedType.codeDescription
                else -> ""
            },
            onValueChange = {},
            label = { Text(text = "Type") },
            readOnly = true,
            enabled = enabled,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            componentTypes.forEach { type ->
                DropdownMenuItem(
                    text = { Text(text = type.codeDescription) },
                    onClick = {
                        onTypeSelected(type.code)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ExistingComponentOptions(
    modifier: Modifier = Modifier,
    bikeId: UUID,
    componentId: String
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Component options", style = MaterialTheme.typography.headlineSmall)
        }

        item {
            ComponentOptionCard(
                title = "Update component",
                description = "Edit this component's details.",
                enabled = false,
                onClick = {}
            )
        }

        item {
            ComponentOptionCard(
                title = "Replace component",
                description = "Close out this part and create its replacement.",
                enabled = false,
                onClick = {}
            )
        }

        item {
            Text(
                text = "Bike: $bikeId - Component: $componentId",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ComponentOptionCard(
    title: String,
    description: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (enabled) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (enabled) 2.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = description)
        }
    }
}


@Preview(showSystemUi = true)
@Composable
private fun BikeComponentScreenAddPreview() {
    PedalPalTheme {
        BikeComponentScreen(
            modifier = Modifier.fillMaxSize(),
            bikeId = UUID.randomUUID(),
            componentId = NewComponentId,
            uiState = AddBikeComponentUiState(
                type = "CHAIN",
                componentTypes = previewComponentTypes,
                brand = "Shimano",
                model = "CN-HG901",
                notes = "Fresh chain for the spring endurance setup."
            )
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun BikeComponentScreenExistingPreview() {
    PedalPalTheme {
        BikeComponentScreen(
            modifier = Modifier.fillMaxSize(),
            bikeId = UUID.randomUUID(),
            componentId = "component-1",
            uiState = AddBikeComponentUiState(componentTypes = previewComponentTypes)
        )
    }
}

