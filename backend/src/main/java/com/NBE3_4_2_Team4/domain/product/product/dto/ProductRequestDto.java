package com.NBE3_4_2_Team4.domain.product.product.dto;

import com.NBE3_4_2_Team4.domain.product.category.entity.ProductCategory;
import com.NBE3_4_2_Team4.domain.product.product.entity.Product;
import com.NBE3_4_2_Team4.domain.product.saleState.entity.ProductSaleState;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProductRequestDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class writeItem {

        @NotBlank(message = "상품 이름은 필수 입력 값입니다.")
        private String productName;

        @NotBlank(message = "상품 가격은 필수 입력 값입니다.")
        private int productPrice;

        @NotBlank(message = "상품 설명은 필수 입력 값입니다.")
        private String productDescription;

        @NotBlank(message = "상품 이미지는 필수 입력 값입니다.")
        private String productImageUrl;

        @NotBlank(message = "상품 카테고리는 필수 입력 값입니다.")
        private String productCategory;

        @NotBlank(message = "상품 판매 상태는 필수 입력 값입니다.")
        private String productSaleState;

        public Product toProduct(ProductCategory productCategory, ProductSaleState productSaleState) {
            return Product.builder()
                    .name(productName)
                    .price(productPrice)
                    .description(productDescription)
                    .imageUrl(productImageUrl)
                    .category(productCategory)
                    .saleState(productSaleState)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)  // Not Null인 필드만 Json에 추가되도록 설정
    public static class updateItem {

        private String productName;

        private Integer productPrice;

        private String productDescription;

        private String productImageUrl;

        private String productCategory;

        private String productSaleState;
    }
}
