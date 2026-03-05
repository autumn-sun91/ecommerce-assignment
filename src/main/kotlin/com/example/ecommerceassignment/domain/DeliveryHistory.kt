package com.example.ecommerceassignment.domain

import java.time.LocalDateTime

class DeliveryHistory(
    val id: Long? = null,
    val orderId: Long,
    val idempotencyKey: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    status: DeliveryStatus = DeliveryStatus.UNKNOWN,
    trackingNumber: String? = null,
    failReason: String? = null,
    attemptCount: Int? = 0,
) {
    var status: DeliveryStatus = status
        private set

    var trackingNumber: String? = trackingNumber
        private set

    var failReason: String? = failReason
        private set

    var attemptCount: Int? = attemptCount
        private set

    fun success(trackingNumber: String) {
        status = DeliveryStatus.SUCCESS
        this.trackingNumber = trackingNumber
    }

    fun fail(reason: String) {
        this.status = DeliveryStatus.FAIL
        this.failReason = reason
    }

    fun increaseAttempt() {
        this.attemptCount = this.attemptCount!! + 1
    }

    enum class DeliveryStatus {
        UNKNOWN,
        SUCCESS,
        FAIL,
    }
}
