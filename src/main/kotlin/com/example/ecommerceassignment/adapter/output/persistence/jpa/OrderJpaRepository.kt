package com.example.ecommerceassignment.adapter.output.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OrderJpaRepository : JpaRepository<OrderJpaEntity, Long> {
    @Query(
        """
        SELECT COALESCE(SUM(o.quantity), 0)
        FROM OrderJpaEntity o
        WHERE o.userId = :userId
        AND o.productId = :productId
        """,
    )
    fun sumQuantityByUserAndProduct(
        @Param("userId") userId: Long,
        @Param("productId") productId: Long,
    ): Int
}
