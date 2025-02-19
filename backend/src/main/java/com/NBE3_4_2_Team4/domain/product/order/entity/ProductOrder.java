package com.NBE3_4_2_Team4.domain.product.order.entity;

import com.NBE3_4_2_Team4.domain.asset.point.entity.PointHistory;
import com.NBE3_4_2_Team4.domain.product.product.entity.Product;
import com.NBE3_4_2_Team4.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ProductOrder extends BaseEntity {

    private LocalDateTime orderTime;    // 구매 시간

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;            // 상품

    @OneToOne
    private PointHistory pointHistory;  // 포인트 히스토리
}
