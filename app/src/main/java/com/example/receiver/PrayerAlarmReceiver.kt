package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.PreferencesManager
import com.example.model.Muezzin
import com.example.notification.AlarmScheduler
import com.example.notification.PrayerNotificationHelper

class PrayerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra("prayer_name_ar") ?: "الصلاة"
        val prefs = PreferencesManager(context)

        val muezzin = Muezzin.allMuezzins.firstOrNull { it.id == prefs.selectedMuezzinId }
            ?: Muezzin.defaultMuezzin

        // Show prayer alert notification
        PrayerNotificationHelper.showPrayerNotification(
            context = context,
            prayerNameArabic = prayerName,
            muezzinNameArabic = muezzin.nameArabic,
            cityNameArabic = prefs.savedCityArabic
        )

        // Reschedule alarms for future prayers
        AlarmScheduler.scheduleAllPrayerAlarms(context)
    }
}
