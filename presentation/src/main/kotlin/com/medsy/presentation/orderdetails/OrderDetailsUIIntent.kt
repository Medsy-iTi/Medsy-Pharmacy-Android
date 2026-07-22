package com.medsy.presentation.orderdetails

sealed interface OrderDetailsUIIntent {
    data class LoadOrder(val orderId: String) : OrderDetailsUIIntent
    data class PharmacistNotesChanged(val notes: String) : OrderDetailsUIIntent
    data object BackClicked : OrderDetailsUIIntent
    data object CallCustomerClicked : OrderDetailsUIIntent
    data object OpenLocationClicked : OrderDetailsUIIntent
    data object ViewPaymentSummaryClicked : OrderDetailsUIIntent
    data object RejectOrderClicked : OrderDetailsUIIntent
    data object ContactCustomerClicked : OrderDetailsUIIntent
    data object AcceptOrderClicked : OrderDetailsUIIntent

}