package com.example.ecommerceassignment.adapter.output.publisher

import com.example.ecommerceassignment.domain.event.OrderPendingEvent
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("producer")
class OrderEventPublisher(
    private val rabbitTemplate: RabbitTemplate,
    @Value("\${order.queue.exchange}") private val exchange: String,
    @Value("\${order.queue.routing-key}") private val routingKey: String,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun publish(event: OrderPendingEvent) {
        runCatching {
            rabbitTemplate.convertAndSend(exchange, routingKey, event)
            log.info("OrderPendingEvent 발행 성공 orderId=${event.orderId}")
        }.onFailure {
            log.error("OrderPendingEvent 발행 실패 orderId=${event.orderId}", it)
            // 발행 실패 시 → DB Order 상태를 PUBLISH_FAILED 로 변경
            throw IllegalStateException("이벤트 발행 실패")
        }
    }
}
