package com.medsy.presentation.orders

enum class RequestStatus {
    Searching,
    New,
    InProgress,
    Delivered,
    Cancelled,
    Completed,
    Expired,
    OfferSubmitted,
}

enum class PaymentMethod {
    Cash,
    Visa,
    Mastercard,
}

data class RequestSummary(
    val id: Long,
    val displayId: String,
    val minutesAgo: Int,
    val status: RequestStatus,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val productImages: List<String?>,
    val total: Double,
    val paymentMethod: PaymentMethod,
    val paymentCardLastDigits: String? = null,
)

enum class RequestFilter {
    All,
    New,
    InProgress,
    Delivered,
    Cancelled,
    Completed,
}


fun RequestSummary.matchesFilter(filter: RequestFilter): Boolean {
    return when (filter) {
        RequestFilter.All -> true
        RequestFilter.New -> this.status == RequestStatus.New || this.status == RequestStatus.Searching || this.status == RequestStatus.OfferSubmitted
        RequestFilter.InProgress -> this.status == RequestStatus.InProgress
        RequestFilter.Completed -> this.status == RequestStatus.Completed
        RequestFilter.Cancelled -> this.status == RequestStatus.Cancelled || this.status == RequestStatus.Expired
        RequestFilter.Delivered -> this.status == RequestStatus.Delivered
    }
}

fun RequestSummary.matchesQuery(query: String): Boolean {
    if (query.isBlank()) return true

    return this.id.toString().contains(query, ignoreCase = true)
}