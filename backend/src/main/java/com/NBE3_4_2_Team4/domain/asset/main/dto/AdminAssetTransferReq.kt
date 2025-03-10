package com.NBE3_4_2_Team4.domain.asset.main.dto

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull


class AdminAssetTransferReq(
    @field:NotBlank(message = "username은 필수 입력 값 입니다.")
    val username: String,

    @field:NotNull(message = "amount는 필수 입력 값 입니다.")
    @field:Min(value = 1, message = "금액은 1 이상이어야 합니다.")
    val amount: Long,

    @field:JsonProperty("assetType")
    @field:NotNull(message = "assetType은 필수 값 입니다")
    val assetType: AssetType,

    @field:JsonProperty("adminAssetCategoryId")
    @field:NotNull(message = "adminAssetCategoryId는 필수 값 입니다")
    val adminAssetCategoryId: Long
)
