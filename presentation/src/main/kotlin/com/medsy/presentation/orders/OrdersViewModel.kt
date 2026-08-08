package com.medsy.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medsy.domain.common.MedsyResult
import com.medsy.domain.offer.model.Offer
import com.medsy.domain.offer.model.OfferStatusConstants
import com.medsy.domain.offer.model.PaginatedOffers
import com.medsy.domain.offer.usecase.GetPharmacyOffersUseCase
import com.medsy.domain.orders.model.OrderStatusConstants
import com.medsy.domain.orders.model.PharmacyOrderDomain
import com.medsy.domain.orders.model.PharmacyOrderPageDomain
import com.medsy.domain.orders.usecase.GetPharmacyOrdersUseCase
import com.medsy.domain.pharmacy.usecase.GetMyPharmacyUseCase
import com.medsy.presentation.requests.OrdersFilter
import com.medsy.presentation.requests.PaymentMethod
import com.medsy.presentation.requests.PharmacyWorkItem
import com.medsy.presentation.requests.PharmacyWorkSource
import com.medsy.presentation.requests.PharmacyWorkStatus
import com.medsy.presentation.requests.calculateMinutesAgoOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
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
    private val getPharmacyOffersUseCase: GetPharmacyOffersUseCase,
    private val getPharmacyOrdersUseCase: GetPharmacyOrdersUseCase,
) : ViewModel() {
    private val mutableState = MutableStateFlow(OrdersUIState())
    val state = mutableState
        .onStart { loadSelected(refresh = true) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), OrdersUIState())

    private val mutableEffect = Channel<OrdersUIEffect>(Channel.BUFFERED)
    val effect = mutableEffect.receiveAsFlow()

    private var pharmacyId: Long? = null
    private var offersPage = 0
    private var ordersPage = 0
    private var offersLoaded = false
    private var ordersLoaded = false

    fun onIntent(intent: OrdersUIIntent) {
        when (intent) {
            is OrdersUIIntent.SearchQueryChanged -> mutableState.update { it.copy(searchQuery = intent.query) }
            is OrdersUIIntent.FilterSelected -> {
                mutableState.update { it.copy(selectedFilter = intent.filter) }
                val needsLoad = when (intent.filter) {
                    OrdersFilter.All -> !offersLoaded || !ordersLoaded
                    OrdersFilter.ActiveOffers, OrdersFilter.RejectedOffers -> !offersLoaded
                    OrdersFilter.InProgress, OrdersFilter.Delivered -> !ordersLoaded
                }
                if (needsLoad) loadSelected(refresh = true)
            }

            is OrdersUIIntent.ItemClicked -> when (intent.source) {
                PharmacyWorkSource.Offer -> sendEffect(OrdersUIEffect.NavigateToOfferDetails(intent.id))
                PharmacyWorkSource.Order -> sendEffect(OrdersUIEffect.NavigateToOrderDetails(intent.id))
                PharmacyWorkSource.Request -> Unit
            }

            OrdersUIIntent.Refresh, OrdersUIIntent.Retry -> loadSelected(refresh = true)
            OrdersUIIntent.LoadMore -> loadSelected(refresh = false)
        }
    }

    private fun loadSelected(refresh: Boolean) {
        if (!refresh && mutableState.value.isLoadingMore) return
        viewModelScope.launch {
            val id = resolvePharmacyId() ?: return@launch
            if (refresh) mutableState.update { it.copy(isLoading = true, hasError = false) }
            else mutableState.update { it.copy(isLoadingMore = true) }

            val filter = mutableState.value.selectedFilter
            val success = coroutineScope {
                when (filter) {
                    OrdersFilter.All -> {
                        val offersResult = async { loadOffers(id, refresh) }
                        val ordersResult = async { loadOrders(id, refresh) }
                        offersResult.await() && ordersResult.await()
                    }

                    OrdersFilter.ActiveOffers, OrdersFilter.RejectedOffers -> loadOffers(
                        id,
                        refresh
                    )

                    OrdersFilter.InProgress, OrdersFilter.Delivered -> loadOrders(id, refresh)
                }
            }
            mutableState.update { current ->
                current.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    hasError = !success && current.visibleItems.isEmpty(),
                )
            }
        }
    }

    private suspend fun resolvePharmacyId(): Long? {
        pharmacyId?.let { return it }
        return when (val result = getMyPharmacyUseCase()) {
            is MedsyResult.Success -> result.data.id.also { pharmacyId = it }
            is MedsyResult.Error -> {
                mutableState.update {
                    it.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        hasError = true
                    )
                }
                null
            }
        }
    }

    private suspend fun loadOffers(pharmacyId: Long, refresh: Boolean): Boolean {
        if (!refresh && !mutableState.value.canLoadMoreOffers) return true
        val page = if (refresh) 0 else offersPage
        return when (val result =
            getPharmacyOffersUseCase(pharmacyId, page, PAGE_SIZE, listOf("id,desc"))) {
            is MedsyResult.Success -> {
                applyOffers(result.data, refresh)
                true
            }

            is MedsyResult.Error -> false
        }
    }

    private suspend fun loadOrders(pharmacyId: Long, refresh: Boolean): Boolean {
        if (!refresh && !mutableState.value.canLoadMoreOrders) return true
        val page = if (refresh) 0 else ordersPage
        return when (val result =
            getPharmacyOrdersUseCase(pharmacyId, page, PAGE_SIZE, listOf("createdAt,desc"))) {
            is MedsyResult.Success -> {
                applyOrders(result.data, refresh)
                true
            }

            is MedsyResult.Error -> false
        }
    }

    private fun applyOffers(page: PaginatedOffers, refresh: Boolean) {
        val incoming = page.content.mapNotNull(Offer::toWorkItem)
        offersLoaded = true
        offersPage = page.pageNumber + 1
        mutableState.update { current ->
            current.copy(
                offers = if (refresh) incoming else (current.offers + incoming).distinctBy { it.id },
                canLoadMoreOffers = !page.last,
            )
        }
    }

    private fun applyOrders(page: PharmacyOrderPageDomain, refresh: Boolean) {
        val incoming = page.content.mapNotNull(PharmacyOrderDomain::toWorkItem)
        ordersLoaded = true
        ordersPage = page.pageNumber + 1
        mutableState.update { current ->
            current.copy(
                orders = if (refresh) incoming else (current.orders + incoming).distinctBy { it.id },
                canLoadMoreOrders = !page.last,
            )
        }
    }

    private fun sendEffect(effect: OrdersUIEffect) {
        viewModelScope.launch { mutableEffect.send(effect) }
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}

private fun Offer.toWorkItem(): PharmacyWorkItem? {
    val workStatus = when (status.uppercase()) {
        OfferStatusConstants.PENDING -> PharmacyWorkStatus.WaitingForCustomer
        OfferStatusConstants.REJECTED -> PharmacyWorkStatus.RejectedOffer
        OfferStatusConstants.ACCEPTED,
        OfferStatusConstants.PARTIALLY_ACCEPTED,
        OfferStatusConstants.EXPIRED -> return null

        else -> return null
    }
    return PharmacyWorkItem(
        id = id,
        displayId = id.toString(),
        source = PharmacyWorkSource.Offer,
        status = workStatus,
        createdAt = null,
        minutesAgo = null,
        customerName = null,
        customerId = null,
        customerPhone = null,
        customerAddress = null,
        productImages = items.map { it.imageUrl },
        total = null,
        paymentMethod = null,
    )
}

private fun PharmacyOrderDomain.toWorkItem(): PharmacyWorkItem? {
    val workStatus = when (status.uppercase()) {
        OrderStatusConstants.PENDING,
        OrderStatusConstants.CONFIRMED,
        OrderStatusConstants.PREPARING,
        OrderStatusConstants.READY_FOR_PICKUP -> PharmacyWorkStatus.Preparing

        OrderStatusConstants.OUT_FOR_DELIVERY -> PharmacyWorkStatus.OnTheWay
        OrderStatusConstants.DELIVERED -> PharmacyWorkStatus.Delivered
        OrderStatusConstants.CANCELLED -> return null
        else -> return null
    }
    return PharmacyWorkItem(
        id = id,
        displayId = id.toString(),
        source = PharmacyWorkSource.Order,
        status = workStatus,
        createdAt = createdAt,
        minutesAgo = calculateMinutesAgoOrNull(createdAt),
        customerName = customerName,
        customerId = customerId,
        customerPhone = customerPhone,
        customerAddress = deliveryAddress,
        productImages = items.map { it.imageUrl },
        total = total,
        paymentMethod = PaymentMethod.fromApiValue(paymentMethod),
    )
}
