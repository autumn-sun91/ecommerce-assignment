package com.example.ecommerceassignment.application.output

import com.example.ecommerceassignment.domain.DeliveryHistory

interface DeliveryHistoryRepository {
    fun getByOrderId(orderId: Long): DeliveryHistory?

    fun getById(id: Long): DeliveryHistory?

    fun getAllByStatus(status: DeliveryHistory.DeliveryStatus): List<DeliveryHistory>

    fun save(history: DeliveryHistory): DeliveryHistory
}
