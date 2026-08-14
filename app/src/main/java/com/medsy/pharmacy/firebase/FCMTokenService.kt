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
import com.medsy.presentation.R
import com.medsy.presentation.notifications.NotificationLocalizer
import com.medsy.domain.common.preferences.usecase.ObserveUserPreferencesUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FCMTokenService : FirebaseMessagingService() {

    @Inject
    lateinit var fcmTokenManager: FcmTokenManager

    @Inject
    lateinit var observePreferences: ObserveUserPreferencesUseCase

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    companion object {
        const val ORDERS_CHANNEL_ID = "orders_channel"
        const val EXTRA_REQUEST_ID = "extra_request_id"
        const val EXTRA_ORDER_ID = "extra_order_id"
        const val EXTRA_RECIPIENT_ID = "extra_recipient_id"

        const val KEY_CATEGORY = "category"
        const val KEY_TITLE = "title"
        const val KEY_BODY = "body"
        const val KEY_REQUEST_ID = "requestId"
        const val KEY_ID = "id"
        const val KEY_ORDER_ID = "orderId"
        const val KEY_LEGACY_ORDER_ID = "orderID"
        const val KEY_RECIPIENT_ID = "recipientId"
        private const val CATEGORY_REQUEST_IN_AREA = "REQUEST_IN_AREA"
        private const val CATEGORY_ORDER_CREATED = "ORDER_CREATED"

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCMTokenService", "New FCM token received")
        scope.launch {
            fcmTokenManager.registerDeviceToken()
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCMTokenService", "Message received from: ${message.from}")

        val rawTitle = message.notification?.title
            ?: message.data[KEY_TITLE]
            ?: getString(R.string.notification_default_title)
        val rawBody = message.notification?.body
            ?: message.data[KEY_BODY]
            ?: getString(R.string.notification_default_body)

        val category = message.data[KEY_CATEGORY]?.uppercase().orEmpty()
        val title = NotificationLocalizer.getLocalizedTitle(this, category, rawTitle)
        val body = NotificationLocalizer.getLocalizedBody(this, category, rawBody)

        val requestId = if (category == CATEGORY_REQUEST_IN_AREA) {
            (message.data[KEY_REQUEST_ID] ?: message.data[KEY_ID])?.toLongOrNull()
        } else null
        val orderId = if (category == CATEGORY_ORDER_CREATED) {
            (message.data[KEY_ORDER_ID] ?: message.data[KEY_LEGACY_ORDER_ID])?.toLongOrNull()
        } else null

        val recipientIdStr = message.data[KEY_RECIPIENT_ID]
        val recipientId = recipientIdStr?.toLongOrNull()

        scope.launch {
            val prefs = observePreferences().first()
            if (prefs.isReceivingNotifications) {
                showNotification(title, body, requestId, orderId, recipientId)
            } else {
                Log.d("FCMTokenService", "Notifications are disabled. Dropping message display.")
            }
        }
    }

    private fun showNotification(
        title: String,
        body: String,
        requestId: Long?,
        orderId: Long?,
        recipientId: Long?,
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (requestId != null) {
                putExtra(EXTRA_REQUEST_ID, requestId)
            }
            if (orderId != null) {
                putExtra(EXTRA_ORDER_ID, orderId)
            }
            if (recipientId != null) {
                putExtra(EXTRA_RECIPIENT_ID, recipientId)
            }
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestId?.hashCode() ?: orderId?.hashCode() ?: 0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ORDERS_CHANNEL_ID,
                getString(com.medsy.presentation.R.string.notification_channel_orders_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.notification_channel_orders_description)
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

        manager.notify(
            requestId?.toInt() ?: orderId?.toInt() ?: System.currentTimeMillis().toInt(),
            notification
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}
