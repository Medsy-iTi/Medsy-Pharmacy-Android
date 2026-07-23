package com.medsy.domain.common.device

interface DeviceRepository {
    suspend fun getFcmToken(): String?
    fun getDeviceId(): String
}
