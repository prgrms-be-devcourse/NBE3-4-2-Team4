package com.NBE3_4_2_Team4.domain.payment.payment.controller;

import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentRequestDto.CancelPayment;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentRequestDto.VerifyPayment;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentResponseDto.CanceledPayment;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentResponseDto.VerifiedPayment;
import com.NBE3_4_2_Team4.domain.payment.payment.service.PaymentService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@Tag(name = "결제 관리", description = "결제 API")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/verify")
    @Operation(summary = "결제 승인 검증", description = "요청 온 결제가 정상적으로 승인되었는지 검증합니다.")
    RsData<VerifiedPayment> verifyPayment(
            @RequestBody VerifyPayment request
    ) {

        VerifiedPayment verifiedPayment = paymentService.verifyPayment(request);

        return new RsData<>(
                "200-1",
                "[%s] 결제가 정상적으로 승인되었습니다.".formatted(request.impUid()),
                verifiedPayment
        );
    }

    @PostMapping("/cancel")
    @Operation(summary = "결제 취소", description = "결제를 취소합니다.")
    RsData<CanceledPayment> cancelPayment(
            @RequestBody CancelPayment request
    ) {

        CanceledPayment canceledPayment = paymentService.cancelPayment(request);

        return new RsData<>(
                "200-1",
                "[%s] 결제가 정상적으로 취소되었습니다.".formatted(request.impUid()),
                canceledPayment
        );
    }
}