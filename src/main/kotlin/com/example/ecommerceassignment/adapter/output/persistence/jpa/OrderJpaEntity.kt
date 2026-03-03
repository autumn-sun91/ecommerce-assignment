package com.example.ecommerceassignment.adapter.output.persistence.jpa

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "orders",
    indexes = [
        Index(name = "idx_user_product", columnList = "userId, productId"),
    ],
)
class OrderJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val userId: Long,
    val productId: Long,
    val quantity: Int,
)
