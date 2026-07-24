package com.medsy.pharmacy.presence

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import com.medsy.pharmacy.R
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.medsy.domain.common.fold
import com.medsy.domain.common.preferences.usecase.SetReceivingOrdersPreferenceUseCase
import com.medsy.domain.pharmacist.usecase.SendHeartbeatUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PresenceForegroundService : Service() {

    @Inject
    lateinit var sendHeartbeat: SendHeartbeatUseCase

    @Inject
    lateinit var setReceivingOrdersPreference: SetReceivingOrdersPreferenceUseCase

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private var heartbeatJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NOTIFICATION_ID, createNotification(), android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
            } else {
                startForeground(NOTIFICATION_ID, createNotification())
            }
        } catch (e: Exception) {
            Log.e("PresenceService", "Failed to start foreground in onCreate", e)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == null || action == ACTION_START) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(NOTIFICATION_ID, createNotification(), android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
                } else {
                    startForeground(NOTIFICATION_ID, createNotification())
                }
            } catch (e: Exception) {
                Log.e("PresenceService", "Failed to start foreground in onStartCommand: ${e.message}", e)
            }
            startHeartbeatLoop()
        } else if (action == ACTION_STOP) {
            Log.d("PresenceService", "ACTION_STOP received, stopping heartbeat loop")
            stopHeartbeatLoop()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } else {
                @Suppress("DEPRECATION")
                stopForeground(true)
            }
            stopSelf()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Log.d("PresenceService", "onDestroy called")
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun startHeartbeatLoop() {
        if (heartbeatJob?.isActive == true) return
        heartbeatJob = serviceScope.launch {
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < SERVICE_DURATION_MILLIS) {
                val result = sendHeartbeat()
                result.fold(
                    onSuccess = { status ->
                        Log.d("PresenceService", "Heartbeat sent: onDuty=${status.onDuty}")
                        if (!status.onDuty) {
                            setReceivingOrdersPreference(false)
                        }
                    },
                    onError = { error ->
                        Log.e("PresenceService", "Heartbeat failed: $error")
                    }
                )
                delay(HEARTBEAT_INTERVAL_MILLIS)
            }
            // 15 minutes are over, go offline
            setReceivingOrdersPreference(false)
            stopSelf()
        }
    }

    private fun stopHeartbeatLoop() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.presence_notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.presence_notification_channel_description)            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, Class.forName(MAIN_ACTIVITY_CLASS_PATH))
        val pendingIntent = android.app.PendingIntent.getActivity(
            this, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val endTime = System.currentTimeMillis() + SERVICE_DURATION_MILLIS

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.presence_notification_title))
            .setContentText(getString(R.string.presence_notification_text))
            .setSmallIcon(com.medsy.designsystem.R.drawable.ic_pharmacy_snake)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setUsesChronometer(true)
            .setWhen(endTime)
            .setTimeoutAfter(SERVICE_DURATION_MILLIS)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(getString(R.string.presence_notification_big_text))
            )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setChronometerCountDown(true)
        }

        return builder.build()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        private const val CHANNEL_ID = "presence_channel"
        private const val NOTIFICATION_ID = 1
        private const val MAIN_ACTIVITY_CLASS_PATH = "com.medsy.pharmacy.MainActivity"

        private const val SERVICE_DURATION_MILLIS = 15 * 60 * 1000L // 15 Minutes
        private const val HEARTBEAT_INTERVAL_MILLIS = 60_000L      // 1 Minute

        fun start(context: Context) {
            val intent = Intent(context, PresenceForegroundService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, PresenceForegroundService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
