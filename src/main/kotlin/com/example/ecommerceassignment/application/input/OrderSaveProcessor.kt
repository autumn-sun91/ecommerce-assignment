package com.example.ecommerceassignment.application.input

import com.example.ecommerceassignment.application.output.OrderRepository
import com.example.ecommerceassignment.domain.Order
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Profile("producer")
class OrderSaveProcessor(
    private val orderRepository: OrderRepository,
) {
    @Transactional
    fun save(
        userId: Long,
        productId: Long,
        quantity: Int,
    ): Order =
        orderRepository.save(
            Order(
                userId = userId,
                productId = productId,
                quantity = quantity,
                status = Order.OrderStatus.PENDING,
            ),
        )
}
