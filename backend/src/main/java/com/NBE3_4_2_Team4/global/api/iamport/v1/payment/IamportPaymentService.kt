package com.NBE3_4_2_Team4.global.api.iamport.v1.payment

import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentRequestDto.CancelPaymentInfo
import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentResponseDto.GetPayment

interface IamportPaymentService {

    fun getPaymentHistory(
        impAccessToken: String,
        impUid: String
    ) : GetPayment?

    fun cancelPayment(
        impAccessToken: String,
        cancelPaymentInfo: CancelPaymentInfo
    ) : GetPayment?
}