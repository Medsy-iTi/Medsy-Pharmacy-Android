package com.medsy.data.dashboard.datasource

import com.medsy.data.dashboard.remote.DashboardApi
import com.medsy.data.dashboard.remote.AiDashboardApi
import com.medsy.data.dashboard.remote.PharmacyDashboardDto
import com.medsy.data.remote.network.safeApiCall
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import javax.inject.Inject

class DashboardRemoteDataSourceImpl @Inject constructor(
    private val api: DashboardApi,
    private val aiApi: AiDashboardApi,
) : DashboardRemoteDataSource {
    override suspend fun getDashboard(
        period: String,
    ): MedsyResult<PharmacyDashboardDto, MedsyError.Remote> =
        safeApiCall { api.getDashboard(period) }

    override suspend fun getAiSummary(period: String) =
        safeApiCall { aiApi.getSummary(period) }
}
