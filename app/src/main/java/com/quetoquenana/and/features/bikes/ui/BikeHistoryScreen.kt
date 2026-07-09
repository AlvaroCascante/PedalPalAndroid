package com.quetoquenana.and.features.bikes.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.quetoquenana.and.R
import com.quetoquenana.and.features.bikes.domain.model.BikeHistory
import com.quetoquenana.and.features.bikes.domain.model.BikeHistoryType
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
    val occurredAtDisplay = history.occurredAt.toOccurredAtDisplay()
    val payloadDisplay = history.toPayloadDisplay()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = history.toDisplayLabel(),
                style = MaterialTheme.typography.titleMedium
            )
            occurredAtDisplay?.let { display ->
                Text(text = "Date: ${display.date}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Time: ${display.time}", style = MaterialTheme.typography.bodyMedium)
            } ?: Text(text = history.occurredAt, style = MaterialTheme.typography.bodyMedium)

            payloadDisplay?.let {
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

@Composable
private fun BikeHistory.toDisplayLabel(): String {
    return if (type == BikeHistoryType.UNKNOWN && rawType != BikeHistoryType.UNKNOWN.name) {
        rawType.toDisplayLabel()
    } else {
        stringResource(type.labelResId())
    }
}

private fun BikeHistoryType.labelResId(): Int {
    return when (translationKey) {
        "bike.history.created" -> R.string.bike_history_type_created
        "bike.history.updated" -> R.string.bike_history_type_updated
        "bike.history.status.changed" -> R.string.bike_history_type_status_changed
        "bike.history.component.added" -> R.string.bike_history_type_component_added
        "bike.history.component.updated" -> R.string.bike_history_type_component_updated
        "bike.history.component.replaced" -> R.string.bike_history_type_component_replaced
        "bike.history.component.status.changed" -> R.string.bike_history_type_component_status_changed
        else -> R.string.bike_history_type_unknown
    }
}

private fun BikeHistory.toPayloadDisplay(): String? {
    if (payload.isBlank()) return null

    return when (type) {
        BikeHistoryType.COMPONENT_ADDED -> payload.extractComponentAddedValue() ?: payload
        BikeHistoryType.CREATED,
        BikeHistoryType.UPDATED,
        BikeHistoryType.STATUS_CHANGED,
        BikeHistoryType.COMPONENT_UPDATED,
        BikeHistoryType.COMPONENT_REPLACED,
        BikeHistoryType.COMPONENT_STATUS_CHANGED,
        BikeHistoryType.UNKNOWN -> payload
    }
}

private fun String.extractComponentAddedValue(): String? {
    return runCatching {
        val payloadEntries = JSONArray(this)
        buildList {
            for (index in 0 until payloadEntries.length()) {
                val entry = payloadEntries.optJSONObject(index) ?: continue
                val newValue = entry.optString("newValue")
                    .takeIf { it.isNotBlank() }
                    ?: continue
                add(newValue)
            }
        }.distinct().joinToString(separator = ", ").takeIf { it.isNotBlank() }
    }.getOrNull()
}

private data class OccurredAtDisplay(
    val date: String,
    val time: String
)

private fun String.toOccurredAtDisplay(): OccurredAtDisplay? {
    val parsedDate = parseOccurredAt(trim()) ?: return null
    return OccurredAtDisplay(
        date = occurredAtDateFormatter.format(parsedDate),
        time = occurredAtTimeFormatter.format(parsedDate)
    )
}

private fun parseOccurredAt(value: String): Date? {
    val candidates = buildList {
        add(value)

        if (' ' in value) {
            add(value.replaceFirst(" ", "T"))
        }
    }

    return candidates.firstNotNullOfOrNull(::parseOccurredAtCandidate)
}

private fun parseOccurredAtCandidate(value: String): Date? {
    return occurredAtInputPatterns.firstNotNullOfOrNull { pattern ->
        runCatching {
            SimpleDateFormat(pattern, Locale.US).apply {
                timeZone = utcTimeZone
            }.parse(value)
        }.getOrNull()
    }
}

private val utcTimeZone: TimeZone = TimeZone.getTimeZone("UTC")

private val occurredAtInputPatterns = listOf(
    "yyyy-MM-dd'T'HH:mm:ss.SSSSSSX",
    "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXX",
    "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",
    "yyyy-MM-dd'T'HH:mm:ss.SSSX",
    "yyyy-MM-dd'T'HH:mm:ss.SSSXX",
    "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
    "yyyy-MM-dd'T'HH:mm:ssX",
    "yyyy-MM-dd'T'HH:mm:ssXX",
    "yyyy-MM-dd'T'HH:mm:ssXXX"
)

private val occurredAtDateFormatter: SimpleDateFormat
    get() = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }

private val occurredAtTimeFormatter: SimpleDateFormat
    get() = SimpleDateFormat("h:mm:ss a", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }

