package com.example.ecommerceassignment.adapter.output.persistence

import com.example.ecommerceassignment.adapter.output.persistence.jpa.DeliveryHistoryJpaEntity
import com.example.ecommerceassignment.adapter.output.persistence.jpa.DeliveryHistoryJpaRepository
import com.example.ecommerceassignment.application.output.DeliveryHistoryRepository
import com.example.ecommerceassignment.domain.DeliveryHistory
import org.springframework.context.annotation.Profile
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
@Profile("consumer")
class DeliveryHistoryPersistenceAdapter(
    private val deliveryHistoryJpaRepository: DeliveryHistoryJpaRepository,
) : DeliveryHistoryRepository {
    override fun getByOrderId(orderId: Long): DeliveryHistory? =
        deliveryHistoryJpaRepository.findByOrderId(orderId)?.let {
            DeliveryHistory(
                it.id!!,
                it.orderId,
                it.idempotencyKey,
                it.createdAt,
                it.updatedAt,
                it.status,
                it.trackingNumber,
                it.failReason,
                it.attemptCount,
            )
        }

    override fun getById(id: Long): DeliveryHistory? =
        deliveryHistoryJpaRepository.findByIdOrNull(id)?.let {
            DeliveryHistory(
                it.id!!,
                it.orderId,
                it.idempotencyKey,
                it.createdAt,
                it.updatedAt,
                it.status,
                it.trackingNumber,
                it.failReason,
                it.attemptCount,
            )
        }

    override fun getAllByStatus(status: DeliveryHistory.DeliveryStatus): List<DeliveryHistory> =
        deliveryHistoryJpaRepository.findAllByStatus(status).map {
            DeliveryHistory(
                it.id!!,
                it.orderId,
                it.idempotencyKey,
                it.createdAt,
                it.updatedAt,
                it.status,
                it.trackingNumber,
                it.failReason,
                it.attemptCount,
            )
        }

    override fun save(history: DeliveryHistory): DeliveryHistory =
        deliveryHistoryJpaRepository
            .save(
                DeliveryHistoryJpaEntity(
                    id = history.id,
                    orderId = history.orderId,
                    idempotencyKey = history.idempotencyKey,
                    status = history.status,
                    trackingNumber = history.trackingNumber,
                    failReason = history.failReason,
                    attemptCount = history.attemptCount,
                ),
            ).let {
                DeliveryHistory(
                    it.id!!,
                    it.orderId,
                    it.idempotencyKey,
                    it.createdAt,
                    it.updatedAt,
                    it.status,
                    it.trackingNumber,
                    it.failReason,
                    it.attemptCount,
                )
            }
}
