package com.example.calculation

import com.example.model.CalculationMethod
import com.example.model.JuristicMethod
import com.example.model.LocationData
import com.example.model.PrayerSchedule
import com.example.model.PrayerType
import com.example.model.SinglePrayerTime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

object PrayerTimeCalculator {

    private fun d2r(d: Double): Double = d * PI / 180.0
    private fun r2d(r: Double): Double = r * 180.0 / PI

    private fun fixHour(a: Double): Double {
        var h = a - 24.0 * floor(a / 24.0)
        if (h < 0) h += 24.0
        return h
    }

    private fun fixAngle(a: Double): Double {
        var d = a - 360.0 * floor(a / 360.0)
        if (d < 0) d += 360.0
        return d
    }

    /**
     * Compute solar coordinates for a Julian Day
     * Returns Pair(Declination in radians, Equation of Time in hours)
     */
    private fun sunPosition(jd: Double): Pair<Double, Double> {
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(d2r(g)) + 0.020 * sin(d2r(2.0 * g)))
        val e = 23.439 - 0.00000036 * d

        val raRad = atan2(cos(d2r(e)) * sin(d2r(l)), cos(d2r(l)))
        var raHours = fixAngle(r2d(raRad)) / 15.0

        val declination = asin(sin(d2r(e)) * sin(d2r(l)))
        val eqt = (q / 15.0) - raHours
        return Pair(declination, eqt)
    }

    private fun julianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2.0 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    /**
     * Hour angle for a given sun altitude angle
     */
    private fun hourAngle(altitudeDeg: Double, latRad: Double, declRad: Double): Double {
        val top = sin(d2r(altitudeDeg)) - sin(latRad) * sin(declRad)
        val bottom = cos(latRad) * cos(declRad)
        if (bottom == 0.0) return 0.0
        val ratio = (top / bottom).coerceIn(-1.0, 1.0)
        return r2d(acos(ratio)) / 15.0
    }

    /**
     * Calculates prayer times for a given location, calendar day, calculation method and juristic school
     */
    fun calculate(
        location: LocationData,
        calendar: Calendar = Calendar.getInstance(),
        method: CalculationMethod = location.defaultMethod,
        juristic: JuristicMethod = JuristicMethod.SHAFI_STANDARD
    ): PrayerSchedule {
        val timeZone = TimeZone.getTimeZone(location.timeZoneId)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val jd = julianDay(year, month, day)
        val (declRad, eqt) = sunPosition(jd)
        val latRad = d2r(location.latitude)

        // Solar noon in UTC hours
        val noonUtcHours = 12.0 - eqt - (location.longitude / 15.0)

        // Sunrise and Sunset (Sun altitude = -0.8333 degrees)
        val sunAlt = -0.8333
        val sunriseSunsetAngle = hourAngle(sunAlt, latRad, declRad)
        val sunriseUtc = noonUtcHours - sunriseSunsetAngle
        val sunsetUtc = noonUtcHours + sunriseSunsetAngle

        // Fajr (altitude = -fajrAngle)
        val fajrAngle = hourAngle(-method.fajrAngle, latRad, declRad)
        val fajrUtc = noonUtcHours - fajrAngle

        // Asr calculation
        val shadowFactor = juristic.shadowFactor
        val noonShadow = abs(location.latitude - r2d(declRad))
        val asrAltitude = r2d(atan(1.0 / (shadowFactor + tan(d2r(noonShadow)))))
        val asrAngle = hourAngle(asrAltitude, latRad, declRad)
        val asrUtc = noonUtcHours + asrAngle

        // Maghrib
        val maghribUtc = sunsetUtc

        // Isha
        val ishaUtc = if (method.ishaIntervalMins > 0) {
            maghribUtc + (method.ishaIntervalMins / 60.0)
        } else {
            val ishaAngle = hourAngle(-method.ishaAngle, latRad, declRad)
            noonUtcHours + ishaAngle
        }

        // Convert UTC hours to local epoch millis for this calendar date
        fun toEpochMillis(utcHours: Double): Long {
            val fixed = fixHour(utcHours)
            val h = floor(fixed).toInt()
            val m = floor((fixed - h) * 60.0).toInt()
            val s = floor(((fixed - h) * 60.0 - m) * 60.0).toInt()

            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, h)
                set(Calendar.MINUTE, m)
                set(Calendar.SECOND, s)
                set(Calendar.MILLISECOND, 0)
            }
            return cal.timeInMillis
        }

        val timeFormat = SimpleDateFormat("h:mm a", Locale("ar")).apply {
            this.timeZone = timeZone
        }

        val fajrMillis = toEpochMillis(fajrUtc)
        val sunriseMillis = toEpochMillis(sunriseUtc)
        val dhuhrMillis = toEpochMillis(noonUtcHours)
        val asrMillis = toEpochMillis(asrUtc)
        val maghribMillis = toEpochMillis(maghribUtc)
        val ishaMillis = toEpochMillis(ishaUtc)

        val rawList = listOf(
            SinglePrayerTime(PrayerType.FAJR, fajrMillis, timeFormat.format(Date(fajrMillis))),
            SinglePrayerTime(PrayerType.SUNRISE, sunriseMillis, timeFormat.format(Date(sunriseMillis)), notificationEnabled = false),
            SinglePrayerTime(PrayerType.DHUHR, dhuhrMillis, timeFormat.format(Date(dhuhrMillis))),
            SinglePrayerTime(PrayerType.ASR, asrMillis, timeFormat.format(Date(asrMillis))),
            SinglePrayerTime(PrayerType.MAGHRIB, maghribMillis, timeFormat.format(Date(maghribMillis))),
            SinglePrayerTime(PrayerType.ISHA, ishaMillis, timeFormat.format(Date(ishaMillis)))
        )

        val now = System.currentTimeMillis()
        var nextPrayer: SinglePrayerTime? = null
        var previousPrayer: SinglePrayerTime? = null

        // Find next prayer among standard prayer times
        for (i in rawList.indices) {
            val p = rawList[i]
            if (p.timeMillis > now) {
                nextPrayer = p
                previousPrayer = if (i > 0) rawList[i - 1] else null
                break
            }
        }

        // If all prayers of today have passed, next is tomorrow's Fajr
        val isTomorrowFajr = (nextPrayer == null)
        val tomorrowFajrMillis = if (isTomorrowFajr) {
            fajrMillis + (24 * 60 * 60 * 1000)
        } else 0L

        val effectiveNextMillis = nextPrayer?.timeMillis ?: tomorrowFajrMillis
        val remainingMillis = (effectiveNextMillis - now).coerceAtLeast(0L)

        val prevMillis = previousPrayer?.timeMillis ?: (rawList.last().timeMillis - (24 * 60 * 60 * 1000))
        val totalInterval = (effectiveNextMillis - prevMillis).coerceAtLeast(1L)
        val elapsed = (now - prevMillis).coerceAtLeast(0L)
        val progress = (elapsed.toFloat() / totalInterval.toFloat()).coerceIn(0f, 1f)

        val updatedList = rawList.map { p ->
            val isNext = if (isTomorrowFajr) p.type == PrayerType.FAJR else (nextPrayer?.type == p.type)
            val isPast = p.timeMillis < now
            p.copy(isNext = isNext, isPast = isPast)
        }

        val dateFormat = SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar")).apply {
            this.timeZone = timeZone
        }

        val hijriDateStr = calculateApproximateHijri(calendar)

        return PrayerSchedule(
            dateFormatted = dateFormat.format(calendar.time),
            hijriDate = hijriDateStr,
            times = updatedList,
            nextPrayer = updatedList.firstOrNull { it.isNext },
            remainingMillis = remainingMillis,
            intervalProgress = progress
        )
    }

    /**
     * Approximate Hijri Date representation with Arabic month names
     */
    fun calculateApproximateHijri(calendar: Calendar): String {
        val y = calendar.get(Calendar.YEAR)
        val m = calendar.get(Calendar.MONTH) + 1
        val d = calendar.get(Calendar.DAY_OF_MONTH)
        val jd = julianDay(y, m, d)

        val l = jd.toLong() - 1948440 + 10632
        val n = ((l - 1) / 10631).toInt()
        val lPrime = l - 10631 * n + 354
        val j = (((10985 - lPrime) / 5316).toInt()) * ((50 * lPrime / 17719).toInt()) +
                ((lPrime / 5670).toInt()) * ((43 * lPrime / 15238).toInt())
        val lDoublePrime = lPrime - (((30 - j) / 15).toInt()) * ((17719 * j / 50).toInt()) -
                ((j / 16).toInt()) * ((15238 * j / 43).toInt()) + 29
        val hijriMonth = (24 * lDoublePrime / 709).toInt()
        val hijriDay = (lDoublePrime - 709 * hijriMonth / 24).toInt()
        val hijriYear = (30 * n + j - 30).toInt()

        val hijriMonths = listOf(
            "محرم", "صفر", "ربيع الأول", "ربيع الآخر",
            "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
            "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
        )
        val monthName = hijriMonths.getOrElse((hijriMonth - 1).coerceIn(0, 11)) { "رمضان" }
        return "$hijriDay $monthName $hijriYear هـ"
    }
}
