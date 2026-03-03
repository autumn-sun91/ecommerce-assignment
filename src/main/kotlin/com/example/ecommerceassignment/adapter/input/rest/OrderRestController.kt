package com.example.ecommerceassignment.adapter.input.rest

import com.example.ecommerceassignment.adapter.input.rest.dto.CreateOrderCommandRequest
import com.example.ecommerceassignment.adapter.input.rest.dto.OrderResponse
import com.example.ecommerceassignment.application.input.CreateOrderUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderRestController(
    private val createOrderUseCase: CreateOrderUseCase,
) {
    // 🔒 Pessimistic Lock 버전
    @PostMapping("/lock")
    fun orderWithLock(
        @RequestBody @Valid request: CreateOrderCommandRequest,
    ): ResponseEntity<OrderResponse> {
        createOrderUseCase.orderWithLock(
            userId = request.userId,
            productId = request.productId,
            quantity = request.quantity,
        )

        return ResponseEntity.ok(
            OrderResponse("주문 성공 (Pessimistic Lock)"),
        )
    }

    // ⚡ Atomic Update 버전
    @PostMapping("/atomic")
    fun orderWithAtomic(
        @RequestBody @Valid request: CreateOrderCommandRequest,
    ): ResponseEntity<OrderResponse> {
        createOrderUseCase.orderWithAtomic(
            userId = request.userId,
            productId = request.productId,
            quantity = request.quantity,
        )

        return ResponseEntity.ok(
            OrderResponse("주문 성공 (Atomic Update)"),
        )
    }

    @PostMapping("/preoccupy")
    fun orderWithRedis(
        @RequestBody @Valid request: CreateOrderCommandRequest,
    ): ResponseEntity<OrderResponse> {
        createOrderUseCase.orderWithPreoccupy(
            userId = request.userId,
            productId = request.productId,
            quantity = request.quantity,
        )

        return ResponseEntity.ok(
            OrderResponse("주문 성공 (Preoccupy Update)"),
        )
    }
}
