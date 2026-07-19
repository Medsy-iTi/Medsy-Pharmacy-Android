package com.medsy.presentation.home

data class HomeUIState(
    val pharmacyInfo: PharmacyUIInfo = PharmacyUIInfo(),
    val stats: HomeStatsUI = HomeStatsUI(),
    val latestOrders: List<HomeOrderUI> = emptyList(),
    val isLoading: Boolean = false,
    val errorRes: Int? = null,
    val notificationsCount: Int = 0
)

data class PharmacyUIInfo(
    val name: String = "",
    val address: String = "",
    val isOpen: Boolean = false,
    val closingTime: String = "",
    val rating: Double = 0.0,
    val reviewsCount: Int = 0,
    val pharmacyId: String = ""
)

data class HomeStatsUI(
    val newOrders: Int = 0,
    val inProgress: Int = 0,
    val deliveredToday: Int = 0,
    val totalSales: String = "0"
)

data class HomeOrderUI(
    val id: String,
    val customerName: String,
    val location: String,
    val timeAgo: String,
    val status: HomeOrderStatus
)

enum class HomeOrderStatus {
    NEW,
    PREPARING,
    DELIVERED
}
