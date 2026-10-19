package com.example.model

data class HijriDate(
    val year: Int,
    val month: Int, // 1 to 12
    val day: Int,   // 1 to 30
    val monthNameArabic: String,
    val monthNameEnglish: String,
    val dayNameArabic: String = ""
) {
    val formatted: String
        get() = "$day $monthNameArabic $year هـ"

    val isAyyamAlBeed: Boolean
        get() = day in 13..15
}

data class IslamicOccasion(
    val id: String,
    val titleArabic: String,
    val hijriMonth: Int,
    val hijriDay: Int,
    val descriptionArabic: String,
    val virtueArabic: String,
    val isFastRecommended: Boolean = false,
    val isHoliday: Boolean = false
) {
    val dateFormatted: String
        get() {
            val months = listOf(
                "محرم", "صفر", "ربيع الأول", "ربيع الآخر",
                "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
                "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
            )
            val monthName = months.getOrElse(hijriMonth - 1) { "" }
            return "$hijriDay $monthName"
        }
}

data class UpcomingOccasionItem(
    val occasion: IslamicOccasion,
    val nextGregorianDateFormatted: String,
    val daysRemaining: Int
)
