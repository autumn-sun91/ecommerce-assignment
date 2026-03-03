package com.example.ecommerceassignment.application.input

import com.example.ecommerceassignment.application.output.ProductRepository
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class StockSyncService(
    private val productRepository: ProductRepository,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    fun syncAllStock() {
        productRepository.getAllStock().forEach {
            val isNew = productRepository.initStock(it.id, it.remainingStock())

            if (isNew) {
                log.info("재고 동기화 완료 - productId: ${it.id}, stock: ${it.remainingStock()}")
            } else {
                log.info("재고 이미 존재 - productId: ${it.id}, 초기화 스킵")
            }
        }
    }
}
