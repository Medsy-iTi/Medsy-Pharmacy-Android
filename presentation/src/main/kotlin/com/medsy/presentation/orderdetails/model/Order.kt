package com.medsy.presentation.orderdetails.model


data class OrderMedicineItem(
    val id: String,
    val name: String,
    val packInfo: String,
    val quantity: Int,
    val price: Int,
    val imageUrl: String?,
)

data class Order(
    val id: String,
    val isNew: Boolean,
    val minutesAgo: Int,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val items: List<OrderMedicineItem>,
    val customerNotes: String?,
    val total: Int,
    val prescriptionUrl: String? = "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?q=80&w=1000&auto=format&fit=crop"
)
