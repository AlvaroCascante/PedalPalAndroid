package com.quetoquenana.and.features.bikes.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.quetoquenana.and.R

private const val FallbackStravaButtonAspectRatio = 4.0f

@Composable
fun StravaBrandedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String = "Connect with Strava"
) {
    val painter = painterResource(id = R.drawable.btn_strava_connect_with_orange)
    val aspectRatio = remember(painter) {
        val intrinsicSize = painter.intrinsicSize
        if (intrinsicSize.isSpecified && intrinsicSize.width > 0f && intrinsicSize.height > 0f) {
            intrinsicSize.width / intrinsicSize.height
        } else {
            FallbackStravaButtonAspectRatio
        }
    }

    Image(
        painter = painter,
        contentDescription = contentDescription,
        contentScale = ContentScale.FillBounds,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .alpha(if (enabled) 1f else 0.6f)
            .semantics { role = Role.Button }
            .clickable(enabled = enabled, onClick = onClick)
    )
}
