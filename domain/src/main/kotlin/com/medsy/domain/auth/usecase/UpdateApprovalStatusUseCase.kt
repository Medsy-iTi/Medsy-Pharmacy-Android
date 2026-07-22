package com.medsy.domain.auth.usecase

import com.medsy.domain.auth.model.PharmacyApprovalStatus
import com.medsy.domain.auth.repository.SessionRepository
import javax.inject.Inject

class UpdateApprovalStatusUseCase @Inject constructor(
    private val repository: SessionRepository,
) {
    suspend operator fun invoke(status: PharmacyApprovalStatus) {
        repository.updateApprovalStatus(status)
    }
}
