package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBg
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Rose500
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate900
import kotlin.math.cos
import kotlin.math.sin

/**
 * Неоновая капсула голосового ввода из HTML-макета.
 */
@Composable
fun VoiceInputNeonCapsule(
    recognizedText: String,
    statusText: String = "Слушаю...",
    assistantText: String = "Давид AI",
    isListening: Boolean = true,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Анимация пульсации внешнего свечения микрофона
    val infiniteTransition = rememberInfiniteTransition(label = "MicGlowTransition")
    val micScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "MicScale"
    )

    val neonGradient = remember {
        Brush.linearGradient(
            colors = listOf(Emerald400, Indigo500, Rose500)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(
                elevation = 20.dp,
                shape = CircleShape,
                ambientColor = Indigo500,
                spotColor = Emerald400
            )
            .clip(CircleShape)
            .background(Slate900.copy(alpha = 0.92f))
            .border(
                width = 1.5.dp,
                brush = neonGradient,
                shape = CircleShape
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // --- ЛЕВАЯ ЧАСТЬ: Иконка микрофона + Экран распознавания ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Пульсирующая иконка микрофона
                Box(
                    modifier = Modifier
                        .scale(if (isListening) micScale else 1f)
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(neonGradient)
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
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Зеленый индикатор активности
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(Emerald400)
                            .border(1.5.dp, DarkBg, CircleShape)
                    )
                }

                // Центральный блок с текстом и эквалайзером
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
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = assistantText,
                            color = Slate500,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Анимированный микро-эквалайзер
                    MiniWaveformVisualizer(
                        isListening = isListening,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .padding(vertical = 2.dp)
                    )

                    // Распознанный текст
                    Text(
                        text = if (recognizedText.isNotBlank()) "«$recognizedText»" else "Скажите сумму и категорию...",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // --- ПРАВАЯ ЧАСТЬ: Элементы управления (Галочка и Крестик) ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Кнопка галочки (Подтвердить)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .shadow(elevation = 6.dp, shape = CircleShape, spotColor = Emerald400)
                        .clip(CircleShape)
                        .background(Emerald400)
                        .clickable { onConfirm() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Подтвердить",
                        tint = DarkBg,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Кнопка закрытия (Крестик)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Rose500.copy(alpha = 0.15f))
                        .border(1.dp, Rose500.copy(alpha = 0.35f), CircleShape)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Отмена",
                        tint = Rose500,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Отрисовка живого неонового эквалайзера с помощью Compose Canvas.
 */
@Composable
fun MiniWaveformVisualizer(
    isListening: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveAnimation")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    Canvas(modifier = modifier) {
        val bars = 22
        val barWidth = size.width / bars
        val gradient = Brush.horizontalGradient(
            colors = listOf(Emerald400, Indigo500, Rose500)
        )

        for (i in 0 until bars) {
            val h = if (isListening) {
                (sin(phase + i * 0.4f) * 4f + cos(phase * 1.5f + i * 0.3f) * 2f + 5f).dp.toPx()
            } else {
                3.dp.toPx()
            }

            val x = i * barWidth + barWidth / 4f
            val y = (size.height - h) / 2f

            drawRoundRect(
                brush = gradient,
                topLeft = Offset(x, y),
                size = Size(barWidth / 2.2f, h),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
        }
    }
}
