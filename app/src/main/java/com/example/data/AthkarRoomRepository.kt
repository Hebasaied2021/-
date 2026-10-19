package com.example.data

import com.example.data.local.ThikrDao
import com.example.data.local.ThikrEntity
import com.example.model.ThikrCategory
import kotlinx.coroutines.flow.Flow
import java.io.File

class AthkarRoomRepository(private val dao: ThikrDao) {

    val allAthkar: Flow<List<ThikrEntity>> = dao.getAllAthkar()
    val morningAthkar: Flow<List<ThikrEntity>> = dao.getAthkarByCategory(ThikrCategory.MORNING.name)
    val eveningAthkar: Flow<List<ThikrEntity>> = dao.getAthkarByCategory(ThikrCategory.EVENING.name)
    val afterPrayerAthkar: Flow<List<ThikrEntity>> = dao.getAthkarByCategory(ThikrCategory.AFTER_PRAYER.name)
    val favoriteAthkar: Flow<List<ThikrEntity>> = dao.getFavoriteAthkar()
    val customVoiceAthkar: Flow<List<ThikrEntity>> = dao.getCustomVoiceAthkar()
    val favoriteCount: Flow<Int> = dao.getFavoriteCount()

    suspend fun toggleFavorite(id: Int, currentFavorite: Boolean) {
        dao.updateFavorite(id, !currentFavorite)
    }

    suspend fun incrementThikr(id: Int): Boolean {
        val thikr = dao.getThikrById(id) ?: return false
        val newCount = (thikr.currentCount + 1).coerceAtMost(thikr.targetCount)
        val completed = newCount >= thikr.targetCount
        dao.updateCount(id, newCount, completed)
        return completed && !thikr.isCompleted
    }

    suspend fun resetCategory(category: ThikrCategory) {
        dao.resetCategoryCounts(category.name)
    }

    suspend fun resetAll() {
        dao.resetAllCounts()
    }

    suspend fun saveCustomVoiceThikr(
        title: String,
        category: ThikrCategory,
        audioFilePath: String,
        durationSeconds: Int,
        targetCount: Int = 1
    ): ThikrEntity {
        val maxId = (dao.getMaxId() ?: 1000).coerceAtLeast(1000)
        val newId = maxId + 1

        val entity = ThikrEntity(
            id = newId,
            category = category.name,
            textArabic = title,
            virtueArabic = "تسجيل صوتي خاص بصوت المستخدم المبارك",
            referenceArabic = "تسجيل شخصي",
            targetCount = targetCount,
            currentCount = 0,
            isCompleted = false,
            isFavorite = false,
            audioFilePath = audioFilePath,
            audioDurationSeconds = durationSeconds,
            isCustomRecording = true,
            createdTimestamp = System.currentTimeMillis()
        )

        dao.insert(entity)
        return entity
    }

    suspend fun deleteCustomThikr(id: Int, filePath: String?) {
        dao.deleteById(id)
        filePath?.let {
            try {
                val file = File(it)
                if (file.exists()) file.delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun ensureDataPopulated() {
        if (dao.getCount() == 0) {
            val allEntities = mutableListOf<ThikrEntity>()
            AthkarRepository.morningAthkar.forEachIndexed { index, it ->
                allEntities.add(
                    ThikrEntity(
                        id = it.id,
                        category = it.category.name,
                        textArabic = it.textArabic,
                        virtueArabic = it.virtueArabic,
                        referenceArabic = it.referenceArabic,
                        targetCount = it.targetCount,
                        currentCount = 0,
                        isCompleted = false,
                        isFavorite = index == 1
                    )
                )
            }
            AthkarRepository.eveningAthkar.forEachIndexed { index, it ->
                allEntities.add(
                    ThikrEntity(
                        id = it.id,
                        category = it.category.name,
                        textArabic = it.textArabic,
                        virtueArabic = it.virtueArabic,
                        referenceArabic = it.referenceArabic,
                        targetCount = it.targetCount,
                        currentCount = 0,
                        isCompleted = false,
                        isFavorite = index == 1
                    )
                )
            }
            AthkarRepository.afterPrayerAthkar.forEach {
                allEntities.add(
                    ThikrEntity(
                        id = it.id,
                        category = it.category.name,
                        textArabic = it.textArabic,
                        virtueArabic = it.virtueArabic,
                        referenceArabic = it.referenceArabic,
                        targetCount = it.targetCount,
                        currentCount = 0,
                        isCompleted = false,
                        isFavorite = false
                    )
                )
            }
            dao.insertAll(allEntities)
        }
    }
}
