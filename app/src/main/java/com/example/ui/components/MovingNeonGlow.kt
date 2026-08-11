package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Rose500
import kotlin.math.cos
import kotlin.math.sin

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
    // 1. Плавная анимация включения/выключения
    val glowAlpha by animateFloatAsState(
        targetValue = if (isRecording) 1f else 0f,
        animationSpec = tween(350),
        label = "glow_alpha"
    )

    // 2. Чувствительность к громкости голоса
    val boostedAmp = (amplitude * 3.2f).coerceIn(0f, 1f)
    val smoothedAmp by animateFloatAsState(
        targetValue = boostedAmp,
        animationSpec = tween(90),
        label = "smoothed_amp"
    )

    // 3. Непрерывное вращение градиента по волне
    val infiniteTransition = rememberInfiniteTransition(label = "wave_motion")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f, // 2 * PI
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (glowAlpha > 0f) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val width = size.width
                val height = size.height

                // Рассчитываем динамическое смещение градиентной волны
                val cosWave = cos(wavePhase.toDouble()).toFloat()
                val sinWave = sin(wavePhase.toDouble()).toFloat()

                val startOffset = Offset(
                    x = width * 0.5f + cosWave * width * 0.4f,
                    y = height * 0.5f + sinWave * height * 0.4f
                )
                val endOffset = Offset(
                    x = width * 0.5f - cosWave * width * 0.4f,
                    y = height * 0.5f - sinWave * height * 0.4f
                )

                // Динамическая прозрачность цвета в зависимости от громкости
                val baseAlpha = (0.25f + smoothedAmp * 0.75f) * glowAlpha

                // Многоцветный живой градиент
                val gradientBrush = Brush.linearGradient(
                    colors = listOf(
                        Emerald400.copy(alpha = baseAlpha * 0.85f),
                        Indigo500.copy(alpha = baseAlpha * 0.95f),
                        Rose500.copy(alpha = baseAlpha * 0.9f),
                        Emerald400.copy(alpha = baseAlpha * 0.85f)
                    ),
                    start = startOffset,
                    end = endOffset,
                    tileMode = TileMode.Repeated
                )

                // Внутреннее белое ядро для отклика на пики громкости
                val coreBrush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = smoothedAmp * 0.8f * glowAlpha),
                        Emerald400.copy(alpha = baseAlpha),
                        Rose500.copy(alpha = baseAlpha),
                        Color.White.copy(alpha = smoothedAmp * 0.8f * glowAlpha)
                    ),
                    start = startOffset,
                    end = endOffset
                )

                // Аккуратный отступ наружу (не раздувает плашку, а создает изящную ауру)
                val auraOffset = (2f + smoothedAmp * 6f).dp.toPx()
                val rectLeft = -auraOffset
                val rectTop = -auraOffset
                val rectRight = width + auraOffset
                val rectBottom = height + auraOffset

                val capsulePath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = Rect(rectLeft, rectTop, rectRight, rectBottom),
                            cornerRadius = CornerRadius((cornerRadiusDp + 3f).dp.toPx())
                        )
                    )
                }

                // СЛОЙ 1: Внешнее глубокое свечение (мягкая волна)
                drawPath(
                    path = capsulePath,
                    brush = gradientBrush,
                    style = Stroke(width = (6f + smoothedAmp * 12f).dp.toPx())
                )

                // СЛОЙ 2: Средний неоновый контур (яркая цветная линия)
                drawPath(
                    path = capsulePath,
                    brush = gradientBrush,
                    style = Stroke(width = (2f + smoothedAmp * 3f).dp.toPx())
                )

                // СЛОЙ 3: Вспышка ядра при произнесении слов
                if (smoothedAmp > 0.05f) {
                    drawPath(
                        path = capsulePath,
                        brush = coreBrush,
                        style = Stroke(width = (1f + smoothedAmp * 1.5f).dp.toPx())
                    )
                }
            }
        }

        // Основная плашка
        content()
    }
}
