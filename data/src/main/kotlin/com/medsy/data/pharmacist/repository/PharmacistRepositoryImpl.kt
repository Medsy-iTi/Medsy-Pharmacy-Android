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
    override suspend fun getCurrentPharmacist(): MedsyResult<Pharmacist, MedsyError> {
        return safeApiCall { api.getCurrentPharmacist() }.map { it.toDomain() }
    }

    override suspend fun updateCurrentPharmacist(
        firstName: String?,
        lastName: String?,
        homeAddress: String?,
        dob: String?
    ): MedsyResult<Pharmacist, MedsyError> {
        return MedsyResult.Error(MedsyError.Remote.Unknown)
    }

    override suspend fun removePharmacistFromPharmacy(pharmacistId: Long, pharmacyId: Long): EmptyMedsyResult<MedsyError> {
        return safeEmptyRestCall { api.removePharmacistFromPharmacy(pharmacistId, pharmacyId) }
    }

    override suspend fun leavePharmacy(pharmacyId: Long): EmptyMedsyResult<MedsyError> {
        return safeEmptyRestCall { api.leavePharmacy(pharmacyId) }
    }
}
