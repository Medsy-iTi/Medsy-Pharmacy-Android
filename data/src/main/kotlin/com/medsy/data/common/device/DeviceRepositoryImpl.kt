package com.medsy.data.common.device

import android.content.Context
import android.provider.Settings
import com.medsy.domain.common.device.DeviceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DeviceRepository {
    override suspend fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "android_device"
    }
}
