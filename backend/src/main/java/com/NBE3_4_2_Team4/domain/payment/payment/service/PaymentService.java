package com.NBE3_4_2_Team4.domain.payment.payment.service;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentRequestDto.CancelPayment;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentRequestDto.VerifyPayment;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentResponseDto.CanceledPayment;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentResponseDto.VerifiedPayment;
import com.NBE3_4_2_Team4.global.api.iamport.v1.IamportService;
import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentRequestDto.CancelPaymentInfo;
import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentResponseDto.CancelHistory;
import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentResponseDto.GetPayment;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.NBE3_4_2_Team4.global.security.AuthManager.getNonNullMember;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final IamportService iamportService;

    @Transactional
    public VerifiedPayment verifyPayment(VerifyPayment verifyPayment) {

        // 회원 확인
        Member member = getNonNullMember();

        // Iamport 액세스 토큰 발급
        String accessToken = iamportService.getAccessToken(member.getId())
                .orElse(iamportService.generateAccessToken(member.getId())
                        .orElseThrow(() -> new ServiceException("500-1", "Iamport 액세스 토큰 발급을 실패했습니다."))
                );

        // 결제이력 단건 조회
        GetPayment getPayment = iamportService.getPaymentHistory(accessToken, verifyPayment.impUid())
                .orElseThrow(() -> new ServiceException("500-2", "[%s] 결제 이력을 조회하는 과정에서 문제가 발생했습니다."
                        .formatted(verifyPayment.impUid())));

        // 결제 검증
        checkPaymentValid(getPayment, member, verifyPayment);

        return VerifiedPayment.builder()
                    .amount(getPayment.amount())
                    .status(getPayment.status())
                    .buyerName(getPayment.buyerName())
                    .buyerEmail(getPayment.buyerEmail())
                    .impUid(getPayment.impUid())
                    .merchantUid(getPayment.merchantUid())
                .build();
    }

    @Transactional
    public CanceledPayment cancelPayment(CancelPayment cancelPayment) {

        // 회원 확인
        Member member = getNonNullMember();

        // Iamport 액세스 토큰 발급
        String accessToken = iamportService.getAccessToken(member.getId())
                .orElse(iamportService.generateAccessToken(member.getId())
                        .orElseThrow(() -> new ServiceException("500-1", "Iamport 액세스 토큰 발급을 실패했습니다."))
                );

        // 결제 취소
        GetPayment getPayment = iamportService.cancelPayment(
                        accessToken,
                        CancelPaymentInfo.builder()
                                    .impUid(cancelPayment.impUid())
                                    .merchantUid(cancelPayment.merchantUid())
                                    .amount(cancelPayment.amount())
                                    .reason(cancelPayment.reason())
                                .build())
                .orElseThrow(() -> new ServiceException("500-2", "[%s] 결제 취소 과정에서 문제가 발생했습니다."
                        .formatted(cancelPayment.impUid())));

        // 취소 이력이 없을 경우
        if (getPayment.cancelHistory().isEmpty()) {
            throw new ServiceException("404-1", "[%s] 결제 취소 이력이 존재하지 않습니다."
                    .formatted(cancelPayment.impUid()));
        }

        CancelHistory cancelHistory = getPayment.cancelHistory().getFirst();

        return CanceledPayment.builder()
                .cancelAmount(cancelHistory.amount())
                .canceledAt(cancelHistory.cancelledAt())
                .build();
    }

    private void checkPaymentValid(GetPayment getPayment, Member member, VerifyPayment verifyPayment) {

        // imp_uid 검증
        if (!getPayment.impUid().equals(verifyPayment.impUid())) {
            throw new ServiceException("", "결제 고유 ID가 일치하지 않습니다. (요청 ID : %s, 결제 ID : %s)"
                    .formatted(verifyPayment.impUid(), getPayment.impUid()));
        }

        // 결제자 이름 검증
        if (!getPayment.buyerName().equals(member.getUsername())) {
            throw new ServiceException("", "결제자 이름이 일치하지 않습니다. (요청 이름 : %s, 결제 이름 : %s)"
                    .formatted(member.getUsername(), getPayment.buyerName()));
        }

        // 결제자 이메일 검증 (존재할 때만 검증)
        if (getPayment.buyerEmail() != null) {

            // 결제 내역 이메일에는 @ 앞에 마스킹 처리되어있음 (ex. admin@test.com -> ad***@test.com)
            String[] buyerEmailSplit = getPayment.buyerEmail().split("@");
            String[] requestEmailSplit = member.getEmailAddress().split("@");

            String frontBuyerEmail = buyerEmailSplit[0].substring(0, buyerEmailSplit[0].length() - 3);
            String frontRequestEmail = requestEmailSplit[0].substring(0, buyerEmailSplit[0].length() - 3);

            if (!frontBuyerEmail.equals(frontRequestEmail)
                    || !buyerEmailSplit[1].equals(requestEmailSplit[1])) {
                throw new ServiceException("", "결제자 이메일이 일치하지 않습니다. (요청 이메일 : %s, 결제 이메일 : %s)"
                        .formatted(member.getEmailAddress(), getPayment.buyerEmail()));
            }
        }

        // 결제 금액 검증
        if (getPayment.amount() != verifyPayment.amount()) {
            throw new ServiceException("", "결제 금액이 일치하지 않습니다. (요청 금액 : %s, 결제 금액 : %s)"
                    .formatted(verifyPayment.amount(), getPayment.amount()));
        }
    }
}