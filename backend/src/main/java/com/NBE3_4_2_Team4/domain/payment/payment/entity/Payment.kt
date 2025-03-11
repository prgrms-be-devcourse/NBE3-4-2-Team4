package com.NBE3_4_2_Team4.domain.payment.payment.entity;

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetHistory;
import com.NBE3_4_2_Team4.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseTime {

    @Column(nullable = false)
    private String impUid;              // 아임포트 결제 고유 ID

    @Column(nullable = false)
    private String merchantUid;         // 가맹점에서 생성한 결제 주문 ID

    @Column(nullable = false)
    private Long amount;                // 결제 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;       // 결제 상태

    @OneToOne
    @JoinColumn(name = "asset_history_id", nullable = false, unique = true)
    private AssetHistory assetHistory;  // 재화 히스토리

    public void updatePaymentStatus(PaymentStatus paymentStatus) {
        if (paymentStatus != null) {
            this.status = paymentStatus;
        }
    }
}