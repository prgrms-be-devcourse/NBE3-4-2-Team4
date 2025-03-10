package com.NBE3_4_2_Team4.domain.asset.main.dto

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter

class AssetTransferReq (
    @field:NotBlank(message = "username은 필수 입력 값입니다.")
    val username: String,

    @field:NotNull(message = "amount는 필수 입력 값입니다.")
    @field:Min(value = 1, message = "금액은 1 이상이어야 합니다.")
    val amount: Long,

    @JsonProperty("assetType")
    @field:NotNull(message = "assetType은 필수 값입니다")
    val assetType:  AssetType
)
