package com.NBE3_4_2_Team4.domain.asset.main.dto

import jakarta.validation.constraints.NotBlank


class AdminAssetCategoryUpdateReq(
    @field:NotBlank
    val name: String
)
