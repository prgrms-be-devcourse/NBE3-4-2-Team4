package com.NBE3_4_2_Team4.global.api.iamport.v1.payment;

import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentRequestDto.CancelPaymentInfo;
import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentResponseDto.GetPayment;

import java.util.Optional;

public interface IamportPaymentService {

    Optional<GetPayment> getPaymentHistory(String impAccessToken, String impUid);

    Optional<GetPayment> cancelPayment(String impAccessToken, CancelPaymentInfo cancelPaymentInfo);
}