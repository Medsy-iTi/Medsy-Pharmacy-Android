package com.medsy.domain.common.device

interface DeviceRepository {
    suspend fun getDeviceId(): String
}
