package com.quetoquenana.and.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StickyBottomCta(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
) {
    Button(
        enabled = enabled,
        onClick = onClick,
        colors = colors,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@DarkLightPreviews
@Composable
private fun EnableStickyBottomCtaPreview() {
    StickyBottomCta(
        text = "Click Me",
        onClick = {}
    )
}

@DarkLightPreviews
@Composable
private fun DisableStickyBottomCtaPreviewDisabled() {
    StickyBottomCta(
        text = "Click Me",
        onClick = {},
        enabled = false
    )
}
