package com.example.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.AthkarRepository
import com.example.data.PreferencesManager
import com.example.notification.PrayerNotificationHelper

class RandomAthkarWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val prefs = PreferencesManager(context)
        if (!prefs.isRandomAthkarEnabled) {
            return Result.success()
        }

        val randomThikr = AthkarRepository.getRandomThikr()
        PrayerNotificationHelper.showRandomAthkarNotification(context, randomThikr)

        // Reschedule next random athkar reminder
        RandomAthkarWorkScheduler.scheduleNextRandomAthkarWork(context)

        return Result.success()
    }
}
