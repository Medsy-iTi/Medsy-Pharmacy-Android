package com.medsy.domain.dashboard.usecase

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.dashboard.model.DashboardPeriod
import com.medsy.domain.dashboard.model.PharmacyDashboard
import com.medsy.domain.dashboard.repository.DashboardRepository
import javax.inject.Inject

class GetPharmacyDashboardUseCase @Inject constructor(
    private val repository: DashboardRepository,
) {
    suspend operator fun invoke(
        period: DashboardPeriod,
    ): MedsyResult<PharmacyDashboard, MedsyError.Remote> = repository.getDashboard(period)
}
