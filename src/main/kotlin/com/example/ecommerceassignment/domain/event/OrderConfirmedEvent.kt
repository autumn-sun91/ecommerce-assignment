package com.example.ecommerceassignment.domain.event

data class OrderConfirmedEvent(
    val orderId: Long,
    val userId: Long,
    val productId: Long,
    val quantity: Int,
)
