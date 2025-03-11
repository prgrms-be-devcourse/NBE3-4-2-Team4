package com.NBE3_4_2_Team4.global.api.iamport.v1.payment;

import lombok.Builder;

public class IamportPaymentRequestDto {

    @Builder
    public record CancelPaymentInfo(
            String impUid,          // 아임포트 결제 고유 ID
            String merchantUid,     // 가맹점에서 생성한 결제 주문 ID

            long amount,            // 결제 금액
            String reason           // 취소 사유
    ) {
    }
}