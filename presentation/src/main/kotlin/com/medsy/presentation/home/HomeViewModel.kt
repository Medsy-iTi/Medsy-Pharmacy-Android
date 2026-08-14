package com.medsy.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.dashboard.model.DashboardPeriod
import com.medsy.domain.dashboard.usecase.GetPharmacyDashboardUseCase
import com.medsy.domain.dashboard.usecase.GetAiDashboardSummaryUseCase
import com.medsy.domain.notifications.usecase.GetUnreadCountUseCase
import com.medsy.domain.orders.usecase.GetPharmacyOrdersUseCase
import com.medsy.domain.pharmacy.usecase.GetMyPharmacyUseCase
import com.medsy.presentation.common.util.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMyPharmacy: GetMyPharmacyUseCase,
    private val getDashboard: GetPharmacyDashboardUseCase,
    private val getPharmacyOrders: GetPharmacyOrdersUseCase,
    private val getUnreadCount: GetUnreadCountUseCase,
    private val getAiDashboardSummary: GetAiDashboardSummaryUseCase,
) : ViewModel() {
    private val mutableState = MutableStateFlow(HomeUIState())
    val state = mutableState.asStateFlow()

    private val mutableEffect = Channel<HomeUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()

    private var loadJob: Job? = null

    init {
        loadHomeData(userRefresh = false)
    }

    fun onIntent(intent: HomeUIIntent) {
        when (intent) {
            HomeUIIntent.Refresh -> loadHomeData(userRefresh = true)
            is HomeUIIntent.OnOrderClicked -> sendEffect(HomeUIEffect.NavigateToOrderDetails(intent.orderId))
            HomeUIIntent.OnViewAllOrdersClicked -> sendEffect(HomeUIEffect.NavigateToViewAllOrders)
            HomeUIIntent.OnNotificationsClicked -> sendEffect(HomeUIEffect.OpenNotifications)
            HomeUIIntent.RetryAiSummary -> loadAiSummary()
        }
    }

    private fun loadHomeData(userRefresh: Boolean) {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            if (userRefresh && mutableState.value.pharmacy != null) {
                mutableState.update { it.copy(isRefreshing = true, errorRes = null) }
            } else mutableState.update { it.copy(isLoading = true, errorRes = null) }

            launch {
                getUnreadCount().onSuccess { count ->
                    mutableState.update { it.copy(notificationsCount = count) }
                }
            }
            getMyPharmacy(forceRefresh = true)
                .onSuccess { pharmacyData ->
                    mutableState.update { it.copy(pharmacy = pharmacyData) }
                    if (pharmacyData.isAdmin) {
                        loadAiSummary()
                        loadAdminDashboard()
                    } else loadRecentOrders(pharmacyData.id)

                }.onError { error ->
                    mutableState.update { current ->
                        current.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorRes = if (current.pharmacy == null) error.toMessageRes() else null,
                        )
                    }
                }
        }
    }

    private suspend fun loadAdminDashboard() {
        getDashboard(DashboardPeriod.LastMonth)
            .onSuccess { dashboard ->
                mutableState.update {
                    it.copy(
                        dashboard = dashboard,
                        latestOrders = dashboard.recentOrders,
                        isLoading = false,
                        isRefreshing = false,
                        errorRes = null,
                    )
                }
            }.onError { error ->
                mutableState.update { current ->
                    current.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorRes = if (current.latestOrders.isEmpty()) error.toMessageRes() else null,
                    )
                }
            }
    }


    private suspend fun loadRecentOrders(pharmacyId: Long) {


        getPharmacyOrders(pharmacyId, 0, 3, listOf("id,desc"))
            .onSuccess { orderPage ->
                mutableState.update {
                    it.copy(
                        dashboard = null,
                        latestOrders = orderPage.content,
                        isLoading = false,
                        isRefreshing = false,
                        errorRes = null,
                    )
                }
            }.onError { error ->
                mutableState.update { current ->
                    current.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorRes = if (current.latestOrders.isEmpty()) error.toMessageRes() else null,
                    )
                }
            }

    }

    private fun loadAiSummary() {
        if (mutableState.value.isAiSummaryLoading || mutableState.value.pharmacy?.isAdmin != true) return
        mutableState.update { it.copy(isAiSummaryLoading = true, aiSummaryErrorRes = null) }
        viewModelScope.launch {
            getAiDashboardSummary(DashboardPeriod.LastMonth)
                .onSuccess { summary ->
                    mutableState.update {
                        it.copy(
                            aiSummary = summary,
                            isAiSummaryLoading = false,
                            aiSummaryErrorRes = null,
                        )
                    }
                }
                .onError { error ->
                    mutableState.update {
                        it.copy(
                            isAiSummaryLoading = false,
                            aiSummaryErrorRes = error.toMessageRes(),
                        )
                    }
                }
        }
    }


    private fun sendEffect(effect: HomeUIEffect) {
        viewModelScope.launch { mutableEffect.send(effect) }
    }
}
