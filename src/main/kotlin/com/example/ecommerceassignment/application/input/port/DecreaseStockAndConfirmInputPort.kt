package com.example.ecommerceassignment.application.input.port

import com.example.ecommerceassignment.domain.event.OrderPendingEvent

interface DecreaseStockAndConfirmInputPort {
    fun decreaseStockAndConfirm(event: OrderPendingEvent)
}
