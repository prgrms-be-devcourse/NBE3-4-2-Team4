package com.NBE3_4_2_Team4.global.api.iamport.v1.payment

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class IamportPaymentResponseDto {

    data class GetPayment(

        val amount: Long,                           // 결제 금액 (단위: 원)

        val bankCode: String?,                      // 은행 코드 (가상계좌 결제 시)
        val bankName: String?,                      // 은행 이름 (가상계좌 결제 시)

        val buyerAddr: String?,                     // 결제자 주소
        val buyerEmail: String?,                    // 결제자 이메일
        val buyerName: String?,                     // 결제자 이름
        val buyerPostcode: String?,                 // 결제자 우편번호
        val buyerTel: String?,                      // 결제자 전화번호

        val cancelAmount: Long,                     // 취소된 금액 (단위: 원)
        val cancelHistory: List<CancelHistory>?,    // 취소 내역
        val cancelReason: String?,                  // 취소 사유
        val cancelReceiptUrls: List<String>?,       // 취소 영수증 URL 리스트
        val cancelledAt: Long,                      // 결제 취소 시간 (Unix Timestamp)

        val applyNum: String?,                      // 카드 승인번호 (카드 결제 시)
        val cardCode: String?,                      // 카드사 코드
        val cardName: String?,                      // 카드사 이름
        val cardNumber: String?,                    // 카드 번호 (마스킹 처리됨)
        val cardQuota: Int,                         // 할부 개월 수 (0이면 일시불)
        val cardType: String?,                      // 카드 타입 (체크카드, 신용카드 등)

        val cashReceiptIssued: Boolean,             // 현금영수증 발행 여부

        val channel: String?,                       // 결제 채널 (pc, mobile 등)
        val currency: String?,                      // 결제 통화 (예: KRW, USD)
        val customData: Any?,                       // 가맹점에서 전달한 추가 데이터 (JSON 형태)
        val customerUid: String?,                   // 정기결제 고객 고유 ID (정기결제 사용 시)
        val customerUidUsage: String?,              // 정기결제 사용 여부 (null 또는 값)
        val embPgProvider: String?,                 // 임베디드 결제 PG사 (예: kakaopay)
        val escrow: Boolean,                        // 에스크로 결제 여부

        val failReason: String?,                    // 결제 실패 사유 (실패 시 값 존재)
        val failedAt: Long,                         // 결제 실패 시간 (Unix Timestamp)

        val impUid: String,                         // 아임포트에서 발급한 결제 고유 ID
        val merchantUid: String,                    // 가맹점에서 발급한 주문 ID
        val name: String?,                          // 결제 상품명
        val startedAt: Long,                        // 결제 요청 시간 (Unix Timestamp)
        val paidAt: Long,                           // 결제 완료 시간 (Unix Timestamp)
        val payMethod: String?,                     // 결제 방식 (card, vbank, trans 등)
        val pgId: String?,                          // PG사 ID (아임포트 내부 식별자)
        val pgProvider: String?,                    // PG사 이름 (예: tosspayments, kakaopay)
        val pgTid: String?,                         // PG사에서 발급한 거래 ID
        val receiptUrl: String?,                    // 결제 영수증 URL
        val status: String,                         // 결제 상태 (paid: 결제완료, ready: 가상계좌 생성, cancelled: 취소)

        val userAgent: String?,                     // 사용자의 브라우저 정보

        val vbankCode: String?,                     // 가상계좌 은행 코드 (가상계좌 결제 시)
        val vbankDate: Long,                        // 가상계좌 입금 기한 (Unix Timestamp)
        val vbankHolder: String?,                   // 가상계좌 예금주명
        val vbankIssuedAt: Long,                    // 가상계좌 발급 시간 (Unix Timestamp)
        val vbankName: String?,                     // 가상계좌 은행 이름
        val vbankNum: String?                       // 가상계좌 계좌번호

    )

    data class CancelHistory(

        val pgTid: String,                          // PG사에서 발급한 결제 취소 거래 ID
        val amount: Long,                           // 취소 금액 (단위: 원)
        val cancelledAt: Long,                      // 취소된 시간 (Unix Timestamp)
        val reason: String?,                        // 취소 사유
        val receiptUrl: String?,                    // 취소 영수증 URL
        val cancellationId: String?                 // 취소 고유 ID (아임포트에서 제공)

    ) {

        fun convertCancelledAtFormat(
        ): LocalDateTime {

            require(cancelledAt > 0) {
                "취소된 시간이 올바르지 않습니다. (cancelledAt: $cancelledAt)"
            }

            return try {
                LocalDateTime.ofInstant(Instant.ofEpochSecond(cancelledAt), ZoneId.systemDefault())
            } catch (e: Exception) {
                throw RuntimeException("취소된 시간의 포맷 변환 중 오류 발생: ${e.message}", e)
            }
        }
    }
}