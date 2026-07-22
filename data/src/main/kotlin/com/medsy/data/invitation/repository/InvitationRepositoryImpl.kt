package com.medsy.data.invitation.repository

import com.medsy.data.invitation.mapper.toDomain
import com.medsy.data.invitation.remote.api.InvitationApi
import com.medsy.data.invitation.remote.dto.InvitePharmacistRequestDto
import com.medsy.data.remote.network.safeApiCall
import com.medsy.data.remote.network.safeEmptyRestCall
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.invitation.model.PharmacyInvitation
import com.medsy.domain.invitation.repository.InvitationRepository
import javax.inject.Inject

class InvitationRepositoryImpl @Inject constructor(
    private val api: InvitationApi
) : InvitationRepository {
    override suspend fun getPendingInvitationsForPharmacy(pharmacyId: Long): MedsyResult<List<PharmacyInvitation>, MedsyError> {
        return safeApiCall { api.getPendingInvitationsForPharmacy(pharmacyId) }.map { list -> list.map { it.toDomain() } }
    }

    override suspend fun invitePharmacist(pharmacyId: Long, email: String): MedsyResult<PharmacyInvitation, MedsyError> {
        return safeApiCall { api.invitePharmacist(pharmacyId, InvitePharmacistRequestDto(email)) }.map { it.toDomain() }
    }

    override suspend fun declineInvitation(invitationId: Long): MedsyResult<PharmacyInvitation, MedsyError> {
        return safeApiCall { api.declineInvitation(invitationId) }.map { it.toDomain() }
    }

    override suspend fun acceptInvitation(invitationId: Long): MedsyResult<PharmacyInvitation, MedsyError> {
        return safeApiCall { api.acceptInvitation(invitationId) }.map { it.toDomain() }
    }

    override suspend fun getMyPendingInvitations(): MedsyResult<List<PharmacyInvitation>, MedsyError> {
        return safeApiCall { api.getMyPendingInvitations() }.map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getAdminPendingInvitations(): MedsyResult<List<PharmacyInvitation>, MedsyError> {
        return safeApiCall { api.getAdminPendingInvitations() }.map { list -> list.map { it.toDomain() } }
    }

    override suspend fun deleteInvitation(invitationId: Long): EmptyMedsyResult<MedsyError> {
        return safeEmptyRestCall { api.deleteInvitation(invitationId) }
    }
}
