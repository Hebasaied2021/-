package com.example.work

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.AthkarRepository
import com.example.data.PreferencesManager
import com.example.data.local.AppDatabase
import com.example.model.ThikrCategory
import com.example.notification.PrayerNotificationHelper
import kotlinx.coroutines.flow.firstOrNull
import java.io.File

class ScheduledAthkarWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val KEY_CATEGORY = "key_category" // "MORNING" or "EVENING"
    }

    override suspend fun doWork(): Result {
        val prefs = PreferencesManager(context)
        val categoryStr = inputData.getString(KEY_CATEGORY) ?: "MORNING"
        val isMorning = categoryStr == "MORNING"

        val title = if (isMorning) "أذكار الصباح المباركة" else "أذكار المساء وحفظ الليل"

        // Query database for custom voice recording or sample text
        val db = AppDatabase.getDatabase(context)
        val athkarList = if (isMorning) {
            db.thikrDao().getAthkarByCategory(ThikrCategory.MORNING.name).firstOrNull() ?: emptyList()
        } else {
            db.thikrDao().getAthkarByCategory(ThikrCategory.EVENING.name).firstOrNull() ?: emptyList()
        }

        // Check if there is a custom voice recording by the user
        val voiceThikr = athkarList.firstOrNull { !it.audioFilePath.isNullOrEmpty() && File(it.audioFilePath).exists() }
        val displayText = voiceThikr?.textArabic ?: if (isMorning) {
            AthkarRepository.morningAthkar.first().textArabic
        } else {
            AthkarRepository.eveningAthkar.first().textArabic
        }

        // Show audible notification with high priority
        PrayerNotificationHelper.showScheduledAthkarNotification(
            context = context,
            title = title,
            thikrText = displayText,
            isMorning = isMorning
        )

        // If audible playback is enabled, play voice recording or melodic tone
        if (prefs.isAudibleAthkarEnabled) {
            playAthkarAudio(voiceThikr?.audioFilePath)
        }

        // Reschedule next occurrence
        ScheduledAthkarWorkScheduler.scheduleAllAthkarWorks(context)

        return Result.success()
    }

    private fun playAthkarAudio(customVoicePath: String?) {
        try {
            if (!customVoicePath.isNullOrEmpty()) {
                val file = File(customVoicePath)
                if (file.exists()) {
                    val player = MediaPlayer().apply {
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                        )
                        setDataSource(context, Uri.fromFile(file))
                        prepare()
                        start()
                    }
                    return
                }
            }

            // Fallback: Play peaceful Islamic audio prompt using AdhanAudioEngine
            val defaultMuezzin = com.example.model.Muezzin.allMuezzins.firstOrNull() ?: return
            com.example.audio.AdhanAudioEngine.playMuezzin(context, defaultMuezzin)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
