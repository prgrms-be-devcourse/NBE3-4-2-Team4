package com.NBE3_4_2_Team4.domain.asset.main.dto

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull


class AdminAssetTransferReq {
    val username: @NotBlank(message = "username은 필수 입력 값 입니다.") String? = null

    val amount: @NotNull(message = "amount는 필수 입력 값 입니다.") @Min(value = 1, message = "금액은 1 이상이어야 합니다.") Long? = null

    @JsonProperty("assetType")
    val assetType: @NotNull(message = "assetType은 필수 값 입니다") AssetType? = null

    @JsonProperty("adminAssetCategoryId")
    val adminAssetCategoryId: @NotNull(message = "adminAssetCategoryId는 필수 값 입니다") Long? = null
}
