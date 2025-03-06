package com.NBE3_4_2_Team4.domain.payment.payment.dto;

import lombok.Builder;

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
}
