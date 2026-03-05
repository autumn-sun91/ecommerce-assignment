package com.example.ecommerceassignment.adapter.output.apiCaller

import com.example.ecommerceassignment.application.output.DeliveryApiCaller
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.RetryRegistry
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("consumer")
class DeliveryApiClient(
    private val deliveryFeignClient: DeliveryFeignClient,
    private val retryRegistry: RetryRegistry,
) : DeliveryApiCaller {
    private val log = LoggerFactory.getLogger(this::class.java)

    private val retry by lazy { retryRegistry.retry("deliveryApi") }

    @CircuitBreaker(name = "deliveryApi", fallbackMethod = "fallback")
    override fun requestDelivery(request: DeliveryApiCaller.DeliveryRequest): DeliveryApiCaller.DeliveryResponse {
        log.info("배송 API 호출 orderId=${request.orderId}, idempotencyKey=${request.idempotencyKey}")

        return retry.executeSupplier {
            deliveryFeignClient.requestDelivery(
                idempotencyKey = request.idempotencyKey,
                request = request,
            )
        }
    }

    fun fallback(
        request: DeliveryApiCaller.DeliveryRequest,
        e: Exception,
    ): DeliveryApiCaller.DeliveryResponse {
        when (e) {
            is CallNotPermittedException ->
                log.error("[fallback] 서킷브레이커 OPEN orderId=${request.orderId}")
            else ->
                log.error("[fallback] 배송 API 최종 실패 orderId=${request.orderId}, cause=${e.message}")
        }

        return DeliveryApiCaller.DeliveryResponse(
            success = false,
            trackingNumber = null,
            reason = "최종 실패: ${e.message}",
        )
    }
}
