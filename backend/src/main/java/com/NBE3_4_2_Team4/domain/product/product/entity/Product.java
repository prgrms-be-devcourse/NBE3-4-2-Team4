package com.NBE3_4_2_Team4.domain.product.product.entity;

import com.NBE3_4_2_Team4.domain.product.order.entity.ProductOrder;
import com.NBE3_4_2_Team4.domain.product.category.entity.ProductCategory;
import com.NBE3_4_2_Team4.domain.product.saleState.entity.ProductSaleState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                        // 상품 아이디

    @Column(nullable = false)
    private String name;                    // 상품명

    @Column(nullable = false)
    private int price;                      // 상품 가격

    @Column(columnDefinition = "text")
    private String description;             // 상품 설명

    @Column(nullable = false)
    private String imageUrl;                // 상품 이미지 URL

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductCategory category;       // 상품 카테고리

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductSaleState saleState;     // 상품 판매 상태

    @OneToMany(mappedBy = "product")        // 주문 리스트
    private List<ProductOrder> productOrders;

    public void updateName(String name) {
        this.name = name;
    }

    public void updatePrice(int price) {
        this.price = price;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateCategory(ProductCategory category) {
        this.category = category;
    }

    public void updateSaleState(ProductSaleState saleState) {
        this.saleState = saleState;
    }
}
