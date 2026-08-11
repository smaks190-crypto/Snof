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
    widthDp: Float, // Оставил для совместимости, чтобы не ломать вызов в VoiceRecordingOverlay
    heightDp: Float, // Оставил для совместимости
    cornerRadiusDp: Float = 28f,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Плавное появление и исчезновение самого компонента свечения
    val glowAlpha by animateFloatAsState(
        targetValue = if (isRecording) 1f else 0f,
        animationSpec = tween(300),
        label = "neon_glow_alpha"
    )

    // Мягко сглаживаем амплитуду голоса, чтобы свечение не дергалось резко
    val smoothedAmplitude by animateFloatAsState(
        targetValue = amplitude,
        animationSpec = tween(100),
        label = "smoothed_amplitude"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Холст строго по размеру плашки (благодаря matchParentSize)
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            if (glowAlpha > 0f) {
                val rectLeft = 0f
                val rectTop = 0f
                val rectRight = size.width
                val rectBottom = size.height

                // Создаем контур с закругленными углами
                val capsulePath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = Rect(rectLeft, rectTop, rectRight, rectBottom),
                            cornerRadius = CornerRadius(cornerRadiusDp.dp.toPx())
                        )
                    )
                }

                // 1. Базовое свечение (когда молчим - еле видно)
                val baseAlpha = 0.15f
                
                // 2. Динамическое свечение (зависит от громкости голоса)
                // Чем громче звук, тем выше значение (добавляем до 0.85f к яркости)
                val voiceAlpha = smoothedAmplitude * 0.85f 
                val totalAlpha = (baseAlpha + voiceAlpha) * glowAlpha

                // Рисуем 3 слоя для эффекта рассеянного неонового свечения
                // Цвет взял Rose500 (как кнопка записи), он отлично подходит для индикации микрофона.

                // Внешний контур (самый широкий, сильно увеличивается от голоса)
                drawPath(
                    path = capsulePath,
                    color = Rose500.copy(alpha = totalAlpha * 0.2f),
                    style = Stroke(width = (10f + smoothedAmplitude * 20f).dp.toPx())
                )

                // Средний контур (основной ореол свечения)
                drawPath(
                    path = capsulePath,
                    color = Rose500.copy(alpha = totalAlpha * 0.5f),
                    style = Stroke(width = (4f + smoothedAmplitude * 10f).dp.toPx())
                )

                // Внутренний яркий контур (четкая граница плашки)
                drawPath(
                    path = capsulePath,
                    color = Rose500.copy(alpha = totalAlpha),
                    style = Stroke(width = (1f + smoothedAmplitude * 3f).dp.toPx())
                )
            }
        }

        // Сам контент (твоя черная плашка и кнопка)
        content()
    }
}
