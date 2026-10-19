package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.model.Muezzin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object AdhanAudioEngine {

    private const val TAG = "AdhanAudioEngine"

    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private var appContext: Context? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentMuezzinId = MutableStateFlow<String?>(null)
    val currentMuezzinId: StateFlow<String?> = _currentMuezzinId.asStateFlow()

    private val _currentPhrase = MutableStateFlow("")
    val currentPhrase: StateFlow<String> = _currentPhrase.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    /**
     * Plays the authentic real audio recording of the selected Muezzin.
     */
    fun playMuezzin(context: Context? = null, muezzin: Muezzin, onFinished: (() -> Unit)? = null) {
        stop()

        val ctx = context ?: appContext
        if (ctx == null) {
            Log.e(TAG, "Cannot play audio: context is null")
            return
        }

        _currentMuezzinId.value = muezzin.id
        _isPlaying.value = true
        _progress.value = 0f
        _currentPhrase.value = "الله أكبر .. الله أكبر"

        try {
            val player = if (muezzin.rawResId != null) {
                // High-fidelity instant playback from local bundled studio audio
                MediaPlayer.create(ctx, muezzin.rawResId)
            } else if (muezzin.audioUrl.isNotEmpty()) {
                // Stream from high-speed reliable public audio URL
                MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    setDataSource(ctx, Uri.parse(muezzin.audioUrl))
                    prepare()
                }
            } else {
                null
            }

            if (player == null) {
                Log.e(TAG, "Failed to create MediaPlayer for muezzin ${muezzin.id}")
                resetState()
                onFinished?.invoke()
                return
            }

            mediaPlayer = player

            player.setOnCompletionListener {
                resetState()
                onFinished?.invoke()
            }

            player.setOnErrorListener { _, what, extra ->
                Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                resetState()
                onFinished?.invoke()
                true
            }

            player.start()

            // Coroutine to monitor progress and update the displayed sacred phrase in real-time
            progressJob = scope.launch {
                val totalDuration = player.duration.coerceAtLeast(1)
                while (isActive && player.isPlaying) {
                    val currentPos = player.currentPosition
                    val p = (currentPos.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)
                    _progress.value = p

                    // Dynamically map progress to the current Adhan phrase
                    _currentPhrase.value = when {
                        p < 0.16f -> "الله أكبر .. الله أكبر"
                        p < 0.32f -> "الله أكبر .. الله أكبر"
                        p < 0.48f -> "أشهد أن لا إله إلا الله"
                        p < 0.64f -> "أشهد أن محمداً رسول الله"
                        p < 0.76f -> "حي على الصلاة"
                        p < 0.88f -> "حي على الفلاح"
                        else -> "لا إله إلا الله"
                    }

                    delay(200)
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error playing muezzin audio", e)
            resetState()
            onFinished?.invoke()
        }
    }

    /**
     * Backward-compatible helper for previewing audio in screens or workers.
     */
    fun playMuezzinPreview(muezzin: Muezzin, onFinished: (() -> Unit)? = null) {
        playMuezzin(context = null, muezzin = muezzin, onFinished = onFinished)
    }

    fun stop() {
        progressJob?.cancel()
        progressJob = null

        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.reset()
                it.release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping MediaPlayer", e)
        }

        mediaPlayer = null
        resetState()
    }

    private fun resetState() {
        _isPlaying.value = false
        _currentMuezzinId.value = null
        _currentPhrase.value = ""
        _progress.value = 0f
    }

    /**
     * Suspending call for WorkManager to wait until the real Adhan finishes or is cancelled
     */
    suspend fun playAdhanSuspend(context: Context? = null, muezzin: Muezzin) =
        suspendCancellableCoroutine<Unit> { cont ->
            playMuezzin(context = context, muezzin = muezzin) {
                if (cont.isActive) {
                    cont.resume(Unit)
                }
            }

            cont.invokeOnCancellation {
                stop()
            }
        }
}
