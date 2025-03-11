package com.NBE3_4_2_Team4.domain.product.product.repository

import com.NBE3_4_2_Team4.domain.product.product.entity.Product
import com.NBE3_4_2_Team4.domain.product.saleState.entity.SaleState
import com.NBE3_4_2_Team4.standard.search.ProductSearchKeywordType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductRepositoryCustom {

    fun findByKeyword(
        searchKeywordType: ProductSearchKeywordType,
        searchKeyword: String,
        categoryKeyword: String,
        saleState: SaleState,
        pageable: Pageable
    ): Page<Product>
}