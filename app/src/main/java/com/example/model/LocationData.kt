package com.example.model

data class LocationData(
    val cityNameArabic: String,
    val cityNameEnglish: String,
    val countryNameArabic: String,
    val countryNameEnglish: String,
    val latitude: Double,
    val longitude: Double,
    val timeZoneId: String,
    val isAutoGps: Boolean = false,
    val defaultMethod: CalculationMethod = CalculationMethod.UMM_AL_QURA
) {
    val displayName: String
        get() = "$cityNameArabic، $countryNameArabic"
}
