package com.example.ecommerceassignment.domain.event

data class OrderPendingEvent(
    val orderId: Long,
    val userId: Long,
    val productId: Long,
    val quantity: Int,
)
