package com.medsy.domain.pharmacist.repository

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.pharmacist.model.Pharmacist
import com.medsy.domain.pharmacist.model.PresenceStatus

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
    suspend fun removePharmacist(id: Long): MedsyResult<Unit, MedsyError>
    fun clearCache()
    suspend fun setPresence(onDuty: Boolean): MedsyResult<PresenceStatus, MedsyError>
    suspend fun sendHeartbeat(): MedsyResult<PresenceStatus, MedsyError>
}
