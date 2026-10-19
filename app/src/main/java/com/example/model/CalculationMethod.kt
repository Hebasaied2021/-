package com.example.model

enum class CalculationMethod(
    val titleArabic: String,
    val titleEnglish: String,
    val fajrAngle: Double,
    val ishaAngle: Double,
    val ishaIntervalMins: Int = 0 // 0 means use ishaAngle, >0 means minutes after maghrib
) {
    UMM_AL_QURA("أم القرى (مكة المكرمة)", "Umm Al-Qura (Makkah)", 18.5, 0.0, 90),
    EGYPTIAN("الهيئة المصرية العامة للمساحة", "Egyptian General Authority", 19.5, 17.5),
    MUSLIM_WORLD_LEAGUE("رابطة العالم الإسلامي", "Muslim World League", 18.0, 17.0),
    ISNA("الجمعية الإسلامية لأمريكا الشمالية (ISNA)", "ISNA (North America)", 15.0, 15.0),
    KARACHI("جامعة العلوم الإسلامية بكراتشي", "University of Islamic Sciences, Karachi", 18.0, 18.0),
    DUBAI("دائرة الشؤون الإسلامية بدبي", "Dubai Islamic Affairs", 18.2, 18.2),
    KUWAIT("وزارة الأوقاف والشؤون الإسلامية بالكويت", "Kuwait Awqaf", 18.0, 17.5)
}

enum class JuristicMethod(
    val titleArabic: String,
    val titleEnglish: String,
    val shadowFactor: Double
) {
    SHAFI_STANDARD("الجمهور (شافعي، مالكي، حنبلي)", "Standard (Shafi'i, Maliki, Hanbali)", 1.0),
    HANAFI("الحنفي", "Hanafi", 2.0)
}
