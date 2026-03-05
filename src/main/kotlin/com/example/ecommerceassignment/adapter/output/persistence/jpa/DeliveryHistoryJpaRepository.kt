package com.example.ecommerceassignment.adapter.output.persistence.jpa

import com.example.ecommerceassignment.domain.DeliveryHistory
import org.springframework.data.jpa.repository.JpaRepository

interface DeliveryHistoryJpaRepository : JpaRepository<DeliveryHistoryJpaEntity, Long> {
    fun findByOrderId(orderId: Long): DeliveryHistoryJpaEntity?

    fun findAllByStatus(status: DeliveryHistory.DeliveryStatus): List<DeliveryHistoryJpaEntity>
}
