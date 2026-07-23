package com.medsy.domain.auth.usecase

import com.medsy.domain.auth.model.AuthSession
import com.medsy.domain.auth.model.AuthUserRole
import com.medsy.domain.auth.model.PharmacyApprovalStatus
import com.medsy.domain.auth.repository.AuthRepository
import com.medsy.domain.auth.repository.SessionRepository
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.flatMap
import com.medsy.domain.common.fold
import com.medsy.domain.common.device.DeviceRepository
import com.medsy.domain.pharmacist.repository.PharmacistRepository
import com.medsy.domain.pharmacy.repository.PharmacyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginPharmacistUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val pharmacyRepository: PharmacyRepository,
    private val sessionRepository: SessionRepository,
    private val pharmacistRepository: PharmacistRepository,
    private val deviceRepository: DeviceRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): MedsyResult<PharmacistLoginOutcome, MedsyError> {

        return authRepository.login(email.trim(), password)
            .flatMap { authSession ->
                if (authSession.user.role != AuthUserRole.PHARMACIST) {
                    sessionRepository.clearSession()
                    return@flatMap MedsyResult.Error(MedsyError.Auth.INVALID_ROLE)
                }

                sessionRepository.savePharmacistSession(
                    authSession,
                    PharmacyApprovalStatus.NoPharmacy,
                )

                registerDeviceTokenAsync()

                return@flatMap resolvePharmacy(authSession)
            }

    }

    private suspend fun resolvePharmacy(
        authSession: AuthSession
    ): MedsyResult<PharmacistLoginOutcome, MedsyError> {

        pharmacyRepository.getMyPharmacy()
            .fold(
                onSuccess = {
                    sessionRepository.savePharmacistSession(
                        authSession,
                        PharmacyApprovalStatus.Approved
                    )

                    return MedsyResult.Success(PharmacistLoginOutcome.Approved)
                },
                onError = { error ->
                    when (error) {
                        MedsyError.Auth.NO_PHARMACY -> {
                            sessionRepository.savePharmacistSession(
                                authSession,
                                PharmacyApprovalStatus.NoPharmacy
                            )
                            return MedsyResult.Success(PharmacistLoginOutcome.NoPharmacy)
                        }

                        else -> {
                            sessionRepository.clearSession()
                            return MedsyResult.Error(error)
                        }
                    }
                }
            )
    }

    private fun registerDeviceTokenAsync() {
        CoroutineScope(Dispatchers.IO).launch {
            val fcmToken = deviceRepository.getFcmToken()
            if (fcmToken != null) {
                val deviceId = deviceRepository.getDeviceId()
                pharmacistRepository.registerDeviceToken(fcmToken, deviceId)
            }
        }
    }
}

sealed interface PharmacistLoginOutcome {

    data object Approved : PharmacistLoginOutcome

    data object NoPharmacy : PharmacistLoginOutcome

    data class ApprovalRequired(
        val status: PharmacyApprovalStatus
    ) : PharmacistLoginOutcome
}