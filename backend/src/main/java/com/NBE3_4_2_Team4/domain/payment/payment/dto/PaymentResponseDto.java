package com.NBE3_4_2_Team4.domain.payment.payment.dto;

import com.NBE3_4_2_Team4.domain.payment.payment.entity.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

public class PaymentResponseDto {

    @Builder
    public record VerifiedPayment(

            String buderName,

            long amount,

            String status
    ) {
    }

    @Builder
    public record CanceledPayment(

            String cancelerName,

            long cancelAmount,

            long canceledAt,

            String status
    ) {
    }

    @Builder
    public record GetPaymentInfo(

            long paymentId,

            String impUid,

            String merchantUid,

            long amount,

            PaymentStatus status,

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime createdAt
    ) {
    }
}
