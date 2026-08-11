package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Rose500

@Composable
fun MovingNeonGlow(
    isRecording: Boolean,
    amplitude: Float,
    widthDp: Float, 
    heightDp: Float, 
    cornerRadiusDp: Float = 28f,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val glowAlpha by animateFloatAsState(
        targetValue = if (isRecording) 1f else 0f,
        animationSpec = tween(300),
        label = "neon_glow_alpha"
    )

    val smoothedAmplitude by animateFloatAsState(
        targetValue = amplitude,
        animationSpec = tween(100),
        label = "smoothed_amplitude"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            if (glowAlpha > 0f) {
                // Выдвигаем контур на 2dp наружу, чтобы свечение не резалось фоном плашки
                val offset = 2.dp.toPx()
                val rectLeft = -offset
                val rectTop = -offset
                val rectRight = size.width + offset
                val rectBottom = size.height + offset

                val capsulePath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = Rect(rectLeft, rectTop, rectRight, rectBottom),
                            cornerRadius = CornerRadius(cornerRadiusDp.dp.toPx())
                        )
                    )
                }

                // Базовое свечение (в тишине) и добавочное (от голоса)
                val baseAlpha = 0.2f
                val voiceAlpha = smoothedAmplitude * 0.8f 
                val totalAlpha = (baseAlpha + voiceAlpha) * glowAlpha

                // 1. Внешний слой: широкий и рассеянный всплеск от голоса
                drawPath(
                    path = capsulePath,
                    color = Rose500.copy(alpha = totalAlpha * 0.15f),
                    style = Stroke(width = (12f + smoothedAmplitude * 24f).dp.toPx())
                )

                // 2. Средний слой: основной ореол
                drawPath(
                    path = capsulePath,
                    color = Rose500.copy(alpha = totalAlpha * 0.4f),
                    style = Stroke(width = (6f + smoothedAmplitude * 12f).dp.toPx())
                )

                // 3. Внутренний слой: тонкий яркий контур у самой плашки
                drawPath(
                    path = capsulePath,
                    color = Rose500.copy(alpha = totalAlpha),
                    style = Stroke(width = (2f + smoothedAmplitude * 4f).dp.toPx())
                )
            }
        }
        
        // Сам контент (черная плашка)
        content()
    }
}
