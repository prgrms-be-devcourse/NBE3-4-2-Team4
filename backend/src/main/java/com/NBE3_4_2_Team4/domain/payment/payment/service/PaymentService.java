package com.NBE3_4_2_Team4.domain.payment.payment.service;

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetHistory;
import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentRequestDto.CancelPayment;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentRequestDto.UpdatePayment;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentRequestDto.VerifyPayment;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentRequestDto.WritePayment;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentResponseDto.CanceledPayment;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentResponseDto.GetPaymentInfo;
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentResponseDto.VerifiedPayment;
import com.NBE3_4_2_Team4.domain.payment.payment.entity.Payment;
import com.NBE3_4_2_Team4.domain.payment.payment.entity.PaymentStatus;
import com.NBE3_4_2_Team4.domain.payment.payment.repository.PaymentRepository;
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
    private final AssetHistoryRepository assetHistoryRepository;

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
                                    .status(payment.getStatus())
                                    .createAt(payment.getCreatedAt())
                                .build()));
    }

    @Transactional
    public GetPaymentInfo writePayment(WritePayment writePayment) {

        AssetHistory assetHistory = assetHistoryRepository.findById(writePayment.assetHistoryId())
                .orElseThrow(() -> new ServiceException(
                        "404-1",
                        "[%d]번에 해당하는 재화 내역이 존재하지 않습니다."
                                .formatted(writePayment.assetHistoryId()))
                );

        Payment saved = paymentRepository.save(
                Payment.builder()
                        .impUid(writePayment.impUid())
                        .merchantUid(writePayment.merchantUid())
                        .amount(writePayment.amount())
                        .assetHistory(assetHistory)
                        .build());

        log.info("Payment Id [{}] is saved.", saved.getId());

        return GetPaymentInfo.builder()
                    .paymentId(saved.getId())
                    .impUid(saved.getImpUid())
                    .merchantUid(saved.getMerchantUid())
                    .status(saved.getStatus())
                    .createAt(saved.getCreatedAt())
                .build();
    }

    @Transactional
    public GetPaymentInfo updatePayment(Long paymentId, UpdatePayment updatePayment) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ServiceException(
                        "404-1",
                        "[%d]번에 해당하는 재화 내역이 존재하지 않습니다.".formatted(paymentId))
                );

        if (updatePayment.status() == null) {
            throw new ServiceException("400-1", "변경할 결제 상태가 존재하지 않습니다.");
        }

        log.debug("Payment Status [{}] is update target.", updatePayment.status());

        payment.updatePaymentStatus(updatePayment.status());

        Payment updated = paymentRepository.save(payment);

        log.info("Payment Id [{}] is updated.", updated.getId());

        return GetPaymentInfo.builder()
                .paymentId(updated.getId())
                .impUid(updated.getImpUid())
                .merchantUid(updated.getMerchantUid())
                .status(updated.getStatus())
                .createAt(updated.getCreatedAt())
                .build();
    }

    private void checkPaymentValid(
            GetPayment getPayment,
            Member member,
            VerifyPayment verifyPayment
    ) {

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