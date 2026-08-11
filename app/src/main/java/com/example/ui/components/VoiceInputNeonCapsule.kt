package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================
// Неоновые Цветовые Константы
// ==========================================
private val Emerald400 = Color(0xFF34D399)
private val Indigo500 = Color(0xFF6366F1)
private val Rose500 = Color(0xFFF43F5E)
private val Slate900 = Color(0xFF0F172A)
private val Slate800 = Color(0xFF1E293B)
private val DarkBg = Color(0xFF0B0F19)

private val NeonSweepGradient = Brush.sweepGradient(
    colors = listOf(Emerald400, Indigo500, Rose500, Emerald400)
)

private val NeonLinearGradient = Brush.horizontalGradient(
    colors = listOf(Emerald400, Indigo500, Rose500)
)

/**
 * 🌊 Новый Нейро-Визуализатор Голоса в стиле Neon Cyberpunk
 */
@Composable
fun VoiceInputNeuralVisualizer(
    audioLevel: Float = 0.5f,
    isListening: Boolean = true,
    statusText: String = "Слушаю...",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "NeuralPhaseTransition")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f * 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PhaseValue"
    )

    val animatedAudioLevel by animateFloatAsState(
        targetValue = if (isListening) audioLevel.coerceIn(0.12f, 1f) else 0.04f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "AudioLevelAnim"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(30.dp),
                    ambientColor = Indigo500.copy(alpha = 0.5f),
                    spotColor = Emerald400.copy(alpha = 0.5f)
                )
                .clip(RoundedCornerShape(30.dp))
                .background(Slate900.copy(alpha = 0.88f))
                .border(
                    width = 1.5.dp,
                    brush = NeonLinearGradient,
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Мягкое внутреннее неон-свечение
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1f + animatedAudioLevel * 0.2f)
                    .blur(24.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Indigo500.copy(alpha = 0.45f * animatedAudioLevel),
                                Emerald400.copy(alpha = 0.25f * animatedAudioLevel),
                                Rose500.copy(alpha = 0.15f * animatedAudioLevel),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Анимированные многослойные волны на Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            ) {
                val width = size.width
                val height = size.height
                val centerY = height / 2f

                val waveLayers = listOf(
                    Triple(Emerald400, 1.0f, 2.4.dp.toPx()),
                    Triple(Indigo500, 0.7f, 1.8.dp.toPx()),
                    Triple(Rose500, 0.4f, 1.4.dp.toPx())
                )

                waveLayers.forEachIndexed { index, (color, speedMult, strokeWidth) ->
                    val path = Path()
                    val wavePhase = phase * speedMult + (index * 1.3f)
                    val maxAmplitude = (10.dp.toPx() + (animatedAudioLevel * 18.dp.toPx())) * (1f - index * 0.18f)

                    path.moveTo(0f, centerY)

                    var x = 0f
                    val step = 3f
                    while (x <= width) {
                        val normalX = x / width
                        val envelope = Math.sin(normalX * Math.PI).toFloat()

                        val y = centerY + (
                            Math.sin(normalX * 3.2 * Math.PI + wavePhase).toFloat() * 0.65f +
                            Math.sin(normalX * 6.8 * Math.PI - wavePhase * 1.4f).toFloat() * 0.35f
                        ) * maxAmplitude * envelope

                        path.lineTo(x, y)
                        x += step
                    }

                    drawPath(
                        path = path,
                        color = color.copy(alpha = if (isListening) 0.9f else 0.25f),
                        style = Stroke(width = strokeWidth)
                    )
                }
            }

            // Компактный индикатор статуса вверху
            Surface(
                shape = CircleShape,
                color = DarkBg.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isListening) Emerald400 else Color.Gray)
                    )
                    Text(
                        text = statusText.uppercase(),
                        color = if (isListening) Emerald400 else Color.Gray,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

/**
 * 🎙️ Обновлённая Неоновая Капсула Голосового Ввода
 */
@Composable
fun VoiceInputNeonCapsule(
    recognizedText: String,
    statusText: String = "Слушаю...",
    assistantText: String = "Давид AI",
    isListening: Boolean = true,
    audioLevel: Float = 0.5f,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "MicGlowTransition")
    val micPulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "MicScaleAnim"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 22.dp,
                shape = CircleShape,
                ambientColor = Indigo500.copy(alpha = 0.6f),
                spotColor = Emerald400.copy(alpha = 0.6f)
            )
            .clip(CircleShape)
            .background(Slate900.copy(alpha = 0.94f))
            .border(
                width = 1.5.dp,
                brush = NeonLinearGradient,
                shape = CircleShape
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Аватар / Иконка микрофона с неоновым двойным кольцом
                Box(
                    modifier = Modifier
                        .scale(if (isListening) micPulseScale else 1f)
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(NeonSweepGradient)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(DarkBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Микрофон",
                            tint = Emerald400,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Онлайн индикатор на аватаре
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Emerald400)
                            .border(1.5.dp, DarkBg, CircleShape)
                    )
                }

                // Информационный блок капсулы
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = statusText.uppercase(),
                            color = Emerald400,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.8.sp
                        )
                        Surface(
                            shape = CircleShape,
                            color = Slate800,
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                        ) {
                            Text(
                                text = assistantText,
                                color = Color.LightGray,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                            )
                        }
                    }

                    // Встроенный нейро-эквалайзер
                    VoiceInputNeuralVisualizer(
                        audioLevel = audioLevel,
                        isListening = isListening,
                        statusText = statusText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    )

                    // Распознанный текст с неоновым выделением цифр
                    val annotatedString = buildAnnotatedString {
                        if (recognizedText.isNotBlank()) {
                            append("«")
                            val words = recognizedText.split(" ")
                            words.forEachIndexed { idx, word ->
                                if (word.any { it.isDigit() }) {
                                    withStyle(style = SpanStyle(color = Rose500, fontWeight = FontWeight.ExtraBold)) {
                                        append(word)
                                    }
                                } else {
                                    withStyle(style = SpanStyle(color = Color.White)) {
                                        append(word)
                                    }
                                }
                                if (idx < words.size - 1) append(" ")
                            }
                            append("»")
                        } else {
                            withStyle(style = SpanStyle(color = Color.Gray)) {
                                append("Скажите сумму и категорию...")
                            }
                        }
                    }

                    Text(
                        text = annotatedString,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Кнопки управления (Подтверждение / Закрытие)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Зеленая кнопка Подтвердить
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            spotColor = Emerald400,
                            ambientColor = Emerald400
                        )
                        .clip(CircleShape)
                        .background(Emerald400)
                        .clickable { onConfirm() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Подтвердить",
                        tint = DarkBg,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Красная кнопка Закрыть
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Rose500.copy(alpha = 0.15f))
                        .border(1.dp, Rose500.copy(alpha = 0.4f), CircleShape)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Отмена",
                        tint = Rose500,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}