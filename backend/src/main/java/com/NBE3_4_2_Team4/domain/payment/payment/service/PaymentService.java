package com.NBE3_4_2_Team4.domain.payment.payment.service;

import com.NBE3_4_2_Team4.domain.asset.factory.AssetServiceFactory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetHistory;
import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository;
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetService;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentRequestDto.CancelPayment;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentRequestDto.ChargePayment;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentResponseDto.CanceledPayment;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentResponseDto.GetPaymentInfo;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentResponseDto.VerifiedPayment;
import com.NBE3_4_2_Team4.domain.payment.payment.entity.Payment;
import com.NBE3_4_2_Team4.domain.payment.payment.entity.PaymentStatus;
import com.NBE3_4_2_Team4.domain.payment.payment.repository.PaymentRepository;
import com.NBE3_4_2_Team4.domain.payment.paymentCancel.entity.PaymentCancel;
import com.NBE3_4_2_Team4.domain.payment.paymentCancel.repository.PaymentCancelRepository;
import com.NBE3_4_2_Team4.global.api.iamport.v1.IamportService;
import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentRequestDto.CancelPaymentInfo;
import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentResponseDto.CancelHistory;
import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentResponseDto.GetPayment;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
import com.NBE3_4_2_Team4.standard.util.Ut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.NBE3_4_2_Team4.global.security.AuthManager.getNonNullMember;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final IamportService iamportService;
    private final PaymentRepository paymentRepository;
    private final AssetServiceFactory assetServiceFactory;
    private final AssetHistoryRepository assetHistoryRepository;
    private final PaymentCancelRepository paymentCancelRepository;

    @Transactional
    public VerifiedPayment chargePayment(ChargePayment chargePayment) {

        // 회원 확인
        Member member = getNonNullMember();

        // Iamport 액세스 토큰 발급
        String accessToken = iamportService.getAccessToken(member.getId())
                .orElse(iamportService.generateAccessToken(member.getId())
                        .orElseThrow(() -> new ServiceException("500-1", "Iamport 액세스 토큰 발급을 실패했습니다."))
                );

        // 결제이력 단건 조회
        GetPayment getPayment = iamportService.getPaymentHistory(accessToken, chargePayment.impUid())
                .orElseThrow(() -> new ServiceException("500-2", "[%s] 결제 이력을 조회하는 과정에서 문제가 발생했습니다."
                        .formatted(chargePayment.impUid())));

        // 결제 검증
        checkPaymentValid(getPayment, member, chargePayment);

        // 캐시 내역 저장 + 캐시 적립
        AssetService assetService = assetServiceFactory.getService(chargePayment.assetType());
        Long assetHistoryId = assetService.accumulate(
                member.getUsername(),
                getPayment.amount(),
                chargePayment.assetCategory());

        AssetHistory assetHistory = assetHistoryRepository.findById(assetHistoryId)
                .orElseThrow(() -> new ServiceException(
                        "404-1",
                        "[%d]번에 해당하는 캐시 내역을 찾을 수 없습니다.".formatted(assetHistoryId))
                );

        // 결제 내역 저장
        Payment savedPayment = paymentRepository.save(Payment.builder()
                .impUid(getPayment.impUid())
                .merchantUid(getPayment.merchantUid())
                .amount(getPayment.amount())
                .status(PaymentStatus.PAID)
                .assetHistory(assetHistory)
                .build());

        log.info("Payment Id [{}] is saved.", savedPayment.getId());

        return VerifiedPayment.builder()
                    .buderName(member.getUsername())
                    .amount(getPayment.amount())
                    .status(getPayment.status())
                .build();
    }

    @Transactional
    public CanceledPayment cancelPayment(CancelPayment cancelPayment) {

        // 회원 확인
        Member member = getNonNullMember();

        // 결제 내역 확인
        Payment updatedPayment = paymentRepository.findById(cancelPayment.paymentId())
                .orElseThrow(() -> new ServiceException(
                        "404-1",
                        "[%d]번에 해당하는 결제 내역이 존재하지 않습니다.".formatted(cancelPayment.paymentId()))
                );

        // 결제 내역에 저장된 금액과 취소 요청 금액이 일치하지 않을 경우
        if (cancelPayment.amount() != updatedPayment.getAmount()) {
            throw new ServiceException(
                    "400-1",
                    "결제 내역의 금액과 취소 요청 금액이 일치하지 않습니다."
            );
        }

        // Iamport 액세스 토큰 발급
        String accessToken = iamportService.getAccessToken(member.getId())
                .orElse(iamportService.generateAccessToken(member.getId())
                        .orElseThrow(() -> new ServiceException(
                                "500-1",
                                "Iamport 액세스 토큰 발급을 실패했습니다."))
                );

        // 결제 취소
        GetPayment getPayment = iamportService.cancelPayment(
                        accessToken,
                        CancelPaymentInfo.builder()
                                .impUid(updatedPayment.getImpUid())
                                .merchantUid(updatedPayment.getMerchantUid())
                                .amount(cancelPayment.amount())
                                .reason(cancelPayment.reason())
                                .build())
                .orElseThrow(() -> new ServiceException(
                        "500-2",
                        "결제 취소 과정에서 문제가 발생했습니다.")
                );

        // 취소 이력이 없을 경우
        if (getPayment.cancelHistory().isEmpty()) {
            throw new ServiceException(
                    "404-2",
                    "결제 취소 이력을 찾을 수 없습니다."
            );
        }

        CancelHistory cancelHistory = getPayment.cancelHistory().getFirst();

        // 결제 상태 변경
        if (updatedPayment.getStatus() == null) {
            throw new ServiceException("400-1", "변경할 결제 상태가 존재하지 않습니다.");
        }

        log.debug("Payment Status [{}] is update target.", updatedPayment.getStatus());

        updatedPayment.updatePaymentStatus(PaymentStatus.CANCELED);
        Payment updated = paymentRepository.save(updatedPayment);

        log.info("Payment Id [{}] is updated.", updated.getId());


        // 결제 취소 이력 생성
        paymentCancelRepository.save(
                PaymentCancel.builder()
                            .cancelAmount(cancelHistory.amount())
                            .cancelReason(cancelHistory.reason())
                            .cancelDate(cancelHistory.convertCancelledAtFormat())
                            .payment(updated)
                        .build());

        // 캐시 반환 내역 저장 + 캐시 반환
        AssetService assetService = assetServiceFactory.getService(cancelPayment.assetType());
        assetService.deduct(
                member.getUsername(),
                getPayment.amount(),
                cancelPayment.assetCategory()
        );

        return CanceledPayment.builder()
                .cancelAmount(cancelHistory.amount())
                .canceledAt(cancelHistory.cancelledAt())
                .build();
    }

    @Transactional(readOnly = true)
    public PageDto<GetPaymentInfo> getPayments(
            int page,
            int pageSize,
            PaymentStatus paymentStatus
    ) {

        Member member = getNonNullMember();
        Pageable pageable = Ut.pageable.makePageable(page, pageSize);

        Page<Payment> payments = paymentRepository.findByMemberIdAndStatusKeyword(
                member.getId(),
                paymentStatus,
                pageable
        );

        log.info("[{}] Payments are found with paging by Status keyword [{}].",
                payments.getContent().size(), paymentStatus);

        return new PageDto<>(
                payments.map(payment ->
                        GetPaymentInfo.builder()
                                .paymentId(payment.getId())
                                .impUid(payment.getImpUid())
                                .merchantUid(payment.getMerchantUid())
                                .amount(payment.getAmount())
                                .status(payment.getStatus())
                                .createdAt(payment.getCreatedAt())
                                .build()));
    }

    private void checkPaymentValid(
            GetPayment getPayment,
            Member member,
            ChargePayment chargePayment
    ) {

        // imp_uid 검증
        if (!getPayment.impUid().equals(chargePayment.impUid())) {
            throw new ServiceException(
                    "400-1",
                    "결제 고유 ID가 일치하지 않습니다. (요청 ID : %s, 결제 ID : %s)"
                    .formatted(chargePayment.impUid(), getPayment.impUid()));
        }

        // 결제자 이름 검증
        if (!getPayment.buyerName().equals(member.getUsername())) {
            throw new ServiceException(
                    "400-2",
                    "결제자 이름이 일치하지 않습니다. (요청 이름 : %s, 결제 이름 : %s)"
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
                throw new ServiceException(
                        "400-3",
                        "결제자 이메일이 일치하지 않습니다. (요청 이메일 : %s, 결제 이메일 : %s)"
                        .formatted(member.getEmailAddress(), getPayment.buyerEmail()));
            }
        }

        // 결제 금액 검증
        if (getPayment.amount() != chargePayment.amount()) {
            throw new ServiceException(
                    "400-4",
                    "결제 금액이 일치하지 않습니다. (요청 금액 : %s, 결제 금액 : %s)"
                    .formatted(chargePayment.amount(), getPayment.amount()));
        }
    }
}