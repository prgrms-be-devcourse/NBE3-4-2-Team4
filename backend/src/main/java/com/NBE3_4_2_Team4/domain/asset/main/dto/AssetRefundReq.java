package com.NBE3_4_2_Team4.domain.asset.main.dto;

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
public class AssetRefundReq {

    @NotNull(message = "amount는 필수 값입니다")
    private final long amount;

    @JsonProperty("assetType")
    @NotNull(message = "assetType은 필수 값입니다")
    private final AssetType assetType;

    @JsonProperty("assetCategory")
    @NotNull(message = "assetCategory는 필수 값입니다")
    private final AssetCategory assetCategory;
}
