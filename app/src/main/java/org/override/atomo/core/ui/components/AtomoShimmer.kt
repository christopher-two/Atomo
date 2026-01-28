/*
 * Copyright (c) 2026 Christopher Alejandro Maldonado Chávez.
 * Override. Todos los derechos reservados.
 * Este código fuente y sus archivos relacionados son propiedad intelectual de Override.
 * Queda estrictamente prohibida la reproducción, distribución o modificación
 * total o parcial de este material sin el consentimiento previo por escrito.
 * Uruapan, Michoacán, México. | atomo.click
 */

package org.override.atomo.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Define the shimmer effect extension
/**
 * Applies a shimmering effect to the modifier.
 * Useful for loading states.
 *
 * @param widthOfShadowBrush Width of the shimmer brush.
 * @param angleOfAxisY Angle of the shimmer movement.
 * @param durationMillis Duration of one full shimmer cycle.
 */
fun Modifier.shimmerEffect(
    widthOfShadowBrush: Int = 500,
    angleOfAxisY: Float = 270f,
    durationMillis: Int = 1000,
): Modifier = composed {
    val shimmerColors = listOf(
        colorScheme.surfaceContainerLow.copy(alpha = 0.3f),
        colorScheme.surfaceContainerLow.copy(alpha = 0.5f),
        colorScheme.surfaceContainerLow.copy(alpha = 1.0f),
        colorScheme.surfaceContainerLow.copy(alpha = 0.5f),
        colorScheme.surfaceContainerLow.copy(alpha = 0.3f),
    )

    val transition = rememberInfiniteTransition(label = "")

    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "Shimmer loading animation",
    )

    this.background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(x = translateAnimation.value - widthOfShadowBrush, y = 0.0f),
            end = Offset(x = translateAnimation.value, y = angleOfAxisY),
        ),
    )
}


/**
 * A basic box container with shimmer effect.
 *
 * @param modifier Styling modifier.
 * @param shape Shape of the box.
 * @param color Background color before/under shimmer.
 */
@Composable
fun ShimmerItem(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    color: Color = colorScheme.surfaceContainerLowest.copy(alpha = 0.8f)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(color)
            .shimmerEffect()
    )
}

/**
 * A rectangular line with shimmer effect, suitable for text placeholders.
 *
 * @param modifier Styling modifier.
 * @param height Height of the line.
 * @param shape Shape of the line.
 */
@Composable
fun ShimmerLine(
    modifier: Modifier = Modifier,
    height: Dp = 20.dp,
    shape: Shape = RoundedCornerShape(4.dp)
) {
    ShimmerItem(
        modifier = modifier,
        shape = shape
    )
}

/**
 * A circular item with shimmer effect, suitable for avatar placeholders.
 *
 * @param modifier Styling modifier.
 * @param size Size (diameter) of the circle.
 */
@Composable
fun ShimmerCircle(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    ShimmerItem(
        modifier = modifier,
        shape = CircleShape
    )
}
