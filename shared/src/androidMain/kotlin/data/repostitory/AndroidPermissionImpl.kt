package data.repostitory
import CommonConst.ALARM_SETTINGS
import CommonConst.APP_SETTINGS
import CommonConst.BATTERY_OPTIMIZATION
import CommonConst.NOTIFICATION
import CommonConst.SOUND
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import domain.repostirory.PermissionRepository
import kotlinx.coroutines.CompletableDeferred


class AndroidPermissionImpl(private val context: Context):PermissionRepository{

    private  var pLauncher: ActivityResultLauncher<String>? = null
    private var deferredPermission : CompletableDeferred<Boolean>? = null

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

  
    override suspend fun requestPermission(permissionName: String) : Boolean{
        if (isChekedPermission(permissionName)) {
            return true
        }
        val reservDeferred = CompletableDeferred<Boolean>()
        deferredPermission = reservDeferred
    when(permissionName){
        NOTIFICATION ->{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pLauncher?.launch(Manifest.permission.POST_NOTIFICATIONS)
            }  else {
                return true

                        }
        }
        ALARM_SETTINGS -> {
            val permissionsToRequest =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
                } else {
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                }

            permissionsToRequest.forEach {
                pLauncher?.launch(it)
            }
        }
        BATTERY_OPTIMIZATION -> {

            val intent = getBatteryOptimizationIntent(context).apply {
                // Этот флаг позволяет запускать Activity без ссылки на текущую активити
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
                context.startActivity(intent)
            return true // Сразу возвращаем true, чтобы разблокировать корутину во ViewModel
        }

        APP_SETTINGS -> {
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
                true
            } catch (e: Exception) {
                try {
                    context.startActivity(fallbackIntent())
                    true
                } catch (ex: Exception) {
                    false
                }
            }
        }
    }
        return deferredPermission?.await() ?: true
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


    fun initLauncher(activit: ComponentActivity){
        pLauncher?.unregister()
        pLauncher = null
        pLauncher = activit.registerForActivityResult(ActivityResultContracts.RequestPermission()) {isGranted->
        deferredPermission?.complete(isGranted)
        deferredPermission = null
        }
    }

    fun destroyLaunch(){
        pLauncher?.unregister()
        pLauncher = null
        deferredPermission?.apply {
            if (isActive) cancel()
        }
        deferredPermission = null
    }
}
