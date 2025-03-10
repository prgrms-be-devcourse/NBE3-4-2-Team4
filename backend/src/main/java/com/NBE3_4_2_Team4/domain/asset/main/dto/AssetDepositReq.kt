package com.NBE3_4_2_Team4.domain.asset.main.dto

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

class AssetDepositReq (
    @field:NotNull(message = "amount는 필수 값입니다")
    val amount: Long,

    @JsonProperty("assetType")
    @field:NotNull(message = "assetType은 필수 값입니다")
    val assetType: AssetType,

    @JsonProperty("assetCategory")
    @field:NotNull(message = "assetCategory는 필수 값입니다")
    val assetCategory: AssetCategory
)