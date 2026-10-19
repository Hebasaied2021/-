package com.example.model

enum class PrayerType(
    val arabicName: String,
    val englishName: String,
    val isSalat: Boolean = true
) {
    FAJR("الفجر", "Fajr", true),
    SUNRISE("الشروق", "Sunrise", false),
    DHUHR("الظهر", "Dhuhr", true),
    ASR("العصر", "Asr", true),
    MAGHRIB("المغرب", "Maghrib", true),
    ISHA("العشاء", "Isha", true),
    QIYAM("قيام الليل", "Qiyam", false);

    companion object {
        val primaryPrayers = listOf(FAJR, SUNRISE, DHUHR, ASR, MAGHRIB, ISHA)
        val fardhPrayers = listOf(FAJR, DHUHR, ASR, MAGHRIB, ISHA)
    }
}

data class SinglePrayerTime(
    val type: PrayerType,
    val timeMillis: Long,
    val formattedTime: String,
    val isNext: Boolean = false,
    val isPast: Boolean = false,
    val notificationEnabled: Boolean = true
)

data class PrayerSchedule(
    val dateFormatted: String,
    val hijriDate: String,
    val times: List<SinglePrayerTime>,
    val nextPrayer: SinglePrayerTime?,
    val remainingMillis: Long,
    val intervalProgress: Float // 0.0 to 1.0 between previous prayer and next prayer
)
