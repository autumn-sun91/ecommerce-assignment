package com.example.ecommerceassignment.application.output

import com.example.ecommerceassignment.domain.Order

interface OrderRepository {
    fun save(order: Order): Order

    fun countByUserAndProduct(
        userId: Long,
        productId: Long,
    ): Int

    fun executeOrderScript(
        userId: Long,
        productId: Long,
        quantity: Int,
        limit: Int = 2,
    ): OrderScriptResult

    fun getOneById(orderId: Long): Order?

    enum class OrderScriptResult {
        SUCCESS,
        EXCEED_LIMIT,
        OUT_OF_STOCK,
        ERROR,
    }
}
