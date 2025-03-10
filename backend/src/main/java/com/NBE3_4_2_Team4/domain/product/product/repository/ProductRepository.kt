package com.NBE3_4_2_Team4.domain.product.product.repository

import com.NBE3_4_2_Team4.domain.product.product.entity.Product
import com.NBE3_4_2_Team4.domain.product.saleState.entity.SaleState
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository : JpaRepository<Product, Long>, ProductRepositoryCustom {

    fun findByCategoryIdIn(categoryIds: Set<Long>): List<Product>

    fun findByCategoryIdIn(categoryIds: Set<Long>, pageable: Pageable): Page<Product>

    @Query(
        """
        SELECT p 
        FROM Product p 
        JOIN ProductSaleState pss ON p.saleState = pss 
        WHERE pss.name = :name
        """
    )
    fun findBySaleStateLike(@Param("name") saleState: SaleState): List<Product>

    @Query(
        """
        SELECT p 
        FROM Product p 
        JOIN ProductSaleState pss ON p.saleState = pss 
        WHERE pss.name = :name
        """
    )
    fun findBySaleStateLike(@Param("name") saleState: SaleState, pageable: Pageable): Page<Product>
}