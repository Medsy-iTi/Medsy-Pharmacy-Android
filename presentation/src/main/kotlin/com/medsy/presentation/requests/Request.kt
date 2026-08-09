package com.medsy.presentation.requests

enum class PharmacyWorkSource {
    Request,
    Offer,
    Order,
}

enum class PharmacyWorkStatus {
    Searching,
    WaitingForCustomer,
    RejectedOffer,
    Preparing,
    OnTheWay,
    Delivered,
}

enum class PaymentMethod {
    Cash,
    Visa,
    Mastercard;

    companion object {
        fun fromApiValue(value: String?): PaymentMethod? = when (value?.uppercase()) {
            "CASH" -> Cash
            "VISA" -> Visa
            "MASTERCARD" -> Mastercard
            else -> null
        }
    }
}

data class PharmacyWorkItem(
    val id: Long,
    val displayId: String,
    val source: PharmacyWorkSource,
    val status: PharmacyWorkStatus,
    val createdAt: String?,
    val minutesAgo: Int?,
    val customerName: String?,
    val customerId: Long?,
    val customerPhone: String?,
    val customerAddress: String?,
    val productImages: List<String?>,
    val total: Double?,
    val paymentMethod: PaymentMethod?,
    val paymentCardLastDigits: String? = null,
) {
    val stableKey: String get() = "${source.name}:$id"
}

enum class OrdersFilter {
    All,
    ActiveOffers,
    RejectedOffers,
    InProgress,
    Delivered,
}

fun PharmacyWorkItem.matchesQuery(query: String): Boolean =
    query.isBlank() || displayId.contains(query, ignoreCase = true)
