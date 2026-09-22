package data.alarmReceiwer

import CommonConst.CHANNEL_ID
import CommonConst.CHANNEL_ID_PASSED
import CommonConst.KEY_INTENT_CALL_BACKREADY
import CommonConst.KEY_INTENT_CALL_POSTPONE
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import coil3.toCoilUri
import com.exampl3.flashlight.R
import data.perository.MultiplatrormAppSettings
import data.room.model.Item
import domain.repostirory.SaveDeleteImageRepositpry
import flashlight.shared.generated.resources.Res
import flashlight.shared.generated.resources.icon
import presentation.MainActivity


class NotificationBuilder(
    private val context: Context,
    private val image: SaveDeleteImageRepositpry,
    val settings: MultiplatrormAppSettings
) {
    val newRingtoneUri: Uri = settings.getUriAlarm().toUri()

    val oldRingtoneUri: Uri = settings.getOldUriAlarm().toUri()

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val atrubute =
        AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()


    fun createInitialStubNotification(): Notification {
        return NotificationCompat.Builder(context, Int.MAX_VALUE.toString())
            .setSmallIcon(R.drawable.icon)
            .setContentTitle("Служба будильников")
            .setContentText("Уведомления активны")
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }


    fun input(item: Item){
        alarmPush().notify(item.id, notificationBuilder(item))

    }

    fun alarmPush(): NotificationManager {
        if (notificationManager.getNotificationChannel(CHANNEL_ID) != null) {
            notificationManager.deleteNotificationChannel(CHANNEL_ID)
        }

        if(notificationManager.getNotificationChannel(Int.MAX_VALUE.toString()) == null){
           notificationManager.createNotificationChannel(createCnanellStub())
        }

        if (newRingtoneUri != oldRingtoneUri) {

        if (notificationManager.getNotificationChannel(oldRingtoneUri.toString()) != null ) {
            notificationManager.deleteNotificationChannel(oldRingtoneUri.toString())
        }
            notificationManager.createNotificationChannel(createChanel(atrubute))
        }

        if (notificationManager.getNotificationChannel(newRingtoneUri.toString()) == null ) {
            notificationManager.createNotificationChannel(createChanel(atrubute))
        }

        settings.saveOldUriAlarm(newRingtoneUri.toString())

        return notificationManager
    }

    private fun createChanel(atrubute: AudioAttributes): NotificationChannel {

        val pattern = longArrayOf(0, 1000, 500, 1000, 500)
    return  NotificationChannel(
        newRingtoneUri.toString(),
        context.getString(R.string.app_name),
        NotificationManager.IMPORTANCE_HIGH
    ).apply {

        setSound(newRingtoneUri, atrubute)
        enableVibration(true)
        vibrationPattern = pattern
        setBypassDnd(true)
        lockscreenVisibility = Notification.VISIBILITY_PRIVATE
    }
    }

    private fun createCnanellStub() : NotificationChannel{

        return NotificationChannel(
            Int.MAX_VALUE.toString(),
            //"Заглушка", NotificationManager.IMPORTANCE_HIGH
            "Заглушка", NotificationManager.IMPORTANCE_LOW
        )
    }




     fun notificationBuilder(item: Item): Notification {

        val intentCancel = Intent(context, AlarmReceiwer::class.java)
        intentCancel.setAction(KEY_INTENT_CALL_BACKREADY)
        intentCancel.putExtra(KEY_INTENT_CALL_BACKREADY, item.id)

        val canselIntent =
            PendingIntent.getBroadcast(
                context, item.id, intentCancel,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val intentPostpone = Intent(context, AlarmReceiwer::class.java)
        intentPostpone.setAction(KEY_INTENT_CALL_POSTPONE)
        intentPostpone.putExtra(KEY_INTENT_CALL_POSTPONE, item.id)

        val postponeIntent =
            PendingIntent.getBroadcast(
                context, item.id, intentPostpone,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )


        val intentPush = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW 
            putExtra("TASK_ID", item.id)
            }
         val intentOpenActivity = Intent(context, MainActivity::class.java).apply {
             action = Intent.ACTION_VIEW
         }

        val contentIntent =
            PendingIntent.getActivity(
                context, item.id, intentPush,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

         val fullScreenIntent =
             PendingIntent.getActivity(
                 context, item.id, intentOpenActivity,
                 PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
             )

            val fullPath = image.getUri(item.uri).removePrefix("file://")

            val bitmap: Bitmap? = try {
                if (fullPath.isNotEmpty()) {
                BitmapFactory.decodeFile(fullPath)
                } else {
                null
                }
                } catch (_: Exception) {
                null
                }

        val bigIcon = NotificationCompat.BigPictureStyle()
            .bigPicture(bitmap)


        val vibrationPattern = longArrayOf(0, 1000, 500, 1000, 500)
        val builder = NotificationCompat.Builder(context,
            newRingtoneUri.toString()
        )
            .setSmallIcon(R.drawable.icon)
            .setContentTitle(item.name)
            .setContentText(item.desc)
            .setVibrate(vibrationPattern)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setStyle(bigIcon)
            .setContentIntent(contentIntent)
            .setFullScreenIntent(fullScreenIntent, true)
            .addAction(0, "Готово", canselIntent)
            .addAction(0, "Отложить", postponeIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)


        val notification = builder.build()
        notification.flags = notification.flags or Notification.FLAG_INSISTENT

        return notification
    }

}
