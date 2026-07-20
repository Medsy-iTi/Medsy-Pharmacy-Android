package com.medsy.domain.pharmacy.usecase

import com.medsy.domain.pharmacy.repository.PharmacyRepository
import javax.inject.Inject

class GetMyPharmacyUseCase @Inject constructor(
    private val repository: PharmacyRepository,
) {
    suspend operator fun invoke() = repository.getMyPharmacy()
}
