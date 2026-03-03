package com.example.ecommerceassignment.adapter.output.persistence.jpa

import com.example.ecommerceassignment.domain.Order
import jakarta.persistence.*

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
    @Enumerated(EnumType.STRING)
    val status: Order.OrderStatus,
)
