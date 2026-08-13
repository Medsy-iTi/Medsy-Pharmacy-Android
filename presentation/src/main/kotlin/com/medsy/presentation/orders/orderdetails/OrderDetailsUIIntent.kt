package com.medsy.presentation.orders.orderdetails

sealed interface OrderDetailsUIIntent {
    data class Load(val orderId: Long) : OrderDetailsUIIntent
    data object Refresh : OrderDetailsUIIntent
    data object Retry : OrderDetailsUIIntent
    data object BackClicked : OrderDetailsUIIntent
    data object CallCustomerClicked : OrderDetailsUIIntent
    data object OpenLocationClicked : OrderDetailsUIIntent
    data object StatusActionClicked : OrderDetailsUIIntent
    data object DismissStatusConfirmation : OrderDetailsUIIntent
    data object ConfirmStatusAction : OrderDetailsUIIntent
}
