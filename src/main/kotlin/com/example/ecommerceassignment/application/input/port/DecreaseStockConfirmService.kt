package com.example.ecommerceassignment.application.input.port

import com.example.ecommerceassignment.application.output.OrderRepository
import com.example.ecommerceassignment.application.output.ProductRepository
import com.example.ecommerceassignment.domain.event.OrderPendingEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DecreaseStockConfirmService(
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
) : DecreaseStockAndConfirmInputPort {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun decreaseStockAndConfirm(event: OrderPendingEvent) {
        log.info("OrderPendingEvent 수신 orderId=${event.orderId}")

        val product =
            productRepository.getOneById(event.productId)
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
