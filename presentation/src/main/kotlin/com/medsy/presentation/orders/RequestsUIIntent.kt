package com.medsy.presentation.orders

sealed interface RequestsUIIntent {
    data class SearchQueryChanged(val query: String) : RequestsUIIntent
    data class FilterSelected(val filter: RequestFilter) : RequestsUIIntent
    data object FilterIconClicked : RequestsUIIntent
    data class RequestClicked(val requestId: Long) : RequestsUIIntent
    data class AcceptRequestClicked(val requestId: Long) : RequestsUIIntent
    data class PrepareRequestClicked(val requestId: Long) : RequestsUIIntent
}
