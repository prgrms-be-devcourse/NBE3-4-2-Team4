package com.NBE3_4_2_Team4.domain.product.order.entity

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetHistory
import com.NBE3_4_2_Team4.domain.product.product.entity.Product
import com.NBE3_4_2_Team4.global.jpa.entity.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
class ProductOrder(

    var orderTime: LocalDateTime = LocalDateTime.now(), // 구매 시간

    @ManyToOne(fetch = FetchType.LAZY)
    var product: Product,                               // 상품

    @OneToOne
    var assetHistory: AssetHistory                      // 포인트 히스토리

) : BaseEntity()