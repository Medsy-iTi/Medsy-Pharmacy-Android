package com.medsy.domain.orders.model

data class PharmacyRequest(
    val id: Long,
    val customerId: Long,
    val deliveryLatitude: Double?,
    val deliveryLongitude: Double?,
    val deliveryAddress: String?,
    val requestStatus: String,
    val assignmentStatus: PharmacyRequestAssignmentStatus,
    val distanceKm: Double?,
    val createdAt: String,
    val items: List<RequestItem>,
    val prescriptionUrl: String?,
    val customerName: String?,
    val customerPhone: String?,
    val paymentMethod: PaymentMethod,
    val notes: String?
)

enum class PharmacyRequestAssignmentStatus(val apiValue: String?) {
    Pending("PENDING"),
    OfferCreated("OFFER_CREATED"),
    Expired("EXPIRED"),
    Unknown(null),
    ;

    companion object {
        fun fromApiValue(value: String?): PharmacyRequestAssignmentStatus =
            when (value?.uppercase()) {
                "PENDING" -> Pending
                "OFFER_CREATED" -> OfferCreated
                "EXPIRED" -> Expired
                else -> Unknown
            }
    }
}

data class RequestItem(
    val id: Long,
    val productId: Long,
    val imageUrl: String?,
    val productName: String,
    val strength: String?,
    val packSize: String?,
    val form: String?,
    val quantity: Int,
    val unitPrice: Double
)

data class PharmacyRequestPage(
    val content: List<PharmacyRequest>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)

data class PharmacyOrder(
    val id: Long,
    val customerId: Long,
    val customerName: String?,
    val customerNotes: String?,
    val customerPhone: String?,
    val deliveryAddress: String?,
    val deliveryLatitude: Double?,
    val deliveryLongitude: Double?,
    val prescriptionUrl: String?,
    val offerId: Long?,
    val subTotal: Double,
    val total: Double,
    val createdAt: String?,
    val status: PharmacyOrderStatus,
    val paymentMethod: PaymentMethod,
    val items: List<OrderItem>,
)

data class OrderItem(
    val id: Long,
    val productId: Long?,
    val productName: String,
    val strength: String?,
    val packSize: String?,
    val form: String?,
    val quantity: Int,
    val unitPrice: Double,
    val imageUrl: String?,
)

data class PharmacyOrderPage(
    val content: List<PharmacyOrder>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean,
)

enum class PharmacyOrderStatus {
    Pending,
    PendingPayment,
    Preparing,
    ReadyForPickup,
    ReadyForDelivery,
    OutForDelivery,
    Delivered,
    Cancelled,
    Unknown;

    val isWaitingForPatient: Boolean
        get() = this == Pending || this == PendingPayment

    val isInProgress: Boolean
        get() = this == Preparing || this == ReadyForPickup ||
            this == ReadyForDelivery || this == OutForDelivery

    val isCompleted: Boolean
        get() = this == Delivered || this == Cancelled

    val canMarkReady: Boolean
        get() = this == Preparing

    companion object {
        fun fromApiValue(value: String?): PharmacyOrderStatus = when (value?.uppercase()) {
            "PENDING" -> Pending
            "PENDING_PAYMENT" -> PendingPayment
            "PREPARING" -> Preparing
            "READY_FOR_PICKUP" -> ReadyForPickup
            "READY_FOR_DELIVERY" -> ReadyForDelivery
            "OUT_FOR_DELIVERY" -> OutForDelivery
            "DELIVERED" -> Delivered
            "CANCELLED" -> Cancelled
            else -> Unknown
        }
    }
}

enum class PaymentMethod {
    Cash,
    Card,
    Unknown;

    companion object {
        fun fromApiValue(value: String?): PaymentMethod = when (value?.uppercase()) {
            "CASH" -> Cash
            "CARD" -> Card
            else -> Unknown
        }
    }
}
