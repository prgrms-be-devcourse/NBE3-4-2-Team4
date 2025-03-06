package com.NBE3_4_2_Team4.domain.payment.payment.dto;

import lombok.Builder;

public class PaymentRequestDto {

    @Builder
    public record VerifyPayment(
            String impUid,
            long amount
    ){
    }

    @Builder
    public record CancelPayment(
            String impUid,
            String merchantUid,
            long amount,
            String reason
    ) {
    }
}