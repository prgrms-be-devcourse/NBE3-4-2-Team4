package com.NBE3_4_2_Team4.domain.board.question.dto.request

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Length

data class QuestionWriteReqDto(
        @field:NotNull
        @field:Length(min = 2)
        val title: String,

        @field:NotNull
        @field:Length(min = 2)
        val content: String,

        @field:NotNull
        @field:JsonProperty("categoryId")
        val categoryId: Long,

        @field:NotNull
        @field:Min(1)
        val amount: Long,

        @field:NotNull
        @field:JsonProperty("assetType")
        val assetType: AssetType
)
