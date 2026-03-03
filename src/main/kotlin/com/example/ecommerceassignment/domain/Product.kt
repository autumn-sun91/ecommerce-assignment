package com.example.ecommerceassignment.domain

class Product(
    val id: Long,
    private var stock: Int,
) {
    fun decrease(quantity: Int) {
        require(quantity > 0) { "수량은 1 이상이어야 합니다." }

        if (stock < quantity) {
            throw IllegalStateException("재고 부족")
        }

        stock -= quantity
    }

    fun remainingStock(): Int = stock
}
