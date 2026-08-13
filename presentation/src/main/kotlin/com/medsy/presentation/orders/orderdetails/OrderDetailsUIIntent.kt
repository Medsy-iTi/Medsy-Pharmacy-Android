package com.medsy.presentation.orders.orderdetails

sealed interface OrderDetailsUIIntent {
    data class Load(val orderId: Long) : OrderDetailsUIIntent
    data object Refresh : OrderDetailsUIIntent
    data object Retry : OrderDetailsUIIntent
    data object BackClicked : OrderDetailsUIIntent
    data object CallCustomerClicked : OrderDetailsUIIntent
    data object OpenLocationClicked : OrderDetailsUIIntent
    data object MarkReadyClicked : OrderDetailsUIIntent
    data object DismissReadyConfirmation : OrderDetailsUIIntent
    data object ConfirmMarkReady : OrderDetailsUIIntent
}
