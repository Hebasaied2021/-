package com.example.work

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.calculation.PrayerTimeCalculator
import com.example.data.PreferencesManager
import java.util.concurrent.TimeUnit

object PrayerWorkScheduler {

    const val TAG_PRAYER_WORK = "prayer_adhan_work"
    const val WORK_NAME_PREFIX = "prayer_adhan_worker_"

    /**
     * Schedules WorkManager tasks for each of the 5 daily prayers
     * with the specific selected muezzin for each prayer
     */
    fun scheduleAllPrayerWorks(context: Context) {
        try {
            val prefs = PreferencesManager(context)
            if (!prefs.isPrayerAlertEnabled) {
                cancelAllPrayerWorks(context)
                return
            }

            val workManager = WorkManager.getInstance(context)
            val location = prefs.getCurrentLocation()
            val schedule = PrayerTimeCalculator.calculate(
                location = location,
                method = prefs.calculationMethod,
                juristic = prefs.juristicMethod
            )

            val now = System.currentTimeMillis()

            for (prayer in schedule.times) {
                // Only schedule for obligatory prayers that have notifications enabled
                if (!prayer.type.isSalat || !prefs.isPrayerNotificationEnabled(prayer.type.name)) {
                    workManager.cancelUniqueWork(WORK_NAME_PREFIX + prayer.type.name)
                    continue
                }

                val offsetMinutes = prefs.getPrayerAlertOffsetMinutes(prayer.type.name)
                var triggerTime = prayer.timeMillis + (offsetMinutes * 60 * 1000L)
                if (triggerTime <= now) {
                    // If this prayer alert has already passed today, schedule for tomorrow's time
                    triggerTime += 24 * 60 * 60 * 1000L
                }

                val delayMillis = (triggerTime - now).coerceAtLeast(1000L)
                val muezzinId = prefs.getMuezzinIdForPrayer(prayer.type.name)

                val inputData = workDataOf(
                    PrayerAdhanWorker.KEY_PRAYER_NAME_AR to prayer.type.arabicName,
                    PrayerAdhanWorker.KEY_PRAYER_TYPE_KEY to prayer.type.name,
                    PrayerAdhanWorker.KEY_MUEZZIN_ID to muezzinId,
                    PrayerAdhanWorker.KEY_CITY_NAME to location.cityNameArabic
                )

                val workRequest = OneTimeWorkRequestBuilder<PrayerAdhanWorker>()
                    .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .addTag(TAG_PRAYER_WORK)
                    .addTag("prayer_${prayer.type.name}")
                    .build()

                workManager.enqueueUniqueWork(
                    WORK_NAME_PREFIX + prayer.type.name,
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Schedules a test WorkManager worker to trigger after a short delay (e.g. 2-3 seconds)
     * so user can immediately verify the background WorkManager audio adhan
     */
    fun scheduleTestAdhanWork(context: Context, muezzinId: String, prayerName: String = "المغرب (تجربة WorkManager)", delaySeconds: Long = 2) {
        try {
            val prefs = PreferencesManager(context)
            val workManager = WorkManager.getInstance(context)
            val location = prefs.getCurrentLocation()

            val inputData = workDataOf(
                PrayerAdhanWorker.KEY_PRAYER_NAME_AR to prayerName,
                PrayerAdhanWorker.KEY_PRAYER_TYPE_KEY to "MAGHRIB",
                PrayerAdhanWorker.KEY_MUEZZIN_ID to muezzinId,
                PrayerAdhanWorker.KEY_CITY_NAME to location.cityNameArabic
            )

            val workRequest = OneTimeWorkRequestBuilder<PrayerAdhanWorker>()
                .setInitialDelay(delaySeconds, TimeUnit.SECONDS)
                .setInputData(inputData)
                .addTag("test_adhan_work")
                .build()

            workManager.enqueue(workRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Cancels all scheduled prayer works
     */
    fun cancelAllPrayerWorks(context: Context) {
        try {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWorkByTag(TAG_PRAYER_WORK)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
