package com.medsy.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.common.onError
import com.medsy.domain.common.onSuccess
import com.medsy.domain.orders.model.PharmacyOrder
import com.medsy.domain.orders.usecase.GetPharmacyOrdersUseCase
import com.medsy.domain.pharmacy.usecase.GetMyPharmacyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val getMyPharmacyUseCase: GetMyPharmacyUseCase,
    private val getPharmacyOrdersUseCase: GetPharmacyOrdersUseCase,
) : ViewModel() {
    private val mutableState = MutableStateFlow(OrdersUIState())
    val state = mutableState
        .onStart { loadOrders(refresh = true, userRefresh = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), OrdersUIState())

    private val mutableEffect = Channel<OrdersUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()
    private var pharmacyId: Long? = null
    private var nextPage = 0
    private var loadingJob: Job? = null

    fun onIntent(intent: OrdersUIIntent) {
        when (intent) {
            is OrdersUIIntent.SearchQueryChanged -> mutableState.update { it.copy(searchQuery = intent.query) }
            is OrdersUIIntent.FilterSelected -> mutableState.update { it.copy(selectedFilter = intent.filter) }
            is OrdersUIIntent.OrderClicked -> sendEffect(OrdersUIEffect.NavigateToOrderDetails(intent.orderId))
            OrdersUIIntent.Refresh -> loadOrders(refresh = true, userRefresh = true)
            OrdersUIIntent.Retry -> loadOrders(refresh = true, userRefresh = false)
            OrdersUIIntent.LoadMore -> loadOrders(refresh = false, userRefresh = false)
        }
    }

    private fun loadOrders(refresh: Boolean, userRefresh: Boolean) {
        val state = mutableState.value
        if (!refresh && (state.isLoadingMore || !state.canLoadMore)) return
        if (userRefresh && state.isRefreshing) return
        loadingJob?.cancel()
        loadingJob = viewModelScope.launch {
            if (userRefresh) mutableState.update { it.copy(isRefreshing = true) }
            else if (refresh && state.orders.isEmpty()) mutableState.update { it.copy(isLoading = true, hasError = false) }
            else if (!refresh) mutableState.update { it.copy(isLoadingMore = true) }

            val id = resolvePharmacyId()
            if (id == null) {
                mutableState.update { it.copy(isLoading = false, isRefreshing = false, isLoadingMore = false, hasError = it.orders.isEmpty()) }
                return@launch
            }
            getPharmacyOrdersUseCase(
                pharmacyId = id,
                page = if (refresh) 0 else nextPage,
                size = PAGE_SIZE,
                sort = listOf("id,desc"),
            ).onSuccess { page ->
                nextPage = page.pageNumber + 1
                mutableState.update { current ->
                    current.copy(
                        orders = if (refresh) page.content else (current.orders + page.content).distinctBy(PharmacyOrder::id),
                        canLoadMore = !page.last,
                        isLoading = false,
                        isRefreshing = false,
                        isLoadingMore = false,
                        hasError = false,
                    )
                }
            }.onError {
                mutableState.update { current ->
                    current.copy(
                        isLoading = false,
                        isRefreshing = false,
                        isLoadingMore = false,
                        hasError = current.orders.isEmpty(),
                    )
                }
            }
        }
    }

    private suspend fun resolvePharmacyId(): Long? {
        pharmacyId?.let { return it }
        return when (val result = getMyPharmacyUseCase()) {
            is MedsyResult.Success -> result.data.id.also { pharmacyId = it }
            is MedsyResult.Error -> null
        }
    }

    private fun sendEffect(effect: OrdersUIEffect) {
        viewModelScope.launch { mutableEffect.send(effect) }
    }

    private companion object { const val PAGE_SIZE = 20 }
}
