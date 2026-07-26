package com.medsy.domain.invitation.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.invitation.model.PharmacyInvitation
import com.medsy.domain.invitation.repository.InvitationRepository
import javax.inject.Inject

class GetMyPendingInvitationsUseCase @Inject constructor(
    private val repository: InvitationRepository,
) {
    suspend operator fun invoke(): MedsyResult<List<PharmacyInvitation>, MedsyError> =
        repository.getMyPendingInvitations()
}
