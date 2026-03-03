package com.example.ecommerceassignment.adapter.output.persistence

import com.example.ecommerceassignment.adapter.output.persistence.jpa.ProductJpaRepository
import com.example.ecommerceassignment.application.output.ProductRepository
import com.example.ecommerceassignment.domain.Product
import org.springframework.stereotype.Component

@Component
class ProductPersistenceAdapter(
    private val jpaRepository: ProductJpaRepository,
) : ProductRepository {
    override fun findByIdForUpdate(id: Long): Product? =
        jpaRepository
            .findByIdForUpdate(id)
            ?.let { Product(it.id!!, it.stock) }

    override fun save(product: Product) {
        val entity =
            jpaRepository
                .findById(product.id)
                .orElseThrow()

        entity.stock = product.remainingStock()
        // dirty checking으로 자동 update
    }

    override fun decreaseStockAtomic(
        productId: Long,
        quantity: Int,
    ): Boolean = jpaRepository.decreaseStockAtomic(productId, quantity) == 1
}
