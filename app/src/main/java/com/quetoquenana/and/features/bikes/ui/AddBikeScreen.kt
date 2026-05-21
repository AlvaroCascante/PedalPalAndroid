package com.quetoquenana.and.features.bikes.ui

import android.content.Intent
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.core.net.toUri
import com.quetoquenana.and.core.ui.components.StickyBottomCta
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import com.quetoquenana.and.features.bikes.domain.model.BikeType
import com.quetoquenana.and.features.bikes.domain.model.StravaBike
import java.util.Calendar

private const val MIN_BIKE_YEAR = 1900

@Composable
fun AddBikeRoute(
    modifier: Modifier = Modifier,
    onNavigateBikes: () -> Unit,
    prefillName: String? = null,
    prefillModel: String? = null,
    prefillNotes: String? = null,
    prefillOdometerKm: String? = null,
    prefillExternalGearId: String? = null,
    viewModel: AddBikeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(prefillName, prefillModel, prefillNotes, prefillOdometerKm, prefillExternalGearId) {
        viewModel.applyPrefill(
            name = prefillName,
            model = prefillModel,
            notes = prefillNotes,
            odometerKm = prefillOdometerKm,
            externalGearId = prefillExternalGearId
        )
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                AddBikeViewModel.AddBikeEvent.NavigateBikes -> onNavigateBikes()
                is AddBikeViewModel.AddBikeEvent.OpenBrowser -> {
                    context.startActivity(Intent(Intent.ACTION_VIEW, event.url.toUri()))
                }
                is AddBikeViewModel.AddBikeEvent.ShowError -> snackBarHostState.showSnackbar(event.message)
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onAppResumedAfterStravaAuth()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
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
        onOdometerChanged = viewModel::onOdometerChanged,
        onIsPublicChanged = viewModel::onIsPublicChanged,
        onImportFromStravaClicked = viewModel::connectToStrava,
        onStravaBikeSelected = viewModel::onStravaBikeSelected,
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
    onOdometerChanged: (String) -> Unit = {},
    onIsPublicChanged: (Boolean) -> Unit = {},
    onImportFromStravaClicked: () -> Unit = {},
    onStravaBikeSelected: (String) -> Unit = {},
    onSaveClicked: () -> Unit = {}
) {
    val wordsKeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
    val sentencesKeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        bottomBar = {
            StickyBottomCta(
                text = if (uiState.isSaving) "Saving..." else "Save bike",
                onClick = onSaveClicked,
                enabled = !uiState.isSaving
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ImportFromStravaBikeCard(
                onClick = onImportFromStravaClicked,
                enabled = !uiState.isSaving && !uiState.stravaImport.isConnecting && !uiState.stravaImport.isLoadingBikes
            )

            when {
                uiState.stravaImport.isConnecting -> {
                    Text(text = "Opening Strava authorization...")
                }

                uiState.stravaImport.isLoadingBikes -> {
                    Text(text = "Loading Strava bikes...")
                }

                uiState.stravaImport.isWaitingForAuthorization -> {
                    Text(text = "Finish authorization in the browser, then return to the app.")
                }
            }

            uiState.importedStravaBikeName?.let { importedBikeName ->
                Text(text = "Imported from Strava: $importedBikeName")
            }

            if (uiState.stravaImport.bikes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Choose a Strava bike")
                Spacer(modifier = Modifier.height(8.dp))
                StravaBikeSelectionColumn(
                    bikes = uiState.stravaImport.bikes,
                    onBikeClicked = onStravaBikeSelected
                )
            }

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
                value = uiState.odometerKm,
                onValueChange = onOdometerChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Odometer (km)") },
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
        }
    }
}

@Composable
private fun StravaBikeSelectionColumn(
    bikes: List<StravaBike>,
    onBikeClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        bikes.forEach { bike ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onBikeClicked(bike.id) },
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = bike.name)
                    bike.nickname?.let { Text(text = "Nickname: $it") }
                    Text(text = "Primary: ${if (bike.primary) "Yes" else "No"}")
                    Text(text = "Retired: ${if (bike.retired) "Yes" else "No"}")
                    bike.distance?.let { Text(text = "Distance: ${it.toInt()} km") }
                }
            }
        }
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
    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }
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



