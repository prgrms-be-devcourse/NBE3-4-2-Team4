package com.NBE3_4_2_Team4.global.api.iamport.v1.payment;

import lombok.Builder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class IamportPaymentResponseDto {

    @Builder
    public record GetPayment(
            long amount,                        // 결제 금액 (단위: 원)

            String bankCode,                    // 은행 코드 (가상계좌 결제 시)
            String bankName,                    // 은행 이름 (가상계좌 결제 시)

            String buyerAddr,                   // 결제자 주소
            String buyerEmail,                  // 결제자 이메일
            String buyerName,                   // 결제자 이름
            String buyerPostcode,               // 결제자 우편번호
            String buyerTel,                    // 결제자 전화번호

            long cancelAmount,                  // 취소된 금액 (단위: 원)
            List<CancelHistory> cancelHistory,  // 취소 내역
            String cancelReason,                // 취소 사유
            List<String> cancelReceiptUrls,     // 취소 영수증 URL 리스트
            long cancelledAt,                   // 결제 취소 시간 (Unix Timestamp)

            String applyNum,                    // 카드 승인번호 (카드 결제 시)
            String cardCode,                    // 카드사 코드
            String cardName,                    // 카드사 이름
            String cardNumber,                  // 카드 번호 (마스킹 처리됨)
            int cardQuota,                      // 할부 개월 수 (0이면 일시불)
            String cardType,                    // 카드 타입 (체크카드, 신용카드 등)

            boolean cashReceiptIssued,          // 현금영수증 발행 여부

            String channel,                     // 결제 채널 (pc, mobile 등)
            String currency,                    // 결제 통화 (예: KRW, USD)
            Object customData,                  // 가맹점에서 전달한 추가 데이터 (JSON 형태)
            String customerUid,                 // 정기결제 고객 고유 ID (정기결제 사용 시)
            String customerUidUsage,            // 정기결제 사용 여부 (null 또는 값)
            String embPgProvider,               // 임베디드 결제 PG사 (예: kakaopay)
            boolean escrow,                     // 에스크로 결제 여부

            String failReason,                  // 결제 실패 사유 (실패 시 값 존재)
            long failedAt,                      // 결제 실패 시간 (Unix Timestamp)

            String impUid,                      // 아임포트에서 발급한 결제 고유 ID
            String merchantUid,                 // 가맹점에서 발급한 주문 ID
            String name,                        // 결제 상품명
            long startedAt,                     // 결제 요청 시간 (Unix Timestamp)
            long paidAt,                        // 결제 완료 시간 (Unix Timestamp)
            String payMethod,                   // 결제 방식 (card, vbank, trans 등)
            String pgId,                        // PG사 ID (아임포트 내부 식별자)
            String pgProvider,                  // PG사 이름 (예: tosspayments, kakaopay)
            String pgTid,                       // PG사에서 발급한 거래 ID
            String receiptUrl,                  // 결제 영수증 URL
            String status,                      // 결제 상태 (paid: 결제완료, ready: 가상계좌 생성, cancelled: 취소)

            String userAgent,                   // 사용자의 브라우저 정보

            String vbankCode,                   // 가상계좌 은행 코드 (가상계좌 결제 시)
            long vbankDate,                     // 가상계좌 입금 기한 (Unix Timestamp)
            String vbankHolder,                 // 가상계좌 예금주명
            long vbankIssuedAt,                 // 가상계좌 발급 시간 (Unix Timestamp)
            String vbankName,                   // 가상계좌 은행 이름
            String vbankNum                     // 가상계좌 계좌번호
    ) {
    }

    public record CancelHistory(
            String pgTid,                       // PG사에서 발급한 결제 취소 거래 ID
            long amount,                        // 취소 금액 (단위: 원)
            long cancelledAt,                   // 취소된 시간 (Unix Timestamp)
            String reason,                      // 취소 사유
            String receiptUrl,                  // 취소 영수증 URL
            String cancellationId               // 취소 고유 ID (아임포트에서 제공)
    ) {

        public LocalDateTime convertCancelledAtFormat() {

            if (cancelledAt <= 0) {
                throw new IllegalArgumentException(
                        "취소된 시간이 올바르지 않습니다. (cancelledAt: " + cancelledAt + ")"
                );
            }

            try {
                return LocalDateTime.ofInstant(Instant.ofEpochSecond(cancelledAt), ZoneId.systemDefault());
            } catch (Exception e) {
                throw new RuntimeException(
                        "취소된 시간의 포맷 변환 중 오류 발생: " + e.getMessage(), e);
            }
        }
    }
}