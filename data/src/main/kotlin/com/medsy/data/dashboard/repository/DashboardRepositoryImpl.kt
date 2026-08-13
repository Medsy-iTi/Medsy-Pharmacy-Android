package com.medsy.data.dashboard.repository

import com.medsy.data.dashboard.datasource.DashboardRemoteDataSource
import com.medsy.data.dashboard.mapper.toDomain
import com.medsy.domain.common.MedsyError
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.map
import com.medsy.domain.dashboard.model.DashboardPeriod
import com.medsy.domain.dashboard.model.PharmacyDashboard
import com.medsy.domain.dashboard.repository.DashboardRepository
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val remoteDataSource: DashboardRemoteDataSource,
) : DashboardRepository {
    override suspend fun getDashboard(
        period: DashboardPeriod,
    ): MedsyResult<PharmacyDashboard, MedsyError.Remote> =
        remoteDataSource.getDashboard(period.apiValue).map { it.toDomain() }
}
