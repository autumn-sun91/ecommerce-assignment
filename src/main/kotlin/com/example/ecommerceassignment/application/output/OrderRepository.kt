package com.example.ecommerceassignment.application.output

import com.example.ecommerceassignment.domain.Order

interface OrderRepository {
    fun save(order: Order): Order

    fun countByUserAndProduct(
        userId: Long,
        productId: Long,
    ): Int
}
