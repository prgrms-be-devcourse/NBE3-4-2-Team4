package com.NBE3_4_2_Team4.domain.payment.payment.repository

import com.NBE3_4_2_Team4.domain.payment.payment.entity.Payment
import com.NBE3_4_2_Team4.domain.payment.payment.entity.PaymentStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PaymentRepositoryCustom {

    fun findByMemberIdAndStatusKeyword(
        memberId: Long,
        paymentStatus: PaymentStatus,
        pageable: Pageable
    ) : Page<Payment>
}