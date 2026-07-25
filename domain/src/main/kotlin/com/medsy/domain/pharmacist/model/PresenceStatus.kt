package com.medsy.domain.pharmacist.model

data class PresenceStatus(
    val onDuty: Boolean,
    val lastHeartbeatAt: String
)
