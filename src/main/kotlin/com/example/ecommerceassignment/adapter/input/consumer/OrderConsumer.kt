package com.example.ecommerceassignment.adapter.input.consumer

import com.example.ecommerceassignment.application.input.port.DecreaseStockAndConfirmInputPort
import com.example.ecommerceassignment.domain.event.OrderPendingEvent
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class OrderConsumer(
    private val decreaseStockAndConfirmInputPort: DecreaseStockAndConfirmInputPort,
    private val redissonClient: RedissonClient,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @RabbitListener(queues = ["\${order.queue.name}"])
    fun consume(event: OrderPendingEvent) {
        val lockKey = "lock:product:${event.productId}"
        val lock = redissonClient.getLock(lockKey)
        log.info("분산락 획득 성공 orderId=${event.orderId}")

        // 락 획득 시도 (waitTime: 5초, leaseTime: 3초)
        if (!lock.tryLock(5, 3, TimeUnit.SECONDS)) {
            log.error("분산락 획득 실패 orderId=${event.orderId}")
            throw IllegalStateException("분산락 획득 실패")
        }

        try {
            decreaseStockAndConfirmInputPort.decreaseStockAndConfirm(event)
        } finally {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }
}
