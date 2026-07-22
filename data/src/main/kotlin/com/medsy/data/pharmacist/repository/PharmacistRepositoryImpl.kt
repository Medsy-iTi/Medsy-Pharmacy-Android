package com.medsy.data.pharmacist.repository

import com.medsy.data.pharmacist.mapper.toDomain
import com.medsy.data.pharmacist.remote.api.PharmacistApi
import com.medsy.data.remote.network.safeApiCall
import com.medsy.data.remote.network.safeEmptyRestCall
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.pharmacist.model.Pharmacist
import com.medsy.domain.pharmacist.repository.PharmacistRepository
import javax.inject.Inject

class PharmacistRepositoryImpl @Inject constructor(
    private val api: PharmacistApi
) : PharmacistRepository {
    private var cachedPharmacist: Pharmacist? = null

    override suspend fun getCurrentPharmacist(forceRefresh: Boolean): MedsyResult<Pharmacist, MedsyError> {
        if (!forceRefresh && cachedPharmacist != null) {
            return MedsyResult.Success(cachedPharmacist!!)
        }
        val result = safeApiCall { api.getCurrentPharmacist() }.map { it.toDomain() }
        if (result is MedsyResult.Success) {
            cachedPharmacist = result.data
        }
        return result
    }

    override suspend fun updateCurrentPharmacist(
        firstName: String?,
        lastName: String?,
        homeAddress: String?,
        dob: String?
    ): MedsyResult<Pharmacist, MedsyError> {
        val result = safeApiCall { 
            api.updateCurrentPharmacist(
                com.medsy.data.pharmacist.remote.dto.UpdatePharmacistRequestDto(
                    firstName = firstName,
                    lastName = lastName,
                    homeAddress = homeAddress,
                    dob = dob
                )
            ) 
        }.map { it.toDomain() }
        if (result is MedsyResult.Success) {
            cachedPharmacist = result.data
        }
        return result
    }

    override suspend fun removePharmacistFromPharmacy(pharmacistId: Long, pharmacyId: Long): EmptyMedsyResult<MedsyError> {
        return safeEmptyRestCall { api.removePharmacistFromPharmacy(pharmacistId, pharmacyId) }
    }

    override suspend fun leavePharmacy(pharmacyId: Long): EmptyMedsyResult<MedsyError> {
        return safeEmptyRestCall { api.leavePharmacy(pharmacyId) }
    }

    override suspend fun setPresence(onDuty: Boolean): MedsyResult<com.medsy.domain.pharmacist.model.PresenceStatus, MedsyError> {
        return safeApiCall {
            if (onDuty) api.onDuty() else api.offDuty()
        }.map { dto ->
            com.medsy.domain.pharmacist.model.PresenceStatus(
                onDuty = dto.onDuty ?: false,
                lastHeartbeatAt = dto.lastHeartbeatAt.orEmpty()
            )
        }
    }
}
