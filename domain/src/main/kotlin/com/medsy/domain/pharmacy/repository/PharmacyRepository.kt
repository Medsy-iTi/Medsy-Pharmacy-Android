package com.medsy.domain.pharmacy.repository

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.pharmacy.model.MyPharmacy
import com.medsy.domain.pharmacy.model.RegisterPharmacyParams

interface PharmacyRepository {
    suspend fun getMyPharmacy(forceRefresh: Boolean = false): MedsyResult<MyPharmacy, MedsyError>
    suspend fun registerPharmacy(
        params: RegisterPharmacyParams,
    ): MedsyResult<MyPharmacy, MedsyError.Remote>
    suspend fun updatePharmacy(
        params: com.medsy.domain.pharmacy.model.UpdatePharmacyParams,
    ): MedsyResult<MyPharmacy, MedsyError.Remote>
    val pharmacyFlow: kotlinx.coroutines.flow.Flow<MyPharmacy?>
    fun clearCache()
}
