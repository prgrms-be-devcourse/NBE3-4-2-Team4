package com.NBE3_4_2_Team4.domain.product.order.dto;

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProductOrderRequestDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseDetails {
        @NotBlank(message = "username은 필수 입력 값입니다.")
        private String username;

        @NotNull(message = "amount는 필수 입력 값입니다.")
        @Min(value = 1, message = "금액은 1 이상이어야 합니다.")
        private Long amount;

        @NotNull(message = "재화 타입은 필수 입력 값입니다.")
        private AssetType assetType;

        @NotNull(message = "재화 카테고리는 필수 입력 값입니다.")
        private AssetCategory assetCategory;
    }
}
