package com.medsy.presentation.orders

import com.medsy.presentation.requests.OrdersFilter
import com.medsy.presentation.requests.PharmacyWorkItem
import com.medsy.presentation.requests.PharmacyWorkSource
import com.medsy.presentation.requests.PharmacyWorkStatus
import com.medsy.presentation.requests.matchesQuery

sealed interface OrdersUIEffect {
    data class NavigateToOfferDetails(val offerId: Long) : OrdersUIEffect
    data class NavigateToOrderDetails(val orderId: Long) : OrdersUIEffect
}

sealed interface OrdersUIIntent {
    data class SearchQueryChanged(val query: String) : OrdersUIIntent
    data class FilterSelected(val filter: OrdersFilter) : OrdersUIIntent
    data class ItemClicked(val id: Long, val source: PharmacyWorkSource) : OrdersUIIntent
    data object Refresh : OrdersUIIntent
    data object Retry : OrdersUIIntent
    data object LoadMore : OrdersUIIntent
}


data class OrdersUIState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val hasError: Boolean = false,
    val selectedFilter: OrdersFilter = OrdersFilter.All,
    val searchQuery: String = "",
    val offers: List<PharmacyWorkItem> = emptyList(),
    val orders: List<PharmacyWorkItem> = emptyList(),
    val canLoadMoreOffers: Boolean = false,
    val canLoadMoreOrders: Boolean = false,
) {
    val visibleItems: List<PharmacyWorkItem>
        get() {
            val selected = when (selectedFilter) {
                OrdersFilter.All -> offers + orders
                OrdersFilter.ActiveOffers -> offers.filter { it.status == PharmacyWorkStatus.WaitingForCustomer }
                OrdersFilter.RejectedOffers -> offers.filter { it.status == PharmacyWorkStatus.RejectedOffer }
                OrdersFilter.InProgress -> orders.filter { it.status == PharmacyWorkStatus.Preparing || it.status == PharmacyWorkStatus.OnTheWay }
                OrdersFilter.Delivered -> orders.filter { it.status == PharmacyWorkStatus.Delivered }
            }
            return selected
                .filter { it.matchesQuery(searchQuery) }
                .sortedWith(compareByDescending<PharmacyWorkItem> { it.createdAt.orEmpty() }.thenBy { it.stableKey })
        }

    val canLoadMore: Boolean
        get() = when (selectedFilter) {
            OrdersFilter.All -> canLoadMoreOffers || canLoadMoreOrders
            OrdersFilter.ActiveOffers, OrdersFilter.RejectedOffers -> canLoadMoreOffers
            OrdersFilter.InProgress, OrdersFilter.Delivered -> canLoadMoreOrders
        }
}
