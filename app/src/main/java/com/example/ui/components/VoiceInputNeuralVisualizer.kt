package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Rose500
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate900
import kotlin.math.sin

/**
 * Флагманский Neural Expressive UI визуал голосового ввода.
 * @param audioLevel Уровень громкости от 0.0f до 1.0f (можно передавать из амплитуды микрофона)
 * @param isListening Состояние активности слушателя
 */
@Composable
fun VoiceInputNeuralVisualizer(
    audioLevel: Float = 0.5f,
    isListening: Boolean = true,
    statusText: String = "Слушаю...",
    modifier: Modifier = Modifier
) {
    // Бесконечная фаза движения волны
    val infiniteTransition = rememberInfiniteTransition(label = "NeuralPhase")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f * 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PhaseSpec"
    )

    // Плавное сглаживание уровня звука для органика-эффекта
    val animatedAudioLevel by animateFloatAsState(
        targetValue = if (isListening) audioLevel.coerceIn(0.1f, 1f) else 0.05f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "AudioLevelAnim"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // --- 1. КВАНТОВОЕ СВЕЧЕНИЕ И ЖИВАЯ НЕОНОВАЯ ВОЛНА ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Slate900.copy(alpha = 0.75f))
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Emerald400.copy(alpha = 0.3f),
                            Indigo500.copy(alpha = 0.5f),
                            Rose500.copy(alpha = 0.3f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Фоновый блюр-ореол (Aura Glow)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1f + animatedAudioLevel * 0.15f)
                    .blur(20.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Indigo500.copy(alpha = 0.35f * animatedAudioLevel),
                                Emerald400.copy(alpha = 0.2f * animatedAudioLevel),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Холст с трёхслойными органическими синусоидами
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                val width = size.width
                val height = size.height
                val centerY = height / 2f

                // Цвета волн ИИ
                val waveColors = listOf(
                    Emerald400 to 1.0f,
                    Indigo500 to 0.7f,
                    Rose500 to 0.5f
                )

                waveColors.forEachIndexed { index, (color, speedMult) ->
                    val path = Path()
                    val wavePhase = phase * speedMult + (index * 1.2f)
                    val baseAmplitude = (10.dp.toPx() + (animatedAudioLevel * 18.dp.toPx())) * (1f - index * 0.2f)

                    path.moveTo(0f, centerY)

                    var x = 0f
                    val step = 4f
                    while (x <= width) {
                        // Потухание волны по краям (Envelope effect)
                        val normalX = x / width
                        val envelope = sin(normalX * Math.PI).toFloat()

                        // Формула сложной гармоники
                        val y = centerY + (
                            sin(normalX * 3.5 * Math.PI + wavePhase).toFloat() * 0.7f +
                            sin(normalX * 7.0 * Math.PI - wavePhase * 1.3f).toFloat() * 0.3f
                        ) * baseAmplitude * envelope

                        path.lineTo(x, y)
                        x += step
                    }

                    drawPath(
                        path = path,
                        color = color.copy(alpha = if (isListening) 0.85f else 0.3f),
                        style = Stroke(
                            width = (2.5f - index * 0.5f).dp.toPx()
                        )
                    )
                }
            }

            // Микро-индикатор статуса по центру над волной
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isListening) Emerald400 else Slate400)
                )
                Text(
                    text = statusText.uppercase(),
                    color = if (isListening) Emerald400 else Slate400,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
