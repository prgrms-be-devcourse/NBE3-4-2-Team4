package com.NBE3_4_2_Team4.domain.payment.payment.service

import com.NBE3_4_2_Team4.domain.asset.factory.AssetServiceFactory
import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetService
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentRequestDto.CancelPayment
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentRequestDto.ChargePayment
import com.NBE3_4_2_Team4.domain.payment.payment.dto.PaymentResponseDto.*
import com.NBE3_4_2_Team4.domain.payment.payment.entity.Payment
import com.NBE3_4_2_Team4.domain.payment.payment.entity.PaymentStatus
import com.NBE3_4_2_Team4.domain.payment.payment.repository.PaymentRepository
import com.NBE3_4_2_Team4.domain.payment.paymentCancel.entity.PaymentCancel
import com.NBE3_4_2_Team4.domain.payment.paymentCancel.repository.PaymentCancelRepository
import com.NBE3_4_2_Team4.global.api.iamport.v1.IamportService
import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentRequestDto.CancelPaymentInfo
import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentResponseDto.GetPayment
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import com.NBE3_4_2_Team4.global.security.AuthManager.getNonNullMember
import com.NBE3_4_2_Team4.standard.dto.PageDto
import com.NBE3_4_2_Team4.standard.util.Ut
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(

    private val iamportService: IamportService,
    private val paymentRepository: PaymentRepository,
    private val assetServiceFactory: AssetServiceFactory,
    private val assetHistoryRepository: AssetHistoryRepository,
    private val paymentCancelRepository: PaymentCancelRepository
) {

    private val logger = KotlinLogging.logger {}

    @Transactional
    fun chargePayment(
        chargePayment: ChargePayment
    ): VerifiedPayment {

        // 회원 확인
        val member = getNonNullMember()

        // Iamport 액세스 토큰 발급
        val accessToken = iamportService.getAccessToken(member.id)
            ?: iamportService.generateAccessToken(member.id)
            ?: throw ServiceException("500-1", "Iamport 액세스 토큰 발급을 실패했습니다.")

        // 결제이력 단건 조회
        val getPayment = iamportService.getPaymentHistory(accessToken, chargePayment.impUid)
            ?: throw ServiceException(
                "500-2",
                "[${chargePayment.impUid}] 결제 이력을 조회하는 과정에서 문제가 발생했습니다."
            )

        // 결제 검증
        checkPaymentValid(getPayment, member, chargePayment)

        // 캐시 내역 저장 + 캐시 적립
        val assetService: AssetService = assetServiceFactory.getService(chargePayment.assetType)
        val assetHistoryId = assetService.accumulate(
            member.username,
            getPayment.amount,
            chargePayment.assetCategory
        )

        val assetHistory = assetHistoryRepository.findById(assetHistoryId)
            .orElseThrow { ServiceException(
                "404-1",
                "[$assetHistoryId]번에 해당하는 캐시 내역을 찾을 수 없습니다."
            ) }

        // 결제 내역 저장
        val savedPayment = paymentRepository.save(
            Payment(
                impUid = getPayment.impUid,
                merchantUid = getPayment.merchantUid,
                amount = getPayment.amount,
                status = PaymentStatus.PAID,
                assetHistory = assetHistory
            )
        )

        logger.info { "Payment Id [${savedPayment.id}] is saved." }

        return VerifiedPayment(
            buyerName = member.username,
            amount = getPayment.amount,
            status = getPayment.status
        )
    }

    @Transactional
    fun cancelPayment(
        cancelPayment: CancelPayment
    ) : CanceledPayment {

        // 회원 확인
        val member = getNonNullMember()

        // 결제 내역 확인
        val updatedPayment = paymentRepository.findById(cancelPayment.paymentId)
            .orElseThrow { ServiceException(
                "404-1",
                "[${cancelPayment.paymentId}]번에 해당하는 결제 내역이 존재하지 않습니다."
            ) }

        // 결제 내역에 저장된 금액과 취소 요청 금액이 일치하지 않을 경우
        if (cancelPayment.amount != updatedPayment.amount) {
            throw ServiceException(
                "400-1",
                "결제 내역의 금액과 취소 요청 금액이 일치하지 않습니다."
            )
        }

        // Iamport 액세스 토큰 발급
        val accessToken = iamportService.getAccessToken(member.id)
            ?: iamportService.generateAccessToken(member.id)
            ?: throw ServiceException(
                "500-1",
                "Iamport 액세스 토큰 발급을 실패했습니다."
            )

        // 결제 취소
        val getPayment = iamportService.cancelPayment(
            accessToken,
            CancelPaymentInfo(
                impUid = updatedPayment.impUid,
                merchantUid = updatedPayment.merchantUid,
                amount = cancelPayment.amount,
                reason = cancelPayment.reason
            )
        ) ?: throw ServiceException("500-2", "결제 취소 과정에서 문제가 발생했습니다.")

        // 취소 이력이 없을 경우
        val cancelHistory = getPayment.cancelHistory?.firstOrNull()
            ?: throw ServiceException("404-2", "결제 취소 이력을 찾을 수 없습니다.")

        // 결제 상태 변경
        logger.debug { "Payment Status [${updatedPayment.status}] is update target." }
        updatedPayment.updatePaymentStatus(PaymentStatus.CANCELED)

        val updated = paymentRepository.save(updatedPayment)
        logger.info { "Payment Id [${updated.id}] is updated." }

        // 결제 취소 이력 생성
        cancelHistory.reason?.let {
            PaymentCancel(
                cancelAmount = cancelHistory.amount,
                cancelReason = it,
                cancelDate = cancelHistory.convertCancelledAtFormat(),
                payment = updated
            )
        }?.let {
            paymentCancelRepository.save(
                it
            )
        }

        // 캐시 반환 내역 저장 + 캐시 반환
        val assetService: AssetService = assetServiceFactory.getService(cancelPayment.assetType)
        assetService.deduct(
            member.username,
            getPayment.amount,
            cancelPayment.assetCategory
        )

        return CanceledPayment(
            cancelerName = member.username,
            cancelAmount = cancelHistory.amount,
            canceledAt = cancelHistory.cancelledAt,
            status = getPayment.status
        )
    }

    @Transactional(readOnly = true)
    fun getPayments(
        page: Int,
        pageSize: Int,
        paymentStatus: PaymentStatus
    ): PageDto<GetPaymentInfo> {

        val member = getNonNullMember()
        val pageable: Pageable = Ut.pageable.makePageable(page, pageSize)

        val payments: Page<Payment> = paymentRepository.findByMemberIdAndStatusKeyword(
            member.id,
            paymentStatus,
            pageable
        )

        logger.info { "[${payments.content.size}] Payments are found with paging by Status keyword [$paymentStatus]." }

        return PageDto(
            payments.map {
                GetPaymentInfo(
                    paymentId = it.id!!,
                    impUid = it.impUid,
                    merchantUid = it.merchantUid,
                    amount = it.amount,
                    status = it.status,
                    createdAt = it.createdAt
                )
            }
        )
    }

    private fun checkPaymentValid(
        getPayment: GetPayment,
        member: Member,
        chargePayment: ChargePayment
    ) {

        // imp_uid 검증
        require(getPayment.impUid == chargePayment.impUid) {
            throw ServiceException(
                "400-1",
                "결제 고유 ID가 일치하지 않습니다. (요청 ID : ${chargePayment.impUid}, 결제 ID : ${getPayment.impUid})"
            )
        }

        // 결제자 이름 검증
        require(getPayment.buyerName == member.username) {
            throw ServiceException(
                "400-2",
                "결제자 이름이 일치하지 않습니다. (요청 이름 : ${member.username}, 결제 이름 : ${getPayment.buyerName})"
            )
        }

        // 결제자 이메일 검증 (존재할 때만 검증)
        getPayment.buyerEmail?.let { buyerEmail ->
            val frontBuyerEmail = buyerEmail.substringBefore("@").dropLast(3)
            val frontRequestEmail = member.emailAddress.substringBefore("@").dropLast(3)

            val domainBuyerEmail = buyerEmail.substringAfter("@")
            val domainRequestEmail = member.emailAddress.substringAfter("@")

            require(frontBuyerEmail == frontRequestEmail && domainBuyerEmail == domainRequestEmail) {
                throw ServiceException(
                    "400-3",
                    "결제자 이메일이 일치하지 않습니다. (요청 이메일 : ${member.emailAddress}, 결제 이메일 : $buyerEmail)"
                )
            }
        }

        // 결제 금액 검증
        require(getPayment.amount == chargePayment.amount) {
            throw ServiceException(
                "400-4",
                "결제 금액이 일치하지 않습니다. (요청 금액 : ${chargePayment.amount}, 결제 금액 : ${getPayment.amount})"
            )
        }
    }
}