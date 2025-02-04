package com.NBE3_4_2_Team4.domain.product.category.entity;

import com.NBE3_4_2_Team4.domain.product.product.entity.Product;
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
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                            // 상품 카테고리 아이디

    @Column(nullable = false)
    private String name;                        // 상품 카테고리 명

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;             // 상품 리스트

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ProductCategory parent;             // 상위 카테고리

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<ProductCategory> children;     // 하위 카테고리
}
