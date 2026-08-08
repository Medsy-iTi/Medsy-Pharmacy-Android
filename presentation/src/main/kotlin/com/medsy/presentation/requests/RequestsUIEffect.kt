package com.medsy.presentation.requests

sealed interface RequestsUIEffect {
    data class NavigateToRequestDetails(val requestId: Long) : RequestsUIEffect
}
