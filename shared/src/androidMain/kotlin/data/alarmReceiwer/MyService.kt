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
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import data.room.CourseDao
import data.room.model.Item
import domain.repostirory.AlarmRepeadRepository
import domain.repostirory.AlarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
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


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        calendarZero = Calendar.getInstance()
        if (intent == null) {
            stopSelf(startId)
            return START_NOT_STICKY
        }

        notificationBuilder.alarmPush()
        startForeground(
            Int.MAX_VALUE,
            notificationBuilder.createInitialStubNotification()
        )

        serviceScope.launch {
            val item = try {
                    getItemFromIntent(intent, KEY_INTENT)
                    } catch (e: Exception) {
                        stopSelf(startId) // ⬅️ важно! иначе сервис повиснет в foreground
                    return@launch
                    }
            if (item.interval != ALARM_REPEAT) processingAlarm(item, "")
            startNotification(startId, item)
        }



        return START_NOT_STICKY
    }

    private suspend fun processingAlarm(item: Item, value: String) {

        when (item.interval) {
            ALARM_ONE -> {
                db.updateItem(
                    item.copy(
                        change = false,
                        changeAlarm = false,
                        name = "${item.name} $value".trim()
                    )
                )
            }

            else -> {
                alarmRepeat.alarmRepead(item.id)
            }
        }


    }


    private suspend fun startNotification(startId: Int, item: Item) {
        withContext(Dispatchers.Main) {
            notificationBuilder.input(item)
            stopSelf(startId)
        }
    }


    private suspend fun getItemFromIntent(intent: Intent, key: String): Item {
        val rawId = intent.getIntExtra(key, 0)
        return if (rawId > 0) {
            db.getItemFromId(rawId)
        } else {
            db.getItemFromId(rawId * -1).copy(interval = ALARM_REPEAT)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel() // Отменяем корутины, если сервис принудительно завершен
    }
}
