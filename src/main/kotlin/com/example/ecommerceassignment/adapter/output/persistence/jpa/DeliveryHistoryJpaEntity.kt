package com.example.ecommerceassignment.adapter.output.persistence.jpa

import com.example.ecommerceassignment.domain.DeliveryHistory
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "delivery_histories")
class DeliveryHistoryJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false, unique = true)
    val orderId: Long,
    @Column(nullable = false)
    val idempotencyKey: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: DeliveryHistory.DeliveryStatus = DeliveryHistory.DeliveryStatus.UNKNOWN,
    @Column
    val trackingNumber: String? = null,
    @Column
    val failReason: String? = null,
    @Column(nullable = false)
    val attemptCount: Int? = null,
    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)
