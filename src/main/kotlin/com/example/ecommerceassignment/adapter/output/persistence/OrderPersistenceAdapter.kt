package com.example.ecommerceassignment.adapter.output.persistence

import com.example.ecommerceassignment.adapter.output.persistence.jpa.OrderJpaEntity
import com.example.ecommerceassignment.adapter.output.persistence.jpa.OrderJpaRepository
import com.example.ecommerceassignment.application.output.OrderRepository
import com.example.ecommerceassignment.domain.Order
import org.slf4j.LoggerFactory
import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class OrderPersistenceAdapter(
    private val orderJpaRepository: OrderJpaRepository,
    private val redisTemplate: RedisTemplate<String, String>,
    private val orderLuaScript: RedisScript<Long>,
) : OrderRepository {
    private val log = LoggerFactory.getLogger(this::class.java)

    companion object {
        private const val USER_ORDER_KEY = "user:%d:product:%d" // 유저별 주문 수량
        private const val PRODUCT_STOCK_KEY = "product:%d:stock" // 상품 재고
    }

    override fun save(order: Order): Order {
        val entity =
            OrderJpaEntity(
                id = order.id,
                userId = order.userId,
                productId = order.productId,
                quantity = order.quantity,
                status = order.status,
            )

        val saved = this.orderJpaRepository.save(entity)

        return Order(
            id = saved.id,
            userId = saved.userId,
            productId = saved.productId,
            quantity = saved.quantity,
            status = saved.status,
        )
    }

    override fun countByUserAndProduct(
        userId: Long,
        productId: Long,
    ): Int = orderJpaRepository.sumQuantityByUserAndProduct(userId, productId)

    override fun executeOrderScript(
        userId: Long,
        productId: Long,
        quantity: Int,
        limit: Int,
    ): OrderRepository.OrderScriptResult {
        val userKey = USER_ORDER_KEY.format(userId, productId)
        val stockKey = PRODUCT_STOCK_KEY.format(productId)

        log.info("Lua Script 실행 - userKey: $userKey, stockKey: $stockKey, quantity: $quantity, limit: $limit")

        // 실행 전 Redis 값 직접 확인
        val stockBefore = redisTemplate.opsForValue().get(stockKey)
        val userCountBefore = redisTemplate.opsForValue().get(userKey)
        log.info("실행 전 stock: $stockBefore, userCount: $userCountBefore")

        return try {
            val result =
                redisTemplate.execute(
                    orderLuaScript,
                    listOf(userKey, stockKey),
                    quantity.toString(),
                    limit.toString(),
                )

            log.info("Lua Script 결과값: $result")

            when (result) {
                1L -> OrderRepository.OrderScriptResult.SUCCESS
                -1L -> OrderRepository.OrderScriptResult.EXCEED_LIMIT
                -2L -> OrderRepository.OrderScriptResult.OUT_OF_STOCK
                else -> {
                    log.error("Lua Script 알 수 없는 결과값: $result")
                    OrderRepository.OrderScriptResult.ERROR
                }
            }
        } catch (e: RedisConnectionFailureException) {
            log.error("Redis 연결 실패", e)
            OrderRepository.OrderScriptResult.ERROR
        } catch (e: Exception) {
            log.error("Lua Script 실행 중 예외 발생 - userKey: $userKey, stockKey: $stockKey", e)
            OrderRepository.OrderScriptResult.ERROR
        }
    }

    override fun getOneById(orderId: Long): Order? =
        orderJpaRepository.findByIdOrNull(orderId)?.let {
            Order(
                it.id!!,
                it.userId,
                it.productId,
                it.quantity,
                it.status,
            )
        }
}
