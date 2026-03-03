package com.example.ecommerceassignment.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionAdvice {
    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(IllegalStateException::class)
    fun handleBusinessException(e: IllegalStateException): ResponseEntity<Map<String, String>> {
        log.warn("[ILLEGAL_STATE_ERROR] message: {}", e.message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to e.message.orEmpty()))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<Map<String, String>> {
        val origin = e.stackTrace.firstOrNull()?.className ?: "Unknown"
        log.error("[INTERNAL_SERVER_ERROR] origin: {}, message: {}", origin, e.message, e)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf("error" to "Internal Server Error"))
    }
}
