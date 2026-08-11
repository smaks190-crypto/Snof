package com.example.utils

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.vosk.Model
import org.vosk.Recognizer
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipInputStream
import kotlin.math.sqrt

class VoiceInputManager(private val context: Context) {
    private var voskModel: Model? = null
    private var voskRecognizer: Recognizer? = null
    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText.asStateFlow()

    private val _partialText = MutableStateFlow("")
    val partialText: StateFlow<String> = _partialText.asStateFlow()

    private val _rmsDb = MutableStateFlow(0f)
    val rmsDb: StateFlow<Float> = _rmsDb.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    // VOSK status
    private val _voskStatus = MutableStateFlow("") // "", "DOWNLOADING", "EXTRACTING", "READY", "ERROR"
    val voskStatus: StateFlow<String> = _voskStatus.asStateFlow()

    private val _voskProgress = MutableStateFlow<Float?>(null)
    val voskProgress: StateFlow<Float?> = _voskProgress.asStateFlow()

    private var accumulatedText = ""
    private var isContinuous = false
    private var isPaused = false
    @Volatile private var isProcessingAllowed = true
    private var lastProcessedChunk = ""

    var onErrorCallback: (() -> Unit)? = null
    var onChunkRecognized: ((String) -> Unit)? = null

    private fun parsePartialHypothesis(json: String): String {
        return try {
            val regex = "\"partial\"\\s*:\\s*\"([^\"]*)\"".toRegex()
            regex.find(json)?.groupValues?.get(1) ?: ""
        } catch (_: Throwable) { "" }
    }

    private fun parseResultHypothesis(json: String): String {
        return try {
            val regex = "\"text\"\\s*:\\s*\"([^\"]*)\"".toRegex()
            regex.find(json)?.groupValues?.get(1) ?: ""
        } catch (_: Throwable) { "" }
    }

    fun startListening(callerContext: Context) {
        GlobalConsoleLogger.i("VOSK", "Запуск VOSK прослушивания...")
        isContinuous = true
        isPaused = false
        isProcessingAllowed = true
        lastProcessedChunk = ""

        accumulatedText = ""
        _recognizedText.value = ""
        _partialText.value = ""
        _errorState.value = null

        val targetDir = File(context.filesDir, "vosk-model-small-ru-0.22")
        if (targetDir.exists() && targetDir.isDirectory && targetDir.list()?.isNotEmpty() == true) {
            _voskStatus.value = "READY"
            GlobalConsoleLogger.i("VOSK", "Найдена локальная офлайн-модель VOSK")
            initVoskAndStart()
        } else {
            GlobalConsoleLogger.i("VOSK", "Модель VOSK не найдена локально, запускаем загрузку")
            downloadAndInitModel()
        }
    }

    fun startListening() {
        startListening(context)
    }

    private fun downloadAndInitModel() {
        _voskStatus.value = "DOWNLOADING"
        _voskProgress.value = 0f
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("https://alphacephei.com/vosk/models/vosk-model-small-ru-0.22.zip")
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 20000
                connection.readTimeout = 20000
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    throw Exception("Ошибка загрузки модели с сервера VOSK code: ${connection.responseCode}")
                }

                val fileLength = connection.contentLength
                val inputStream = BufferedInputStream(connection.inputStream)
                val tempZip = File(context.cacheDir, "vosk_model.zip")
                val outputStream = FileOutputStream(tempZip)

                val data = ByteArray(8192)
                var total: Long = 0
                var count: Int
                while (inputStream.read(data).also { count = it } != -1) {
                    if (isPaused || !isContinuous) {
                        outputStream.close()
                        inputStream.close()
                        tempZip.delete()
                        _voskStatus.value = ""
                        _voskProgress.value = null
                        return@launch
                    }
                    total += count
                    if (fileLength > 0) {
                        _voskProgress.value = total.toFloat() / fileLength
                    }
                    outputStream.write(data, 0, count)
                }
                outputStream.flush()
                outputStream.close()
                inputStream.close()

                _voskStatus.value = "EXTRACTING"
                _voskProgress.value = null

                val buffer = ByteArray(8192)
                ZipInputStream(BufferedInputStream(tempZip.inputStream())).use { zis ->
                    var entry = zis.nextEntry
                    while (entry != null) {
                        val newFile = File(context.filesDir, entry.name)
                        if (entry.isDirectory) {
                            newFile.mkdirs()
                        } else {
                            newFile.parentFile?.mkdirs()
                            FileOutputStream(newFile).use { fos ->
                                var len: Int
                                while (zis.read(buffer).also { len = it } > 0) {
                                    fos.write(buffer, 0, len)
                                }
                            }
                        }
                        zis.closeEntry()
                        entry = zis.nextEntry
                    }
                }
                tempZip.delete()

                _voskStatus.value = "READY"
                withContext(Dispatchers.Main) {
                    initVoskAndStart()
                }
            } catch (e: Exception) {
                _voskStatus.value = "ERROR"
                _voskProgress.value = null
                _errorState.value = "Не удалось загрузить модель VOSK: ${e.localizedMessage}"
                GlobalConsoleLogger.e("VOSK", "Vosk download error: ${e.localizedMessage}", e)
            }
        }
    }

    private fun initVoskAndStart() {
        try {
            GlobalConsoleLogger.i("VOSK", "Инициализация офлайн-модели VOSK...")
            val targetDir = File(context.filesDir, "vosk-model-small-ru-0.22")
            if (voskModel == null) {
                voskModel = Model(targetDir.absolutePath)
            }
            if (voskRecognizer == null) {
                voskRecognizer = Recognizer(voskModel, 16000f)
            }

            startVoskAudioRecording()
        } catch (e: Throwable) {
            GlobalConsoleLogger.e("VOSK", "Ошибка инициализации VOSK: ${e.localizedMessage}", e)
            _errorState.value = "Ошибка запуска VOSK: ${e.localizedMessage}"
        }
    }

    @SuppressLint("MissingPermission")
    private fun startVoskAudioRecording() {
        stopVoskAudioRecording()

        val recognizer = voskRecognizer ?: return
        val sampleRate = 16000
        val minBufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val bufferSize = maxOf(minBufferSize, 4096)

        try {
            val record = AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            if (record.state != AudioRecord.STATE_INITIALIZED) {
                _errorState.value = "Не удалось инициализировать микрофон для VOSK"
                _isListening.value = false
                return
            }

            audioRecord = record
            record.startRecording()
            _isListening.value = true
            _errorState.value = null

            recordingJob = CoroutineScope(Dispatchers.IO).launch {
                val buffer = ShortArray(bufferSize / 2)
                while (isContinuous && !isPaused && record.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    val readCount = record.read(buffer, 0, buffer.size)
                    if (readCount > 0 && isProcessingAllowed) {
                        // 1. Вычисляем RMS громкость для визуализации неона
                        var sum = 0.0
                        for (i in 0 until readCount) {
                            val sample = buffer[i].toDouble()
                            sum += sample * sample
                        }
                        val rms = sqrt(sum / readCount)
                        
                        // Приводим громкость к диапазону 0..12 dB для совместимости с интерфейсом
                        val rmsDbValue = (rms / 250.0).toFloat().coerceIn(0f, 12f)
                        _rmsDb.value = rmsDbValue

                        // 2. Передаем аудиопоток в Vosk
                        if (recognizer.acceptWaveForm(buffer, readCount)) {
                            val resultJson = recognizer.result
                            val text = parseResultHypothesis(resultJson).trim()
                            if (text.isNotBlank() && text != lastProcessedChunk) {
                                lastProcessedChunk = text
                                GlobalConsoleLogger.d("VOSK", "Распознан фрагмент: «$text»")
                                _recognizedText.value = text
                                _partialText.value = ""
                                withContext(Dispatchers.Main) {
                                    onChunkRecognized?.invoke(text)
                                }
                            }
                        } else {
                            val partialJson = recognizer.partialResult
                            val partial = parsePartialHypothesis(partialJson).trim()
                            if (partial.isNotBlank()) {
                                _partialText.value = partial
                            }
                        }
                    }
                }
                _rmsDb.value = 0f
            }
        } catch (e: Exception) {
            GlobalConsoleLogger.e("VOSK", "Ошибка запуска записи VOSK: ${e.localizedMessage}", e)
            _errorState.value = "Ошибка записи микрофона: ${e.localizedMessage}"
            _isListening.value = false
        }
    }

    private fun stopVoskAudioRecording() {
        recordingJob?.cancel()
        recordingJob = null

        try {
            audioRecord?.apply {
                if (recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    stop()
                }
                release()
            }
        } catch (_: Throwable) {}
        audioRecord = null

        _isListening.value = false
        _rmsDb.value = 0f

        voskRecognizer?.let { recognizer ->
            try {
                val finalJson = recognizer.finalResult
                val text = parseResultHypothesis(finalJson).trim()
                if (text.isNotBlank() && text != lastProcessedChunk) {
                    lastProcessedChunk = text
                    _recognizedText.value = text
                    _partialText.value = ""
                    onChunkRecognized?.invoke(text)
                }
            } catch (_: Throwable) {}
        }
    }

    fun stopListening() {
        GlobalConsoleLogger.i("VOSK", "Остановка распознавания речи VOSK")
        isProcessingAllowed = false
        isContinuous = false
        isPaused = false
        stopVoskAudioRecording()
    }

    fun pauseListening() {
        if (isContinuous) {
            isPaused = true
            stopVoskAudioRecording()
        }
    }

    fun resumeListening() {
        if (isContinuous && isPaused) {
            isPaused = false
            startListening()
        }
    }

    fun clear() {
        accumulatedText = ""
        _recognizedText.value = ""
        _partialText.value = ""
        _errorState.value = null
    }

    fun setRecognizedTextManually(text: String) {
        accumulatedText = text
        _recognizedText.value = text
        _partialText.value = ""
    }

    fun destroy() {
        stopListening()
        try {
            voskRecognizer?.close()
            voskModel?.close()
        } catch (_: Throwable) {}
        voskRecognizer = null
        voskModel = null
    }
}
