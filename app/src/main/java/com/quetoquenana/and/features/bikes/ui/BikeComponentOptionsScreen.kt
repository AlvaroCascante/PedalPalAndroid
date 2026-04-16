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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BikeComponentOptionsRoute(
    modifier: Modifier = Modifier,
    bikeId: String = "",
    componentId: String = ""
) {
    BikeComponentOptionsScreen(
        modifier = modifier,
        bikeId = bikeId,
        componentId = componentId
    )
}

@Composable
fun BikeComponentOptionsScreen(
    modifier: Modifier = Modifier,
    bikeId: String,
    componentId: String
) {
    val isNewComponent = componentId == "new"
    var selectedAction by remember { mutableStateOf<String?>(null) }

    Scaffold(modifier = modifier) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isNewComponent) "Add component" else "Component options",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Choose how to manage this bike component. The form wiring can build on the endpoints already exposed in the data layer.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                ComponentOptionCard(
                    title = "Add component",
                    description = "Register a new component on this bike using the add component endpoint.",
                    enabled = true,
                    onClick = { selectedAction = "Add component" }
                )
            }

            item {
                ComponentOptionCard(
                    title = "Update component",
                    description = "Edit name, type, brand, model, notes, odometer, or usage time for the selected component.",
                    enabled = !isNewComponent,
                    onClick = { selectedAction = "Update component" }
                )
            }

            item {
                ComponentOptionCard(
                    title = "Replace component",
                    description = "Close out the current part and create its replacement in one action.",
                    enabled = !isNewComponent,
                    onClick = { selectedAction = "Replace component" }
                )
            }

            selectedAction?.let { action ->
                item {
                    Text(
                        text = "$action selected. The next step is wiring the form for this action.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            item {
                Text(
                    text = "Bike: $bikeId · Component: $componentId",
                    style = MaterialTheme.typography.bodySmall
                )
            }
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
            if (!enabled) {
                Text(
                    text = "Select an existing component first.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
