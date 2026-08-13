package com.medsy.presentation.orders.orderdetails

import androidx.annotation.StringRes
import com.medsy.domain.orders.model.OrderFulfillmentMethod
import com.medsy.domain.orders.model.PharmacyOrder
import com.medsy.domain.orders.model.PharmacyOrderStatus
import com.medsy.presentation.R

internal data class OrderStatusActionUi(
    @StringRes val labelRes: Int,
    @StringRes val titleRes: Int,
    @StringRes val messageRes: Int,
)

internal enum class OrderStatusTransition {
    MarkReady,
    StartDelivery,
    MarkCollected,
    MarkDelivered,
}

internal fun PharmacyOrder.statusTransition(): OrderStatusTransition? =
    when (status) {
        PharmacyOrderStatus.Preparing -> OrderStatusTransition.MarkReady
        PharmacyOrderStatus.ReadyForDelivery ->
            if (effectiveFulfillmentMethod == OrderFulfillmentMethod.Delivery) {
                OrderStatusTransition.StartDelivery
            } else {
                null
            }
        PharmacyOrderStatus.ReadyForPickup ->
            if (effectiveFulfillmentMethod == OrderFulfillmentMethod.Pickup) {
                OrderStatusTransition.MarkCollected
            } else {
                null
            }
        PharmacyOrderStatus.OutForDelivery ->
            if (effectiveFulfillmentMethod == OrderFulfillmentMethod.Delivery) {
                OrderStatusTransition.MarkDelivered
            } else {
                null
            }
        PharmacyOrderStatus.Pending,
        PharmacyOrderStatus.PendingPayment,
        PharmacyOrderStatus.Delivered,
        PharmacyOrderStatus.Cancelled,
        PharmacyOrderStatus.Unknown -> null
    }

internal fun OrderStatusTransition.toOrderStatusActionUi(): OrderStatusActionUi = when (this) {
    OrderStatusTransition.MarkReady -> OrderStatusActionUi(
        labelRes = R.string.order_details_mark_ready_action,
        titleRes = R.string.order_details_mark_ready_title,
        messageRes = R.string.order_details_mark_ready_message,
    )
    OrderStatusTransition.StartDelivery -> OrderStatusActionUi(
        labelRes = R.string.order_details_start_delivery_action,
        titleRes = R.string.order_details_start_delivery_title,
        messageRes = R.string.order_details_start_delivery_message,
    )
    OrderStatusTransition.MarkCollected -> OrderStatusActionUi(
        labelRes = R.string.order_details_mark_collected_action,
        titleRes = R.string.order_details_mark_collected_title,
        messageRes = R.string.order_details_mark_collected_message,
    )
    OrderStatusTransition.MarkDelivered -> OrderStatusActionUi(
        labelRes = R.string.order_details_mark_delivered_action,
        titleRes = R.string.order_details_mark_delivered_title,
        messageRes = R.string.order_details_mark_delivered_message,
    )
}
