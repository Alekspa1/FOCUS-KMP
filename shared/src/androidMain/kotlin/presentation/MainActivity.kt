package presentation

import StartApp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import data.repostitory.AndroidPermissionImpl
import org.koin.android.ext.android.inject
import MainViewModel
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.ViewGroup
import data.repostitory.AndroidPlatformFilePickerImpl
import data.repostitory.AndroidVoiceIntentImpl



class MainActivity : ComponentActivity() {

    private val permissionImp: AndroidPermissionImpl by inject()
    private val mainViewModel: MainViewModel by inject()
    private val filePickerImp: AndroidPlatformFilePickerImpl by inject()
    private val openVoiceImpl : AndroidVoiceIntentImpl by inject()

    private var wasLocked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionImp.initLauncher(this@MainActivity)
        filePickerImp.initLauncher(this@MainActivity)
        openVoiceImpl.initVoice(this@MainActivity)

        if (savedInstanceState == null) {
            val intent = Intent(this@MainActivity, WarmupActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            this@MainActivity.startActivity(intent)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                this@MainActivity.overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
            } else {
                @Suppress("DEPRECATION")
                this@MainActivity.overridePendingTransition(0, 0)
            }
        }
        setContent {
            StartApp()
        }

        handleSharedIntent(intent)
        handleNotificationIntent(intent)
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleSharedIntent(intent)
        handleNotificationIntent(intent)
    }

    override fun onStop() {
        super.onStop()
        wasLocked = true
    }

    override fun onResume() {
        super.onResume()
        forceRenderReset()

        // Делаем сброс только на Android 9–10 (API 28–29), где есть баг с Surface
        if (wasLocked && Build.VERSION.SDK_INT in 28..29) {
            forceRenderReset()
            wasLocked = false
        } else {
            // На других версиях просто сбрасываем флаг, чтобы не накапливать состояние
            wasLocked = false
        }
    }



    private fun forceRenderReset() {
        window.decorView.post {
            val dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar).apply {
                setContentView(View(this@MainActivity).apply {
                    layoutParams = ViewGroup.LayoutParams(0, 0)
                })
                window?.setDimAmount(0f)
            }
            dialog.show()
            dialog.window?.decorView?.postDelayed({ dialog.dismiss() }, 200)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        permissionImp.destroyLaunch()
        filePickerImp.destroyLauncher()
        openVoiceImpl.destroyVoice()
    }



    private fun handleNotificationIntent(intent: Intent?) {
        if (intent == null) return
        val taskId = intent.getIntExtra("TASK_ID", -1)
        if (taskId != -1) {
            mainViewModel.openDialogByTaskId(taskId)
        }
    }

    private fun handleSharedIntent(intent: Intent?) {
        if (intent == null || intent.action != Intent.ACTION_SEND) return
        val type = intent.type ?: return

        when {
            type.startsWith("text/") -> {
                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (!sharedText.isNullOrBlank()) {
                    // Защита: очищаем ClipData интента, чтобы KMP-слой не пытался прочитать скрытые медиа-ссылки сайтов
                    val cleanText = sharedText.trim().replace("\r", "")
                    mainViewModel.openDialogWithSharedData(text = cleanText, imageUri = null)
                }
            }

            type.startsWith("image/") -> {
                val imageUri: Uri? = intent.getParcelableExtra(Intent.EXTRA_STREAM)
                imageUri?.let { uri ->
                    try {
                        // 1. Пытаемся закрепить права на чтение.
                        // Это то, для чего нужен был флаг!
                        contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (e: Exception) {

                    }

                    // 2. Отправляем во ViewModel
                    mainViewModel.openDialogWithSharedData(text = null, imageUri = uri.toString())
                }
            }
        }
    }

}
