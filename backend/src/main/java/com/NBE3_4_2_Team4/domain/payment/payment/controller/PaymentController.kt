package com.NBE3_4_2_Team4.domain.payment.payment.controller

import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentRequestDto.CancelPayment
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentRequestDto.ChargePayment
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentResponseDto.*
import com.NBE3_4_2_Team4.domain.payment.payment.entity.PaymentStatus
import com.NBE3_4_2_Team4.domain.payment.payment.service.PaymentService
import com.NBE3_4_2_Team4.global.rsData.RsData
import com.NBE3_4_2_Team4.standard.dto.PageDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/payments")
@Tag(name = "결제 관리", description = "결제 API")
class PaymentController(

    private val paymentService: PaymentService
) {

    @PostMapping("/charge")
    @Operation(
        summary = "결제 승인 검증 + 결제 내역 생성 + 캐시 적립",
        description = "요청 온 결제를 승인 검증하고 승인된 결제 내역을 생성 후 해당 유저에게 캐시를 적립합니다."
    )
    fun chargePayment(
        @RequestBody chargePayment: ChargePayment
    ) : RsData<VerifiedPayment> {

        val verifiedPayment = paymentService.chargePayment(chargePayment)

        return RsData(
            "200-1",
            "[${verifiedPayment.buyerName}] 유저의 캐시가 ${verifiedPayment.amount} 적립되었습니다.",
            verifiedPayment
        )
    }

    @PostMapping("/cancel")
    @Operation(
        summary = "결제 취소 + 결제 상태 변경 + 캐시 반환",
        description = "요청 온 결제 취소를 하고 결제 상태를 변경한 후 해당 유저의 캐시를 반환합니다."
    )
    fun cancelPayment(
        @RequestBody request: CancelPayment
    ): RsData<CanceledPayment> {

        val canceledPayment = paymentService.cancelPayment(request)

        return RsData(
            "200-1",
            "[${canceledPayment.cancelerName}] 유저의 [${request.paymentId}]번 결제가 정상적으로 취소되었습니다.",
            canceledPayment
        )
    }

    @GetMapping
    @Operation(
        summary = "해당 유저의 전체 결제 내역 조회 with 필터",
        description = "해당 유저의 전체 결제 내역을 필터링, 페이징 처리하여 조회합니다."
    )
    fun getAllPaymentByStatusWithPaging(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(name = "page_size", defaultValue = "5") pageSize: Int,
        @RequestParam(name = "payment_status_keyword", defaultValue = "ALL") paymentStatus: PaymentStatus
    ): RsData<PageDto<GetPaymentInfo>> {

        val payments = paymentService.getPayments(page, pageSize, paymentStatus)

        return RsData(
            "200-1",
            "[${payments.items.size}] 건의 결제 내역이 조회되었습니다.",
            payments
        )
    }
}