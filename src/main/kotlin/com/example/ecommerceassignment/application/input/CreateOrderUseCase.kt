package com.example.ecommerceassignment.application.input

interface CreateOrderUseCase {
    fun orderWithLock(
        userId: Long,
        productId: Long,
        quantity: Int,
    )

    fun orderWithAtomic(
        userId: Long,
        productId: Long,
        quantity: Int,
    )
}
