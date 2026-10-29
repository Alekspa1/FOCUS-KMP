package data.alarmReceiwer

import CommonConst.ALARM_ONE
import CommonConst.ALARM_REPEAT
import CommonConst.KEY_INTENT
import CommonConst.KEY_INTENT_ALARM
import CommonConst.KEY_INTENT_CALL_BACKREADY
import CommonConst.KEY_INTENT_CALL_POSTPONE
import CommonConst.REBOOT
import CommonConst.STATE_IS_SERVICE_TURN_ON
import CommonConst.TEN_MINUTES
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

import android.util.Log
import androidx.core.content.ContextCompat
import data.perository.MultiplatrormSettings

import data.room.CourseDao
import data.room.model.Item
import domain.repostirory.AlarmRepeadRepository
import domain.repostirory.AlarmRepository
import domain.repostirory.SharedPrefRepository
import kotlinx.coroutines.withContext
import kotlinx.coroutines.SupervisorJob

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject



import java.util.Calendar
import kotlin.getValue


class AlarmReceiwer : BroadcastReceiver(), KoinComponent {



    private val db: CourseDao by inject()
    private val notificationBuilder: NotificationBuilder by inject()
    private val notificationBuilderPassed: NotificationBuilderPassed by inject()
    private val alarm : AlarmRepository by inject ()
    private val alarmRepeat: AlarmRepeadRepository by inject ()
    private val pref: SharedPrefRepository by inject()
    private lateinit var calendarZero: Calendar


    override fun onReceive(context: Context, intent: Intent) {

        calendarZero = Calendar.getInstance()

        val pendingResult = goAsync() // тут я говорю подожди, пока не убивай ресивер, у меня там корутина
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try{
             when (intent.action) {

            KEY_INTENT_ALARM -> {
                val item = getItemFromIntent(intent, KEY_INTENT)

                withContext(Dispatchers.Main){notificationBuilder.input(item)}

               if(item.interval != ALARM_REPEAT) processingAlarm(item, "")

            } // Приход будильника

            KEY_INTENT_CALL_BACKREADY -> {
                val item = getItemFromIntent(intent, KEY_INTENT_CALL_BACKREADY)
                withContext(Dispatchers.Main){notificationBuilder.alarmPush().cancel(item.id)}
                when (item.interval) {
                    ALARM_ONE -> {
                            db.updateItem(item.copy(change = true, changeAlarm = false))
                    }
                }
                

            } // Когда нажал кнопку готово

            KEY_INTENT_CALL_POSTPONE -> {

                val time = calendarZero.timeInMillis + TEN_MINUTES
                val item = getItemFromIntent(intent, KEY_INTENT_CALL_POSTPONE)
                withContext(Dispatchers.Main){
                    notificationBuilder.alarmPush().cancel(item.id)
                }
                when (item.interval) {
                    ALARM_ONE -> {
                        val newItem = item.copy(changeAlarm = true, alarmTime = time)
                            db.updateItem(newItem)

                            alarm.createAlarm(newItem)
                    }

                    else -> {
                        val newItemFals = item.copy(
                            id = item.id*-1,
                            interval = ALARM_REPEAT,
                            alarmTime = time
                        )
                        alarm.createAlarm(newItemFals)
                    }
                }
                    withContext(Dispatchers.Main){
                        Toast.makeText(
                            context.applicationContext,
                            "Отложено на 10 минут",
                            Toast.LENGTH_SHORT
                        ).show()



                    }

            } // Когда нажал кнопку отложить


            REBOOT -> {
                    db.getActiveAlarms().forEach { item ->
                        if (item.alarmTime > calendarZero.timeInMillis) {
                            alarm.createAlarm(item)
                        }
                        else {
                            withContext(Dispatchers.Main){notificationBuilderPassed.input(item)}
                            processingAlarm(item, "(Пропущено)")
                        }
                    }

                if (pref.loadStateService() == STATE_IS_SERVICE_TURN_ON) {
                    // Создаем интент со строго определенным экшеном для запуска
                    val startIntent = Intent(context, MyService::class.java).apply {
                        action = "com.example.app.ACTION_START_SERVICE"
                        // Если нужно передать extras из Активити, передавайте только точечно:
                        // putExtra("KEY", value)
                    }
                    ContextCompat.startForegroundService(context, startIntent)
                } else {
                    // Создаем интент со строго определенным экшеном для остановки
                    val stopIntent = Intent(context, MyService::class.java).apply {
                        action = "com.example.app.ACTION_STOP_SERVICE"
                    }
                    // Для надежного выключения Foreground-сервиса лучше передать ему экшен на стоп,
                    // чтобы он сам корректно вызвал stopForeground(STOP_FOREGROUND_REMOVE) и stopSelf()
                    ContextCompat.startForegroundService(context, stopIntent)
                }



            } // После перезагрузки
        }    
            }
            
            catch(e: Exception){Log.d("MyLog", "$e -> REBOOT END")}
            
            finally{pendingResult.finish()}

            
        }

        
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
                        alarmRepeat.alarmRepead(item.id,true)

                }

            }

    } // Установка повторяющихся будильников

  private suspend fun getItemFromIntent(intent: Intent, key: String): Item {
    val rawId = intent.getIntExtra(key, 0)
    val item = if (rawId > 0) {
        db.getItemFromId(rawId)
    } else {
        db.getItemFromId(rawId * -1).copy(interval = ALARM_REPEAT)
    }
   
    return item
}


}

