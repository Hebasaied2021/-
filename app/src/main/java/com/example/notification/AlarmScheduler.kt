package com.example.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.calculation.PrayerTimeCalculator
import com.example.data.PreferencesManager
import com.example.model.PrayerType
import com.example.receiver.PrayerAlarmReceiver
import com.example.receiver.RandomAthkarReceiver
import java.util.Calendar
import kotlin.random.Random

object AlarmScheduler {

    private const val REQUEST_CODE_PRAYER_BASE = 5000
    private const val REQUEST_CODE_ATHKAR = 6000

    fun scheduleAllPrayerAlarms(context: Context) {
        val prefs = PreferencesManager(context)
        if (!prefs.isPrayerAlertEnabled) return

        val location = prefs.getCurrentLocation()
        val schedule = PrayerTimeCalculator.calculate(
            location = location,
            method = prefs.calculationMethod,
            juristic = prefs.juristicMethod
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val now = System.currentTimeMillis()

        for (prayer in schedule.times) {
            // Only alert for fardh prayers with notifications enabled
            if (!prayer.type.isSalat || !prefs.isPrayerNotificationEnabled(prayer.type.name)) {
                continue
            }

            var triggerTime = prayer.timeMillis
            if (triggerTime <= now) {
                // If prayer has already passed today, schedule for tomorrow
                triggerTime += 24 * 60 * 60 * 1000
            }

            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                putExtra("prayer_name_ar", prayer.type.arabicName)
                putExtra("prayer_type_key", prayer.type.name)
            }

            val requestCode = REQUEST_CODE_PRAYER_BASE + prayer.type.ordinal
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            scheduleAlarmCompat(alarmManager, triggerTime, pendingIntent)
        }
    }

    fun scheduleNextRandomAthkar(context: Context) {
        val prefs = PreferencesManager(context)
        if (!prefs.isRandomAthkarEnabled) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        // Compute randomized delay around user interval setting
        val baseMin = prefs.randomAthkarIntervalMinutes
        // Add random fluctuation between -15 min and +25 min
        val randomOffset = Random.nextInt(-15, 26)
        val actualMinutes = (baseMin + randomOffset).coerceAtLeast(15)
        val triggerTime = System.currentTimeMillis() + (actualMinutes * 60 * 1000L)

        val intent = Intent(context, RandomAthkarReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_ATHKAR,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        scheduleAlarmCompat(alarmManager, triggerTime, pendingIntent)
    }

    fun cancelAllAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        // Cancel prayer alarms
        for (type in PrayerType.values()) {
            val intent = Intent(context, PrayerAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_PRAYER_BASE + type.ordinal,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.let { alarmManager.cancel(it) }
        }

        // Cancel athkar alarm
        val athkarIntent = Intent(context, RandomAthkarReceiver::class.java)
        val athkarPendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_ATHKAR,
            athkarIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        athkarPendingIntent?.let { alarmManager.cancel(it) }
    }

    private fun scheduleAlarmCompat(
        alarmManager: AlarmManager,
        triggerTimeMillis: Long,
        pendingIntent: PendingIntent
    ) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTimeMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTimeMillis,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
        }
    }
}
