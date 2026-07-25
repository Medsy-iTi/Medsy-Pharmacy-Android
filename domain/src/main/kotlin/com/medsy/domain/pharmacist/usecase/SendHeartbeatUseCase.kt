package com.medsy.domain.pharmacist.usecase

import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.pharmacist.model.PresenceStatus
import com.medsy.domain.pharmacist.repository.PharmacistRepository
import javax.inject.Inject

class SendHeartbeatUseCase @Inject constructor(
    private val repository: PharmacistRepository
) {
    suspend operator fun invoke(): MedsyResult<PresenceStatus, MedsyError> {
        return repository.sendHeartbeat()
    }
}
