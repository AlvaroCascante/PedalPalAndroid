package com.quetoquenana.and.features.appointments.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )
    ) {
        Column(
            modifier = Modifier.padding(all = 12.dp)
        ) {
            Text(
                text = appointment.dateText,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = appointment.bikeName ,
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
                    style = MaterialTheme.typography.bodyMedium
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


