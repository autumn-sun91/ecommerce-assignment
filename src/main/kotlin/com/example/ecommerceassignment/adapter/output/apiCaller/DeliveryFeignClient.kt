package com.example.ecommerceassignment.adapter.output.apiCaller

import com.example.ecommerceassignment.application.output.DeliveryApiCaller
import com.example.ecommerceassignment.config.FeignConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    name = "deliveryApi",
    configuration = [FeignConfig::class],
)
interface DeliveryFeignClient {
    @PostMapping("/deliveries")
    fun requestDelivery(
        @RequestHeader("X-Idempotency-Key") idempotencyKey: String,
        @RequestBody request: DeliveryApiCaller.DeliveryRequest,
    ): DeliveryApiCaller.DeliveryResponse
}
