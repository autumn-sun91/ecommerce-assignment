package com.example.ecommerceassignment.application.output

interface DeliveryApiCaller {
    fun requestDelivery(request: DeliveryRequest): DeliveryResponse

    data class DeliveryRequest(
        val orderId: Long,
        val idempotencyKey: String,
        val userId: Long,
        val productId: Long,
        val quantity: Int,
    )

    data class DeliveryResponse(
        val success: Boolean,
        val trackingNumber: String?,
        val reason: String?,
    )
}
