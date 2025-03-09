package com.NBE3_4_2_Team4.domain.payment.payment.controller;

import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentRequestDto.CancelPayment;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentRequestDto.UpdatePayment;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentRequestDto.VerifyPayment;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentRequestDto.WritePayment;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentResponseDto.CanceledPayment;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentResponseDto.GetPaymentInfo;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentResponseDto.VerifiedPayment;
import com.NBE3_4_2_Team4.domain.payment.payment.entity.PaymentStatus;
import com.NBE3_4_2_Team4.domain.payment.payment.service.PaymentService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
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

    @GetMapping
    @Operation(summary = "해당 유저의 전체 결제 내역 조회 with 필터", description = "해당 유저의 전체 결제 내역을 필터링, 페이징 처리하여 조회합니다.")
    RsData<PageDto<GetPaymentInfo>> getAllPaymentByStatusWithPaging(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "5") int pageSize,
            @RequestParam(name = "payment_status_keyword", defaultValue = "ALL") PaymentStatus paymentStatus
    ) {

        PageDto<GetPaymentInfo> payments = paymentService.getPayments(page, pageSize, paymentStatus);

        return new RsData<>(
                "200-1",
                "[%s] 건의 결제 내역이 조회되었습니다.".formatted(payments.getItems().size()),
                payments
        );
    }

    @PostMapping
    @Operation(summary = "해당 유저의 결제 내역 생성", description = "해당 유저가 결제한 내역을 생성합니다.")
    RsData<GetPaymentInfo> writePayment(
            @RequestBody WritePayment writePayment
    ) {

        GetPaymentInfo payment = paymentService.writePayment(writePayment);

        return new RsData<>(
                "200-1",
                "%d번 결제 내역이 생성되었습니다.".formatted(payment.paymentId()),
                payment
        );
    }

    @PatchMapping("/{payment_id}")
    @Operation(summary = "해당 유저의 결제 상태 변경", description = "해당 유저가 결제한 내역의 상태를 변경합니다.")
    RsData<GetPaymentInfo> updatePayment(
            @PathVariable(name = "payment_id") Long paymentId,
            @RequestBody UpdatePayment updatePayment
    ) {

        GetPaymentInfo payment = paymentService.updatePayment(paymentId, updatePayment);

        return new RsData<>(
                "200-1",
                "%d번 결제 내역의 결제 상태가 수정되었습니다.".formatted(payment.paymentId()),
                payment
        );
    }
}