package data.repostitory
import CommonConst.ALARM_SETTINGS
import CommonConst.APP_SETTINGS
import CommonConst.BATTERY_OPTIMIZATION
import CommonConst.NOTIFICATION
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import domain.repostirory.PermissionRepository
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidPermissionImpl(
    private val context: Context,
):PermissionRepository{

    private  var pLauncher: ActivityResultLauncher<String>? = null

   private var activeContinuation: CancellableContinuation<Boolean>? = null

    override fun isChekedPermission(permissionName: String) : Boolean{

        return when(permissionName){
            NOTIFICATION->  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                isPermissionGranted(context, Manifest.permission.POST_NOTIFICATIONS)

            } else {
                true
            }
            ALARM_SETTINGS -> {
                val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_AUDIO
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }
                isPermissionGranted(context, permission)
            }
            BATTERY_OPTIMIZATION -> {
                val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                return powerManager.isIgnoringBatteryOptimizations(context.packageName)
            }

            APP_SETTINGS -> {false }
            else -> true
        }
    }


    // override suspend fun requestPermission(permissionName: String) : Boolean {
    //     // 1. Твоя родная супер-страховка: если право уже есть, вообще не трогаем лаунчеры
    //     if (isChekedPermission(permissionName)) {
    //         return true
    //     }

    //     if (pLauncher == null) return false

    //     when (permissionName) {
    //         BATTERY_OPTIMIZATION -> {
    //             val intent = getBatteryOptimizationIntent(context).apply {
    //                 addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    //             }
    //             context.startActivity(intent)
    //             return true
    //         }
    //         APP_SETTINGS -> {
    //             openAppSettingsAndReturn()
    //             return true
    //         }
    //     }


    //     return suspendCancellableCoroutine { continuation ->
           

    //         // Задаем действие на случай, если корутина будет отменена извне
    //         continuation.invokeOnCancellation {
    //             permissionCallback = null
    //         }
    //          permissionCallback?.invoke(false)
    //         // Регистрируем мост: когда лаунчер вернет ответ, корутина возобновится
    //         permissionCallback = { isGranted ->
    //             if (continuation.isActive) {
    //                 continuation.resume(isGranted)
    //             }
    //         }

    //         // 3. Запускаем лаунчер
    //         try{
    //         when (permissionName) {


    //             NOTIFICATION -> {
    //                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    //                     pLauncher?.launch(Manifest.permission.POST_NOTIFICATIONS)
    //                 } else {
    //                     permissionCallback = null
    //                     continuation.resume(true)
    //                 }
    //             }
    //             ALARM_SETTINGS -> {
    //                 val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    //                     Manifest.permission.READ_MEDIA_AUDIO
    //                 } else {
    //                     Manifest.permission.READ_EXTERNAL_STORAGE
    //                 }
    //                 pLauncher?.launch(permission)
    //             }
    //         }
    //     } catch (e: Exception) {
    //         permissionCallback = null
    //             if (continuation.isActive) continuation.resume(false)
    //         }
    //     }
    // }
    
 override suspend fun requestPermission(permissionName: String): Boolean {


        if (isChekedPermission(permissionName)) {
            return true
        }

        if (pLauncher == null) {

            return false
        }



        when (permissionName) {
            BATTERY_OPTIMIZATION -> {

                val intent = getBatteryOptimizationIntent(context).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                return true
            }
            APP_SETTINGS -> {

                openAppSettingsAndReturn()
                return true
            }
        }



        return suspendCancellableCoroutine { continuation ->
            
            // 1. Защита от двойного клика: если корутина уже висела, мягко завершаем её с false
            if (activeContinuation?.isActive == true) {
                activeContinuation?.resume(false)
            }
            
            // 2. Запоминаем текущую активную корутину
            activeContinuation = continuation

            // 3. Настраиваем логику отмены корутины извне (например, уничтожение ViewModel)
            continuation.invokeOnCancellation {
                if(activeContinuation == continuation) {
                    activeContinuation = null
                }
            }

            try {
                when (permissionName) {
                    NOTIFICATION -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            pLauncher?.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            activeContinuation = null
                            continuation.resume(true)
                        }
                    }
                    ALARM_SETTINGS -> {
                        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Manifest.permission.READ_MEDIA_AUDIO
                        } else {
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        }
                        pLauncher?.launch(permission)
                    }
                }
            } catch (e: Exception) {
                if(activeContinuation == continuation) {
                    activeContinuation = null
                }
                if (continuation.isActive) continuation.resume(false)
            }
        }
    }




    fun openAppSettingsAndReturn(){
        val manufacturer = android.os.Build.MANUFACTURER.lowercase()
        val packageName = context.packageName

        fun fallbackIntent(): Intent {
            return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

        fun tryStart(candidates: List<Intent>): Boolean {
            for (intent in candidates) {
                try {
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                        return true
                    }
                } catch (_: Exception) {
                    // пробуем следующий
                }
            }
            return false
        }

        val candidates: List<Intent> = when {
            manufacturer.contains("huawei") || manufacturer.contains("honor") -> listOf(
                Intent().apply {
                    component = android.content.ComponentName(
                        "com.huawei.systemmanager",
                        "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
                    )
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                },
                Intent().apply {
                    component = android.content.ComponentName(
                        "com.huawei.systemmanager",
                        "com.huawei.systemmanager.optimize.process.ProtectActivity"
                    )
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )

            manufacturer.contains("xiaomi") || manufacturer.contains("redmi") || manufacturer.contains("poco") -> listOf(
                Intent().apply {
                    component = android.content.ComponentName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"
                    )
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )

            manufacturer.contains("oppo") || manufacturer.contains("realme") -> listOf(
                Intent().apply {
                    component = android.content.ComponentName(
                        "com.coloros.safecenter",
                        "com.coloros.safecenter.startupapp.StartupAppListActivity"
                    )
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                },
                Intent().apply {
                    component = android.content.ComponentName(
                        "com.oppo.safe",
                        "com.oppo.safe.permission.startup.StartupAppListActivity"
                    )
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )

            manufacturer.contains("vivo") -> listOf(
                Intent().apply {
                    component = android.content.ComponentName(
                        "com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                    )
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                },
                Intent().apply {
                    component = android.content.ComponentName(
                        "com.iqoo.secure",
                        "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"
                    )
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )

            manufacturer.contains("samsung") -> listOf(
                Intent().apply {
                    component = android.content.ComponentName(
                        "com.samsung.android.lool",
                        "com.samsung.android.sm.ui.battery.BatteryActivity"
                    )
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )

            else -> listOf(fallbackIntent())
        }

        try {
            if (!tryStart(candidates)) {
                context.startActivity(fallbackIntent())
            }

        } catch (e: Exception) {
            try {
                context.startActivity(fallbackIntent())

            } catch (ex: Exception) {

            }
        }
    }


    private fun isPermissionGranted(con: Context, p: String): Boolean {
        return ContextCompat.checkSelfPermission(con, p) == PackageManager.PERMISSION_GRANTED
    }

    fun getBatteryOptimizationIntent(context: Context): Intent {
        return try {
            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
        } catch (e: Exception) {
            Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        }
    }



    fun initLauncher(activity: ComponentActivity) {
        pLauncher?.unregister()
        pLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            
            // Будим корутину напрямую через сохраненный activeContinuation
            if (activeContinuation?.isActive == true) {
                activeContinuation?.resume(isGranted)
            }
            activeContinuation = null
        }
    }

    fun destroyLaunch() {
        pLauncher?.unregister()
        pLauncher = null

        // Если Activity уничтожается, а корутина всё ещё ждала ответа — чисто закрываем её
        if (activeContinuation?.isActive == true) {
            activeContinuation?.resume(false)
        }
        activeContinuation = null
    }
}
