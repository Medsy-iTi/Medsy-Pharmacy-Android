package com.medsy.domain.invitation.repository

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.invitation.model.PharmacyInvitation

interface InvitationRepository {
    suspend fun getPendingInvitationsForPharmacy(pharmacyId: Long): MedsyResult<List<PharmacyInvitation>, MedsyError>
    suspend fun invitePharmacist(pharmacyId: Long, email: String): MedsyResult<PharmacyInvitation, MedsyError>
    suspend fun declineInvitation(invitationId: Long): MedsyResult<PharmacyInvitation, MedsyError>
    suspend fun acceptInvitation(invitationId: Long): MedsyResult<PharmacyInvitation, MedsyError>
    suspend fun getMyPendingInvitations(): MedsyResult<List<PharmacyInvitation>, MedsyError>
    suspend fun getAdminPendingInvitations(): MedsyResult<List<PharmacyInvitation>, MedsyError>
    suspend fun deleteInvitation(invitationId: Long): EmptyMedsyResult<MedsyError>
}
