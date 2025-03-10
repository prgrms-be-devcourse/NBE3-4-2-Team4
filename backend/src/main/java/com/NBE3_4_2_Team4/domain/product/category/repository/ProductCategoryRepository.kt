package com.NBE3_4_2_Team4.domain.product.category.repository

import com.NBE3_4_2_Team4.domain.product.category.entity.ProductCategory
import org.springframework.data.jpa.repository.JpaRepository

interface ProductCategoryRepository : JpaRepository<ProductCategory, Long> {

    fun findByNameContainingOrderByIdAsc(categoryKeyword: String): List<ProductCategory>

    fun findByNameAndParent(categoryName: String, parent: ProductCategory?): ProductCategory?
}