package com.example.ecommerceassignment.application.input

import com.example.ecommerceassignment.adapter.output.publisher.OrderEventPublisher
import com.example.ecommerceassignment.application.input.usecase.CreateOrderUseCase
import com.example.ecommerceassignment.application.output.OrderRepository
import com.example.ecommerceassignment.application.output.ProductRepository
import com.example.ecommerceassignment.domain.Order
import com.example.ecommerceassignment.domain.event.OrderPendingEvent
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Profile("producer")
class CreateOrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val orderEventPublisher: OrderEventPublisher,
) : CreateOrderUseCase {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun orderWithLock(
        userId: Long,
        productId: Long,
        quantity: Int,
    ) {
        val product =
            productRepository.getProductWithLock(productId)
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
                status = Order.OrderStatus.CONFIRMED,
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
                status = Order.OrderStatus.CONFIRMED,
            ),
        )
    }

    @Transactional
    override fun orderWithPreoccupy(
        userId: Long,
        productId: Long,
        quantity: Int,
    ) {
        try {
            // Lua Script로 원자적 처리 (검증 + 재고차감 + 유저카운트 한번에)
            val result = orderRepository.executeOrderScript(userId, productId, quantity)

            when (result) {
                OrderRepository.OrderScriptResult.EXCEED_LIMIT -> throw IllegalStateException("최대 구매 수량 초과")
                OrderRepository.OrderScriptResult.OUT_OF_STOCK -> throw IllegalStateException("재고 부족")
                OrderRepository.OrderScriptResult.ERROR -> throw IllegalStateException("주문 처리 중 오류")
                OrderRepository.OrderScriptResult.SUCCESS -> Unit
            }

            // Redis 선점 성공 → DB에 주문 저장 (PENDING 상태)
            val order =
                orderRepository.save(
                    Order(
                        userId = userId,
                        productId = productId,
                        quantity = quantity,
                        status = Order.OrderStatus.PENDING,
                    ),
                )

            orderEventPublisher.publish(
                OrderPendingEvent(
                    order.id!!,
                    userId,
                    productId,
                    quantity,
                ),
            )
        } catch (e: RedisConnectionFailureException) {
            log.warn("Redis 장애 발생, fallback to DB lock")
            orderWithLock(userId, productId, quantity)
        }
    }
}
