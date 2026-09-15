package data.repository

import domain.repostirory.VoiceIntentRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.AVFAudio.AVAudioEngine
import platform.AVFAudio.AVAudioPCMBuffer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryRecord
import platform.AVFAudio.AVAudioSessionModeMeasurement
import platform.AVFAudio.setActive
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Speech.SFSpeechAudioBufferRecognitionRequest
import platform.Speech.SFSpeechRecognitionTask
import platform.Speech.SFSpeechRecognizer
import platform.Speech.SFSpeechRecognizerAuthorizationStatus

class IosVoiceIntentImpl : VoiceIntentRepository {

    private val audioEngine = AVAudioEngine()

    // Принудительно задаём русскую локаль для распознавания речи
    private val speechRecognizer = SFSpeechRecognizer(NSLocale("ru-RU"))

    private var recognitionRequest: SFSpeechAudioBufferRecognitionRequest? = null
    private var recognitionTask: SFSpeechRecognitionTask? = null

    private var deferredVoice: CompletableDeferred<Result<String>>? = null

    // Специфичный для платформы Scope для управления таймером тишины
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var silenceJob: Job? = null

    override suspend fun openVoice(): Result<String> = withContext(Dispatchers.Main) {
        if (!speechRecognizer.isAvailable()) {
            return@withContext Result.failure(Exception("Распознавание речи недоступно или отключено в настройках"))
        }

        val deferred = CompletableDeferred<Result<String>>()
        deferredVoice = deferred

        // Запрос разрешений на использование распознавания речи
        SFSpeechRecognizer.requestAuthorization { status ->
            scope.launch {
                if (status == SFSpeechRecognizerAuthorizationStatus.SFSpeechRecognizerAuthorizationStatusAuthorized) {
                    startRecording(deferred)
                } else {
                    deferred.complete(Result.failure(Exception("Доступ к распознаванию речи отклонен пользователем")))
                }
            }
        }

        return@withContext deferred.await()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun startRecording(deferred: CompletableDeferred<Result<String>>) {
        try {
            stopRecording()

            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setCategory(AVAudioSessionCategoryRecord, error = null)
            audioSession.setMode(AVAudioSessionModeMeasurement, error = null)
            audioSession.setActive(true, withOptions = 0u, error = null)

            // Включаем частичные результаты, чтобы таймер тишины мог анализировать текст "на лету"
            recognitionRequest = SFSpeechAudioBufferRecognitionRequest().apply {
                shouldReportPartialResults = true
            }

            val inputNode = audioEngine.inputNode
            val recordingFormat = inputNode.outputFormatForBus(0u)

            // Явно типизируем buffer как AVAudioPCMBuffer?, чтобы избежать проблем интеропа Kotlin/Native
            inputNode.installTapOnBus(0u, bufferSize = 1024u, format = recordingFormat) { buffer: AVAudioPCMBuffer?, _ ->
                if (buffer != null) {
                    recognitionRequest?.appendAudioPCMBuffer(buffer)
                }
            }

            audioEngine.prepare()
            audioEngine.startAndReturnError(null)

            var lastRecognizedText = ""

            recognitionTask = speechRecognizer.recognitionTaskWithRequest(recognitionRequest!!) { result, error ->
                if (error != null) {
                    // Если корутина уже завершилась успехом по таймеру тишины, игнорируем системную ошибку отмены сессии
                    if (!deferred.isCompleted) {
                        deferred.complete(Result.failure(Exception(error.localizedDescription)))
                    }
                    stopRecording()
                } else if (result != null) {
                    lastRecognizedText = result.bestTranscription.formattedString

                    // Сбрасываем предыдущий таймер тишины при каждом новом слове
                    silenceJob?.cancel()
                    silenceJob = scope.launch {
                        delay(1500) // Пауза в 1.5 секунды означает, что пользователь закончил говорить

                        if (lastRecognizedText.isNotBlank() && !deferred.isCompleted) {
                            deferred.complete(Result.success(lastRecognizedText))
                        } else if (!deferred.isCompleted) {
                            deferred.complete(Result.failure(Exception("Превышено время ожидания речи (тишина)")))
                        }
                        stopRecording()
                    }
                }
            }
        } catch (e: Exception) {
            if (!deferred.isCompleted) {
                deferred.complete(Result.failure(e))
            }
            stopRecording()
        }
    }

    private fun stopRecording() {
        silenceJob?.cancel()
        silenceJob = null

        if (audioEngine.isRunning()) {
            audioEngine.stop()
            audioEngine.inputNode.removeTapOnBus(0u)
        }

        recognitionRequest?.endAudio()
        recognitionRequest = null

        recognitionTask?.cancel()
        recognitionTask = null
    }

    fun destroyVoice() {
        stopRecording()
        scope.cancel()
        if (deferredVoice?.isActive == true) {
            deferredVoice?.cancel()
        }
        deferredVoice = null
    }
}