package com.medsy.domain.pharmacist.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.pharmacist.model.Pharmacist
import com.medsy.domain.pharmacist.repository.PharmacistRepository
import javax.inject.Inject

class UpdateCurrentPharmacistUseCase @Inject constructor(
    private val repository: PharmacistRepository
) {
    suspend operator fun invoke(
        firstName: String?,
        lastName: String?,
        homeAddress: String?,
        dob: String?
    ): MedsyResult<Pharmacist, MedsyError> {
        return repository.updateCurrentPharmacist(
            firstName = firstName,
            lastName = lastName,
            homeAddress = homeAddress,
            dob = dob
        )
    }
}
