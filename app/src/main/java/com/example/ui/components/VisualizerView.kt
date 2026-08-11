package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarkSlate
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Rose500
import kotlin.math.sin

/**
 * Cyberpunk Dark Neon Waveform Visualizer for VOSK voice recording.
 *
 * Renders multi-layer smooth Bezier curves and glowing audio spectrum bars driven by [rmsDb].
 */
@Composable
fun VisualizerView(
    rmsDb: Float,
    isRecording: Boolean,
    modifier: Modifier = Modifier,
    height: Dp = 64.dp,
    barCount: Int = 24,
    primaryColor: Color = Emerald400,
    secondaryColor: Color = Indigo500,
    accentColor: Color = Rose500
) {
    val normalizedAmp by animateFloatAsState(
        targetValue = if (isRecording) (rmsDb / 12f).coerceIn(0.05f, 1f) else 0.02f,
        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
        label = "normalized_amplitude"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "visualizer_wave_animation")

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    val secondaryPhase by infiniteTransition.animateFloat(
        initialValue = (Math.PI / 2).toFloat(),
        targetValue = (2.5 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "secondary_wave_phase"
    )

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    val neonGradient = remember(primaryColor, secondaryColor, accentColor) {
        Brush.horizontalGradient(
            colors = listOf(primaryColor, secondaryColor, accentColor)
        )
    }

    val glowAlpha by animateFloatAsState(
        targetValue = if (isRecording) pulseGlow else 0f,
        animationSpec = tween(300),
        label = "glow_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .shadow(
                elevation = if (isRecording) 12.dp else 2.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = secondaryColor.copy(alpha = 0.4f),
                spotColor = primaryColor.copy(alpha = 0.6f)
            )
            .background(
                color = DarkSlate.copy(alpha = 0.85f),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        primaryColor.copy(alpha = 0.3f),
                        secondaryColor.copy(alpha = 0.4f),
                        accentColor.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("visualizer_view"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val centerY = size.height / 2f
            val maxAmplitude = (size.height / 2f) * 0.85f

            if (width <= 0f || size.height <= 0f) return@Canvas

            // 1. Draw Background Glow Aura
            if (glowAlpha > 0f) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            secondaryColor.copy(alpha = 0.25f * glowAlpha),
                            primaryColor.copy(alpha = 0.15f * glowAlpha),
                            Color.Transparent
                        ),
                        center = Offset(width / 2f, centerY),
                        radius = width / 2f
                    ),
                    radius = width / 2f,
                    center = Offset(width / 2f, centerY)
                )
            }

            // 2. Draw Audio Spectrum Neon Bars
            val barWidth = (width / (barCount * 1.8f)).coerceAtLeast(3f)
            val spacing = (width - (barCount * barWidth)) / (barCount - 1).coerceAtLeast(1)

            for (i in 0 until barCount) {
                val x = i * (barWidth + spacing) + barWidth / 2f
                val fraction = i.toFloat() / (barCount - 1).coerceAtLeast(1)

                // Sine wave variation across bar spectrum
                val sineFactor = sin(phase + fraction * Math.PI * 3.5).toFloat()
                val barHeightFactor = (0.2f + 0.8f * sin(fraction * Math.PI).toFloat())
                val dynamicBarHeight = (normalizedAmp * barHeightFactor * (0.4f + 0.6f * Math.abs(sineFactor)) * maxAmplitude)
                    .coerceIn(4f, maxAmplitude)

                val barColor = when {
                    fraction < 0.4f -> primaryColor
                    fraction < 0.75f -> secondaryColor
                    else -> accentColor
                }

                // Outer soft glow line
                if (isRecording) {
                    drawLine(
                        color = barColor.copy(alpha = 0.35f * glowAlpha),
                        start = Offset(x, centerY - dynamicBarHeight - 2f),
                        end = Offset(x, centerY + dynamicBarHeight + 2f),
                        strokeWidth = barWidth + 4f,
                        cap = StrokeCap.Round
                    )
                }

                // Core neon bar
                drawLine(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            barColor.copy(alpha = if (isRecording) 1f else 0.4f),
                            barColor.copy(alpha = if (isRecording) 0.6f else 0.2f)
                        ),
                        startY = centerY - dynamicBarHeight,
                        endY = centerY + dynamicBarHeight
                    ),
                    start = Offset(x, centerY - dynamicBarHeight),
                    end = Offset(x, centerY + dynamicBarHeight),
                    strokeWidth = barWidth,
                    cap = StrokeCap.Round
                )
            }

            // 3. Draw Overlaid Smooth Bezier Sine Wave Curve
            if (isRecording) {
                drawWavePath(
                    phase = phase,
                    amplitude = normalizedAmp * maxAmplitude * 0.9f,
                    centerY = centerY,
                    strokeWidth = 3f,
                    brush = neonGradient,
                    alpha = 0.85f
                )

                drawWavePath(
                    phase = secondaryPhase,
                    amplitude = normalizedAmp * maxAmplitude * 0.5f,
                    centerY = centerY,
                    strokeWidth = 1.8f,
                    brush = Brush.horizontalGradient(listOf(accentColor, primaryColor)),
                    alpha = 0.5f
                )
            }
        }
    }
}

private fun DrawScope.drawWavePath(
    phase: Float,
    amplitude: Float,
    centerY: Float,
    strokeWidth: Float,
    brush: Brush,
    alpha: Float
) {
    val width = size.width
    val path = Path()
    val points = 32
    val step = width / points

    path.moveTo(0f, centerY)

    for (i in 0..points) {
        val x = i * step
        val angle = (i.toFloat() / points) * 3f * Math.PI + phase
        val y = centerY + sin(angle).toFloat() * amplitude

        if (i == 0) {
            path.moveTo(x, y)
        } else {
            val prevX = (i - 1) * step
            val prevAngle = ((i - 1).toFloat() / points) * 3f * Math.PI + phase
            val prevY = centerY + sin(prevAngle).toFloat() * amplitude

            val controlX1 = prevX + step / 2f
            val controlY1 = prevY
            val controlX2 = prevX + step / 2f
            val controlY2 = y

            path.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y)
        }
    }

    drawPath(
        path = path,
        brush = brush,
        alpha = alpha,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
}
