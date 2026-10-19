package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.CalculationMethod
import com.example.model.JuristicMethod
import com.example.model.LocationData
import com.example.model.Muezzin

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("noor_al_salah_prefs", Context.MODE_PRIVATE)

    var isGpsEnabled: Boolean
        get() = prefs.getBoolean("is_gps_enabled", false)
        set(value) = prefs.edit().putBoolean("is_gps_enabled", value).apply()

    var savedLatitude: Double
        get() = prefs.getFloat("saved_latitude", 21.4225f).toDouble()
        set(value) = prefs.edit().putFloat("saved_latitude", value.toFloat()).apply()

    var savedLongitude: Double
        get() = prefs.getFloat("saved_longitude", 39.8262f).toDouble()
        set(value) = prefs.edit().putFloat("saved_longitude", value.toFloat()).apply()

    var savedCityArabic: String
        get() = prefs.getString("saved_city_ar", "مكة المكرمة") ?: "مكة المكرمة"
        set(value) = prefs.edit().putString("saved_city_ar", value).apply()

    var savedCityEnglish: String
        get() = prefs.getString("saved_city_en", "Makkah") ?: "Makkah"
        set(value) = prefs.edit().putString("saved_city_en", value).apply()

    var savedCountryArabic: String
        get() = prefs.getString("saved_country_ar", "المملكة العربية السعودية") ?: "المملكة العربية السعودية"
        set(value) = prefs.edit().putString("saved_country_ar", value).apply()

    var savedCountryEnglish: String
        get() = prefs.getString("saved_country_en", "Saudi Arabia") ?: "Saudi Arabia"
        set(value) = prefs.edit().putString("saved_country_en", value).apply()

    var savedTimeZoneId: String
        get() = prefs.getString("saved_timezone_id", "Asia/Riyadh") ?: "Asia/Riyadh"
        set(value) = prefs.edit().putString("saved_timezone_id", value).apply()

    var calculationMethod: CalculationMethod
        get() {
            val name = prefs.getString("calc_method", CalculationMethod.UMM_AL_QURA.name)
            return try {
                CalculationMethod.valueOf(name ?: CalculationMethod.UMM_AL_QURA.name)
            } catch (e: Exception) {
                CalculationMethod.UMM_AL_QURA
            }
        }
        set(value) = prefs.edit().putString("calc_method", value.name).apply()

    var juristicMethod: JuristicMethod
        get() {
            val name = prefs.getString("juristic_method", JuristicMethod.SHAFI_STANDARD.name)
            return try {
                JuristicMethod.valueOf(name ?: JuristicMethod.SHAFI_STANDARD.name)
            } catch (e: Exception) {
                JuristicMethod.SHAFI_STANDARD
            }
        }
        set(value) = prefs.edit().putString("juristic_method", value.name).apply()

    var selectedMuezzinId: String
        get() = prefs.getString("selected_muezzin_id", Muezzin.defaultMuezzin.id) ?: Muezzin.defaultMuezzin.id
        set(value) = prefs.edit().putString("selected_muezzin_id", value).apply()

    fun getMuezzinIdForPrayer(prayerTypeKey: String): String {
        return prefs.getString("muezzin_for_$prayerTypeKey", null) ?: selectedMuezzinId
    }

    fun setMuezzinIdForPrayer(prayerTypeKey: String, muezzinId: String) {
        prefs.edit().putString("muezzin_for_$prayerTypeKey", muezzinId).apply()
    }

    var isAdhanAudioPlaybackEnabled: Boolean
        get() = prefs.getBoolean("adhan_audio_playback_enabled", true)
        set(value) = prefs.edit().putBoolean("adhan_audio_playback_enabled", value).apply()

    var themeMode: com.example.model.ThemeMode
        get() {
            val name = prefs.getString("theme_mode", com.example.model.ThemeMode.SYSTEM.name)
            return try {
                com.example.model.ThemeMode.valueOf(name ?: com.example.model.ThemeMode.SYSTEM.name)
            } catch (e: Exception) {
                com.example.model.ThemeMode.SYSTEM
            }
        }
        set(value) = prefs.edit().putString("theme_mode", value.name).apply()

    var isPrayerAlertEnabled: Boolean
        get() = prefs.getBoolean("prayer_alert_enabled", true)
        set(value) = prefs.edit().putBoolean("prayer_alert_enabled", value).apply()

    fun isPrayerNotificationEnabled(prayerName: String): Boolean {
        return prefs.getBoolean("notify_prayer_$prayerName", true)
    }

    fun setPrayerNotificationEnabled(prayerName: String, enabled: Boolean) {
        prefs.edit().putBoolean("notify_prayer_$prayerName", enabled).apply()
    }

    // Prayer timing offset in minutes (e.g. 0 for exact time, -10 for 10 min before)
    fun getPrayerAlertOffsetMinutes(prayerTypeKey: String): Int {
        return prefs.getInt("alert_offset_$prayerTypeKey", 0)
    }

    fun setPrayerAlertOffsetMinutes(prayerTypeKey: String, offsetMinutes: Int) {
        prefs.edit().putInt("alert_offset_$prayerTypeKey", offsetMinutes).apply()
    }

    // Scheduled Audible Athkar Preferences
    var isAudibleAthkarEnabled: Boolean
        get() = prefs.getBoolean("audible_athkar_enabled", true)
        set(value) = prefs.edit().putBoolean("audible_athkar_enabled", value).apply()

    var isMorningAthkarScheduled: Boolean
        get() = prefs.getBoolean("scheduled_morning_athkar_enabled", true)
        set(value) = prefs.edit().putBoolean("scheduled_morning_athkar_enabled", value).apply()

    var morningAthkarHour: Int
        get() = prefs.getInt("morning_athkar_hour", 6)
        set(value) = prefs.edit().putInt("morning_athkar_hour", value).apply()

    var morningAthkarMinute: Int
        get() = prefs.getInt("morning_athkar_minute", 30)
        set(value) = prefs.edit().putInt("morning_athkar_minute", value).apply()

    var isEveningAthkarScheduled: Boolean
        get() = prefs.getBoolean("scheduled_evening_athkar_enabled", true)
        set(value) = prefs.edit().putBoolean("scheduled_evening_athkar_enabled", value).apply()

    var eveningAthkarHour: Int
        get() = prefs.getInt("evening_athkar_hour", 17)
        set(value) = prefs.edit().putInt("evening_athkar_hour", value).apply()

    var eveningAthkarMinute: Int
        get() = prefs.getInt("evening_athkar_minute", 30)
        set(value) = prefs.edit().putInt("evening_athkar_minute", value).apply()

    var isRandomAthkarEnabled: Boolean
        get() = prefs.getBoolean("random_athkar_enabled", true)
        set(value) = prefs.edit().putBoolean("random_athkar_enabled", value).apply()

    var randomAthkarIntervalMinutes: Int
        get() = prefs.getInt("random_athkar_interval_min", 90) // default 90 mins (1.5 hrs)
        set(value) = prefs.edit().putInt("random_athkar_interval_min", value).apply()

    var tasbihTotalCount: Int
        get() = prefs.getInt("tasbih_total_count", 0)
        set(value) = prefs.edit().putInt("tasbih_total_count", value).apply()

    fun getCurrentLocation(): LocationData {
        return LocationData(
            cityNameArabic = savedCityArabic,
            cityNameEnglish = savedCityEnglish,
            countryNameArabic = savedCountryArabic,
            countryNameEnglish = savedCountryEnglish,
            latitude = savedLatitude,
            longitude = savedLongitude,
            timeZoneId = savedTimeZoneId,
            isAutoGps = isGpsEnabled,
            defaultMethod = calculationMethod
        )
    }

    fun saveLocation(location: LocationData) {
        savedCityArabic = location.cityNameArabic
        savedCityEnglish = location.cityNameEnglish
        savedCountryArabic = location.countryNameArabic
        savedCountryEnglish = location.countryNameEnglish
        savedLatitude = location.latitude
        savedLongitude = location.longitude
        savedTimeZoneId = location.timeZoneId
        isGpsEnabled = location.isAutoGps
        calculationMethod = location.defaultMethod
    }
}
