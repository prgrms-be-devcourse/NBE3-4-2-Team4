package com.NBE3_4_2_Team4.domain.payment.payment.dto;

import com.NBE3_4_2_Team4.domain.payment.payment.entity.PaymentStatus;
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

    @Builder
    public record WritePayment(
            Long assetHistoryId,
            String impUid,
            String merchantUid,
            Long amount,
            PaymentStatus status
    ) {
    }

    @Builder
    public record UpdatePayment(
            PaymentStatus status
    ) {
    }
}