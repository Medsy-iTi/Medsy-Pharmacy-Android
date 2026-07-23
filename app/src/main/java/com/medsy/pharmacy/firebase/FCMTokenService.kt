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

        // Handle the incoming message here if needed.
        // For standard notification messages, Firebase SDK handles showing the notification automatically
        // when the app is in the background. When the app is in the foreground, we would handle it here.
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
