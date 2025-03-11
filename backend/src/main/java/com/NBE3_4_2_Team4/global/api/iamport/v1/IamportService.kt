package com.NBE3_4_2_Team4.global.api.iamport.v1

import com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountRequestDto.BankAccountValidator
import com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountResponseDto.BankInfo
import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentRequestDto.CancelPaymentInfo
import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentResponseDto.GetPayment

interface IamportService {

    /**
     * @apiNote 아임포트 V1 인증 토큰 요청 + Redis 저장
     * @implNote
     * 토큰 사용법 : 다른 API 호출 시 Authorization Header에 Bearer <액세스 토큰> 형식으로 사용
     * 토큰 만료 시간 : 발행시간으로부터 30분
     * 토큰 재갱신 시간 : 만료 시간으로부터 1분 이내로 남았을 경우, 5분 재갱신
     */
    fun generateAccessToken(memberId: Long): String?

    /**
     * @apiNote 아임포트 V1 인증 토큰 조회 (Redis)
     */
    fun getAccessToken(memberId: Long): String?

    /**
     * @apiNote 아임포트 V1 예금주 조회
     */
    fun validateBankAccount(impAccessToken: String, bankAccount: BankAccountValidator): String?

    /**
     * @apiNote 아임포트 V1 은행 코드 전체 검색
     */
    fun getBankCodes(impAccessToken: String): List<BankInfo>

    /**
     * @apiNote 아임포트 V1 은행명 조회
     */
    fun findBankNameByBankCode(impAccessToken: String, bankCode: String): BankInfo?

    /**
     * @apiNote 아임포트 V1 결제내역 단건 조회
     */
    fun getPaymentHistory(impAccessToken: String, impUid: String): GetPayment?

    /**
     * @apiNote 아임포트 V1 결제 취소
     */
    fun cancelPayment(impAccessToken: String, cancelPaymentInfo: CancelPaymentInfo): GetPayment?

}