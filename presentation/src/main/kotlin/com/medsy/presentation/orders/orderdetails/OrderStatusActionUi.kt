package com.medsy.presentation.orders.orderdetails

import androidx.annotation.StringRes
import com.medsy.domain.orders.model.PharmacyOrderStatus
import com.medsy.presentation.R

internal data class OrderStatusActionUi(
    @StringRes val labelRes: Int,
    @StringRes val titleRes: Int,
    @StringRes val messageRes: Int,
)

internal fun PharmacyOrderStatus.toOrderStatusActionUi(): OrderStatusActionUi? = when (this) {
    PharmacyOrderStatus.Preparing -> OrderStatusActionUi(
        labelRes = R.string.order_details_mark_ready_action,
        titleRes = R.string.order_details_mark_ready_title,
        messageRes = R.string.order_details_mark_ready_message,
    )
    PharmacyOrderStatus.ReadyForDelivery -> OrderStatusActionUi(
        labelRes = R.string.order_details_start_delivery_action,
        titleRes = R.string.order_details_start_delivery_title,
        messageRes = R.string.order_details_start_delivery_message,
    )
    PharmacyOrderStatus.ReadyForPickup -> OrderStatusActionUi(
        labelRes = R.string.order_details_mark_collected_action,
        titleRes = R.string.order_details_mark_collected_title,
        messageRes = R.string.order_details_mark_collected_message,
    )
    PharmacyOrderStatus.OutForDelivery -> OrderStatusActionUi(
        labelRes = R.string.order_details_mark_delivered_action,
        titleRes = R.string.order_details_mark_delivered_title,
        messageRes = R.string.order_details_mark_delivered_message,
    )
    PharmacyOrderStatus.Pending,
    PharmacyOrderStatus.PendingPayment,
    PharmacyOrderStatus.Delivered,
    PharmacyOrderStatus.Cancelled,
    PharmacyOrderStatus.Unknown -> null
}
