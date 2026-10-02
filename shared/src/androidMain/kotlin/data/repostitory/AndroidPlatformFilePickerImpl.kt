package data.repostitory

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import domain.repostirory.PickerRepository
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidPlatformFilePickerImpl() : PickerRepository {

    private var importLauncher: ActivityResultLauncher<Array<String>>? = null
    private var exportLauncher: ActivityResultLauncher<String>? = null

    // Наш единый мост для активной корутины файлового пикера
    private var activeFileContinuation: CancellableContinuation<PlatformFile?>? = null

    // 1. ИМПОРТ (Выбор файла)
    override suspend fun openZipPicker(): PlatformFile? {


        if (importLauncher == null) {
            return null
        }

        return suspendCancellableCoroutine { continuation ->
            // Защита от двойного клика
            if (activeFileContinuation?.isActive == true) {
                activeFileContinuation?.resume(null)
            }

            activeFileContinuation = continuation

            continuation.invokeOnCancellation {
                if (activeFileContinuation == continuation) {
                    activeFileContinuation = null
                }
            }

            try {
                importLauncher?.launch(arrayOf("application/zip"))
            } catch (e: Exception) {
                if (activeFileContinuation == continuation) {
                    activeFileContinuation = null
                }
                if (continuation.isActive) continuation.resume(null)
            }
        }
    }

    // 2. ЭКСПОРТ (Создание файла)
    override suspend fun createZipPicker(defaultName: String): PlatformFile? {

        if (exportLauncher == null) {
            return null
        }

        return suspendCancellableCoroutine { continuation ->
            // Защита от двойного клика
            if (activeFileContinuation?.isActive == true) {
                activeFileContinuation?.resume(null)
            }

            activeFileContinuation = continuation

            continuation.invokeOnCancellation {
                if (activeFileContinuation == continuation) {
                    activeFileContinuation = null
                }
            }

            try {
                exportLauncher?.launch(defaultName)
            } catch (e: Exception) {
                if (activeFileContinuation == continuation) {
                    activeFileContinuation = null
                }
                if (continuation.isActive) continuation.resume(null)
            }
        }
    }

    fun initLauncher(activity: ComponentActivity) {
        importLauncher = activity.registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            val currentContinuation = activeFileContinuation

            // Чистим глобальное поле ТОЛЬКО если это наш текущий запрос


            val file = uri?.let { PlatformFile(it, activity) }
            if (currentContinuation?.isActive == true) {
                currentContinuation.resume(file)
            }
            activeFileContinuation = null
        }

        exportLauncher = activity.registerForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri ->
            val currentContinuation = activeFileContinuation


            val file = uri?.let { PlatformFile(it, activity) }
            if (currentContinuation?.isActive == true) {
                currentContinuation.resume(file)
            }
            activeFileContinuation = null
        }
    }

    fun destroyLauncher() {
        importLauncher?.unregister()
        exportLauncher?.unregister()
        importLauncher = null
        exportLauncher = null

        if (activeFileContinuation?.isActive == true) {
            activeFileContinuation?.resume(null) // Мягко возвращаем null при уничтожении экрана
        }
        activeFileContinuation = null
    }
}