package com.NBE3_4_2_Team4.domain.product.product.dto

import com.NBE3_4_2_Team4.domain.product.product.entity.Product
import com.NBE3_4_2_Team4.domain.product.saleState.entity.SaleState
import com.NBE3_4_2_Team4.standard.dto.PageDto
import org.springframework.data.domain.Page

class ProductResponseDto {

    data class GetItem(

        val productId: Long,

        val productName: String,

        val productPrice: Int,

        val productDescription: String?,

        val productImageUrl: String,

        val productCategory: String,

        val productSaleState: String
    ) {
        constructor(product: Product, productCategory: String, productSaleState: SaleState) : this(
            productId = product.id!!,
            productName = product.name,
            productPrice = product.price,
            productDescription = product.description,
            productImageUrl = product.imageUrl,
            productCategory = productCategory,
            productSaleState = productSaleState.name
        )
    }

    data class GetItemsByKeyword(
        val keyword: String,
        val products: List<GetItem>
    )

    class PageDtoWithKeyword<T>(
        page: Page<T>,
        val keyword: String
    ) : PageDto<T>(page)
}