package com.medsy.domain.pharmacist.repository

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.pharmacist.model.Pharmacist

interface PharmacistRepository {
    suspend fun getCurrentPharmacist(forceRefresh: Boolean = false): MedsyResult<Pharmacist, MedsyError>
    suspend fun updateCurrentPharmacist(
        firstName: String?,
        lastName: String?,
        homeAddress: String?,
        dob: String?
    ): MedsyResult<Pharmacist, MedsyError>
    suspend fun removePharmacistFromPharmacy(pharmacistId: Long, pharmacyId: Long): EmptyMedsyResult<MedsyError>
    suspend fun leavePharmacy(pharmacyId: Long): EmptyMedsyResult<MedsyError>
    suspend fun setPresence(onDuty: Boolean): MedsyResult<com.medsy.domain.pharmacist.model.PresenceStatus, MedsyError>
    suspend fun sendHeartbeat(): MedsyResult<com.medsy.domain.pharmacist.model.PresenceStatus, MedsyError>
}
