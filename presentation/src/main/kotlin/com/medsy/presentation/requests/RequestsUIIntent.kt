package com.medsy.presentation.requests

sealed interface RequestsUIIntent {
    data class SearchQueryChanged(val query: String) : RequestsUIIntent
    data class FilterSelected(val filter: RequestsFilter) : RequestsUIIntent
    data class RequestClicked(val requestId: Long) : RequestsUIIntent
    data object Refresh : RequestsUIIntent
    data object Retry : RequestsUIIntent
    data object LoadMore : RequestsUIIntent
}
