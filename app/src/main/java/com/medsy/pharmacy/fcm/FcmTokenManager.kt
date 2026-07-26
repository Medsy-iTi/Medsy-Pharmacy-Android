package com.medsy.pharmacy.fcm

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.medsy.domain.common.device.DeviceRepository
import com.google.firebase.messaging.FirebaseMessaging
import com.medsy.pharmacy.fcm.work.RegisterTokenWorker
import com.medsy.pharmacy.fcm.work.UnregisterTokenWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmTokenManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val deviceRepository: DeviceRepository
) {
    suspend fun registerDeviceToken() {
        try {

            val deviceId = deviceRepository.getDeviceId()

            // هنبعت الـ deviceId بس للـ Worker
            val data = workDataOf(
                RegisterTokenWorker.KEY_DEVICE_ID to deviceId
            )

            val request = OneTimeWorkRequestBuilder<RegisterTokenWorker>()
                .setInputData(data)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "register_fcm_token",
                ExistingWorkPolicy.REPLACE,
                request
            )
            Log.d("FcmTokenManager", "Enqueued FCM Token registration worker")
        } catch (e: Exception) {
            Log.e("FcmTokenManager", "Error scheduling FCM token registration", e)
        }
    }

    fun unregisterDeviceToken(fcmToken: String) {
        val data = workDataOf(
            UnregisterTokenWorker.KEY_FCM_TOKEN to fcmToken
        )

        val request = OneTimeWorkRequestBuilder<UnregisterTokenWorker>()
            .setInputData(data)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "unregister_fcm_token",
            ExistingWorkPolicy.REPLACE,
            request
        )
        Log.d("FcmTokenManager", "Enqueued FCM Token unregistration worker")
    }
}
