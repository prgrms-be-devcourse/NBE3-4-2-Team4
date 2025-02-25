package com.NBE3_4_2_Team4.domain.board.question.dto.request;

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record QuestionWriteReqDto(
        @NotNull @Length(min = 2)
        String title,
        @NotNull @Length(min = 2)
        String content,
        @NotNull
        @JsonProperty("categoryId")
        Long categoryId,
        @NotNull @Min(1)
        long amount,
        @NotNull @JsonProperty("assetType")
        AssetType assetType
) {}
