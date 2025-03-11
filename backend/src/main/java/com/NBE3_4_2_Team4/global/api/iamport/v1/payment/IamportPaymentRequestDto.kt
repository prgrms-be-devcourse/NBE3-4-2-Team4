package com.NBE3_4_2_Team4.global.api.iamport.v1.payment

class IamportPaymentRequestDto {

    data class CancelPaymentInfo(

        val impUid: String,         // 아임포트 결제 고유 ID
        val merchantUid: String,    // 가맹점에서 생성한 결제 주문 ID

        val amount: Long,            // 결제 금액
        val reason: String          // 취소 사유
    )
}