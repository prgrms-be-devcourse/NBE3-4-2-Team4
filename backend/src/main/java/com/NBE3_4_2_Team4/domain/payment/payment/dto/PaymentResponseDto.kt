package com.NBE3_4_2_Team4.domain.payment.payment.dto

import com.NBE3_4_2_Team4.domain.payment.payment.entity.PaymentStatus
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

class PaymentResponseDto {

    data class VerifiedPayment(

        val buyerName: String,
        val amount: Long,
        val status: String
    )

    data class CanceledPayment(

        val cancelerName: String,
        val cancelAmount: Long,
        val canceledAt: Long,
        val status: String
    )

    data class GetPaymentInfo(

        val paymentId: Long,
        val impUid: String,
        val merchantUid: String,
        val amount: Long,
        val status: PaymentStatus,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        val createdAt: LocalDateTime
    )
}