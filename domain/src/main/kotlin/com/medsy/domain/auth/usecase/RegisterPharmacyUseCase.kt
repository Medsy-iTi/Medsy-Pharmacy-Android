package com.medsy.domain.auth.usecase

import com.medsy.domain.auth.model.RegisterPharmacyParams
import com.medsy.domain.auth.repository.AuthRepository
import com.medsy.domain.common.EmptyMedsyResult
import com.medsy.domain.common.MedsyError
import javax.inject.Inject

class RegisterPharmacyUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(params: RegisterPharmacyParams): EmptyMedsyResult<MedsyError.Remote> {
        return repository.registerPharmacy(params)
    }
}
