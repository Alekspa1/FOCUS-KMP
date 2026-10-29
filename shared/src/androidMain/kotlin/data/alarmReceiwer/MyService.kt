package data.alarmReceiwer

import CommonConst.ALARM_ONE
import CommonConst.ALARM_REPEAT
import CommonConst.KEY_INTENT
import CommonConst.KEY_INTENT_ALARM
import CommonConst.KEY_INTENT_CALL_BACKREADY
import CommonConst.KEY_INTENT_CALL_POSTPONE
import CommonConst.REBOOT
import CommonConst.TEN_MINUTES
import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import data.room.CourseDao
import data.room.model.Item
import domain.repostirory.AlarmRepeadRepository
import domain.repostirory.AlarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.descriptors.PrimitiveKind
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Calendar


class MyService : Service(), KoinComponent {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    private val db: CourseDao by inject()
    private val notificationBuilder: NotificationBuilder by inject()
    private val alarmRepeat: AlarmRepeadRepository by inject()

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private lateinit var calendarZero: Calendar

    private var isServiceRunning = false
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "com.example.app.ACTION_STOP_SERVICE") {
            isServiceRunning = false
            stopForeground(STOP_FOREGROUND_REMOVE)
            // Важно: отпускаем процессор при выключении
            if (wakeLock?.isHeld == true) wakeLock?.release()

            stopSelf()
            return START_NOT_STICKY
        }

        if (intent?.action == "com.example.app.ACTION_RECREATE_NOTIFICATION") {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            // Перерисовываем уведомление с тем же ID
            notificationManager.notify(Int.MAX_VALUE, notificationBuilder.createInitialStubNotification())
            return START_STICKY
        }

        if (!isServiceRunning) {
            notificationBuilder.createChanelStub()
            startForeground(Int.MAX_VALUE, notificationBuilder.createInitialStubNotification())
            isServiceRunning = true

            // ВКЛЮЧАЕМ ЖЕЛЕЗНУЮ БОДРСТВЕННОСТЬ ПРОЦЕССОРА
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyService::WakeLockTag").apply {
                acquire(Long.MAX_VALUE) // Процессор больше не уснет
            }
            var count = 0
            var way = true
            serviceScope.launch {
                while (isActive) {
                    delay(60000L)
                    if(way) count++ else count--
                   if (count == 10) way = false
                   if (count == 0) way = true
                    Log.d("MyLog", count.toString())
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        serviceJob.cancel()
        // На всякий случай освобождаем замок и тут
        if (wakeLock?.isHeld == true) wakeLock?.release()
        isServiceRunning = false
        super.onDestroy()
    }
}
