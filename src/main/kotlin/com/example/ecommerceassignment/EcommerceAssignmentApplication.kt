package com.example.ecommerceassignment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients(
    basePackages = ["com.example.ecommerceassignment.adapter.output.apiCaller"],
)
@SpringBootApplication
class EcommerceAssignmentApplication

fun main(args: Array<String>) {
    runApplication<EcommerceAssignmentApplication>(*args)
}
