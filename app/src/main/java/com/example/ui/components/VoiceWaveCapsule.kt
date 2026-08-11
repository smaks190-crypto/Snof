package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Rose500
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.math.abs

// Цветовые акценты под стилистику вашего приложения
private val CapsuleBgColor = Slate900.copy(alpha = 0.92f)
private val StatusTextColor = Rose500
private val BorderGradientColors = listOf(
    Emerald400,
    Indigo500,
    Rose500
)

/**
 * Голосовая капсула с визуализатором звуковой волны реального времени
 */
@Composable
fun VoiceWaveCapsule(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    statusTextOverride: String? = null,
    externalAmplitudes: List<Float>? = null,
    onClose: () -> Unit = {}
) {
    val context = LocalContext.current
    var amplitudes by remember { mutableStateOf(List(32) { 0.05f }) }
    var statusText by remember { mutableStateOf("Слушаю...") }

    val activeAmplitudes = externalAmplitudes ?: amplitudes
    val activeStatus = statusTextOverride ?: statusText

    // Проверяем разрешения и запрашиваем чтение буфера микрофона
    LaunchedEffect(isVisible, externalAmplitudes) {
        if (!isVisible || externalAmplitudes != null) return@LaunchedEffect

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            statusText = "Слушаю..."
            startAudioRecording { newAmplitudes ->
                amplitudes = newAmplitudes
            }
        } else {
            statusText = "Нет доступа к микрофону"
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
            // Главная капсула с неоновой рамкой
            Box(
                modifier = Modifier
                    .weight(1f)
                    .shadow(
                        elevation = 16.dp,
                        shape = CircleShape,
                        ambientColor = Indigo500.copy(alpha = 0.5f),
                        spotColor = Emerald400.copy(alpha = 0.5f)
                    )
                    .clip(CircleShape)
                    .background(
                        brush = Brush.horizontalGradient(BorderGradientColors),
                        shape = CircleShape
                    )
                    .padding(1.5.dp) // Неоновая окантовка
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(CapsuleBgColor)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = activeStatus.uppercase(),
                        color = StatusTextColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Холст волнового эквалайзера
                    VoiceWaveCanvas(
                        amplitudes = activeAmplitudes,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                    )
                }
            }

            // Кнопка закрытия
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CapsuleBgColor)
                    .border(1.dp, Slate800, CircleShape)
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Закрыть",
                    tint = StatusTextColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Отрисовка живой сглаженной волны
 */
@Composable
fun VoiceWaveCanvas(
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

        // Отрисовка неонового свечения (Native Android Paint)
        drawIntoCanvas { canvas ->
            val nativePaint = android.graphics.Paint().apply {
                isAntiAlias = true
                style = android.graphics.Paint.Style.STROKE
                strokeWidth = 2.dp.toPx()
                setShadowLayer(
                    8.dp.toPx(),
                    0f,
                    0f,
                    android.graphics.Color.parseColor("#6366F1")
                )
            }
            canvas.nativeCanvas.drawPath(wavePath.asAndroidPath(), nativePaint)
        }

        // Основная линия волны
        drawPath(
            path = wavePath,
            brush = brush,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

/**
 * Запись буфера звука и вычисление амплитуды
 */
private suspend fun startAudioRecording(
    onAmplitudeChange: (List<Float>) -> Unit
) = withContext(Dispatchers.IO) {
    val sampleRate = 44100
    val channelConfig = AudioFormat.CHANNEL_IN_MONO
    val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    if (minBufferSize <= 0) return@withContext

    var audioRecord: AudioRecord? = null
    try {
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            minBufferSize
        )

        val buffer = ShortArray(minBufferSize / 2)
        audioRecord.startRecording()

        val pointsCount = 32
        val localAmplitudes = FloatArray(pointsCount) { 0.05f }

        while (isActive && audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            val readSize = audioRecord.read(buffer, 0, buffer.size)
            if (readSize > 0) {
                var maxVal = 0
                for (i in 0 until readSize) {
                    val absVal = abs(buffer[i].toInt())
                    if (absVal > maxVal) maxVal = absVal
                }

                val normalizedValue = (maxVal / 32768f) * 2.2f

                for (i in 0 until pointsCount - 1) {
                    localAmplitudes[i] = localAmplitudes[i + 1]
                }
                localAmplitudes[pointsCount - 1] = normalizedValue.coerceIn(0.08f, 1f)

                val currentList = localAmplitudes.toList()
                withContext(Dispatchers.Main) {
                    onAmplitudeChange(currentList)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (_: Exception) {}
    }
}
