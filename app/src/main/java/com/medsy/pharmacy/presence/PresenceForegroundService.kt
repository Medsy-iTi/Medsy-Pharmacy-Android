package com.medsy.pharmacy.presence

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
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
            val duration = 15 * 60 * 1000L
            while (System.currentTimeMillis() - startTime < duration) {
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
                delay(60_000)
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
                "Presence Status",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps the app online to receive orders"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, Class.forName("com.medsy.pharmacy.MainActivity"))
        val pendingIntent = android.app.PendingIntent.getActivity(
            this, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val timeoutMillis = 15 * 60 * 1000L
        val endTime = System.currentTimeMillis() + timeoutMillis

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("أنت متاح الآن لتلقي الطلبات")
            .setContentText("التطبيق يعمل في الخلفية وسيغلق تلقائياً بعد 15 دقيقة")
            .setSmallIcon(com.medsy.designsystem.R.drawable.ic_pharmacy_snake)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setUsesChronometer(true)
            .setWhen(endTime)
            .setTimeoutAfter(timeoutMillis)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("أنت الآن متصل وتتلقى الطلبات من العملاء القريبين.\nهذه الحالة ستستمر لمدة 15 دقيقة وسيبدأ العداد العكسي بالأسفل.\nاضغط هنا لفتح التطبيق ومتابعة الطلبات.")
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
