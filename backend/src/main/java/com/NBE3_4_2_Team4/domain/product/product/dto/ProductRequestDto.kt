package com.NBE3_4_2_Team4.domain.product.product.dto

import com.NBE3_4_2_Team4.domain.product.category.entity.ProductCategory
import com.NBE3_4_2_Team4.domain.product.product.entity.Product
import com.NBE3_4_2_Team4.domain.product.saleState.entity.ProductSaleState
import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.NotBlank

class ProductRequestDto {

    data class WriteItem(

        @field:NotBlank(message = "상품 이름은 필수 입력 값입니다.")
        val productName: String,

        @field:NotBlank(message = "상품 가격은 필수 입력 값입니다.")
        val productPrice: Int,

        @field:NotBlank(message = "상품 설명은 필수 입력 값입니다.")
        val productDescription: String,

        @field:NotBlank(message = "상품 이미지는 필수 입력 값입니다.")
        val productImageUrl: String,

        @field:NotBlank(message = "상품 카테고리는 필수 입력 값입니다.")
        val productCategory: String,

        @field:NotBlank(message = "상품 판매 상태는 필수 입력 값입니다.")
        val productSaleState: String
    ) {

        fun toProduct(category: ProductCategory, saleState: ProductSaleState): Product {
            return Product(
                name = productName,
                price = productPrice,
                description = productDescription,
                imageUrl = productImageUrl,
                category = category,
                saleState = saleState
            )
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)  // Null이 아닌 필드만 JSON에 포함
    data class UpdateItem(

        val productName: String? = null,

        val productPrice: Int? = null,

        val productDescription: String? = null,

        val productImageUrl: String? = null,

        val productCategory: String? = null,

        val productSaleState: String? = null
    )
}