package com.medsy.presentation.orders

import com.medsy.domain.orders.model.PharmacyOrderStatus

enum class OrdersFilter {
    All,
    WaitingForPatient,
    InProgress,
    Completed;


    val statuses: List<PharmacyOrderStatus>
        get() = when (this) {
            All -> listOf(
                PharmacyOrderStatus.Pending,
                PharmacyOrderStatus.PendingPayment,
                PharmacyOrderStatus.Preparing,
                PharmacyOrderStatus.ReadyForPickup,
                PharmacyOrderStatus.ReadyForDelivery,
                PharmacyOrderStatus.OutForDelivery,
                PharmacyOrderStatus.Delivered,
                PharmacyOrderStatus.Cancelled,
            )

            WaitingForPatient -> listOf(
                PharmacyOrderStatus.Pending,
                PharmacyOrderStatus.PendingPayment,
            )

            InProgress -> listOf(
                PharmacyOrderStatus.Preparing,
                PharmacyOrderStatus.ReadyForPickup,
                PharmacyOrderStatus.ReadyForDelivery,
                PharmacyOrderStatus.OutForDelivery,
            )

            Completed -> listOf(
                PharmacyOrderStatus.Delivered,
                PharmacyOrderStatus.Cancelled,
            )
        }

}
