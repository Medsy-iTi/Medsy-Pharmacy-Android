package com.medsy.domain.auth.usecase

import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.auth.model.AuthUserRole
import com.medsy.domain.auth.model.PharmacyApprovalStatus
import com.medsy.domain.auth.repository.AuthRepository
import com.medsy.domain.auth.repository.SessionRepository
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.onSuccess
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(
        email: String,
        otpCode: String,
    ): MedsyResult<AuthSession, MedsyError> {
        val cleanCode = otpCode.trim()
        if (cleanCode.length != 6 || !cleanCode.all { it.isDigit() }) {
            return MedsyResult.Error(MedsyError.Validation.INVALID_OTP)
        }
        val result = repository.verifyOtp(email, cleanCode)
        return when (result) {
            is MedsyResult.Error -> result
            is MedsyResult.Success -> {
                if (result.data.user.role != AuthUserRole.PHARMACIST) {
                    sessionRepository.clearSession()
                    MedsyResult.Error(MedsyError.Auth.INVALID_ROLE)
                } else {
                    MedsyResult.Success(result.data)
                        .onSuccess {
                            sessionRepository.savePharmacistSession(
                                session = it,
                                approvalStatus = PharmacyApprovalStatus.NoPharmacy,
                            )
                        }
                }
            }
        }
    }
}
