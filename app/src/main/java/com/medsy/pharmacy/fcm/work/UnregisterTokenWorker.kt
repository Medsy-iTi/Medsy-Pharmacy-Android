package com.medsy.pharmacy.fcm.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.medsy.domain.common.fold
import com.medsy.domain.notifications.usecase.UnregisterDeviceTokenUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UnregisterTokenWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val unregisterDeviceTokenUseCase: UnregisterDeviceTokenUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val fcmToken = inputData.getString(KEY_FCM_TOKEN) ?: return Result.failure()

        val response = unregisterDeviceTokenUseCase(fcmToken)
        return response.fold(
            onSuccess = {
                Result.success()
            },
            onError = {
                Result.retry()
            }
        )
    }

    companion object {
        const val KEY_FCM_TOKEN = "key_fcm_token"
    }
}
