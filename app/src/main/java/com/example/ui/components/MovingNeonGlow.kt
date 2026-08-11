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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Emerald400
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

    // Усиливаем чувствительность к громкости голоса, чтобы реакция была отчетливой
    val boostedAmp = (amplitude * 3.0f).coerceIn(0f, 1f)

    val smoothedAmplitude by animateFloatAsState(
        targetValue = boostedAmp,
        animationSpec = tween(70),
        label = "smoothed_amplitude"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (glowAlpha > 0f) {
            Canvas(
                modifier = Modifier.matchParentSize()
            ) {
                // Выносим контур свечения ЗА ПРЕДЕЛЫ темного фона плашки
                val extraPadding = (6f + smoothedAmplitude * 14f).dp.toPx()
                val rectLeft = -extraPadding
                val rectTop = -extraPadding
                val rectRight = size.width + extraPadding
                val rectBottom = size.height + extraPadding

                val capsulePath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = Rect(rectLeft, rectTop, rectRight, rectBottom),
                            cornerRadius = CornerRadius((cornerRadiusDp + 6f).dp.toPx())
                        )
                    )
                }

                // В тишине свечение видно на 20%, при разговоре раскрывается до 100%
                val intensity = (0.20f + smoothedAmplitude * 0.80f) * glowAlpha

                // 1. Внешний рассеянный неоновый ореол
                drawPath(
                    path = capsulePath,
                    color = Emerald400.copy(alpha = intensity * 0.4f),
                    style = Stroke(width = (8f + smoothedAmplitude * 16f).dp.toPx())
                )

                // 2. Средний яркий контур
                drawPath(
                    path = capsulePath,
                    color = Rose500.copy(alpha = intensity * 0.75f),
                    style = Stroke(width = (3f + smoothedAmplitude * 8f).dp.toPx())
                )

                // 3. Яркая сердцевина
                drawPath(
                    path = capsulePath,
                    color = Color.White.copy(alpha = intensity * 0.9f),
                    style = Stroke(width = (1f + smoothedAmplitude * 2f).dp.toPx())
                )
            }
        }

        content()
    }
}
