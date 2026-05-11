package com.quetoquenana.and.features.appointments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.quetoquenana.and.features.stores.domain.model.Store
import com.quetoquenana.and.features.stores.domain.model.StoreLocation
import com.quetoquenana.and.features.services.domain.model.ServicePackage
import com.quetoquenana.and.features.services.domain.model.ServiceProduct

@Composable
fun AddAppointmentScreen(
    modifier: Modifier = Modifier,
    onDone: () -> Unit = {},
    viewModel: AddAppointmentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    AddAppointmentContent(
        modifier = modifier,
        uiState = uiState,
        onStoreSelected = viewModel::onStoreSelected,
        onLocationSelected = viewModel::onLocationSelected,
        onPackageToggled = viewModel::onPackageToggled,
        onProductToggled = viewModel::onProductToggled,
        onNotesChanged = viewModel::onNotesChanged,
        onDone = onDone
    )
}

@Composable
private fun AddAppointmentContent(
    modifier: Modifier = Modifier,
    uiState: AddAppointmentUiState,
    onStoreSelected: (String) -> Unit,
    onLocationSelected: (String) -> Unit,
    onPackageToggled: (String) -> Unit,
    onProductToggled: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onDone: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Book service",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Choose a store and service location.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        when {
            uiState.isLoadingStores -> LoadingStoresCard()
            uiState.stores.isEmpty() -> EmptyStoresCard(errorMessage = uiState.errorMessage)
            else -> StoreSelectionFields(
                uiState = uiState,
                onStoreSelected = onStoreSelected,
                onLocationSelected = onLocationSelected
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        when {
            uiState.isLoadingServices -> LoadingStoresCard(text = "Loading services")
            uiState.serviceCatalog.packages.isEmpty() && uiState.serviceCatalog.products.isEmpty() -> EmptyStoresCard(
                title = "No services available",
                errorMessage = uiState.errorMessage
            )
            else -> ServiceSelectionFields(
                uiState = uiState,
                onPackageToggled = onPackageToggled,
                onProductToggled = onProductToggled
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        AppointmentReviewCard(
            uiState = uiState,
            onNotesChanged = onNotesChanged
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            enabled = uiState.selectedLocation != null && uiState.requestedServiceCount > 0,
            onClick = onDone
        ) {
            Text("Review appointment")
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
    errorMessage: String?
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
        }
    }
}

@Composable
private fun ServiceSelectionFields(
    uiState: AddAppointmentUiState,
    onPackageToggled: (String) -> Unit,
    onProductToggled: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                selected = product.id in uiState.selectedProductIds,
                onClick = { onProductToggled(product.id) }
            )
        }
    }
}

@Composable
private fun <T> ServiceRowSection(
    title: String,
    emptyText: String,
    items: List<T>,
    key: (T) -> String,
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
                text = price?.let { "$$it" } ?: "Price unavailable",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

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
    onStoreSelected: (String) -> Unit,
    onLocationSelected: (String) -> Unit
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
    }
}

@Composable
private fun StoreDropdown(
    label: String,
    selectedStore: Store?,
    stores: List<Store>,
    onStoreSelected: (String) -> Unit
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
    onLocationSelected: (String) -> Unit
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
