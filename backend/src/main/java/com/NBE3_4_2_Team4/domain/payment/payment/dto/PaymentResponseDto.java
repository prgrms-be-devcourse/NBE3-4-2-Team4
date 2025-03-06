package com.NBE3_4_2_Team4.domain.payment.payment.dto;

import com.NBE3_4_2_Team4.domain.payment.payment.entity.PaymentStatus;
import lombok.Builder;

import java.time.LocalDateTime;

public class PaymentResponseDto {

    @Builder
    public record VerifiedPayment(
            long amount,
            String buyerName,
            String buyerEmail,
            String impUid,
            String merchantUid,
            String status
    ) {
    }

    @Builder
    public record CanceledPayment(
            long cancelAmount,
            long canceledAt
    ) {
    }

    @Builder
    public record GetPaymentInfo(
            long paymentId,
            String impUid,
            String merchantUid,
            PaymentStatus status,
            LocalDateTime createAt
    ) {
    }
}
