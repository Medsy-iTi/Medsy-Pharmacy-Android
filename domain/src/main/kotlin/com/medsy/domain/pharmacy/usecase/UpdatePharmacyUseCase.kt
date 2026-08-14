package com.medsy.domain.pharmacy.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.pharmacy.model.MyPharmacy
import com.medsy.domain.pharmacy.model.UpdatePharmacyParams
import com.medsy.domain.pharmacy.repository.PharmacyRepository
import javax.inject.Inject

class UpdatePharmacyUseCase @Inject constructor(
    private val pharmacyRepository: PharmacyRepository
) {
    suspend operator fun invoke(params: UpdatePharmacyParams): MedsyResult<MyPharmacy, MedsyError.Remote> {
        return pharmacyRepository.updatePharmacy(params)
    }
}
