package com.NBE3_4_2_Team4.domain.payment.payment.repository

import com.NBE3_4_2_Team4.domain.payment.payment.entity.Payment
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepository : JpaRepository<Payment, Long>, PaymentRepositoryCustom {
}