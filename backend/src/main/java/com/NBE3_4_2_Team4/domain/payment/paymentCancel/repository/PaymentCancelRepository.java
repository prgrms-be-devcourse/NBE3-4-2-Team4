package com.NBE3_4_2_Team4.domain.payment.paymentCancel.repository;

import com.NBE3_4_2_Team4.domain.payment.paymentCancel.entity.PaymentCancel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentCancelRepository extends JpaRepository<PaymentCancel, Long> {
}