package com.example.ecommerceassignment.application.output

import com.example.ecommerceassignment.domain.Product

interface ProductRepository {
    // X Lock
    fun getProductWithLock(id: Long): Product?

    fun save(product: Product)

    // Atomic Update 방식
    fun decreaseStockAtomic(
        productId: Long,
        quantity: Int,
    ): Boolean

    fun getOneById(productId: Long): Product?

    fun initStock(
        productId: Long,
        stock: Int,
    ): Boolean

    fun getStock(productId: Long): Int

    fun getAllStock(): List<Product>
}
