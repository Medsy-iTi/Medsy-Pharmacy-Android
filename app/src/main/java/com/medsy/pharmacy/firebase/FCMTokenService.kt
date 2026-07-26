package com.medsy.pharmacy.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.medsy.pharmacy.MainActivity
import com.medsy.pharmacy.fcm.FcmTokenManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FCMTokenService : FirebaseMessagingService() {

    @Inject
    lateinit var fcmTokenManager: FcmTokenManager

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    companion object {
        const val ORDERS_CHANNEL_ID = "orders_channel"
        const val EXTRA_REQUEST_ID = "extra_request_id"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCMTokenService", "New FCM Token received: $token")
        scope.launch {
            fcmTokenManager.registerDeviceToken()
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCMTokenService", "Message received from: ${message.from}")

        val title = message.notification?.title
            ?: message.data["title"]
            ?: getString(com.medsy.presentation.R.string.notification_default_title)
        val body = message.notification?.body 
            ?: message.data["body"] 
            ?: getString(com.medsy.presentation.R.string.notification_default_body)

        val requestIdStr = message.data["requestId"] ?: message.data["id"]
        val requestId = requestIdStr?.toLongOrNull()

        showNotification(title, body, requestId)
    }

    private fun showNotification(title: String, body: String, requestId: Long?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (requestId != null) {
                putExtra(EXTRA_REQUEST_ID, requestId)
            }
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 
            requestId?.hashCode() ?: 0, 
            intent, 
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ORDERS_CHANNEL_ID,
                getString(com.medsy.presentation.R.string.notification_channel_orders_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(com.medsy.presentation.R.string.notification_channel_orders_description)
            }
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, ORDERS_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(com.medsy.designsystem.R.drawable.ic_pharmacy_snake)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(requestId?.toInt() ?: System.currentTimeMillis().toInt(), notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
