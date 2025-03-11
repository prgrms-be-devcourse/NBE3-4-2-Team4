package com.NBE3_4_2_Team4.domain.product.saleState.entity

import com.NBE3_4_2_Team4.domain.product.product.entity.Product
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class ProductSaleState(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,                                   // 상품 판매 상태 아이디

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val name: SaleState,                                    // 상품 판매 상태명

    @OneToMany(mappedBy = "saleState", cascade = [CascadeType.ALL])
    val products: MutableList<Product> = mutableListOf()    // 상품 리스트
)