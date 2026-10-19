package com.example.ui

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AdhanAudioEngine
import com.example.calculation.PrayerTimeCalculator
import com.example.calculation.QiblaCalculator
import com.example.data.AthkarRepository
import com.example.data.CitiesRepository
import com.example.data.PreferencesManager
import com.example.model.CalculationMethod
import com.example.model.JuristicMethod
import com.example.model.LocationData
import com.example.model.Muezzin
import com.example.model.PrayerSchedule
import com.example.model.ThikrCategory
import com.example.model.ThikrItem
import com.example.audio.AudioRecorderManager
import com.example.audio.AthkarAudioPlayerManager
import com.example.data.AthkarRoomRepository
import com.example.data.local.AppDatabase
import com.example.data.local.ThikrEntity
import com.example.sensor.CompassManager
import com.example.util.LocationHelper
import com.example.notification.AlarmScheduler
import com.example.work.ScheduledAthkarWorkScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val prefs = PreferencesManager(application)
    private val locationHelper = LocationHelper(application)
    private val compassManager = CompassManager(application)

    // Current Location
    private val _currentLocation = MutableStateFlow(prefs.getCurrentLocation())
    val currentLocation: StateFlow<LocationData> = _currentLocation.asStateFlow()

    private val _isLocating = MutableStateFlow(false)
    val isLocating: StateFlow<Boolean> = _isLocating.asStateFlow()

    // Prayer Schedule & Countdown
    private val _prayerSchedule = MutableStateFlow(
        PrayerTimeCalculator.calculate(
            location = _currentLocation.value,
            method = prefs.calculationMethod,
            juristic = prefs.juristicMethod
        )
    )
    val prayerSchedule: StateFlow<PrayerSchedule> = _prayerSchedule.asStateFlow()

    // Qibla
    private val _qiblaBearing = MutableStateFlow(
        QiblaCalculator.calculateBearing(
            _currentLocation.value.latitude,
            _currentLocation.value.longitude
        )
    )
    val qiblaBearing: StateFlow<Double> = _qiblaBearing.asStateFlow()

    private val _distanceToKaabaKm = MutableStateFlow(
        QiblaCalculator.calculateDistanceKm(
            _currentLocation.value.latitude,
            _currentLocation.value.longitude
        )
    )
    val distanceToKaabaKm: StateFlow<Double> = _distanceToKaabaKm.asStateFlow()

    val compassHeading: StateFlow<Float> = compassManager.heading

    private val _isQiblaAligned = MutableStateFlow(false)
    val isQiblaAligned: StateFlow<Boolean> = _isQiblaAligned.asStateFlow()

    // Muezzin & Audio
    private val _selectedMuezzin = MutableStateFlow(
        Muezzin.allMuezzins.firstOrNull { it.id == prefs.selectedMuezzinId } ?: Muezzin.defaultMuezzin
    )
    val selectedMuezzin: StateFlow<Muezzin> = _selectedMuezzin.asStateFlow()

    private val _isAdhanAudioPlaybackEnabled = MutableStateFlow(prefs.isAdhanAudioPlaybackEnabled)
    val isAdhanAudioPlaybackEnabled: StateFlow<Boolean> = _isAdhanAudioPlaybackEnabled.asStateFlow()

    val isAudioPlaying: StateFlow<Boolean> = AdhanAudioEngine.isPlaying
    val currentAudioPhrase: StateFlow<String> = AdhanAudioEngine.currentPhrase
    val audioProgress: StateFlow<Float> = AdhanAudioEngine.progress
    val playingMuezzinId: StateFlow<String?> = AdhanAudioEngine.currentMuezzinId
    // Room Local Database & Repository
    private val athkarDatabase = AppDatabase.getDatabase(application)
    val athkarRoomRepository = AthkarRoomRepository(athkarDatabase.thikrDao())

    val morningAthkarEntities: StateFlow<List<ThikrEntity>> = athkarRoomRepository.morningAthkar
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val eveningAthkarEntities: StateFlow<List<ThikrEntity>> = athkarRoomRepository.eveningAthkar
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val afterPrayerAthkarEntities: StateFlow<List<ThikrEntity>> = athkarRoomRepository.afterPrayerAthkar
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteAthkarEntities: StateFlow<List<ThikrEntity>> = athkarRoomRepository.favoriteAthkar
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteCount: StateFlow<Int> = athkarRoomRepository.favoriteCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val customVoiceAthkar: StateFlow<List<ThikrEntity>> = athkarRoomRepository.customVoiceAthkar
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Voice Recorder & Audio Player Managers
    val audioRecorderManager = AudioRecorderManager(application)
    val audioPlayerManager = AthkarAudioPlayerManager(application)

    val isRecording: StateFlow<Boolean> = audioRecorderManager.isRecording
    val recordingDurationSeconds: StateFlow<Int> = audioRecorderManager.recordingDurationSeconds
    val recordingAmplitude: StateFlow<Int> = audioRecorderManager.amplitude

    val playingVoiceThikrId: StateFlow<Int?> = audioPlayerManager.currentlyPlayingThikrId
    val isVoiceThikrPlaying: StateFlow<Boolean> = audioPlayerManager.isPlaying
    val voicePlaybackProgress: StateFlow<Float> = audioPlayerManager.playbackProgress

    // Scheduled Audible Athkar
    private val _isAudibleAthkarEnabled = MutableStateFlow(prefs.isAudibleAthkarEnabled)
    val isAudibleAthkarEnabled: StateFlow<Boolean> = _isAudibleAthkarEnabled.asStateFlow()

    private val _isMorningAthkarScheduled = MutableStateFlow(prefs.isMorningAthkarScheduled)
    val isMorningAthkarScheduled: StateFlow<Boolean> = _isMorningAthkarScheduled.asStateFlow()

    private val _morningAthkarTime = MutableStateFlow(prefs.morningAthkarHour to prefs.morningAthkarMinute)
    val morningAthkarTime: StateFlow<Pair<Int, Int>> = _morningAthkarTime.asStateFlow()

    private val _isEveningAthkarScheduled = MutableStateFlow(prefs.isEveningAthkarScheduled)
    val isEveningAthkarScheduled: StateFlow<Boolean> = _isEveningAthkarScheduled.asStateFlow()

    private val _eveningAthkarTime = MutableStateFlow(prefs.eveningAthkarHour to prefs.eveningAthkarMinute)
    val eveningAthkarTime: StateFlow<Pair<Int, Int>> = _eveningAthkarTime.asStateFlow()

    // Athkar in-memory fallback
    private val _morningAthkarList = MutableStateFlow(AthkarRepository.morningAthkar)
    val morningAthkarList: StateFlow<List<ThikrItem>> = _morningAthkarList.asStateFlow()

    private val _eveningAthkarList = MutableStateFlow(AthkarRepository.eveningAthkar)
    val eveningAthkarList: StateFlow<List<ThikrItem>> = _eveningAthkarList.asStateFlow()

    private val _afterPrayerAthkarList = MutableStateFlow(AthkarRepository.afterPrayerAthkar)
    val afterPrayerAthkarList: StateFlow<List<ThikrItem>> = _afterPrayerAthkarList.asStateFlow()

    private val _randomThikr = MutableStateFlow(AthkarRepository.getRandomThikr())
    val randomThikr: StateFlow<String> = _randomThikr.asStateFlow()

    // Tasbih
    private val _tasbihPhrases = listOf(
        "سُبْحَانَ اللَّهِ",
        "الْحَمْدُ لِلَّهِ",
        "لاَ إِلَهَ إِلاَّ اللَّهُ",
        "اللَّهُ أَكْبَرُ",
        "لاَ حَوْلَ وَلاَ قُوَّةَ إِلاَّ بِاللَّهِ",
        "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
        "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ",
        "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ سُبْحَانَ اللَّهِ الْعَظِيمِ"
    )
    val tasbihPhrases: List<String> = _tasbihPhrases

    private val _currentTasbihPhraseIndex = MutableStateFlow(0)
    val currentTasbihPhraseIndex: StateFlow<Int> = _currentTasbihPhraseIndex.asStateFlow()

    private val _tasbihCurrentCount = MutableStateFlow(0)
    val tasbihCurrentCount: StateFlow<Int> = _tasbihCurrentCount.asStateFlow()

    private val _tasbihTargetCount = MutableStateFlow(33)
    val tasbihTargetCount: StateFlow<Int> = _tasbihTargetCount.asStateFlow()

    private val _tasbihTotalCount = MutableStateFlow(prefs.tasbihTotalCount)
    val tasbihTotalCount: StateFlow<Int> = _tasbihTotalCount.asStateFlow()

    // Settings
    private val _calculationMethod = MutableStateFlow(prefs.calculationMethod)
    val calculationMethod: StateFlow<CalculationMethod> = _calculationMethod.asStateFlow()

    private val _juristicMethod = MutableStateFlow(prefs.juristicMethod)
    val juristicMethod: StateFlow<JuristicMethod> = _juristicMethod.asStateFlow()

    private val _prayerAlertsEnabled = MutableStateFlow(prefs.isPrayerAlertEnabled)
    val prayerAlertsEnabled: StateFlow<Boolean> = _prayerAlertsEnabled.asStateFlow()

    private val _randomAthkarEnabled = MutableStateFlow(prefs.isRandomAthkarEnabled)
    val randomAthkarEnabled: StateFlow<Boolean> = _randomAthkarEnabled.asStateFlow()

    private val _randomAthkarIntervalMin = MutableStateFlow(prefs.randomAthkarIntervalMinutes)
    val randomAthkarIntervalMin: StateFlow<Int> = _randomAthkarIntervalMin.asStateFlow()

    // Theme Mode (Dark Mode, Light Mode, System)
    private val _themeMode = MutableStateFlow(prefs.themeMode)
    val themeMode: StateFlow<com.example.model.ThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: com.example.model.ThemeMode) {
        prefs.themeMode = mode
        _themeMode.value = mode
    }

    fun toggleDarkMode() {
        vibrate(25)
        val nextMode = when (_themeMode.value) {
            com.example.model.ThemeMode.DARK -> com.example.model.ThemeMode.LIGHT
            com.example.model.ThemeMode.LIGHT -> com.example.model.ThemeMode.DARK
            com.example.model.ThemeMode.SYSTEM -> com.example.model.ThemeMode.DARK
        }
        setThemeMode(nextMode)
    }

    private var countdownJob: Job? = null
    private var lastVibratedHeading = false

    init {
        AdhanAudioEngine.init(application)
        startCountdownTicker()
        observeCompass()
        scheduleAlarms()
        viewModelScope.launch(Dispatchers.IO) {
            athkarRoomRepository.ensureDataPopulated()
        }
    }

    private fun startCountdownTicker() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (isActive) {
                refreshPrayerTimes()
                delay(1000)
            }
        }
    }

    private fun observeCompass() {
        compassManager.startListening()

        viewModelScope.launch {
            combine(compassManager.heading, _qiblaBearing) { heading, bearing ->
                QiblaCalculator.isAligned(heading, bearing)
            }.collect { aligned ->
                if (aligned != _isQiblaAligned.value) {
                    _isQiblaAligned.value = aligned
                    if (aligned && !lastVibratedHeading) {
                        vibrate(50)
                        lastVibratedHeading = true
                    } else if (!aligned) {
                        lastVibratedHeading = false
                    }
                }
            }
        }
    }

    fun refreshPrayerTimes() {
        val schedule = PrayerTimeCalculator.calculate(
            location = _currentLocation.value,
            calendar = Calendar.getInstance(),
            method = _calculationMethod.value,
            juristic = _juristicMethod.value
        )
        _prayerSchedule.value = schedule
    }

    fun setLocation(location: LocationData) {
        prefs.saveLocation(location)
        _currentLocation.value = location
        _qiblaBearing.value = QiblaCalculator.calculateBearing(location.latitude, location.longitude)
        _distanceToKaabaKm.value = QiblaCalculator.calculateDistanceKm(location.latitude, location.longitude)
        refreshPrayerTimes()
        scheduleAlarms()
    }

    fun requestGpsLocation() {
        viewModelScope.launch {
            _isLocating.value = true
            try {
                val loc = locationHelper.getCurrentLocation()
                if (loc != null) {
                    setLocation(loc)
                }
            } finally {
                _isLocating.value = false
            }
        }
    }

    fun selectMuezzin(muezzin: Muezzin) {
        prefs.selectedMuezzinId = muezzin.id
        _selectedMuezzin.value = muezzin
        com.example.work.PrayerWorkScheduler.scheduleAllPrayerWorks(getApplication())
    }

    fun toggleAdhanAudioPlayback(enabled: Boolean) {
        prefs.isAdhanAudioPlaybackEnabled = enabled
        _isAdhanAudioPlaybackEnabled.value = enabled
    }

    fun testWorkManagerAdhan(muezzinId: String, delaySeconds: Long = 2) {
        vibrate(40)
        com.example.work.PrayerWorkScheduler.scheduleTestAdhanWork(
            getApplication(),
            muezzinId,
            delaySeconds = delaySeconds
        )
    }

    fun toggleAudioPreview(muezzin: Muezzin) {
        if (AdhanAudioEngine.isPlaying.value && AdhanAudioEngine.currentMuezzinId.value == muezzin.id) {
            AdhanAudioEngine.stop()
        } else {
            AdhanAudioEngine.playMuezzin(getApplication(), muezzin)
        }
    }

    fun stopAudio() {
        AdhanAudioEngine.stop()
    }

    // Athkar actions
    fun incrementThikr(item: ThikrItem) {
        vibrate(20)
        when (item.category) {
            ThikrCategory.MORNING -> {
                _morningAthkarList.value = _morningAthkarList.value.map {
                    if (it.id == item.id) {
                        val newCount = (it.currentCount + 1).coerceAtMost(it.targetCount)
                        val completed = newCount >= it.targetCount
                        if (completed && !it.isCompleted) vibrate(60)
                        it.copy(currentCount = newCount, isCompleted = completed)
                    } else it
                }
            }
            ThikrCategory.EVENING -> {
                _eveningAthkarList.value = _eveningAthkarList.value.map {
                    if (it.id == item.id) {
                        val newCount = (it.currentCount + 1).coerceAtMost(it.targetCount)
                        val completed = newCount >= it.targetCount
                        if (completed && !it.isCompleted) vibrate(60)
                        it.copy(currentCount = newCount, isCompleted = completed)
                    } else it
                }
            }
            ThikrCategory.AFTER_PRAYER -> {
                _afterPrayerAthkarList.value = _afterPrayerAthkarList.value.map {
                    if (it.id == item.id) {
                        val newCount = (it.currentCount + 1).coerceAtMost(it.targetCount)
                        val completed = newCount >= it.targetCount
                        if (completed && !it.isCompleted) vibrate(60)
                        it.copy(currentCount = newCount, isCompleted = completed)
                    } else it
                }
            }
            else -> {}
        }
    }

    fun resetThikrCategory(category: ThikrCategory) {
        when (category) {
            ThikrCategory.MORNING -> {
                _morningAthkarList.value = _morningAthkarList.value.map {
                    it.copy(currentCount = 0, isCompleted = false)
                }
            }
            ThikrCategory.EVENING -> {
                _eveningAthkarList.value = _eveningAthkarList.value.map {
                    it.copy(currentCount = 0, isCompleted = false)
                }
            }
            ThikrCategory.AFTER_PRAYER -> {
                _afterPrayerAthkarList.value = _afterPrayerAthkarList.value.map {
                    it.copy(currentCount = 0, isCompleted = false)
                }
            }
            else -> {}
        }
    }

    // Room Entity actions (Persistence & Favorites)
    fun toggleThikrFavorite(id: Int, currentFavorite: Boolean) {
        vibrate(35)
        viewModelScope.launch(Dispatchers.IO) {
            athkarRoomRepository.toggleFavorite(id, currentFavorite)
        }
    }

    fun incrementThikrEntity(id: Int) {
        vibrate(25)
        viewModelScope.launch(Dispatchers.IO) {
            val newlyCompleted = athkarRoomRepository.incrementThikr(id)
            if (newlyCompleted) {
                vibrate(70)
            }
        }
    }

    fun resetThikrCategoryEntity(category: ThikrCategory) {
        vibrate(35)
        viewModelScope.launch(Dispatchers.IO) {
            athkarRoomRepository.resetCategory(category)
        }
    }

    fun shuffleRandomThikr() {
        vibrate(30)
        _randomThikr.value = AthkarRepository.getRandomThikr()
    }

    // Tasbih actions
    fun incrementTasbih() {
        vibrate(25)
        val nextCount = _tasbihCurrentCount.value + 1
        val target = _tasbihTargetCount.value

        if (nextCount >= target && target > 0) {
            vibrate(80)
            _tasbihCurrentCount.value = 0
            // cycle to next phrase automatically or stay
            _currentTasbihPhraseIndex.value = (_currentTasbihPhraseIndex.value + 1) % _tasbihPhrases.size
        } else {
            _tasbihCurrentCount.value = nextCount
        }

        val total = _tasbihTotalCount.value + 1
        _tasbihTotalCount.value = total
        prefs.tasbihTotalCount = total
    }

    fun resetTasbihCurrent() {
        _tasbihCurrentCount.value = 0
    }

    fun selectTasbihPhrase(index: Int) {
        _currentTasbihPhraseIndex.value = index
        _tasbihCurrentCount.value = 0
    }

    fun setTasbihTarget(target: Int) {
        _tasbihTargetCount.value = target
        _tasbihCurrentCount.value = 0
    }

    // Settings actions
    fun setCalculationMethod(method: CalculationMethod) {
        prefs.calculationMethod = method
        _calculationMethod.value = method
        refreshPrayerTimes()
        scheduleAlarms()
    }

    fun setJuristicMethod(juristic: JuristicMethod) {
        prefs.juristicMethod = juristic
        _juristicMethod.value = juristic
        refreshPrayerTimes()
        scheduleAlarms()
    }

    fun togglePrayerAlerts(enabled: Boolean) {
        prefs.isPrayerAlertEnabled = enabled
        _prayerAlertsEnabled.value = enabled
        if (enabled) {
            scheduleAlarms()
        } else {
            AlarmScheduler.cancelAllAlarms(getApplication())
            com.example.work.PrayerWorkScheduler.cancelAllPrayerWorks(getApplication())
        }
    }

    fun togglePrayerNotification(prayerName: String, enabled: Boolean) {
        prefs.setPrayerNotificationEnabled(prayerName, enabled)
        refreshPrayerTimes()
        scheduleAlarms()
    }

    fun toggleRandomAthkar(enabled: Boolean) {
        prefs.isRandomAthkarEnabled = enabled
        _randomAthkarEnabled.value = enabled
        if (enabled) {
            AlarmScheduler.scheduleNextRandomAthkar(getApplication())
            com.example.work.RandomAthkarWorkScheduler.scheduleNextRandomAthkarWork(getApplication())
        } else {
            com.example.work.RandomAthkarWorkScheduler.cancelWork(getApplication())
        }
    }

    fun setRandomAthkarInterval(minutes: Int) {
        prefs.randomAthkarIntervalMinutes = minutes
        _randomAthkarIntervalMin.value = minutes
        if (_randomAthkarEnabled.value) {
            AlarmScheduler.scheduleNextRandomAthkar(getApplication())
            com.example.work.RandomAthkarWorkScheduler.scheduleNextRandomAthkarWork(getApplication())
        }
    }

    fun testRandomAthkarNotification(delaySeconds: Long = 2) {
        vibrate(30)
        com.example.work.RandomAthkarWorkScheduler.scheduleImmediateTestWork(getApplication(), delaySeconds)
    }

    // Voice Athkar Actions (Record, Save in Room, Play, Delete)
    fun startVoiceRecording(): File? {
        vibrate(30)
        return audioRecorderManager.startRecording()
    }

    fun stopAndSaveVoiceRecording(title: String, category: ThikrCategory, targetCount: Int = 1) {
        val duration = audioRecorderManager.stopRecording()
        vibrate(50)
        val recordingsDir = File(getApplication<Application>().filesDir, "athkar_recordings")
        val latestFile = recordingsDir.listFiles()?.filter { it.extension == "m4a" }?.maxByOrNull { it.lastModified() }
        if (latestFile != null && latestFile.exists()) {
            viewModelScope.launch(Dispatchers.IO) {
                athkarRoomRepository.saveCustomVoiceThikr(
                    title = if (title.isBlank()) "ذكر صوتي (${category.titleArabic})" else title,
                    category = category,
                    audioFilePath = latestFile.absolutePath,
                    durationSeconds = duration,
                    targetCount = targetCount
                )
            }
        }
    }

    fun cancelVoiceRecording() {
        vibrate(20)
        audioRecorderManager.cancelRecording()
    }

    fun playVoiceThikr(thikr: ThikrEntity) {
        vibrate(25)
        val path = thikr.audioFilePath ?: return
        audioPlayerManager.play(path, thikr.id)
    }

    fun stopVoiceThikrPlayback() {
        audioPlayerManager.stop()
    }

    fun deleteVoiceThikr(thikr: ThikrEntity) {
        vibrate(40)
        audioPlayerManager.stop()
        viewModelScope.launch(Dispatchers.IO) {
            athkarRoomRepository.deleteCustomThikr(thikr.id, thikr.audioFilePath)
        }
    }

    // Scheduled Audible Athkar Actions
    fun setAudibleAthkarEnabled(enabled: Boolean) {
        prefs.isAudibleAthkarEnabled = enabled
        _isAudibleAthkarEnabled.value = enabled
    }

    fun setMorningAthkarSchedule(enabled: Boolean, hour: Int, minute: Int) {
        prefs.isMorningAthkarScheduled = enabled
        prefs.morningAthkarHour = hour
        prefs.morningAthkarMinute = minute
        _isMorningAthkarScheduled.value = enabled
        _morningAthkarTime.value = hour to minute
        ScheduledAthkarWorkScheduler.scheduleAllAthkarWorks(getApplication())
    }

    fun setEveningAthkarSchedule(enabled: Boolean, hour: Int, minute: Int) {
        prefs.isEveningAthkarScheduled = enabled
        prefs.eveningAthkarHour = hour
        prefs.eveningAthkarMinute = minute
        _isEveningAthkarScheduled.value = enabled
        _eveningAthkarTime.value = hour to minute
        ScheduledAthkarWorkScheduler.scheduleAllAthkarWorks(getApplication())
    }

    fun testAudibleAthkarWork(category: ThikrCategory, delaySeconds: Long = 2) {
        vibrate(30)
        ScheduledAthkarWorkScheduler.scheduleImmediateTest(getApplication(), category, delaySeconds)
    }

    // Prayer Alert Offset and Muezzin Customization
    fun getPrayerOffsetMinutes(prayerName: String): Int {
        return prefs.getPrayerAlertOffsetMinutes(prayerName)
    }

    fun setPrayerOffsetMinutes(prayerName: String, offsetMinutes: Int) {
        prefs.setPrayerAlertOffsetMinutes(prayerName, offsetMinutes)
        scheduleAlarms()
    }

    fun getMuezzinForPrayer(prayerName: String): String {
        return prefs.getMuezzinIdForPrayer(prayerName)
    }

    fun getMuezzinObjectForPrayer(prayerName: String): Muezzin {
        val id = prefs.getMuezzinIdForPrayer(prayerName)
        return Muezzin.allMuezzins.firstOrNull { it.id == id } ?: _selectedMuezzin.value
    }

    fun setMuezzinForPrayer(prayerName: String, muezzinId: String) {
        prefs.setMuezzinIdForPrayer(prayerName, muezzinId)
        scheduleAlarms()
    }

    private fun scheduleAlarms() {
        AlarmScheduler.scheduleAllPrayerAlarms(getApplication())
        com.example.work.PrayerWorkScheduler.scheduleAllPrayerWorks(getApplication())
        com.example.work.ScheduledAthkarWorkScheduler.scheduleAllAthkarWorks(getApplication())
        if (prefs.isRandomAthkarEnabled) {
            AlarmScheduler.scheduleNextRandomAthkar(getApplication())
            com.example.work.RandomAthkarWorkScheduler.scheduleNextRandomAthkarWork(getApplication())
        }
    }

    private fun vibrate(durationMs: Long) {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(durationMs)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCleared() {
        super.onCleared()
        compassManager.stopListening()
        AdhanAudioEngine.stop()
        audioRecorderManager.cancelRecording()
        audioPlayerManager.stop()
        countdownJob?.cancel()
    }
}
