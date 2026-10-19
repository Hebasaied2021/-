package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.calculation.HijriCalendarHelper
import com.example.calculation.PrayerTimeCalculator
import com.example.calculation.QiblaCalculator
import com.example.data.AthkarRepository
import com.example.data.CitiesRepository
import com.example.model.CalculationMethod
import com.example.model.Muezzin
import com.example.model.PrayerType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun testAppName() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("نور الصلاة", appName)
    }

    @Test
    fun testPrayerCalculation() {
        val makkah = CitiesRepository.defaultCity
        val schedule = PrayerTimeCalculator.calculate(
            location = makkah,
            calendar = Calendar.getInstance(),
            method = CalculationMethod.UMM_AL_QURA
        )

        assertEquals(6, schedule.times.size)
        assertTrue(schedule.times.any { it.type == PrayerType.FAJR })
        assertTrue(schedule.times.any { it.type == PrayerType.DHUHR })
        assertTrue(schedule.times.any { it.type == PrayerType.ASR })
        assertTrue(schedule.times.any { it.type == PrayerType.MAGHRIB })
        assertTrue(schedule.times.any { it.type == PrayerType.ISHA })
        assertNotNull(schedule.hijriDate)
    }

    @Test
    fun testQiblaCalculation() {
        // Cairo coordinates: 30.0444, 31.2357
        val bearing = QiblaCalculator.calculateBearing(30.0444, 31.2357)
        assertTrue("Bearing should be approx south-east (130-140 deg)", bearing in 130.0..140.0)

        val distance = QiblaCalculator.calculateDistanceKm(30.0444, 31.2357)
        assertTrue("Distance should be approx 1200-1400 km", distance in 1200.0..1400.0)
    }

    @Test
    fun testMuezzinList() {
        val muezzins = Muezzin.allMuezzins
        assertTrue(muezzins.size >= 5)
        assertTrue(muezzins.any { it.id == "ali_mulla" })
        assertTrue(muezzins.any { it.id == "abdulbasit" })
        assertTrue(muezzins.any { it.id == "alafasy" })
    }

    @Test
    fun testAthkarRepository() {
        assertTrue(AthkarRepository.morningAthkar.isNotEmpty())
        assertTrue(AthkarRepository.eveningAthkar.isNotEmpty())
        assertTrue(AthkarRepository.afterPrayerAthkar.isNotEmpty())
        val randomThikr = AthkarRepository.getRandomThikr()
        assertTrue(randomThikr.isNotBlank())
    }

    @Test
    fun testHijriCalendarAndOccasions() {
        val now = Calendar.getInstance()
        val hijriDate = HijriCalendarHelper.gregorianToHijri(now)
        assertTrue(hijriDate.year >= 1445)
        assertTrue(hijriDate.month in 1..12)
        assertTrue(hijriDate.day in 1..30)
        assertTrue(hijriDate.monthNameArabic.isNotEmpty())

        // Test matrix generation
        val matrix = HijriCalendarHelper.generateMonthMatrix(now.get(Calendar.YEAR), now.get(Calendar.MONTH))
        assertTrue(matrix.size >= 28)
        assertTrue(matrix.any { it.isToday })

        // Test Islamic occasions
        val occasions = HijriCalendarHelper.islamicOccasions
        assertTrue(occasions.any { it.id == "ashura" })
        assertTrue(occasions.any { it.id == "first_ramadan" })
        assertTrue(occasions.any { it.id == "eid_al_fitr" })
        assertTrue(occasions.any { it.id == "arafah" })
        assertTrue(occasions.any { it.id == "eid_al_adha" })

        val upcoming = HijriCalendarHelper.getUpcomingOccasions(now)
        assertTrue(upcoming.isNotEmpty())
    }

    @Test
    fun testPrayerWorkSchedulerAndMuezzinSelection() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = com.example.data.PreferencesManager(context)

        // Verify default muezzin
        assertEquals("ali_mulla", prefs.selectedMuezzinId)

        // Customise for Fajr specifically
        prefs.setMuezzinIdForPrayer("FAJR", "abdulbasit")
        assertEquals("abdulbasit", prefs.getMuezzinIdForPrayer("FAJR"))
        assertEquals("ali_mulla", prefs.getMuezzinIdForPrayer("DHUHR"))

        // Test scheduling does not throw exceptions
        com.example.work.PrayerWorkScheduler.scheduleAllPrayerWorks(context)
        com.example.work.PrayerWorkScheduler.scheduleTestAdhanWork(context, "alafasy", delaySeconds = 1)
        com.example.work.PrayerWorkScheduler.cancelAllPrayerWorks(context)
    }

    @Test
    fun testRoomAthkarDatabaseAndFavorites() = kotlinx.coroutines.runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = com.example.data.local.AppDatabase.getDatabase(context)
        val dao = db.thikrDao()
        val repo = com.example.data.AthkarRoomRepository(dao)

        repo.ensureDataPopulated()

        val count = dao.getCount()
        assertTrue("Database should have pre-populated athkar", count > 0)

        // Test marking favorite
        val thikrId = 1
        repo.toggleFavorite(thikrId, currentFavorite = false)
        val thikr = dao.getThikrById(thikrId)
        assertNotNull(thikr)
        assertTrue(thikr!!.isFavorite)

        // Test unmarking favorite
        repo.toggleFavorite(thikrId, currentFavorite = true)
        val thikrUnfav = dao.getThikrById(thikrId)
        assertNotNull(thikrUnfav)
        assertTrue(!thikrUnfav!!.isFavorite)
    }

    @Test
    fun testMuezzinAuthenticRecordings() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        com.example.audio.AdhanAudioEngine.init(context)

        val muezzins = com.example.model.Muezzin.allMuezzins
        assertTrue(muezzins.isNotEmpty())

        val aliMulla = muezzins.firstOrNull { it.id == "ali_mulla" }
        assertNotNull(aliMulla)
        assertNotNull(aliMulla!!.rawResId)
        assertTrue(aliMulla.isRealHumanVoice)

        val abdulbasit = muezzins.firstOrNull { it.id == "abdulbasit" }
        assertNotNull(abdulbasit)
        assertNotNull(abdulbasit!!.rawResId)

        val alafasy = muezzins.firstOrNull { it.id == "alafasy" }
        assertNotNull(alafasy)
        assertNotNull(alafasy!!.rawResId)

        com.example.audio.AdhanAudioEngine.stop()
        assertEquals(false, com.example.audio.AdhanAudioEngine.isPlaying.value)
    }
}
