package com.quetoquenana.and.core.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BoxSurface(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(30.dp),
    bgColor: Color = Color(0xFFe0e0e0),
    lightShadow: Color = Color(0xFFFFFFFF),
    darkShadow: Color = Color(0xFFb1b1b1),
    upperOffset: Dp = (-10).dp,
    lowerOffset: Dp = 10.dp,
    radius: Dp = 5.dp,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Box(
        modifier = modifier
            .background(bgColor)
            .padding(all = 8.dp)
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = radius,
                    color = lightShadow,
                    offset = DpOffset(upperOffset, upperOffset)
                )
            )
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = radius,
                    color = darkShadow,
                    offset = DpOffset(lowerOffset, lowerOffset)
                )
            )
            .background(bgColor, shape)
    ) {
        content()
    }
}

@Composable
fun AnimatedPressSurface(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(20.dp),
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val transition = updateTransition(
        targetState = isPressed,
        label = "press_surface_transition"
    )

    fun <T> buttonPressAnimation() = tween<T>(
        durationMillis = 220,
        easing = EaseInOut
    )

    val isLightSurface = containerColor.luminance() > 0.5f
    val topShadowBaseColor = if (isLightSurface) {
        Color.White.copy(alpha = 0.85f)
    } else {
        MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.24f)
    }
    val bottomShadowBaseColor = if (isLightSurface) {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.scrim.copy(alpha = 0.42f)
    }
    val innerHighlightBaseColor = if (isLightSurface) {
        MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.9f)
    } else {
        MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.16f)
    }
    val innerShadeBaseColor = if (isLightSurface) {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.24f)
    } else {
        MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f)
    }

    val shadowAlpha by transition.animateFloat(
        label = "outer_shadow_alpha",
        transitionSpec = { buttonPressAnimation() }
    ) { pressed ->
        if (pressed) 0f else 1f
    }
    val innerShadowAlpha by transition.animateFloat(
        label = "inner_shadow_alpha",
        transitionSpec = { buttonPressAnimation() }
    ) { pressed ->
        if (pressed) 1f else 0f
    }
    val topShadowColor by transition.animateColor(
        label = "top_shadow_color",
        transitionSpec = { buttonPressAnimation() }
    ) { pressed ->
        if (pressed) Color.Transparent else topShadowBaseColor
    }
    val bottomShadowColor by transition.animateColor(
        label = "bottom_shadow_color",
        transitionSpec = { buttonPressAnimation() }
    ) { pressed ->
        if (pressed) Color.Transparent else bottomShadowBaseColor
    }
    val innerHighlightColor by transition.animateColor(
        label = "inner_highlight_color",
        transitionSpec = { buttonPressAnimation() }
    ) { pressed ->
        if (pressed) innerHighlightBaseColor else Color.Transparent
    }
    val innerShadeColor by transition.animateColor(
        label = "inner_shade_color",
        transitionSpec = { buttonPressAnimation() }
    ) { pressed ->
        if (pressed) innerShadeBaseColor else Color.Transparent
    }

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .semantics(mergeDescendants = true) {}
            .then(clickableModifier)
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 12.dp,
                    spread = 0.dp,
                    color = topShadowColor,
                    offset = DpOffset(x = (-2).dp, y = (-2).dp),
                    alpha = shadowAlpha
                )
            )
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 16.dp,
                    spread = 0.dp,
                    color = bottomShadowColor,
                    offset = DpOffset(x = 4.dp, y = 8.dp),
                    alpha = shadowAlpha
                )
            )
            .background(
                color = containerColor,
                shape = shape
            )
            .innerShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 8.dp,
                    spread = 1.dp,
                    color = innerHighlightColor,
                    offset = DpOffset(x = (-2).dp, y = (-2).dp),
                    alpha = innerShadowAlpha
                )
            )
            .innerShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 12.dp,
                    spread = 2.dp,
                    color = innerShadeColor,
                    offset = DpOffset(x = 3.dp, y = 4.dp),
                    alpha = innerShadowAlpha
                )
            ),
        contentAlignment = contentAlignment
    ) {
        content()
    }
}

@Composable
fun AnimatedColoredShadows(
    content: @Composable BoxScope.() -> Unit = {}
) {
    Box(Modifier.fillMaxSize()) {
        AnimatedPressSurface(
            modifier = Modifier.padding(vertical = 12.dp),
            onClick = {}
        ) {
            content()
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun BoxSurfacePreview() {
    AnimatedColoredShadows(
        content = {
            Text(
                "Animated Shadows",
                // [START_EXCLUDE]
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 12.dp),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 24.sp,
                color = Color.Black
                // [END_EXCLUDE]
            )
        }
    )
}