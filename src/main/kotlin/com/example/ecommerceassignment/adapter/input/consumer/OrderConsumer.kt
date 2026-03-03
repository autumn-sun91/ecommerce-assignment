package com.example.ecommerceassignment.adapter.input.consumer

import com.example.ecommerceassignment.application.output.OrderRepository
import com.example.ecommerceassignment.application.output.ProductRepository
import com.example.ecommerceassignment.domain.event.OrderPendingEvent
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderConsumer(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @RabbitListener(queues = ["\${order.queue.name}"])
    @Transactional
    fun consume(event: OrderPendingEvent) {
        log.info("OrderPendingEvent 수신 orderId=${event.orderId}")

        val product =
            productRepository.getProductWithLock(event.productId)
                ?: throw IllegalStateException("상품 없음")

        if (product.remainingStock() < 0) {
            log.error("DB 재고 부족 - productId: ${event.productId}, stock: ${product.remainingStock()}")
            return
        }

        product.decrease(event.quantity)
        productRepository.save(product)

        // Order CONFIRMED 변경
        val order =
            orderRepository.getOneById(event.orderId)
                ?: throw IllegalStateException("주문 없음")
        order.confirm()
        orderRepository.save(order)

        log.info("주문 확정 완료 orderId=${event.orderId}")
    }
}
