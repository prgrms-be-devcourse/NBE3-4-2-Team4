package com.NBE3_4_2_Team4.domain.product.order.dto

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class ProductOrderRequestDto {

    data class PurchaseDetails(
        @field:NotBlank(message = "username은 필수 입력 값입니다.")
        val username: String,

        @field:NotNull(message = "amount는 필수 입력 값입니다.")
        @field:Min(value = 1, message = "금액은 1 이상이어야 합니다.")
        val amount: Long,

        @field:NotNull(message = "재화 타입은 필수 입력 값입니다.")
        val assetType: AssetType,

        @field:NotNull(message = "재화 카테고리는 필수 입력 값입니다.")
        val assetCategory: AssetCategory
    )
}