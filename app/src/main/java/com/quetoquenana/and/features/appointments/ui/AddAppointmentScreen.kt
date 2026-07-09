package com.quetoquenana.and.features.appointments.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.quetoquenana.and.core.ui.components.StickyBottomCta
import com.quetoquenana.and.core.ui.components.previewBikes
import com.quetoquenana.and.core.ui.components.previewServiceCatalog
import com.quetoquenana.and.core.ui.components.previewStore
import com.quetoquenana.and.core.ui.theme.PedalPalTheme
import com.quetoquenana.and.features.bikes.domain.model.Bike
import com.quetoquenana.and.features.services.domain.model.ServicePackage
import com.quetoquenana.and.features.services.domain.model.ServiceProduct
import com.quetoquenana.and.features.stores.domain.model.Store
import com.quetoquenana.and.features.stores.domain.model.StoreLocation
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Currency
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

@Composable
fun AddAppointmentScreen(
    modifier: Modifier = Modifier,
    onDone: () -> Unit = {},
    viewModel: AddAppointmentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                AddAppointmentEvent.AppointmentCreated -> onDone()
                is AddAppointmentEvent.ServiceSelectionRejected -> Unit
                is AddAppointmentEvent.ShowError -> {
                    Toast.makeText(
                        context, 
                        event.message, 
                        Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    AddAppointmentContent(
        modifier = modifier,
        uiState = uiState,
        onStoreSelected = viewModel::onStoreSelected,
        onLocationSelected = viewModel::onLocationSelected,
        onBikeSelected = viewModel::onBikeSelected,
        onScheduledDateSelected = viewModel::onScheduledDateSelected,
        onPackageToggled = viewModel::onPackageToggled,
        onProductToggled = viewModel::onProductToggled,
        onNotesChanged = viewModel::onNotesChanged,
        onRetryCatalog = { viewModel.loadServiceCatalog(refresh = true) },
        onCreateAppointment = viewModel::createAppointment
    )
}

@Composable
private fun AddAppointmentContent(
    modifier: Modifier = Modifier,
    uiState: AddAppointmentUiState,
    onStoreSelected: (UUID) -> Unit,
    onLocationSelected: (UUID) -> Unit,
    onBikeSelected: (UUID) -> Unit,
    onScheduledDateSelected: (Long) -> Unit,
    onPackageToggled: (UUID) -> Unit,
    onProductToggled: (UUID) -> Unit,
    onNotesChanged: (String) -> Unit,
    onRetryCatalog: () -> Unit,
    onCreateAppointment: () -> Unit
) {
    val isCreateEnabled = uiState.selectedBike != null &&
        uiState.selectedLocation != null &&
        uiState.scheduledAt != null &&
        uiState.requestedServiceCount > 0 &&
        !uiState.isSubmitting

    Scaffold(
        modifier = modifier,
        bottomBar = {
            StickyBottomCta(
                text = if (uiState.isSubmitting) "Creating appointment" else "Create appointment",
                onClick = onCreateAppointment,
                enabled = isCreateEnabled
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {

            when {
                uiState.isLoadingBikes -> LoadingStoresCard(text = "Loading bikes")
                uiState.bikes.isEmpty() -> EmptyStoresCard(
                    title = "No bikes available",
                    errorMessage = uiState.errorMessage
                )
                else -> BikeDropdown(
                    label = "Bike",
                    selectedBike = uiState.selectedBike,
                    bikes = uiState.bikes,
                    onBikeSelected = onBikeSelected
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            when {
                uiState.isLoadingStores -> LoadingStoresCard()
                uiState.stores.isEmpty() -> EmptyStoresCard(errorMessage = uiState.errorMessage)
                else -> StoreSelectionFields(
                    uiState = uiState,
                    onStoreSelected = onStoreSelected,
                    onLocationSelected = onLocationSelected,
                    onScheduledDateSelected = onScheduledDateSelected
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            when {
                uiState.selectedLocation == null -> EmptyStoresCard(
                    title = "Select a location",
                    errorMessage = "Services load after a store location is selected."
                )
                uiState.isLoadingServices -> LoadingStoresCard(text = "Loading services")
                uiState.serviceCatalog.packages.isEmpty() && uiState.serviceCatalog.products.isEmpty() -> EmptyStoresCard(
                    title = "No services available",
                    errorMessage = uiState.catalogFetchErrorMessage ?: uiState.errorMessage,
                    actionText = if (uiState.selectedLocation != null) "Retry" else null,
                    onActionClick = onRetryCatalog
                )
                else -> ServiceSelectionFields(
                    uiState = uiState,
                    onPackageToggled = onPackageToggled,
                    onProductToggled = onProductToggled,
                    onRetryCatalog = onRetryCatalog
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AppointmentReviewCard(
                uiState = uiState,
                onNotesChanged = onNotesChanged
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BikeDropdown(
    label: String,
    selectedBike: Bike?,
    bikes: List<Bike>,
    onBikeSelected: (UUID) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    DropdownField(
        label = label,
        value = selectedBike?.name ?: "Select a bike",
        enabled = bikes.isNotEmpty(),
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        bikes.forEach { bike ->
            DropdownMenuItem(
                text = {
                    Column {
                        Text(text = bike.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            text = bike.type,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                onClick = {
                    expanded = false
                    onBikeSelected(bike.id)
                }
            )
        }
    }
}

@Composable
private fun LoadingStoresCard(text: String = "Loading stores") {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 2.dp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun EmptyStoresCard(
    title: String = "No stores available",
    errorMessage: String?,
    actionText: String? = null,
    onActionClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            errorMessage?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
            }
            actionText?.let {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = onActionClick) {
                    Text(text = it)
                }
            }
        }
    }
}

@Composable
private fun ServiceSelectionFields(
    uiState: AddAppointmentUiState,
    onPackageToggled: (UUID) -> Unit,
    onProductToggled: (UUID) -> Unit,
    onRetryCatalog: () -> Unit
) {
    val currencyCode = uiState.selectedLocation?.currency

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CatalogFreshnessHint(
            lastUpdated = uiState.catalogLastUpdated,
            isUsingCachedCatalog = uiState.isUsingCachedCatalog,
            errorMessage = uiState.catalogFetchErrorMessage,
            onRetryCatalog = onRetryCatalog
        )

        uiState.submitErrorMessage?.let { message ->
            Text(text = message, style = MaterialTheme.typography.bodySmall)
        }

        ServiceRowSection(
            title = "Packages",
            emptyText = "No active packages available.",
            items = uiState.serviceCatalog.packages,
            key = ServicePackage::id
        ) { servicePackage ->
            ServiceCard(
                title = servicePackage.name,
                description = servicePackage.description,
                price = servicePackage.price,
                currencyCode = currencyCode,
                selected = servicePackage.id in uiState.selectedPackageIds,
                onClick = { onPackageToggled(servicePackage.id) }
            )
        }

        ServiceRowSection(
            title = "Extra products",
            emptyText = "No active products available.",
            items = uiState.serviceCatalog.products,
            key = ServiceProduct::id
        ) { product ->
            ServiceCard(
                title = product.name,
                description = product.description,
                price = product.price,
                currencyCode = currencyCode,
                selected = product.id in uiState.selectedProductIds,
                onClick = { onProductToggled(product.id) }
            )
        }
    }
}

@Composable
private fun CatalogFreshnessHint(
    lastUpdated: Long?,
    isUsingCachedCatalog: Boolean,
    errorMessage: String?,
    onRetryCatalog: () -> Unit
) {
    if (lastUpdated == null && errorMessage == null) return

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        lastUpdated?.let {
            val prefix = if (isUsingCachedCatalog) "Cached data" else "Last updated"
            Text(
                text = "$prefix ${DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(it))}",
                style = MaterialTheme.typography.bodySmall
            )
        }
        if (isUsingCachedCatalog && errorMessage != null) {
            Text(text = errorMessage, style = MaterialTheme.typography.bodySmall)
            OutlinedButton(onClick = onRetryCatalog) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun <T> ServiceRowSection(
    title: String,
    emptyText: String,
    items: List<T>,
    key: (T) -> UUID,
    itemContent: @Composable (T) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        if (items.isEmpty()) {
            Text(text = emptyText, style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = items, key = key) { item ->
                    itemContent(item)
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(
    title: String,
    description: String?,
    price: String?,
    currencyCode: String?,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(width = 220.dp, height = 150.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 6.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (selected) {
                    Text(text = "Selected", style = MaterialTheme.typography.labelSmall)
                }
            }
            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = formatServicePrice(price = price, currencyCode = currencyCode),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun formatServicePrice(price: String?, currencyCode: String?): String {
    val normalizedPrice = price?.trim().takeUnless { it.isNullOrEmpty() } ?: return "Price unavailable"
    val normalizedCurrencyCode = currencyCode?.trim()?.uppercase().orEmpty()

    if (normalizedCurrencyCode.isEmpty()) {
        return normalizedPrice
    }

    val symbol = currencySymbolFor(normalizedCurrencyCode)
    return if (symbol.all { it.isLetter() }) {
        "$symbol $normalizedPrice"
    } else {
        "$symbol$normalizedPrice"
    }
}

private fun currencySymbolFor(currencyCode: String): String {
    return currencySymbolsByCode[currencyCode]
        ?: runCatching { Currency.getInstance(currencyCode).symbol }
            .getOrNull()
            ?.takeUnless { it.equals(currencyCode, ignoreCase = true) }
        ?: currencyCode
}

private val currencySymbolsByCode = mapOf(
    "CRC" to "₡",
    "EUR" to "€",
    "GBP" to "£",
    "JPY" to "¥",
    "USD" to "$"
)

@Composable
private fun AppointmentReviewCard(
    uiState: AddAppointmentUiState,
    onNotesChanged: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "Review", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${uiState.requestedServiceCount} selected service${if (uiState.requestedServiceCount == 1) "" else "s"}",
                style = MaterialTheme.typography.bodyMedium
            )
            formatScheduledDateText(uiState.scheduledAt)?.let { scheduledDate ->
                Text(
                    text = "Scheduled date: $scheduledDate",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            uiState.selectedPackages.forEach { servicePackage ->
                SelectedServiceLine(name = servicePackage.name, type = "Package")
            }
            uiState.selectedProducts.forEach { product ->
                SelectedServiceLine(name = product.name, type = "Product")
            }
            OutlinedTextField(
                value = uiState.notes,
                onValueChange = onNotesChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Notes") },
                minLines = 3
            )
        }
    }
}

@Composable
private fun SelectedServiceLine(name: String, type: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = "$type · $name",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun StoreSelectionFields(
    uiState: AddAppointmentUiState,
    onStoreSelected: (UUID) -> Unit,
    onLocationSelected: (UUID) -> Unit,
    onScheduledDateSelected: (Long) -> Unit
) {
    Column {
        StoreDropdown(
            label = "Store",
            selectedStore = uiState.selectedStore,
            stores = uiState.stores,
            onStoreSelected = onStoreSelected
        )

        Spacer(modifier = Modifier.height(16.dp))

        StoreLocationDropdown(
            label = "Location",
            selectedLocation = uiState.selectedLocation,
            locations = uiState.availableLocations,
            enabled = uiState.selectedStore != null,
            onLocationSelected = onLocationSelected
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppointmentDatePickerField(
            selectedDateIso = uiState.scheduledAt,
            enabled = uiState.selectedLocation != null,
            onDateSelected = onScheduledDateSelected
        )
    }
}

@Composable
private fun AppointmentDatePickerField(
    selectedDateIso: String?,
    enabled: Boolean,
    onDateSelected: (Long) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }
    val todayUtcStartMillis = remember {
        Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    val selectedDateText = selectedDateIso
        ?.let(::formatScheduledDateText)
        ?: if (enabled) "Select a date" else "Select a location first"

    Column {
        Text(text = "Date", style = MaterialTheme.typography.titleSmall)
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            onClick = { showPicker = true }
        ) {
            Text(
                text = selectedDateText,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    if (showPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateIso
                ?.let(::parseIsoUtcMillis)
                ?: todayUtcStartMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= todayUtcStartMillis
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let(onDateSelected)
                        showPicker = false
                    },
                    enabled = datePickerState.selectedDateMillis != null
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun formatScheduledDateText(scheduledAtIso: String?): String? {
    val iso = scheduledAtIso ?: return null
    val millis = parseIsoUtcMillis(iso) ?: return null
    return DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(millis))
}

private fun parseIsoUtcMillis(value: String): Long? {
    return listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSX",
        "yyyy-MM-dd'T'HH:mm:ssX"
    ).firstNotNullOfOrNull { pattern ->
        runCatching {
            SimpleDateFormat(pattern, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
                isLenient = false
            }.parse(value)?.time
        }.getOrNull()
    }
}

@Composable
private fun StoreDropdown(
    label: String,
    selectedStore: Store?,
    stores: List<Store>,
    onStoreSelected: (UUID) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    DropdownField(
        label = label,
        value = selectedStore?.name ?: "Select a store",
        enabled = stores.isNotEmpty(),
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        stores.forEach { store ->
            DropdownMenuItem(
                text = { Text(text = store.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                onClick = {
                    expanded = false
                    onStoreSelected(store.id)
                }
            )
        }
    }
}

@Composable
private fun StoreLocationDropdown(
    label: String,
    selectedLocation: StoreLocation?,
    locations: List<StoreLocation>,
    enabled: Boolean,
    onLocationSelected: (UUID) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val value = when {
        !enabled -> "Select a store first"
        locations.isEmpty() -> "No locations available"
        else -> selectedLocation?.name ?: "Select a location"
    }

    DropdownField(
        label = label,
        value = value,
        enabled = enabled && locations.isNotEmpty(),
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        locations.forEach { location ->
            DropdownMenuItem(
                text = {
                    Column {
                        Text(text = location.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        location.address?.let { address ->
                            Text(
                                text = address,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                onClick = {
                    expanded = false
                    onLocationSelected(location.id)
                }
            )
        }
    }
}

@Composable
private fun DropdownField(
    label: String,
    value: String,
    enabled: Boolean,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    menuContent: @Composable () -> Unit
) {
    Column {
        Text(text = label, style = MaterialTheme.typography.titleSmall)
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            onClick = { onExpandedChange(true) }
        ) {
            Text(
                text = value,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.fillMaxWidth()
        ) {
            menuContent()
        }
    }
}


@Preview(showSystemUi = false)
@Composable
private fun ServiceCardPreview() {
    PedalPalTheme {
        ServiceCard(
            title = "Full tune-up",
            description = "Brake, drivetrain, and shifting inspection.",
            price = "79.99",
            currencyCode = "CRC",
            selected = true,
            onClick = {}
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun AddAppointmentContentPreview() {
    PedalPalTheme {
        AddAppointmentContent(
            uiState = AddAppointmentUiState(
                bikes = previewBikes,
                stores = listOf(previewStore),
                serviceCatalog = previewServiceCatalog,
                selectedStoreId = UUID.randomUUID(),
                selectedLocationId = UUID.randomUUID(),
                selectedBikeId = UUID.randomUUID(),
                selectedPackageIds = setOf(UUID.randomUUID()),
                selectedProductIds = setOf(UUID.randomUUID()),
                scheduledAt = "2026-05-20T00:00:00Z",
                notes = "Please check the rear brake rub.",
                catalogLastUpdated = 1_715_788_800_000
            ),
            onStoreSelected = {},
            onLocationSelected = {},
            onBikeSelected = {},
            onScheduledDateSelected = {},
            onPackageToggled = {},
            onProductToggled = {},
            onNotesChanged = {},
            onRetryCatalog = {},
            onCreateAppointment = {}
        )
    }
}

