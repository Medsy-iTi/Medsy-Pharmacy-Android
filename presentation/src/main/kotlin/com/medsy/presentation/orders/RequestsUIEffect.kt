package com.medsy.presentation.orders

sealed interface RequestsUIEffect {
    data class NavigateToRequestDetails(val requestId: Long) : RequestsUIEffect
    data object OpenFilters : RequestsUIEffect
    data class ShowMessage(val messageRes: Int) : RequestsUIEffect
}
