package com.medsy.presentation.orders

enum class OrderStatus {
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

data class OrderSummary(
    val id: Long,
    val minutesAgo: String,
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
    Cancelled,
    Completed,
}

private fun OrderStatus.toFilter(): OrderFilter = when (this) {
    OrderStatus.New -> OrderFilter.New
    OrderStatus.InProgress -> OrderFilter.InProgress
    OrderStatus.Delivered -> OrderFilter.Delivered
    OrderStatus.Cancelled -> OrderFilter.Cancelled
    OrderStatus.Completed -> OrderFilter.Completed
}


fun OrderSummary.matchesFilter(filter: OrderFilter): Boolean {
    return when (filter) {
        OrderFilter.All -> true
        OrderFilter.New -> this.status == OrderStatus.New
        OrderFilter.InProgress -> this.status == OrderStatus.InProgress
        OrderFilter.Completed -> this.status == OrderStatus.Completed
        OrderFilter.Cancelled -> this.status == OrderStatus.Cancelled
        OrderFilter.Delivered -> this.status == OrderStatus.Delivered
    }
}

fun OrderSummary.matchesQuery(query: String): Boolean {
    if (query.isBlank()) return true

    return this.id.toString().contains(query, ignoreCase = true)
}