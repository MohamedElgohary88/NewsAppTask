package com.elgohary.newsapptask.presentation.designsystem.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.material3.MaterialTheme
import com.elgohary.newsapptask.presentation.designsystem.AppColors
import com.elgohary.newsapptask.presentation.designsystem.UiDefaults

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val shimmerX by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = UiDefaults.SHIMMER_DURATION_MS, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerX"
    )

    val baseColor = MaterialTheme.colorScheme.surfaceVariant
    val highlight = AppColors.ShimmerHighlight
    val brush = Brush.linearGradient(
        colors = listOf(baseColor, highlight, baseColor),
        start = Offset.Zero,
        end = Offset(1000f, 0f)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseColor)
            .drawWithContent {
                clipRect {
                    val width = size.width
                    val x = shimmerX * width
                    drawRect(brush = brush, topLeft = Offset(x, 0f), size = size)
                }
            }
    )
}