package com.quetoquenana.and.features.appointments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.quetoquenana.and.features.appointments.domain.model.Appointment

@Composable
fun AppointmentSummaryCard(
    appointment: Appointment,
    modifier: Modifier = Modifier,
    actionHint: String? = null,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = appointment.dateText,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = appointment.bikeName ?: appointment.bikeId,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            appointment.status?.let { status ->
                Text(
                    text = status.toDisplayStatus(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            actionHint?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

private fun String.toDisplayStatus(): String {
    return lowercase()
        .split("_", "-", " ")
        .filter { it.isNotBlank() }
        .joinToString(separator = " ") { part ->
            part.replaceFirstChar { char -> char.uppercase() }
        }
}


