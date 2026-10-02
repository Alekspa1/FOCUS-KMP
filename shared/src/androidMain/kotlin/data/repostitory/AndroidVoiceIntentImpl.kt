package data.repostitory

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import domain.repostirory.VoiceIntentRepository
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidVoiceIntentImpl : VoiceIntentRepository {

    private var voiceLauncher: ActivityResultLauncher<Intent>? = null
    private var deferredVoice: CancellableContinuation<Result<String>>? = null

    override suspend fun openVoice(): Result<String> {

        if (voiceLauncher == null) {
            return Result.failure(Throwable("voiceLauncher == null"))
        }

        val voiceIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        voiceIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        return suspendCancellableCoroutine { continuation ->
            // 1. Защита от двойного клика: если корутина уже висела, мягко завершаем её
            if (deferredVoice?.isActive == true) {
                deferredVoice?.resume(Result.success(""))

            }
            deferredVoice = continuation

            continuation.invokeOnCancellation {
                if (deferredVoice == continuation) {
                    deferredVoice = null
                }
            }

            try {
                voiceLauncher?.launch(voiceIntent)
            } catch (e: Exception) {
                deferredVoice = null
                if (continuation.isActive) continuation.resume(Result.failure(e))

            }


        }
    }




    fun initVoice(activity: ComponentActivity) {
        if (voiceLauncher != null) return

        voiceLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            if (it.resultCode == Activity.RESULT_OK) {
                val text = it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!text.isNullOrEmpty()) {
                    if (deferredVoice?.isActive == true) {
                        deferredVoice?.resume(Result.success(text[0]))
                    }
                } else {
                    deferredVoice?.resume(Result.success(""))
                }
            } else {
                deferredVoice?.resume(Result.success(""))
            }
            deferredVoice = null
        }
    }


    fun destroyVoice() {
        voiceLauncher?.unregister()
        voiceLauncher = null
        if (deferredVoice?.isActive == true) deferredVoice?.resume(Result.success(""))
        deferredVoice = null
    }
}