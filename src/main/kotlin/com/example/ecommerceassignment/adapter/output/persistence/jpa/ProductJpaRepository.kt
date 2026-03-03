package com.example.ecommerceassignment.adapter.output.persistence.jpa

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductJpaRepository : JpaRepository<ProductJpaEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ProductJpaEntity p where p.id = :id")
    fun findByIdForUpdate(
        @Param("id") id: Long,
    ): ProductJpaEntity?

    // Atomic Update
    @Modifying
    @Query(
        """
        UPDATE ProductJpaEntity p
        SET p.stock = p.stock - :quantity
        WHERE p.id = :id
        AND p.stock >= :quantity
        """,
    )
    fun decreaseStockAtomic(
        @Param("id") id: Long,
        @Param("quantity") quantity: Int,
    ): Int
}
