package com.medsy.presentation.home

import com.medsy.domain.dashboard.model.PharmacyDashboard
import com.medsy.domain.dashboard.model.AiDashboardSummary
import com.medsy.domain.orders.model.PharmacyOrder
import com.medsy.domain.pharmacy.model.MyPharmacy

data class HomeUIState(
    val pharmacy: MyPharmacy? = null,
    val dashboard: PharmacyDashboard? = null,
    val latestOrders: List<PharmacyOrder> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorRes: Int? = null,
    val notificationsCount: Int = 0,
    val aiSummary: AiDashboardSummary? = null,
    val isAiSummaryLoading: Boolean = false,
    val aiSummaryErrorRes: Int? = null,
)
