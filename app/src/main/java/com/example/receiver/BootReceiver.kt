package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.notification.AlarmScheduler

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            AlarmScheduler.scheduleAllPrayerAlarms(context)
            AlarmScheduler.scheduleNextRandomAthkar(context)
            com.example.work.PrayerWorkScheduler.scheduleAllPrayerWorks(context)
        }
    }
}
