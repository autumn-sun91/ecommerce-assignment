package com.example.ecommerceassignment.adapter.output.persistence

import com.example.ecommerceassignment.adapter.output.persistence.jpa.ProductJpaRepository
import com.example.ecommerceassignment.application.output.ProductRepository
import com.example.ecommerceassignment.domain.Product
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ProductPersistenceAdapter(
    private val productJpaRepository: ProductJpaRepository,
    private val redisTemplate: RedisTemplate<String, String>,
) : ProductRepository {
    companion object {
        private const val PRODUCT_STOCK_KEY = "product:%d:stock" // 상품 재고
    }

    override fun getProductWithLock(id: Long): Product? =
        productJpaRepository
            .findByIdForUpdate(id)
            ?.let { Product(it.id!!, it.stock) }

    override fun save(product: Product) {
        val entity =
            productJpaRepository
                .findById(product.id)
                .orElseThrow()

        entity.stock = product.remainingStock()
        // dirty checking으로 자동 update
    }

    override fun decreaseStockAtomic(
        productId: Long,
        quantity: Int,
    ): Boolean = productJpaRepository.decreaseStockAtomic(productId, quantity) == 1

    override fun getOneById(productId: Long): Product? =
        productJpaRepository.findByIdOrNull(productId)?.let {
            Product(
                it.id!!,
                it.stock,
            )
        }

    override fun initStock(
        productId: Long,
        stock: Int,
    ): Boolean {
        val stockKey = PRODUCT_STOCK_KEY.format(productId)
        return redisTemplate.opsForValue().setIfAbsent(stockKey, stock.toString()) ?: false
    }

    override fun getStock(productId: Long): Int {
        val stockKey = PRODUCT_STOCK_KEY.format(productId)
        return redisTemplate.opsForValue().get(stockKey)?.toInt() ?: 0
    }

    override fun getAllStock(): List<Product> =
        productJpaRepository.findAll().map {
            Product(
                it.id!!,
                it.stock,
            )
        }
}
