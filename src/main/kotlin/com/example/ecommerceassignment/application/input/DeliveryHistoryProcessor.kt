package com.example.ecommerceassignment.application.input

import com.example.ecommerceassignment.application.output.DeliveryHistoryRepository
import com.example.ecommerceassignment.domain.DeliveryHistory
import com.example.ecommerceassignment.domain.event.OrderConfirmedEvent
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Profile("consumer")
class DeliveryHistoryProcessor(
    private val deliveryHistoryRepository: DeliveryHistoryRepository,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun findOrCreate(event: OrderConfirmedEvent): DeliveryHistory? {
        val existing = deliveryHistoryRepository.getByOrderId(event.orderId)

        if (existing?.status == DeliveryHistory.DeliveryStatus.SUCCESS) {
            return null // 이미 처리됨
        }

        return existing ?: deliveryHistoryRepository
            .save(
                DeliveryHistory(
                    orderId = event.orderId,
                    idempotencyKey = "order-${event.orderId}",
                ),
            ).also { it.increaseAttempt() }
    }

    @Transactional
    fun success(
        historyId: Long,
        trackingNumber: String,
    ) {
        val history =
            deliveryHistoryRepository.getById(historyId)
                ?: throw IllegalStateException("DeliveryHistory 없음 id=$historyId")
        history.success(trackingNumber)
        deliveryHistoryRepository.save(history)
    }

    @Transactional
    fun fail(
        historyId: Long,
        reason: String,
    ) {
        val history =
            deliveryHistoryRepository.getById(historyId)
                ?: throw IllegalStateException("DeliveryHistory 없음 id=$historyId")
        history.fail(reason)
        deliveryHistoryRepository.save(history)
    }
}
