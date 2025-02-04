package com.NBE3_4_2_Team4.domain.product.product.dto;

import com.NBE3_4_2_Team4.domain.product.product.entity.Product;
import com.NBE3_4_2_Team4.domain.product.saleState.entity.SaleState;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

public class ProductResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetItems {

        private Long productId;

        private String productName;

        private int productPrice;

        private String productDescription;

        private String productImageUrl;

        private String productCategory;

        private String productSaleState;

        public GetItems(Product product, String productCategory, SaleState productSaleState) {
            this.productId = product.getId();
            this.productName = product.getName();
            this.productPrice = product.getPrice();
            this.productDescription = product.getDescription();
            this.productImageUrl = product.getImageUrl();
            this.productCategory = productCategory;
            this.productSaleState = productSaleState.name();
        }
    }

    @Builder
    public record GetItemsByKeyword(String keyword, List<GetItems> products) {

    }

    @Getter
    public static class PageDtoWithKeyword<T> extends PageDto<T> {

        private final String keyword;

        public PageDtoWithKeyword(Page<T> page, String keyword) {
            super(page);
            this.keyword = keyword;
        }
    }
}
