package com.example.work

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.data.PreferencesManager
import com.example.model.ThikrCategory
import java.util.Calendar
import java.util.concurrent.TimeUnit

object ScheduledAthkarWorkScheduler {

    const val WORK_MORNING_ATHKAR = "work_scheduled_morning_athkar"
    const val WORK_EVENING_ATHKAR = "work_scheduled_evening_athkar"

    fun scheduleAllAthkarWorks(context: Context) {
        try {
            val prefs = PreferencesManager(context)
            val workManager = WorkManager.getInstance(context)

            // Schedule Morning Athkar
            if (prefs.isMorningAthkarScheduled) {
                val delayMillis = calculateDelayMillis(
                    targetHour = prefs.morningAthkarHour,
                    targetMinute = prefs.morningAthkarMinute
                )

                val morningRequest = OneTimeWorkRequestBuilder<ScheduledAthkarWorker>()
                    .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                    .setInputData(workDataOf(ScheduledAthkarWorker.KEY_CATEGORY to ThikrCategory.MORNING.name))
                    .addTag("scheduled_athkar_tag")
                    .build()

                workManager.enqueueUniqueWork(
                    WORK_MORNING_ATHKAR,
                    ExistingWorkPolicy.REPLACE,
                    morningRequest
                )
            } else {
                workManager.cancelUniqueWork(WORK_MORNING_ATHKAR)
            }

            // Schedule Evening Athkar
            if (prefs.isEveningAthkarScheduled) {
                val delayMillis = calculateDelayMillis(
                    targetHour = prefs.eveningAthkarHour,
                    targetMinute = prefs.eveningAthkarMinute
                )

                val eveningRequest = OneTimeWorkRequestBuilder<ScheduledAthkarWorker>()
                    .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                    .setInputData(workDataOf(ScheduledAthkarWorker.KEY_CATEGORY to ThikrCategory.EVENING.name))
                    .addTag("scheduled_athkar_tag")
                    .build()

                workManager.enqueueUniqueWork(
                    WORK_EVENING_ATHKAR,
                    ExistingWorkPolicy.REPLACE,
                    eveningRequest
                )
            } else {
                workManager.cancelUniqueWork(WORK_EVENING_ATHKAR)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun calculateDelayMillis(targetHour: Int, targetMinute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, targetHour)
            set(Calendar.MINUTE, targetMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        return (target.timeInMillis - now.timeInMillis).coerceAtLeast(1000L)
    }

    fun scheduleImmediateTest(context: Context, category: ThikrCategory, delaySeconds: Long = 2) {
        try {
            val request = OneTimeWorkRequestBuilder<ScheduledAthkarWorker>()
                .setInitialDelay(delaySeconds, TimeUnit.SECONDS)
                .setInputData(workDataOf(ScheduledAthkarWorker.KEY_CATEGORY to category.name))
                .addTag("scheduled_athkar_test_tag")
                .build()

            WorkManager.getInstance(context).enqueue(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelAll(context: Context) {
        try {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelUniqueWork(WORK_MORNING_ATHKAR)
            workManager.cancelUniqueWork(WORK_EVENING_ATHKAR)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
