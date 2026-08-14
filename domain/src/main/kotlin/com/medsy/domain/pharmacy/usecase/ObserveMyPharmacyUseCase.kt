package com.medsy.domain.pharmacy.usecase

import com.medsy.domain.pharmacy.model.MyPharmacy
import com.medsy.domain.pharmacy.repository.PharmacyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMyPharmacyUseCase @Inject constructor(
    private val pharmacyRepository: PharmacyRepository
) {
    operator fun invoke(): Flow<MyPharmacy?> {
        return pharmacyRepository.pharmacyFlow
    }
}
