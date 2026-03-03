package com.example.ecommerceassignment.domain

class Order(
    val id: Long? = null,
    val userId: Long,
    val productId: Long,
    val quantity: Int,
) {
    init {
        require(quantity in 1..2) {
            "한 고객당 최대 2개까지 구매 가능합니다."
        }
    }
}
