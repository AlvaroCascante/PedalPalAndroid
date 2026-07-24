package com.quetoquenana.and.features.bikes.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.quetoquenana.and.R
import com.quetoquenana.and.core.ui.components.AddBikeUiStateProvider
import com.quetoquenana.and.core.ui.components.BasePreviewContainer
import com.quetoquenana.and.core.ui.components.DarkLightPreviews
import com.quetoquenana.and.core.ui.components.DefaultOutlinedTextField
import com.quetoquenana.and.core.ui.components.DefaultStickyButtonHeight
import com.quetoquenana.and.core.ui.components.defaultFormPaddingValues
import com.quetoquenana.and.core.ui.components.defaultPaddingValues
import com.quetoquenana.and.core.ui.components.previewAddBikeUiStateError
import com.quetoquenana.and.core.ui.components.sharedSectionTopShape
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import com.quetoquenana.and.core.utils.MIN_BIKE_YEAR
import com.quetoquenana.and.features.bikes.domain.model.BikeType
import java.util.Calendar

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun AddBikeRoute(
    contentPadding: PaddingValues,
    onNavigateBikes: () -> Unit,
    args: AddBikeRouteArgs = AddBikeRouteArgs(),
    onImportFromStravaClicked: (() -> Unit)? = null,
    viewModel: AddBikeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = args) {
        viewModel.applyPrefill(
            name = args.prefill.name,
            brand = args.prefill.brand,
            model = args.prefill.model,
            notes = args.prefill.notes,
            odometerKm = args.prefill.odometerKm,
            externalGearId = args.prefill.externalGearId
        )
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.events.collect { event ->
            when (event) {
                AddBikeViewModel.AddBikeEvent.NavigateBikes -> onNavigateBikes()
                is AddBikeViewModel.AddBikeEvent.OpenBrowser -> {
                    context.startActivity(Intent(Intent.ACTION_VIEW, event.url.toUri()))
                }
                is AddBikeViewModel.AddBikeEvent.ShowError -> snackBarHostState.showSnackbar(event.message)
                is AddBikeViewModel.AddBikeEvent.ShowErrorRes -> snackBarHostState.showSnackbar(
                    message = context.getString(event.resId)
                )
            }
        }
    }

    AddBikeScreen(
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
        showStravaCard = args.entrySource == AddBikeEntrySource.StravaImport,
        onImportFromStravaClicked = onImportFromStravaClicked ?: viewModel::connectToStrava,
        onSaveClicked = viewModel::saveBike,
        contentPadding = contentPadding
    )
}

@Composable
fun AddBikeScreen(
    contentPadding: PaddingValues = PaddingValues(all = 0.dp),
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
    showStravaCard: Boolean = false,
    onImportFromStravaClicked: () -> Unit = {},
    onSaveClicked: () -> Unit = {}
) {
    val wordsKeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
    val sentencesKeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)

    val baseModifier = Modifier
        .fillMaxWidth()
        .background(color = MaterialTheme.colorScheme.primary)
    val layoutDirection = LocalLayoutDirection.current

    Box {
        LazyColumn(
            modifier = baseModifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                start = contentPadding.calculateStartPadding(layoutDirection),
                top = contentPadding.calculateTopPadding(),
                end = contentPadding.calculateEndPadding(layoutDirection),
                bottom = contentPadding.calculateBottomPadding() + DefaultStickyButtonHeight
            )
        ) {
            item {
                AddBikeHeaderSection(
                    modifier = baseModifier
                )
            }
            item {
                AddBikeFormSection(
                    uiState = uiState,
                    showStravaCard = showStravaCard,
                    onImportFromStravaClicked = onImportFromStravaClicked,
                    wordsKeyboardOptions = wordsKeyboardOptions,
                    sentencesKeyboardOptions = sentencesKeyboardOptions,
                    onNameChanged = onNameChanged,
                    onTypeChanged = onTypeChanged,
                    onBrandChanged = onBrandChanged,
                    onModelChanged = onModelChanged,
                    onYearChanged = onYearChanged,
                    onSerialNumberChanged = onSerialNumberChanged,
                    onNotesChanged = onNotesChanged,
                    onOdometerChanged = onOdometerChanged,
                    onIsPublicChanged = onIsPublicChanged
                )
            }
        }

        val buttonText = if (uiState.isSaving) stringResource(R.string.saving) else stringResource(R.string.save_bike)
        Button(
            enabled = !uiState.isSaving,
            onClick = onSaveClicked,
            modifier = Modifier
                .align (Alignment.BottomEnd)
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 18.dp),
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.secondary)
        ) {
            Text(
                text = buttonText,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun AddBikeHeaderSection(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(defaultPaddingValues),
        verticalArrangement = spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.add_bike),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = stringResource(id = R.string.add_the_bike_details_yourself),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun AddBikeFormSection(
    uiState: AddBikeUiState,
    showStravaCard: Boolean,
    onImportFromStravaClicked: () -> Unit,
    wordsKeyboardOptions: KeyboardOptions,
    sentencesKeyboardOptions: KeyboardOptions,
    onNameChanged: (String) -> Unit,
    onTypeChanged: (BikeType) -> Unit,
    onBrandChanged: (String) -> Unit,
    onModelChanged: (String) -> Unit,
    onYearChanged: (String) -> Unit,
    onSerialNumberChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onOdometerChanged: (String) -> Unit,
    onIsPublicChanged: (Boolean) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = sharedSectionTopShape
    ) {
        Column(
            modifier = Modifier
                .padding(defaultFormPaddingValues)
                .fillMaxWidth(),
            verticalArrangement = spacedBy(12.dp)
        ) {
            if (showStravaCard) {
                ImportFromStravaBikeCard(
                    onClick = onImportFromStravaClicked,
                    enabled = !uiState.isSaving && !uiState.stravaImport.isConnecting && !uiState.stravaImport.isLoadingBikes
                )
            }

            when {
                showStravaCard && uiState.stravaImport.isConnecting -> {
                    Text(text = stringResource(R.string.opening_strava_authorization))
                }
                showStravaCard && uiState.stravaImport.isLoadingBikes -> {
                    Text(text = stringResource(R.string.loading_strava_bikes))
                }
                showStravaCard && uiState.stravaImport.isWaitingForAuthorization -> {
                    Text(text = stringResource(R.string.finish_strava_authorization))
                }
            }

            if (showStravaCard) {
                uiState.importedStravaBikeName?.let { importedBikeName ->
                    Text(text = stringResource(id = R.string.imported_from_strava, importedBikeName))
                }
            }

            DefaultOutlinedTextField(
                text = uiState.name,
                onTextChanged = onNameChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(R.string.bike_name)) },
                keyboardOptions = wordsKeyboardOptions,
                enabled = !uiState.isSaving,
                isError = uiState.nameErrorRes != null,
                supportingText = {
                    uiState.nameErrorRes?.let { errId ->
                        Text(text = stringResource(errId))
                    }
                },
            )

            BikeTypeDropdownSelector(
                selectedType = uiState.type,
                onTypeSelected = onTypeChanged,
                enabled = !uiState.isSaving,
                isError = uiState.typeErrorRes != null
            )

            DefaultOutlinedTextField(
                text = uiState.brand,
                onTextChanged = onBrandChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(id = R.string.bike_brand_label)) },
                keyboardOptions = wordsKeyboardOptions,
                enabled = !uiState.isSaving,
            )

            DefaultOutlinedTextField(
                text = uiState.model,
                onTextChanged = onModelChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(id = R.string.bike_model_label)) },
                keyboardOptions = wordsKeyboardOptions,
                enabled = !uiState.isSaving
            )

            BikeYearDropdownSelector(
                selectedYear = uiState.year,
                onYearSelected = onYearChanged,
                enabled = !uiState.isSaving
            )

            DefaultOutlinedTextField(
                text = uiState.serialNumber,
                onTextChanged = onSerialNumberChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(id = R.string.bike_serial_number)) },
                enabled = !uiState.isSaving
            )

            DefaultOutlinedTextField(
                text = uiState.odometerKm,
                onTextChanged = onOdometerChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(id = R.string.bike_odometer)) },
                enabled = !uiState.isSaving
            )

            DefaultOutlinedTextField(
                text = uiState.notes,
                onTextChanged = onNotesChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(id = R.string.bike_notes)) },
                minLines = 3,
                keyboardOptions = sentencesKeyboardOptions,
                enabled = !uiState.isSaving
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(id = R.string.bike_public_profile))
                Switch(
                    checked = uiState.isPublic,
                    onCheckedChange = onIsPublicChanged,
                    enabled = !uiState.isSaving
                )
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
    var expanded by rememberSaveable { mutableStateOf(value = false) }
    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }
    val yearOptions = remember(key1 = currentYear) {
        ((currentYear + 1) downTo MIN_BIKE_YEAR).map(transform = Int::toString)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        DefaultOutlinedTextField(
            text = selectedYear,
            onTextChanged = {},
            modifier = Modifier
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = enabled
                )
                .fillMaxWidth(),
            readOnly = true,
            label = { Text(text = stringResource(id = R.string.year)) },
            placeholder = { Text(text = stringResource(id = R.string.select_year)) },
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
    modifier: Modifier = Modifier,
    selectedType: BikeType?,
    onTypeSelected: (BikeType) -> Unit,
    enabled: Boolean,
    isError: Boolean = false
) {
    var expanded by rememberSaveable { mutableStateOf(value = false) }

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
            label = { Text(text = stringResource(id = R.string.type)) },
            placeholder = { Text(text = stringResource(id = R.string.select_bike_type)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            isError = isError,
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

@DarkLightPreviews
@Composable
private fun AddBikeScreenPreview(
    @PreviewParameter(provider = AddBikeUiStateProvider::class) addBikeUiState: AddBikeUiState
) {
    BasePreviewContainer {
        AddBikeScreen(
            uiState = addBikeUiState
        )
    }
}

@DarkLightPreviews
@Composable
private fun AddBikeFromStravaScreenPreview(
    @PreviewParameter(provider = AddBikeUiStateProvider::class) addBikeUiState: AddBikeUiState
) {
    BasePreviewContainer {
        AddBikeScreen(
            uiState = addBikeUiState,
            showStravaCard = true,
        )
    }
}

@DarkLightPreviews
@Composable
private fun AddBikeScreenPreview_Error() {
    PedalPalTheme {
        AddBikeScreen(
            uiState = previewAddBikeUiStateError
        )
    }
}
