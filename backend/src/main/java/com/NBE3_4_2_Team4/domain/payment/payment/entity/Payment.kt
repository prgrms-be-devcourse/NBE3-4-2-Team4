package com.NBE3_4_2_Team4.domain.payment.payment.entity

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetHistory
import com.NBE3_4_2_Team4.global.jpa.entity.BaseTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne

@Entity
class Payment(

    @Column(nullable = false)
    val impUid: String,             // 아임포트 결제 고유 ID

    @Column(nullable = false)
    val merchantUid: String,        // 가맹점에서 생성한 결제 주문 ID

    @Column(nullable = false)
    var amount: Long,               // 결제 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: PaymentStatus,      // 결제 상태

    @OneToOne
    @JoinColumn(name = "asset_history_id", nullable = false, unique = true)
    var assetHistory: AssetHistory  // 재화 히스토리

) : BaseTime() {

    fun updatePaymentStatus(paymentStatus: PaymentStatus) {
        this.status = paymentStatus
    }
}