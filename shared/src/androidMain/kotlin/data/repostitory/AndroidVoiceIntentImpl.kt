package data.repostitory

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import domain.repostirory.VoiceIntentRepository
import kotlinx.coroutines.CompletableDeferred

class AndroidVoiceIntentImpl : VoiceIntentRepository {

    private var voiceLauncher: ActivityResultLauncher<Intent>? = null
    private var deferredVoice: CompletableDeferred<Result<String>>? = null

    override suspend fun openVoice(): Result<String> {
        val voiceIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        voiceIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        val deferred = CompletableDeferred<Result<String>>()
        deferredVoice = deferred
        try {
            voiceLauncher?.launch(voiceIntent)
        } catch (e: Exception) {
            deferredVoice?.complete(Result.failure(e))
            deferredVoice = null
            return Result.failure(e)
        }


       return deferred.await()
    }




    fun initVoice(activity: ComponentActivity) {
        voiceLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult() )   {
            if (it.resultCode == Activity.RESULT_OK) {
                val text = it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!text.isNullOrEmpty()) {
                    deferredVoice?.complete(Result.success(text[0]))
                    deferredVoice = null
                } else {
                    deferredVoice?.complete(Result.failure(Exception("Речь не распознана или данные пусты")))
                }
            } else {

                deferredVoice?.complete(Result.failure(Exception("Голосовой ввод отменен пользователем")))
            }
            deferredVoice = null

        }
    }


    fun destroyVoice() {
        voiceLauncher?.unregister()
        voiceLauncher = null
        if (deferredVoice?.isActive == true) deferredVoice?.cancel()
        deferredVoice = null
    }
}