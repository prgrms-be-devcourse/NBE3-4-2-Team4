package com.NBE3_4_2_Team4.domain.product.saleState.repository

import com.NBE3_4_2_Team4.domain.product.saleState.entity.ProductSaleState
import com.NBE3_4_2_Team4.domain.product.saleState.entity.SaleState
import org.springframework.data.jpa.repository.JpaRepository

interface ProductSaleStateRepository : JpaRepository<ProductSaleState, Long> {

    fun findByName(name: SaleState): ProductSaleState?
}