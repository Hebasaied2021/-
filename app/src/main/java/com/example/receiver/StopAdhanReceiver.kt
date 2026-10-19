package com.example.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.audio.AdhanAudioEngine

class StopAdhanReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Stop audio playback immediately
        AdhanAudioEngine.stop()

        // Cancel notification if notificationId is provided
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)
        if (notificationId != -1) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.cancel(notificationId)
        }
    }

    companion object {
        const val ACTION_STOP_ADHAN = "com.example.action.STOP_ADHAN"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    }
}
