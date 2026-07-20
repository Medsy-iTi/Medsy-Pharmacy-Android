package com.medsy.presentation.orders

enum class OrderStatus {
    New,
    InProgress,
    Delivered,
}

enum class PaymentMethod {
    Cash,
    Visa,
    Mastercard,
}

data class OrderSummary(
    val id: String,
    val minutesAgo: Int,
    val status: OrderStatus,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val total: Int,
    val paymentMethod: PaymentMethod,
    val paymentCardLastDigits: String? = null,
)

enum class OrderFilter {
    All,
    New,
    InProgress,
    Delivered,
}

private fun OrderStatus.toFilter(): OrderFilter = when (this) {
    OrderStatus.New -> OrderFilter.New
    OrderStatus.InProgress -> OrderFilter.InProgress
    OrderStatus.Delivered -> OrderFilter.Delivered
}

fun OrderSummary.matchesFilter(filter: OrderFilter): Boolean =
    filter == OrderFilter.All || status.toFilter() == filter

fun OrderSummary.matchesQuery(query: String): Boolean =
    query.isBlank() ||
        customerName.contains(query, ignoreCase = true) ||
        id.contains(query, ignoreCase = true)
