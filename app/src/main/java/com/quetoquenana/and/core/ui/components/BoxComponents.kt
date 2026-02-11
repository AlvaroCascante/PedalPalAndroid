package com.quetoquenana.and.core.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quetoquenana.and.R

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
fun AnimatedColoredShadows(
    content: @Composable BoxScope.() -> Unit = {}
) {
    Box(Modifier.fillMaxSize()) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()

        // Create transition with pressed state
        val transition = updateTransition(
            targetState = isPressed,
            label = "button_press_transition"
        )

        fun <T> buttonPressAnimation() = tween<T>(
            durationMillis = 400,
            easing = EaseInOut
        )

        // Animate all properties using the transition
        val shadowAlpha by transition.animateFloat(
            label = "shadow_alpha",
            transitionSpec = { buttonPressAnimation() }
        ) { pressed ->
            if (pressed) 0f else 1f
        }
        // [START_EXCLUDE]
        val innerShadowAlpha by transition.animateFloat(
            label = "shadow_alpha",
            transitionSpec = { buttonPressAnimation() }
        ) { pressed ->
            if (pressed) 1f else 0f
        }

        val blueDropShadowColor = Color(0x5C007AFF)

        val darkBlueDropShadowColor = Color(0x66007AFF)

        val greyInnerShadowColor1 = Color(0x1A007AFF)

        val greyInnerShadowColor2 = Color(0x1A007AFF)
        // [END_EXCLUDE]

        val blueDropShadow by transition.animateColor(
            label = "shadow_color",
            transitionSpec = { buttonPressAnimation() }
        ) { pressed ->
            if (pressed) Color.Transparent else blueDropShadowColor
        }

        // [START_EXCLUDE]
        val darkBlueDropShadow by transition.animateColor(
            label = "shadow_color",
            transitionSpec = { buttonPressAnimation() }
        ) { pressed ->
            if (pressed) Color.Transparent else darkBlueDropShadowColor
        }

        val innerShadowColor1 by transition.animateColor(
            label = "shadow_color",
            transitionSpec = { buttonPressAnimation() }
        ) { pressed ->
            if (pressed) greyInnerShadowColor1
            else greyInnerShadowColor2
        }

        val innerShadowColor2 by transition.animateColor(
            label = "shadow_color",
            transitionSpec = { buttonPressAnimation() }
        ) { pressed ->
            if (pressed) Color(0x4D007AFF)
            else Color(0x1A007AFF)
        }
        // [END_EXCLUDE]

        Box(
            Modifier
                .clickable(interactionSource, indication = null) {
                    // ** ...... **//
                }
                .padding(vertical = 12.dp)
                .dropShadow(
                    shape = RoundedCornerShape(20.dp),
                    shadow = Shadow(
                        radius = 10.dp,
                        spread = 0.dp,
                        color = blueDropShadow,
                        offset = DpOffset(x = 0.dp, -(2).dp),
                        alpha = shadowAlpha
                    )
                )
                .dropShadow(
                    shape = RoundedCornerShape(20.dp),
                    shadow = Shadow(
                        radius = 10.dp,
                        spread = 0.dp,
                        color = darkBlueDropShadow,
                        offset = DpOffset(x = 2.dp, 6.dp),
                        alpha = shadowAlpha
                    )
                )
                // note that the background needs to be defined before defining the inner shadow
                .background(
                    color = Color(0xFFFFFFFF),
                    shape = RoundedCornerShape(20.dp)
                )
                .innerShadow(
                    shape = RoundedCornerShape(20.dp),
                    shadow = Shadow(
                        radius = 8.dp,
                        spread = 4.dp,
                        color = innerShadowColor2,
                        offset = DpOffset(x = 4.dp, 0.dp)
                    )
                )
                .innerShadow(
                    shape = RoundedCornerShape(20.dp),
                    shadow = Shadow(
                        radius = 20.dp,
                        spread = 4.dp,
                        color = innerShadowColor1,
                        offset = DpOffset(x = 4.dp, 0.dp),
                        alpha = innerShadowAlpha
                    )
                )

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