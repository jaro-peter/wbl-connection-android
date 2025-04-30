package com.jaro.wblconnection

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.parse.ParseObject
import com.parse.ParseQuery
@SuppressLint("StaticFieldLeak")
object MessageWatcher {
    private var isRunning = false
    private var lastCheckedAt: Long = System.currentTimeMillis()
    private lateinit var licencePlate: String
    private lateinit var context: Context

    private val handler = Handler(Looper.getMainLooper())
    private val intervalMillis = 30000L // 30 másodperc

    fun startWatching(context: Context, licencePlate: String) {
        if (isRunning) return

        this.context = context.applicationContext
        this.licencePlate = licencePlate
        isRunning = true
        handler.post(checkRunnable)
    }

    private val checkRunnable = object : Runnable {
        override fun run() {
            if (!isRunning) return

            val query = ParseQuery.getQuery<ParseObject>("Messages")
            query.whereEqualTo("To", licencePlate)
            query.whereGreaterThan("createdAt", java.util.Date(lastCheckedAt))
            query.orderByDescending("createdAt")
            query.setLimit(1)

            query.findInBackground { messages, e ->
                if (e == null && messages.isNotEmpty()) {
                    Log.d("MessageWatcher", "\uD83D\uDEA8 New message received!")
                    playBeep()
                    lastCheckedAt = System.currentTimeMillis()
                }
            }

            handler.postDelayed(this, intervalMillis)
        }
    }

    private fun playBeep() {
        try {
            val mp = MediaPlayer.create(context, R.raw.notification_beep2)
            mp.start()
        } catch (e: Exception) {
            Log.e("MessageWatcher", "Error during playback: ${e.localizedMessage}")
        }
    }

    fun stopWatching() {
        isRunning = false
        handler.removeCallbacksAndMessages(null)
    }
}
