package com.example.ecommerceassignment.application.output

import com.example.ecommerceassignment.domain.Product

interface ProductRepository {
    // X Lock
    fun findByIdForUpdate(id: Long): Product?

    fun save(product: Product)

    // Atomic Update 방식
    fun decreaseStockAtomic(
        productId: Long,
        quantity: Int,
    ): Boolean
}
