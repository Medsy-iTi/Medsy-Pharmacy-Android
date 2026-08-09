package com.medsy.presentation.orderdetails.model


data class RequestMedicineItem(
    val id: String,
    val name: String,
    val packInfo: String,
    val quantity: Int,
    val price: Double,
    val imageUrl: String?,
    val productId: Long? = null,
)

enum class PaymentMethod {
    Cash,
    Visa,
    Mastercard,
    Unknown;

    companion object {
        fun fromApiValue(value: String?): PaymentMethod {
            return when (value?.uppercase()) {
                "VISA" -> Visa
                "MASTERCARD" -> Mastercard
                "CASH" -> Cash
                else -> Unknown
            }
        }
    }
}

data class Request(
    val id: String,
    val isNew: Boolean,
    val minutesAgo: Int,
    val customerName: String?,
    val customerId: Long,
    val customerPhone: String,
    val customerAddress: String,
    val deliveryLatitude: Double?,
    val deliveryLongitude: Double?,
    val items: List<RequestMedicineItem>,
    val customerNotes: String?,
    val total: Double,
    val prescriptionUrl: String?,
    val paymentMethod: PaymentMethod
)
