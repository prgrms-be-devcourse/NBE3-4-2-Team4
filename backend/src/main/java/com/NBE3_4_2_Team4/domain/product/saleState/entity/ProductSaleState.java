package com.NBE3_4_2_Team4.domain.product.saleState.entity;

import com.NBE3_4_2_Team4.domain.product.product.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSaleState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                         // 상품 판매 상태 아이디

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleState name;                                  // 상품 판매 상태명

    @OneToMany(mappedBy = "saleState", cascade = CascadeType.ALL)
    private List<Product> products;                          // 상품 리스트
}
