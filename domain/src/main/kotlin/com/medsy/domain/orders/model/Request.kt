package com.medsy.domain.orders.model

data class PharmacyRequestDomain(
    val id: Long,
    val customerId: Long,
    val deliveryLatitude: Double?,
    val deliveryLongitude: Double?,
    val deliveryAddress: String?,
    val requestStatus: String,
    val assignmentStatus: PharmacyRequestAssignmentStatus,
    val distanceKm: Double?,
    val createdAt: String,
    val items: List<RequestItemDomain>,
    val prescriptionUrl: String?,
    val customerName: String?,
    val customerPhone: String?,
    val paymentMethod: String?,
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

data class RequestItemDomain(
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

data class PharmacyRequestPageDomain(
    val content: List<PharmacyRequestDomain>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)

data class PharmacyOrderDomain(
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
    val deliveryFee: Double,
    val total: Double,
    val createdAt: String?,
    val status: String,
    val paymentMethod: String?,
    val paymentStatus: String?,
    val paidAt: String?,
    val items: List<OrderItemDomain>,
)

data class OrderItemDomain(
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

data class PharmacyOrderPageDomain(
    val content: List<PharmacyOrderDomain>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean,
)
