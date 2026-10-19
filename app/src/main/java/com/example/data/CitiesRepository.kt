package com.example.data

import com.example.model.CalculationMethod
import com.example.model.LocationData

object CitiesRepository {

    val defaultCity = LocationData(
        cityNameArabic = "مكة المكرمة",
        cityNameEnglish = "Makkah",
        countryNameArabic = "المملكة العربية السعودية",
        countryNameEnglish = "Saudi Arabia",
        latitude = 21.4225,
        longitude = 39.8262,
        timeZoneId = "Asia/Riyadh",
        isAutoGps = false,
        defaultMethod = CalculationMethod.UMM_AL_QURA
    )

    val allCities = listOf(
        defaultCity,
        LocationData("المدينة المنورة", "Madinah", "المملكة العربية السعودية", "Saudi Arabia", 24.5247, 39.5692, "Asia/Riyadh", false, CalculationMethod.UMM_AL_QURA),
        LocationData("الرياض", "Riyadh", "المملكة العربية السعودية", "Saudi Arabia", 24.7136, 46.6753, "Asia/Riyadh", false, CalculationMethod.UMM_AL_QURA),
        LocationData("جدة", "Jeddah", "المملكة العربية السعودية", "Saudi Arabia", 21.5433, 39.1728, "Asia/Riyadh", false, CalculationMethod.UMM_AL_QURA),
        LocationData("الدمام", "Dammam", "المملكة العربية السعودية", "Saudi Arabia", 26.4207, 50.0888, "Asia/Riyadh", false, CalculationMethod.UMM_AL_QURA),
        LocationData("القاهرة", "Cairo", "جمهورية مصر العربية", "Egypt", 30.0444, 31.2357, "Africa/Cairo", false, CalculationMethod.EGYPTIAN),
        LocationData("الإسكندرية", "Alexandria", "جمهورية مصر العربية", "Egypt", 31.2001, 29.9187, "Africa/Cairo", false, CalculationMethod.EGYPTIAN),
        LocationData("الجيزة", "Giza", "جمهورية مصر العربية", "Egypt", 30.0131, 31.2089, "Africa/Cairo", false, CalculationMethod.EGYPTIAN),
        LocationData("أسوان", "Aswan", "جمهورية مصر العربية", "Egypt", 24.0889, 32.8998, "Africa/Cairo", false, CalculationMethod.EGYPTIAN),
        LocationData("دبي", "Dubai", "الإمارات العربية المتحدة", "United Arab Emirates", 25.2048, 55.2708, "Asia/Dubai", false, CalculationMethod.DUBAI),
        LocationData("أبوظبي", "Abu Dhabi", "الإمارات العربية المتحدة", "United Arab Emirates", 24.4539, 54.3773, "Asia/Dubai", false, CalculationMethod.DUBAI),
        LocationData("الشارقة", "Sharjah", "الإمارات العربية المتحدة", "United Arab Emirates", 25.3463, 55.4209, "Asia/Dubai", false, CalculationMethod.DUBAI),
        LocationData("الدوحة", "Doha", "دولة قطر", "Qatar", 25.2854, 51.5310, "Asia/Qatar", false, CalculationMethod.UMM_AL_QURA),
        LocationData("الكويت", "Kuwait City", "دولة الكويت", "Kuwait", 29.3759, 47.9774, "Asia/Kuwait", false, CalculationMethod.KUWAIT),
        LocationData("المنامة", "Manama", "مملكة البحرين", "Bahrain", 26.2285, 50.5860, "Asia/Bahrain", false, CalculationMethod.UMM_AL_QURA),
        LocationData("مسقط", "Muscat", "سلطنة عمان", "Oman", 23.5880, 58.3829, "Asia/Muscat", false, CalculationMethod.UMM_AL_QURA),
        LocationData("عَمّان", "Amman", "المملكة الأردنية الهاشمية", "Jordan", 31.9454, 35.9284, "Asia/Amman", false, CalculationMethod.UMM_AL_QURA),
        LocationData("القدس الشريف", "Jerusalem", "فلسطين", "Palestine", 31.7683, 35.2137, "Asia/Jerusalem", false, CalculationMethod.UMM_AL_QURA),
        LocationData("غزة", "Gaza", "فلسطين", "Palestine", 31.5017, 34.4668, "Asia/Gaza", false, CalculationMethod.UMM_AL_QURA),
        LocationData("بيروت", "Beirut", "الجمهورية اللبنانية", "Lebanon", 33.8938, 35.5018, "Asia/Beirut", false, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        LocationData("دمشق", "Damascus", "الجمهورية العربية السورية", "Syria", 33.5138, 36.2765, "Asia/Damascus", false, CalculationMethod.UMM_AL_QURA),
        LocationData("بغداد", "Baghdad", "جمهورية العراق", "Iraq", 33.3152, 44.3661, "Asia/Baghdad", false, CalculationMethod.UMM_AL_QURA),
        LocationData("أربيل", "Erbil", "جمهورية العراق", "Iraq", 36.1901, 44.0091, "Asia/Baghdad", false, CalculationMethod.UMM_AL_QURA),
        LocationData("طرابلس", "Tripoli", "دولة ليبيا", "Libya", 32.8872, 13.1913, "Africa/Tripoli", false, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        LocationData("تونس", "Tunis", "الجمهورية التونسية", "Tunisia", 36.8065, 10.1815, "Africa/Tunis", false, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        LocationData("الجزائر", "Algiers", "الجمهورية الجزائرية", "Algeria", 36.7538, 3.0588, "Africa/Algiers", false, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        LocationData("الرباط", "Rabat", "المملكة المغربية", "Morocco", 34.0209, -6.8416, "Africa/Casablanca", false, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        LocationData("الدار البيضاء", "Casablanca", "المملكة المغربية", "Morocco", 33.5731, -7.5898, "Africa/Casablanca", false, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        LocationData("الخرطوم", "Khartoum", "جمهورية السودان", "Sudan", 15.5007, 32.5599, "Africa/Khartoum", false, CalculationMethod.EGYPTIAN),
        LocationData("صنعاء", "Sanaa", "الجمهورية اليمنية", "Yemen", 15.3694, 44.1910, "Asia/Aden", false, CalculationMethod.UMM_AL_QURA),
        LocationData("إسطنبول", "Istanbul", "تركيا", "Turkey", 41.0082, 28.9784, "Europe/Istanbul", false, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        LocationData("أنقرة", "Ankara", "تركيا", "Turkey", 39.9334, 32.8597, "Europe/Istanbul", false, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        LocationData("لندن", "London", "المملكة المتحدة", "United Kingdom", 51.5074, -0.1278, "Europe/London", false, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        LocationData("باريس", "Paris", "فرنسا", "France", 48.8566, 2.3522, "Europe/Paris", false, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        LocationData("برلين", "Berlin", "ألمانيا", "Germany", 52.5200, 13.4050, "Europe/Berlin", false, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        LocationData("نيويورك", "New York", "الولايات المتحدة", "United States", 40.7128, -74.0060, "America/New_York", false, CalculationMethod.ISNA),
        LocationData("واشنطن", "Washington D.C.", "الولايات المتحدة", "United States", 38.9072, -77.0369, "America/New_York", false, CalculationMethod.ISNA),
        LocationData("شيكاغو", "Chicago", "الولايات المتحدة", "United States", 41.8781, -87.6298, "America/Chicago", false, CalculationMethod.ISNA),
        LocationData("لوس أنجلوس", "Los Angeles", "الولايات المتحدة", "United States", 34.0522, -118.2437, "America/Los_Angeles", false, CalculationMethod.ISNA),
        LocationData("تورونتو", "Toronto", "كندا", "Canada", 43.6532, -79.3832, "America/Toronto", false, CalculationMethod.ISNA),
        LocationData("جاكرتا", "Jakarta", "إندونيسيا", "Indonesia", -6.2088, 106.8456, "Asia/Jakarta", false, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        LocationData("كوالالمبور", "Kuala Lumpur", "ماليزيا", "Malaysia", 3.1390, 101.6869, "Asia/Kuala_Lumpur", false, CalculationMethod.MUSLIM_WORLD_LEAGUE),
        LocationData("كراتشي", "Karachi", "باكستان", "Pakistan", 24.8607, 67.0011, "Asia/Karachi", false, CalculationMethod.KARACHI),
        LocationData("إسلام آباد", "Islamabad", "باكستان", "Pakistan", 33.6844, 73.0479, "Asia/Karachi", false, CalculationMethod.KARACHI),
        LocationData("دكا", "Dhaka", "بنغلاديش", "Bangladesh", 23.8103, 90.4125, "Asia/Dhaka", false, CalculationMethod.KARACHI),
        LocationData("سيدني", "Sydney", "أستراليا", "Australia", -33.8688, 151.2093, "Australia/Sydney", false, CalculationMethod.MUSLIM_WORLD_LEAGUE)
    )

    fun searchCities(query: String): List<LocationData> {
        val clean = query.trim().lowercase()
        if (clean.isEmpty()) return allCities
        return allCities.filter {
            it.cityNameArabic.contains(clean, ignoreCase = true) ||
            it.cityNameEnglish.contains(clean, ignoreCase = true) ||
            it.countryNameArabic.contains(clean, ignoreCase = true) ||
            it.countryNameEnglish.contains(clean, ignoreCase = true)
        }
    }
}
