package com.NBE3_4_2_Team4.domain.payment.paymentCancel.entity;

import com.NBE3_4_2_Team4.domain.payment.payment.entity.Payment;
import com.NBE3_4_2_Team4.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancel extends BaseEntity {

    private Long cancelAmount;          // 취소 금액

    private String cancelReason;        // 취소 이유

    private LocalDateTime cancelDate;   // 취소 날짜

    @OneToOne
    @JoinColumn(name = "payment_id", nullable = false, unique = true)
    private Payment payment;            // 결제 이력
}