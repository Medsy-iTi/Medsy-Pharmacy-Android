package com.medsy.domain.pharmacist.usecase

import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import com.medsy.domain.pharmacist.repository.PharmacistRepository

class RemovePharmacistUseCase(
    private val repository: PharmacistRepository
) {
    suspend operator fun invoke(pharmacistId: Long, pharmacyId: Long): EmptyMedsyResult<MedsyError> {
        return repository.removePharmacistFromPharmacy(pharmacistId, pharmacyId)
    }
}
