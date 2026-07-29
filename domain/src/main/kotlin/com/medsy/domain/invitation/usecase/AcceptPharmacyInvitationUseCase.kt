package com.medsy.domain.invitation.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.invitation.model.PharmacyInvitation
import com.medsy.domain.invitation.repository.InvitationRepository
import javax.inject.Inject

class AcceptPharmacyInvitationUseCase @Inject constructor(
    private val repository: InvitationRepository,
) {
    suspend operator fun invoke(invitationId: Long): MedsyResult<PharmacyInvitation, MedsyError> =
        repository.acceptInvitation(invitationId)
}
