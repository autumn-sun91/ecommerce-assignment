package com.example.ecommerceassignment.adapter.input.consumer

import com.example.ecommerceassignment.application.input.DeliveryHistoryProcessor
import com.example.ecommerceassignment.application.output.DeliveryApiCaller
import com.example.ecommerceassignment.domain.event.OrderConfirmedEvent
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("consumer")
class DeliveryConsumer(
    private val deliveryApiCaller: DeliveryApiCaller,
    private val deliveryHistoryProcessor: DeliveryHistoryProcessor,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @RabbitListener(queues = ["\${order.confirmed.queue}"])
    fun consumer(event: OrderConfirmedEvent) {
        log.info("OrderConfirmedEvent 수신 orderId=${event.orderId}")

        // 1. 멱등성 체크 (트랜잭션 종료 후 커넥션 반납)
        val history = deliveryHistoryProcessor.findOrCreate(event)
        if (history?.id == null) {
            log.info("이미 처리된 배송 요청 스킵 orderId=${event.orderId}")
            return
        }

        // 2. 배송 API 호출 (커넥션 없는 상태)
        val response =
            runCatching {
                deliveryApiCaller.requestDelivery(
                    DeliveryApiCaller.DeliveryRequest(
                        orderId = event.orderId,
                        idempotencyKey = history.idempotencyKey,
                        userId = event.userId,
                        productId = event.productId,
                        quantity = event.quantity,
                    ),
                )
            }.getOrElse { e ->
                log.error("배송 API 최종 실패 orderId=${event.orderId}", e)
                DeliveryApiCaller.DeliveryResponse(success = false, trackingNumber = null, reason = e.message)
            }

        // 3. 결과 저장 (트랜잭션 종료 후 커넥션 반납)
        when {
            response.success -> {
                deliveryHistoryProcessor.success(history.id, response.trackingNumber!!)
                log.info("배송 성공 orderId=${event.orderId}")
            }
            else -> {
                deliveryHistoryProcessor.fail(history.id, response.reason ?: "알 수 없는 오류")
                log.error("배송 실패 orderId=${event.orderId}")
            }
        }
    }
}
