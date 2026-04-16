package com.quetoquenana.and.features.bikes.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.quetoquenana.and.features.bikes.domain.model.BikeHistory

@Composable
fun BikeHistoryRoute(
    modifier: Modifier = Modifier,
    viewModel: BikeHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    BikeHistoryScreen(
        modifier = modifier,
        uiState = uiState
    )
}

@Composable
fun BikeHistoryScreen(
    modifier: Modifier = Modifier,
    uiState: BikeHistoryUiState
) {
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
                Text(text = "Bike history", style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "Maintenance, component, status, and sync events for this bike.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            when {
                uiState.isLoading -> item { Text(text = "Loading history...") }
                uiState.errorMessage != null -> item {
                    Text(text = uiState.errorMessage)
                }
                uiState.history.isEmpty() -> item {
                    Text(text = "No history events yet.")
                }
                else -> items(uiState.history, key = { it.id }) { history ->
                    BikeHistoryCard(history = history)
                }
            }
        }
    }
}

@Composable
private fun BikeHistoryCard(history: BikeHistory) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = history.type.toDisplayLabel(), style = MaterialTheme.typography.titleMedium)
            Text(text = history.occurredAt, style = MaterialTheme.typography.bodyMedium)
            history.performedBy?.takeIf { it.isNotBlank() }?.let {
                Text(text = "Performed by $it", maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            history.payload.takeIf { it.isNotBlank() }?.let {
                Text(text = it, maxLines = 4, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

private fun String.toDisplayLabel(): String {
    return lowercase()
        .split("_", "-", " ")
        .filter { it.isNotBlank() }
        .joinToString(separator = " ") { part ->
            part.replaceFirstChar { it.uppercase() }
        }
}
