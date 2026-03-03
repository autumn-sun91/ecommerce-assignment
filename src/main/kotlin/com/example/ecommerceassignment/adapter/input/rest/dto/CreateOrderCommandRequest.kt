package com.example.ecommerceassignment.adapter.input.rest.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class CreateOrderCommandRequest(
    @field:NotNull
    val userId: Long,
    @field:NotNull
    val productId: Long,
    @field:Min(1)
    @field:Max(2)
    val quantity: Int,
)
