package com.quetoquenana.and.features.bikes.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private const val DefaultStravaDescription =
    "Connect Strava, choose existing gear, then review and save it in PedalPal."

private const val DefaultManualDescription =
    "Add the bike details yourself. Best when this bike is not in Strava yet."

private enum class BikeActionStyle {
    Default,
    Strava
}

@Composable
private fun BikeActionCard(
    title: String,
    description: String,
    actionText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String = actionText,
    actionStyle: BikeActionStyle = BikeActionStyle.Default
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = description)
            if (actionStyle == BikeActionStyle.Strava) {
                StravaBrandedButton(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled,
                    contentDescription = contentDescription
                )
            } else {
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled
                ) {
                    Text(text = actionText)
                }
            }
        }
    }
}

@Composable
fun ImportFromStravaBikeCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    title: String = "Import from Strava",
    description: String = DefaultStravaDescription,
    contentDescription: String = "Connect Strava"
) {
    BikeActionCard(
        title = title,
        description = description,
        modifier = modifier,
        actionText = contentDescription,
        onClick = onClick,
        enabled = enabled,
        contentDescription = contentDescription,
        actionStyle = BikeActionStyle.Strava
    )
}

@Composable
fun CreateBikeManuallyCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    title: String = "Create from scratch",
    description: String = DefaultManualDescription,
    actionText: String = "Create manually"
) {
    BikeActionCard(
        title = title,
        description = description,
        modifier = modifier,
        actionText = actionText,
        onClick = onClick,
        enabled = enabled
    )
}



