package com.medsy.presentation.orders

enum class RequestStatus {
    Searching,
    New,
    InProgress,
    Delivered,
    Cancelled,
    Completed,
}

enum class PaymentMethod {
    Cash,
    Visa,
    Mastercard,
}

data class RequestSummary(
    val id: Long,
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

private fun RequestStatus.toFilter(): RequestFilter = when (this) {
    RequestStatus.Searching -> RequestFilter.New
    RequestStatus.New -> RequestFilter.New
    RequestStatus.InProgress -> RequestFilter.InProgress
    RequestStatus.Delivered -> RequestFilter.Delivered
    RequestStatus.Cancelled -> RequestFilter.Cancelled
    RequestStatus.Completed -> RequestFilter.Completed
}


fun RequestSummary.matchesFilter(filter: RequestFilter): Boolean {
    return when (filter) {
        RequestFilter.All -> true
        RequestFilter.New -> this.status == RequestStatus.New || this.status == RequestStatus.Searching
        RequestFilter.InProgress -> this.status == RequestStatus.InProgress
        RequestFilter.Completed -> this.status == RequestStatus.Completed
        RequestFilter.Cancelled -> this.status == RequestStatus.Cancelled
        RequestFilter.Delivered -> this.status == RequestStatus.Delivered
    }
}

fun RequestSummary.matchesQuery(query: String): Boolean {
    if (query.isBlank()) return true

    return this.id.toString().contains(query, ignoreCase = true)
}