package com.NBE3_4_2_Team4.domain.payment.paymentCancel.entity

import com.NBE3_4_2_Team4.domain.payment.payment.entity.Payment
import com.NBE3_4_2_Team4.global.jpa.entity.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import java.time.LocalDateTime

@Entity
class PaymentCancel(

    val cancelAmount: Long,         // 취소 금액

    val cancelReason: String,       // 취소 이유

    val cancelDate: LocalDateTime,  // 취소 날짜

    @OneToOne
    @JoinColumn(name = "payment_id", nullable = false, unique = true)
    val payment: Payment            // 결제 이력

) : BaseEntity()