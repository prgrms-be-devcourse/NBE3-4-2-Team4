package com.NBE3_4_2_Team4.domain.product.category.entity

import com.NBE3_4_2_Team4.domain.product.product.entity.Product
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

@Entity
class ProductCategory (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,                                           // 상품 카테고리 아이디

    @Column(nullable = false)
    val name: String,                                               // 상품 카테고리 명

    @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL])
    val products: MutableList<Product> = mutableListOf(),           // 상품 리스트

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: ProductCategory? = null,                            // 상위 카테고리

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL])
    val children: MutableList<ProductCategory> = mutableListOf()    // 하위 카테고리
)