package com.medsy.domain.dashboard.usecase

import com.medsy.domain.dashboard.model.DashboardPeriod
import com.medsy.domain.dashboard.repository.DashboardRepository
import javax.inject.Inject

class GetAiDashboardSummaryUseCase @Inject constructor(
    private val repository: DashboardRepository,
) {
    suspend operator fun invoke(period: DashboardPeriod) = repository.getAiSummary(period)
}
