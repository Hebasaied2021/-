package com.example.work

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.PreferencesManager
import java.util.concurrent.TimeUnit
import kotlin.random.Random

object RandomAthkarWorkScheduler {

    const val WORK_NAME_RANDOM_ATHKAR = "work_random_athkar"

    fun scheduleNextRandomAthkarWork(context: Context) {
        try {
            val prefs = PreferencesManager(context)
            if (!prefs.isRandomAthkarEnabled) {
                cancelWork(context)
                return
            }

            val baseMin = prefs.randomAthkarIntervalMinutes
            // Random fluctuation between -10 min and +20 min around chosen interval
            val randomOffset = Random.nextInt(-10, 21)
            val actualMinutes = (baseMin + randomOffset).coerceAtLeast(15)

            val workRequest = OneTimeWorkRequestBuilder<RandomAthkarWorker>()
                .setInitialDelay(actualMinutes.toLong(), TimeUnit.MINUTES)
                .addTag("random_athkar_tag")
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME_RANDOM_ATHKAR,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun scheduleImmediateTestWork(context: Context, delaySeconds: Long = 2) {
        try {
            val workRequest = OneTimeWorkRequestBuilder<RandomAthkarWorker>()
                .setInitialDelay(delaySeconds, TimeUnit.SECONDS)
                .addTag("random_athkar_test_tag")
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelWork(context: Context) {
        try {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_RANDOM_ATHKAR)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
