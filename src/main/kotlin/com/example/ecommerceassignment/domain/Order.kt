package com.example.ecommerceassignment.domain

class Order(
    val id: Long? = null,
    val userId: Long,
    val productId: Long,
    val quantity: Int,
    var status: OrderStatus,
) {
    fun confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw IllegalStateException("PENDING 상태에서만 확정 가능")
        }
        this.status = OrderStatus.CONFIRMED
    }

    init {
        require(quantity in 1..2) {
            "한 고객당 최대 2개까지 구매 가능합니다."
        }
    }

    enum class OrderStatus {
        PENDING,
        CONFIRMED,
    }
}
