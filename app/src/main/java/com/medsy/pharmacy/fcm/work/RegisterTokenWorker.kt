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
            // 1. جلب ה- FCM Token مباشرة من داخل الـ Worker
            val fcmToken = FirebaseMessaging.getInstance().token.await()

            // 2. جلب Device ID بأمان
            val deviceId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            ) ?: "android_device"

            val registration = DeviceTokenRegistrationDomain(
                fcmToken = fcmToken,
                platform = "ANDROID",
                deviceId = deviceId
            )

            // 3. إرسال الـ Token للـ Backend
            val response = registerDeviceTokenUseCase(registration)

            response.fold(
                onSuccess = {
                    setRegisteredFcmTokenUseCase(fcmToken)
                    Log.d(TAG, "FCM Token registered successfully")
                    Result.success()
                },
                onError = { error ->
                    Log.w(TAG, "Server error registering token: $error, retrying...")
                    Result.retry()
                }
            )
        } catch (e: IOException) {
            // يتعامل مع خطأ SERVICE_NOT_AVAILABLE وانقطاع الإنترنت عند الاتصال بشركة Google
            Log.w(TAG, "FCM Service or network unavailable. WorkManager will retry later.", e)
            Result.retry()
        } catch (e: Exception) {
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