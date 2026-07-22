package com.medsy.domain.auth.usecase

import com.medsy.domain.auth.model.PharmacyApprovalStatus
import com.medsy.domain.auth.repository.SessionRepository
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.onSuccess
import com.medsy.domain.pharmacy.model.MyPharmacy
import com.medsy.domain.pharmacy.model.RegisterPharmacyParams
import com.medsy.domain.pharmacy.repository.PharmacyRepository
import javax.inject.Inject

class RegisterPharmacyUseCase @Inject constructor(
    private val repository: PharmacyRepository,
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(
        params: RegisterPharmacyParams,
    ): MedsyResult<MyPharmacy, MedsyError> {
        if (params.name.isBlank()) {
            return MedsyResult.Error(MedsyError.Validation.REQUIRED_FIELDS)
        }

        if (params.latitude !in MIN_LATITUDE..MAX_LATITUDE ||
            params.longitude !in MIN_LONGITUDE..MAX_LONGITUDE
        ) {
            return MedsyResult.Error(MedsyError.Validation.INVALID_LOCATION)
        }

        if (params.licensePdfBytes.isEmpty()) {
            return MedsyResult.Error(MedsyError.Validation.INVALID_LICENSE_DOCUMENT)
        }

        if (params.licensePdfBytes.size > MAX_LICENSE_PDF_BYTES) {
            return MedsyResult.Error(MedsyError.Validation.PDF_TOO_LARGE)
        }

        return repository.registerPharmacy(params)
            .onSuccess {
                sessionRepository.updateApprovalStatus(PharmacyApprovalStatus.PendingApproval)
            }
    }

    private companion object {
        const val MIN_LATITUDE = -90.0
        const val MAX_LATITUDE = 90.0
        const val MIN_LONGITUDE = -180.0
        const val MAX_LONGITUDE = 180.0
        const val MAX_LICENSE_PDF_BYTES = 1_048_576
    }
}
