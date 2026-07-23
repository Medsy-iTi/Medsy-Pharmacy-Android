package com.medsy.data.common.device

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.google.firebase.messaging.FirebaseMessaging
import com.medsy.domain.common.device.DeviceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DeviceRepository {

    override suspend fun getFcmToken(): String? {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @SuppressLint("HardwareIds")
    override fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}
