package com.medsy.data.dashboard.datasource

import com.medsy.data.dashboard.remote.PharmacyDashboardDto
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult

interface DashboardRemoteDataSource {
    suspend fun getDashboard(
        period: String,
    ): MedsyResult<PharmacyDashboardDto, MedsyError.Remote>
}
