package com.example.ecommerceassignment.adapter.output.persistence

import com.example.ecommerceassignment.adapter.output.persistence.jpa.OrderJpaEntity
import com.example.ecommerceassignment.adapter.output.persistence.jpa.OrderJpaRepository
import com.example.ecommerceassignment.application.output.OrderRepository
import com.example.ecommerceassignment.domain.Order
import org.springframework.stereotype.Component

@Component
class OrderPersistenceAdapter(
    private val jpaRepository: OrderJpaRepository,
) : OrderRepository {
    override fun save(order: Order): Order {
        val entity =
            OrderJpaEntity(
                id = order.id,
                userId = order.userId,
                productId = order.productId,
                quantity = order.quantity,
            )

        val saved = jpaRepository.save(entity)

        return Order(
            id = saved.id,
            userId = saved.userId,
            productId = saved.productId,
            quantity = saved.quantity,
        )
    }

    override fun countByUserAndProduct(
        userId: Long,
        productId: Long,
    ): Int = jpaRepository.sumQuantityByUserAndProduct(userId, productId)
}
