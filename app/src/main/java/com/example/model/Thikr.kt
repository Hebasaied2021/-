package com.example.model

enum class ThikrCategory(val titleArabic: String) {
    MORNING("أذكار الصباح"),
    EVENING("أذكار المساء"),
    AFTER_PRAYER("أذكار بعد الصلاة"),
    SLEEP("أذكار النوم"),
    RANDOM("تذكيرات عشوائية")
}

data class ThikrItem(
    val id: Int,
    val category: ThikrCategory,
    val textArabic: String,
    val virtueArabic: String,
    val referenceArabic: String,
    val targetCount: Int = 1,
    val currentCount: Int = 0,
    val isCompleted: Boolean = false
)
