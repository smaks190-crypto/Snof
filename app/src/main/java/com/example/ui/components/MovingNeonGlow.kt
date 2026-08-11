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

    // Усиливаем чувствительность к громкости речи (х3.5)
    val boostedAmp = (amplitude * 3.5f).coerceIn(0f, 1f)
    val smoothedAmp by animateFloatAsState(
        targetValue = boostedAmp,
        animationSpec = tween(80),
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
                // ВЫНОСИМ ОРЕОЛ НАРУЖУ: рисуем за пределами границ плашки,
                // чтобы тёмный фон плашки её не перекрывал.
                // Размеры самой плашки в интерфейсе остаются неизменными.
                val glowOffset = (3f + smoothedAmp * 10f).dp.toPx()
                val rectLeft = -glowOffset
                val rectTop = -glowOffset
                val rectRight = size.width + glowOffset
                val rectBottom = size.height + glowOffset

                val capsulePath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = Rect(rectLeft, rectTop, rectRight, rectBottom),
                            cornerRadius = CornerRadius((cornerRadiusDp + 4f).dp.toPx())
                        )
                    )
                }

                val intensity = (0.25f + smoothedAmp * 0.75f) * glowAlpha

                // 1. Внешнее мягкое неоновое свечение вокруг плашки
                drawPath(
                    path = capsulePath,
                    color = Rose500.copy(alpha = intensity * 0.35f),
                    style = Stroke(width = (5f + smoothedAmp * 12f).dp.toPx())
                )

                // 2. Четкая контурная линия
                drawPath(
                    path = capsulePath,
                    color = Rose500.copy(alpha = intensity * 0.85f),
                    style = Stroke(width = (2f + smoothedAmp * 4f).dp.toPx())
                )

                // 3. Белый всплеск-блик при произнесении слов
                if (smoothedAmp > 0.08f) {
                    drawPath(
                        path = capsulePath,
                        color = Color.White.copy(alpha = smoothedAmp * 0.75f * glowAlpha),
                        style = Stroke(width = (1f + smoothedAmp * 2f).dp.toPx())
                    )
                }
            }
        }

        // Черная плашка
        content()
    }
}
