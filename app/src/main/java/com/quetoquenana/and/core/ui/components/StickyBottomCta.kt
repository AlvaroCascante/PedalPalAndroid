package com.quetoquenana.and.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun Modifier.stickyBottomCtaContainer(): Modifier {
    return fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 8.dp)
}

@Composable
fun StickyBottomCta(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.stickyBottomCtaContainer()) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            onClick = onClick
        ) {
            Text(text = text)
        }
    }
}

