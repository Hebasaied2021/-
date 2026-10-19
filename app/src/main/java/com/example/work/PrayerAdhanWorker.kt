package com.example.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.audio.AdhanAudioEngine
import com.example.data.PreferencesManager
import com.example.model.Muezzin
import com.example.notification.PrayerNotificationHelper

class PrayerAdhanWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val KEY_PRAYER_NAME_AR = "prayer_name_ar"
        const val KEY_PRAYER_TYPE_KEY = "prayer_type_key"
        const val KEY_MUEZZIN_ID = "muezzin_id"
        const val KEY_CITY_NAME = "city_name"
    }

    override suspend fun doWork(): Result {
        val prayerName = inputData.getString(KEY_PRAYER_NAME_AR) ?: "الصلاة"
        val prayerTypeKey = inputData.getString(KEY_PRAYER_TYPE_KEY) ?: "DHUHR"
        val inputMuezzinId = inputData.getString(KEY_MUEZZIN_ID)
        val prefs = PreferencesManager(context)

        // Resolve muezzin: specific input parameter, or per-prayer preferred muezzin, or global default
        val resolvedMuezzinId = if (!inputMuezzinId.isNullOrEmpty()) {
            inputMuezzinId
        } else {
            prefs.getMuezzinIdForPrayer(prayerTypeKey)
        }

        val muezzin = Muezzin.allMuezzins.firstOrNull { it.id == resolvedMuezzinId }
            ?: Muezzin.defaultMuezzin

        val cityName = inputData.getString(KEY_CITY_NAME) ?: prefs.savedCityArabic

        // Display high-priority prayer notification with Stop Adhan action
        PrayerNotificationHelper.showPrayerAdhanNotification(
            context = context,
            prayerNameArabic = prayerName,
            prayerTypeKey = prayerTypeKey,
            muezzin = muezzin,
            cityNameArabic = cityName,
            isAudioPlaying = prefs.isAdhanAudioPlaybackEnabled
        )

        // Play adhan audio vocal chant in background if enabled
        if (prefs.isAdhanAudioPlaybackEnabled && prefs.isPrayerNotificationEnabled(prayerTypeKey)) {
            try {
                AdhanAudioEngine.playAdhanSuspend(context, muezzin)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Reschedule upcoming prayers in WorkManager
        PrayerWorkScheduler.scheduleAllPrayerWorks(context)

        return Result.success()
    }
}
