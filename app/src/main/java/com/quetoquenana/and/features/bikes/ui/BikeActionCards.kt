package com.quetoquenana.and.features.bikes.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.quetoquenana.and.R
import com.quetoquenana.and.core.ui.components.HomeAnnouncementShape
import com.quetoquenana.and.core.ui.components.StickyBottomCta

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
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true,
    contentDescription: String = actionText,
    actionStyle: BikeActionStyle = BikeActionStyle.Default,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
) {
    Surface(
        modifier = Modifier
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = HomeAnnouncementShape,
        color = backgroundColor,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.secondary
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
            if (actionStyle == BikeActionStyle.Strava) {
                StravaBrandedButton(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled,
                    contentDescription = contentDescription
                )
            } else {
                StickyBottomCta(
                    onClick = onClick,
                    text = actionText,
                    colors = colors,
                )
            }
        }
    }
}

@Composable
fun ImportFromStravaBikeCard(
    onClick: () -> Unit,
    enabled: Boolean = true,
    title: String = stringResource(R.string.import_from_strava),
    description: String = stringResource(id = R.string.connect_strava_description),
    contentDescription: String = "Connect Strava"
) {
    BikeActionCard(
        title = title,
        description = description,
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
    enabled: Boolean = true,
    title: String = stringResource(id = R.string.create_from_scratch),
    description: String = stringResource(id = R.string.add_the_bike_details_yourself),
    actionText: String = "Create manually",
    colors: ButtonColors
) {
    BikeActionCard(
        title = title,
        description = description,
        actionText = actionText,
        onClick = onClick,
        enabled = enabled,
        colors = colors
    )
}



