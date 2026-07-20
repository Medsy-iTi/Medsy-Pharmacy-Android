package com.medsy.domain.invitation.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.invitation.model.PharmacyInvitation
import com.medsy.domain.invitation.repository.InvitationRepository

class InvitePharmacistUseCase(
    private val repository: InvitationRepository
) {
    suspend operator fun invoke(pharmacyId: Long, email: String): MedsyResult<PharmacyInvitation, MedsyError> {
        return repository.invitePharmacist(pharmacyId, email)
    }
}
