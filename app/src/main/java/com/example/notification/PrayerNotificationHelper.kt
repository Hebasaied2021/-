package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity

object PrayerNotificationHelper {

    const val CHANNEL_PRAYER_ID = "channel_prayer_times"
    const val CHANNEL_ATHKAR_ID = "channel_random_athkar"

    private const val NOTIFICATION_PRAYER_BASE_ID = 1000
    private const val NOTIFICATION_ATHKAR_ID = 2000

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            // Channel for Prayer Times (High importance, sound, vibration)
            val prayerChannel = NotificationChannel(
                CHANNEL_PRAYER_ID,
                "تنبيهات مواقيت الصلاة والأذان",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات دخول وقت الصلاة والتنبيه التلقائي بصوت المؤذن"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                setSound(
                    defaultSoundUri,
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }

            // Channel for Random Athkar Reminders (Default importance)
            val athkarChannel = NotificationChannel(
                CHANNEL_ATHKAR_ID,
                "تذكيرات الأذكار العشوائية",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "إشعارات دورية وعشوائية بذكر الله والصلاة على النبي"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(prayerChannel)
            notificationManager.createNotificationChannel(athkarChannel)
        }
    }

    fun showPrayerNotification(
        context: Context,
        prayerNameArabic: String,
        muezzinNameArabic: String,
        cityNameArabic: String
    ) {
        createNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            prayerNameArabic.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_PRAYER_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("حان الآن موعد صلاة $prayerNameArabic")
            .setContentText("أذان $prayerNameArabic حسب التوقيت المحلي لمدينة $cityNameArabic • $muezzinNameArabic")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("الله أكبر، الله أكبر.. حان الآن موعد صلاة $prayerNameArabic حسب التوقيت المحلي لمدينة $cityNameArabic بصوت $muezzinNameArabic. حي على الصلاة، حي على الفلاح.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.notify(NOTIFICATION_PRAYER_BASE_ID + prayerNameArabic.hashCode() % 100, notification)
    }

    fun showPrayerAdhanNotification(
        context: Context,
        prayerNameArabic: String,
        prayerTypeKey: String,
        muezzin: com.example.model.Muezzin,
        cityNameArabic: String,
        isAudioPlaying: Boolean
    ): Int {
        createNotificationChannels(context)

        val notificationId = NOTIFICATION_PRAYER_BASE_ID + Math.abs(prayerTypeKey.hashCode() % 100)

        // Intent to open app
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent to stop adhan audio
        val stopIntent = Intent(context, com.example.receiver.StopAdhanReceiver::class.java).apply {
            action = com.example.receiver.StopAdhanReceiver.ACTION_STOP_ADHAN
            putExtra(com.example.receiver.StopAdhanReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 500,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_PRAYER_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("حان الآن موعد صلاة $prayerNameArabic 🕌")
            .setContentText("يصدح الآن بالأذان بصوت ${muezzin.nameArabic} • $cityNameArabic")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("الله أكبر، الله أكبر.. حان الآن موعد صلاة $prayerNameArabic حسب التوقيت المحلي لمدينة $cityNameArabic بصوت ${muezzin.nameArabic} (${muezzin.titleArabic}). حي على الصلاة، حي على الفلاح.")
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)

        if (isAudioPlaying) {
            builder.addAction(
                android.R.drawable.ic_media_pause,
                "إيقاف الأذان",
                stopPendingIntent
            )
        }

        val notification = builder.build()
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.notify(notificationId, notification)

        return notificationId
    }

    fun showRandomAthkarNotification(context: Context, thikrText: String) {
        createNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ATHKAR_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ATHKAR_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("تذكير بذكر الله 🌿")
            .setContentText(thikrText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(thikrText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.notify(NOTIFICATION_ATHKAR_ID, notification)
    }

    fun showScheduledAthkarNotification(
        context: Context,
        title: String,
        thikrText: String,
        isMorning: Boolean
    ) {
        createNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            if (isMorning) 3001 else 3002,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_PRAYER_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(if (isMorning) "🌅 $title" else "🌇 $title")
            .setContentText(thikrText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(thikrText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.notify(if (isMorning) 3001 else 3002, notification)
    }
}
