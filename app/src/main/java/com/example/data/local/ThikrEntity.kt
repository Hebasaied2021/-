package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.ThikrCategory

@Entity(tableName = "athkar")
data class ThikrEntity(
    @PrimaryKey
    val id: Int,
    val category: String, // MORNING, EVENING, AFTER_PRAYER
    val textArabic: String,
    val virtueArabic: String,
    val referenceArabic: String,
    val targetCount: Int = 1,
    val currentCount: Int = 0,
    val isCompleted: Boolean = false,
    val isFavorite: Boolean = false,
    val audioFilePath: String? = null,
    val audioDurationSeconds: Int = 0,
    val isCustomRecording: Boolean = false,
    val createdTimestamp: Long = System.currentTimeMillis()
) {
    fun toCategory(): ThikrCategory {
        return try {
            ThikrCategory.valueOf(category)
        } catch (e: Exception) {
            ThikrCategory.MORNING
        }
    }
}
