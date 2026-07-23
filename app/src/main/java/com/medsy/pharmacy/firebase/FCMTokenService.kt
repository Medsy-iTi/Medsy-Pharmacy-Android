package com.medsy.pharmacy.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.medsy.domain.common.device.DeviceRepository
import com.medsy.domain.pharmacist.repository.PharmacistRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FCMTokenService : FirebaseMessagingService() {

    @Inject
    lateinit var pharmacistRepository: PharmacistRepository

    @Inject
    lateinit var deviceRepository: DeviceRepository

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCMTokenService", "New FCM Token received: $token")
        
        scope.launch {
            val deviceId = deviceRepository.getDeviceId()
            pharmacistRepository.registerDeviceToken(token, deviceId)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCMTokenService", "Message received from: ${message.from}")

        val title = message.notification?.title ?: message.data["title"] ?: "طلب جديد!"
        val body = message.notification?.body ?: message.data["body"] ?: "لديك طلب جديد ينتظر الموافقة"

        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        val intent = android.content.Intent(this, Class.forName("com.medsy.pharmacy.MainActivity")).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            this, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "orders_channel"
        val manager = getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                "الطلبات الجديدة",
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات الطلبات الجديدة"
            }
            manager.createNotificationChannel(channel)
        }

        val notification = androidx.core.app.NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(com.medsy.designsystem.R.drawable.ic_pharmacy_snake)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
