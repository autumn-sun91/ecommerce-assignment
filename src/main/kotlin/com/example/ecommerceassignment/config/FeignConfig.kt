package com.example.ecommerceassignment.config

import com.example.ecommerceassignment.exception.DeliveryBadRequestException
import feign.Logger
import feign.Response
import feign.codec.ErrorDecoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.IOException
import java.lang.Exception

@Configuration
class FeignConfig {
    @Bean
    fun feignLoggerLevel(): Logger.Level = Logger.Level.BASIC

    @Bean
    fun errorDecoder(): ErrorDecoder = DeliveryErrorDecoder()

    class DeliveryErrorDecoder : ErrorDecoder {
        override fun decode(
            methodKey: String,
            response: Response,
        ): Exception =
            when (response.status()) {
                in 400..499 -> DeliveryBadRequestException("잘못된 배송 요청 status=${response.status()}")
                in 500..599 -> IOException("배송 서버 오류 status=${response.status()}")
                else -> IOException("알 수 없는 오류 status=${response.status()}")
            }
    }
}
