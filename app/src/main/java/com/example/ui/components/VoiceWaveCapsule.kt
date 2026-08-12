package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.math.abs

/**
 * Цветовые константы под дизайн приложения
 */
private val CapsuleBgColor = Color(0xF20B0F19) // #0B0F19 с прозрачностью 95%
private val StatusTextColor = Color(0xFFF43F5E) // Розовый акцент
private val BorderGradientColors = listOf(
    Color(0xFF10B981), // Emerald
    Color(0xFF6366F1), // Indigo
    Color(0xFFA855F7), // Purple
    Color(0xFFF43F5E)  // Rose
)

/**
 * Основной Composable голосовой капсулы
 *
 * @param modifier Модификатор внешней разметки
 * @param isVisible Флаг видимости капсулы
 * @param statusTextCustom Дополнительный текст статуса (например, при распознавании)
 * @param externalAmplitudes Внешние амплитуды (если запись ведет сторонний менеджер)
 * @param onClose Callback при нажатии на кнопку закрытия
 */
@Composable
fun VoiceWaveCapsule(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    statusTextCustom: String? = null,
    externalAmplitudes: List<Float>? = null,
    onClose: () -> Unit = {}
) {
    val context = LocalContext.current
    var internalAmplitudes by remember { mutableStateOf(List(32) { 0.08f }) }
    var statusText by remember { mutableStateOf("Слушаю...") }

    val amplitudesToDisplay = externalAmplitudes ?: internalAmplitudes

    // Проверяем разрешение на запись аудио и запускаем внутренний слушатель микрофона,
    // если не переданы внешние амплитуды
    LaunchedEffect(isVisible, externalAmplitudes) {
        if (!isVisible) return@LaunchedEffect

        if (externalAmplitudes != null) {
            statusText = statusTextCustom ?: "Слушаю..."
            return@LaunchedEffect
        }

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            statusText = statusTextCustom ?: "Слушаю..."
            startAudioRecording { newAmplitudes ->
                internalAmplitudes = newAmplitudes
            }
        } else {
            statusText = "Нет разрешения на микрофон"
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Главный капсульный блок с градиентной рамкой
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.horizontalGradient(BorderGradientColors),
                        shape = CircleShape
                    )
                    .padding(1.5.dp) // Толщина неоновой рамки
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(CapsuleBgColor)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Текст статуса
                    Text(
                        text = statusTextCustom ?: statusText,
                        color = StatusTextColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Холст с неон-волной
                    VoiceWaveCanvas(
                        amplitudes = amplitudesToDisplay,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                    )
                }
            }

            // Кнопка закрытия (крестик)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(CapsuleBgColor)
                    .border(1.dp, Color(0xFF1E293B), CircleShape)
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Закрыть",
                    tint = StatusTextColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Компонент отрисовки сглаженной неоновой волны на Canvas
 */
@Composable
private fun VoiceWaveCanvas(
    amplitudes: List<Float>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (amplitudes.isEmpty()) return@Canvas

        val width = size.width
        val height = size.height
        val centerY = height / 2f
        val sliceWidth = width / (amplitudes.size - 1).coerceAtLeast(1)

        val wavePath = Path()
        val brush = Brush.horizontalGradient(BorderGradientColors)

        // Подготовка пути волны с использованием сглаживания
        var currentX = 0f
        wavePath.moveTo(0f, centerY)

        for (i in amplitudes.indices) {
            val amp = amplitudes[i].coerceIn(0.05f, 1f)
            val waveHeight = amp * (height / 2.2f)
            val direction = if (i % 2 == 0) 1f else -1f
            val y = centerY + (waveHeight * direction)

            if (i == 0) {
                wavePath.moveTo(currentX, centerY)
            } else {
                val prevX = currentX - sliceWidth
                val controlX = (prevX + currentX) / 2f
                wavePath.quadraticTo(prevX, y, controlX, y)
            }
            currentX += sliceWidth
        }
        wavePath.lineTo(width, centerY)

        // Отрисовка внешнего неонового свечения
        drawIntoCanvas { canvas ->
            val nativePaint = android.graphics.Paint().apply {
                isAntiAlias = true
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = 2.5.dp.toPx()
                setShadowLayer(
                    12.dp.toPx(),
                    0f,
                    0f,
                    android.graphics.Color.parseColor("#6366F1")
                )
            }

            canvas.nativeCanvas.drawPath(wavePath.asAndroidPath(), nativePaint)
        }

        // Отрисовка основной градиентной линии волны
        drawPath(
            path = wavePath,
            brush = brush,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 2.5.dp.toPx()
            )
        )
    }
}

/**
 * Вспомогательная функция для считывания громкости с микрофона
 */
private suspend fun startAudioRecording(
    onAmplitudeChange: (List<Float>) -> Unit
) = withContext(Dispatchers.IO) {
    val sampleRate = 44100
    val channelConfig = AudioFormat.CHANNEL_IN_MONO
    val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    try {
        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            minBufferSize
        )

        val buffer = ShortArray(minBufferSize / 2)
        audioRecord.startRecording()

        val pointsCount = 32
        val localAmplitudes = FloatArray(pointsCount) { 0.08f }

        while (isActive) {
            val readSize = audioRecord.read(buffer, 0, buffer.size)
            if (readSize > 0) {
                var maxVal = 0
                for (i in 0 until readSize) {
                    val absVal = abs(buffer[i].toInt())
                    if (absVal > maxVal) maxVal = absVal
                }

                val normalizedValue = (maxVal / 32768f) * 2.0f // Повышенная чувствительность

                for (i in 0 until pointsCount - 1) {
                    localAmplitudes[i] = localAmplitudes[i + 1]
                }
                localAmplitudes[pointsCount - 1] = normalizedValue.coerceIn(0.08f, 1f)

                withContext(Dispatchers.Main) {
                    onAmplitudeChange(localAmplitudes.toList())
                }
            }
        }

        audioRecord.stop()
        audioRecord.release()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}