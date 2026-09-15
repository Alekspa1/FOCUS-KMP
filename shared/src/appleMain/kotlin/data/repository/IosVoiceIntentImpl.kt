package data.repository

import domain.repostirory.VoiceIntentRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.AVFAudio.AVAudioEngine
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

    private val audioEngine = AVAudioEngine() // Управляет аудио-потоком с микрофона
    private val speechRecognizer =
        SFSpeechRecognizer(NSLocale.currentLocale) // Нативный распознаватель
    private var recognitionRequest: SFSpeechAudioBufferRecognitionRequest? = null
    private var recognitionTask: SFSpeechRecognitionTask? = null

    private var deferredVoice: CompletableDeferred<Result<String>>? = null

    override suspend fun openVoice(): Result<String> = withContext(Dispatchers.Main) {
        // Сразу проверяем доступность распознавателя
        if (speechRecognizer == null || !speechRecognizer.isAvailable()) {
            return@withContext Result.failure(Exception("Распознавание речи недоступно"))
        }

        val deferred = CompletableDeferred<Result<String>>()
        deferredVoice = deferred

        // Запрашиваем разрешения у пользователя
        SFSpeechRecognizer.requestAuthorization { status ->
            if (status == SFSpeechRecognizerAuthorizationStatus.SFSpeechRecognizerAuthorizationStatusAuthorized) {
                // Если разрешено — запускаем запись
                startRecording(deferred)
            } else {
                deferred.complete(Result.failure(Exception("Доступ к распознаванию речи отклонен")))
            }
        }

        return@withContext deferred.await()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun startRecording(deferred: CompletableDeferred<Result<String>>) {
        try {
            // Сбрасываем старые задачи, если они были
            stopRecording()

            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setCategory(AVAudioSessionCategoryRecord, error = null)
            audioSession.setMode(AVAudioSessionModeMeasurement, error = null)
            audioSession.setActive(true, withOptions = 0u, error = null)

            recognitionRequest = SFSpeechAudioBufferRecognitionRequest().apply {
                shouldReportPartialResults = false // Нам нужен только финальный текст, как на Android
            }

            val inputNode = audioEngine.inputNode
            val recordingFormat = inputNode.outputFormatForBus(0u)

            // Устанавливаем «ответвитель» (Tap) на микрофон для передачи буфера в Apple Speech
            inputNode.installTapOnBus(0u, bufferSize = 1024u, format = recordingFormat) { buffer, _ ->
                recognitionRequest?.appendAudioPCMBuffer(buffer!!)
            }

            audioEngine.prepare()
            audioEngine.startAndReturnError(null)

            // Запуск сессии распознавания речи
            recognitionTask = speechRecognizer.recognitionTaskWithRequest(recognitionRequest!!) { result, error ->
                if (error != null) {
                    deferred.complete(Result.failure(Exception(error.localizedDescription)))
                    stopRecording()
                } else if (result != null) {
                    if (result.isFinal()) {
                        val text = result.bestTranscription.formattedString
                        deferred.complete(Result.success(text))
                        stopRecording()
                    }
                }
            }
        } catch (e: Exception) {
            deferred.complete(Result.failure(e))
            stopRecording()
        }
    }

    private fun stopRecording() {
        audioEngine.stop()
        audioEngine.inputNode.removeTapOnBus(0u)
        recognitionRequest?.endAudio()
        recognitionRequest = null
        recognitionTask?.cancel()
        recognitionTask = null
    }

    // Вызывать при уничтожении экрана / прерывании операции
    fun destroyVoice() {
        stopRecording()
        if (deferredVoice?.isActive == true) {
            deferredVoice?.cancel()
        }
        deferredVoice = null
    }
}