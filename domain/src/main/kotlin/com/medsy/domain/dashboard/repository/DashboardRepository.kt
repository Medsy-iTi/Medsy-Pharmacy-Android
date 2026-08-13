package com.medsy.domain.dashboard.repository

import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.dashboard.model.DashboardPeriod
import com.medsy.domain.dashboard.model.PharmacyDashboard

interface DashboardRepository {
    suspend fun getDashboard(
        period: DashboardPeriod,
    ): MedsyResult<PharmacyDashboard, MedsyError.Remote>
}
