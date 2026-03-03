package com.example.ecommerceassignment.application.input

import com.example.ecommerceassignment.application.output.OrderRepository
import com.example.ecommerceassignment.application.output.ProductRepository
import com.example.ecommerceassignment.domain.Order
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
) : CreateOrderUseCase {
    @Transactional
    override fun orderWithLock(
        userId: Long,
        productId: Long,
        quantity: Int,
    ) {
        val product =
            productRepository.findByIdForUpdate(productId)
                ?: throw IllegalArgumentException("상품 없음")

        val orderedCount = orderRepository.countByUserAndProduct(userId, productId)
        if (orderedCount + quantity > 2) {
            throw IllegalStateException("최대 구매 수량 초과")
        }

        product.decrease(quantity)

        orderRepository.save(
            Order(
                userId = userId,
                productId = productId,
                quantity = quantity,
            ),
        )

        productRepository.save(product)
    }

    @Transactional
    override fun orderWithAtomic(
        userId: Long,
        productId: Long,
        quantity: Int,
    ) {
        // 사용자 구매 제한 검증
        val orderedCount = orderRepository.countByUserAndProduct(userId, productId)
        if (orderedCount + quantity > 2) {
            throw IllegalStateException("최대 구매 수량 초과")
        }

        // 재고 차감
        val success =
            productRepository
                .decreaseStockAtomic(productId, quantity)

        if (!success) {
            throw IllegalStateException("재고 부족")
        }

        // 주문 생성
        orderRepository.save(
            Order(
                userId = userId,
                productId = productId,
                quantity = quantity,
            ),
        )
    }
}
