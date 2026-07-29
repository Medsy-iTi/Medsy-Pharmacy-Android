package com.medsy.pharmacy.fcm.work

import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.messaging.FirebaseMessaging
import com.medsy.domain.common.fold
import com.medsy.domain.common.preferences.usecase.SetRegisteredFcmTokenUseCase
import com.medsy.domain.notifications.model.DeviceTokenRegistrationDomain
import com.medsy.domain.notifications.usecase.RegisterDeviceTokenUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await
import java.io.IOException

@HiltWorker
class RegisterTokenWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val registerDeviceTokenUseCase: RegisterDeviceTokenUseCase,
    private val setRegisteredFcmTokenUseCase: SetRegisteredFcmTokenUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val deviceId = inputData.getString(KEY_DEVICE_ID)

            if (deviceId.isNullOrEmpty()) {
                Log.e(TAG, "Device ID is missing in inputData")
                return Result.failure()
            }

            val fcmToken = FirebaseMessaging.getInstance().token.await()
            Log.d(TAG, "Successfully got FCM Token: $fcmToken")

            val registration = DeviceTokenRegistrationDomain(
                fcmToken = fcmToken,
                platform = "ANDROID",
                deviceId = deviceId
            )

            val response = registerDeviceTokenUseCase(registration)

            response.fold(
                onSuccess = {
                    setRegisteredFcmTokenUseCase(fcmToken)
                    Log.d(TAG, "FCM Token registered successfully on backend")
                    Result.success()
                },
                onError = { error ->
                    Log.w(TAG, "Server error registering token: $error, retrying...")
                    Result.retry()
                }
            )
        } catch (e: IOException) {
            Log.w(TAG, "FCM Service or network unavailable. WorkManager will retry later.", e)
            Result.retry()
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            Log.e(TAG, "Unexpected error in RegisterTokenWorker", e)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "RegisterTokenWorker"
        const val KEY_FCM_TOKEN = "key_fcm_token"
        const val KEY_DEVICE_ID = "key_device_id"
    }
}