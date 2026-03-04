package com.example.ecommerceassignment.application.input.usecase

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

    fun orderWithPreoccupy(
        userId: Long,
        productId: Long,
        quantity: Int,
    )
}
