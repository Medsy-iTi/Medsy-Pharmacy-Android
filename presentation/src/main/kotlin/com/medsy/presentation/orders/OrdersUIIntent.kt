package com.medsy.presentation.orders

import com.medsy.presentation.requests.OrdersFilter
import com.medsy.presentation.requests.PharmacyWorkSource

sealed interface OrdersUIIntent {
    data class SearchQueryChanged(val query: String) : OrdersUIIntent
    data class FilterSelected(val filter: OrdersFilter) : OrdersUIIntent
    data class ItemClicked(val id: Long, val source: PharmacyWorkSource) : OrdersUIIntent
    data object Refresh : OrdersUIIntent
    data object Retry : OrdersUIIntent
    data object LoadMore : OrdersUIIntent
}
