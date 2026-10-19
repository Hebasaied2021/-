package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.AthkarRepository
import com.example.notification.AlarmScheduler
import com.example.notification.PrayerNotificationHelper

class RandomAthkarReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val randomThikr = AthkarRepository.getRandomThikr()
        PrayerNotificationHelper.showRandomAthkarNotification(context, randomThikr)

        // Schedule next randomized athkar notification
        AlarmScheduler.scheduleNextRandomAthkar(context)
    }
}
