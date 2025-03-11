package com.NBE3_4_2_Team4.global.api.iamport.v1

import com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountRequestDto
import com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountResponseDto
import com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountService
import com.NBE3_4_2_Team4.global.api.iamport.v1.authentication.IamportAuthenticationService
import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentRequestDto
import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentResponseDto
import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentService
import org.springframework.stereotype.Service

@Service
class IamportServiceImpl(

    private val authenticationService: IamportAuthenticationService,
    private val accountService: IamportAccountService,
    private val paymentService: IamportPaymentService,

    ) : IamportService {

    override fun generateAccessToken(memberId: Long): String? {
        return authenticationService.generateAccessToken(memberId)
    }

    override fun getAccessToken(memberId: Long): String? {
        return authenticationService.getAccessToken(memberId)
    }

    override fun validateBankAccount(
        impAccessToken: String,
        bankAccount: IamportAccountRequestDto.BankAccountValidator
    ): String? {
        return accountService.validateBankAccount(impAccessToken, bankAccount)
    }

    override fun getBankCodes(impAccessToken: String): List<IamportAccountResponseDto.BankInfo> {
        return accountService.getBankCodes(impAccessToken)
    }

    override fun findBankNameByBankCode(impAccessToken: String, bankCode: String): IamportAccountResponseDto.BankInfo? {
        return accountService.findBankNameByBankCode(impAccessToken, bankCode)
    }

    override fun getPaymentHistory(impAccessToken: String, impUid: String): IamportPaymentResponseDto.GetPayment? {
        return paymentService.getPaymentHistory(impAccessToken, impUid)
    }

    override fun cancelPayment(
        impAccessToken: String,
        cancelPaymentInfo: IamportPaymentRequestDto.CancelPaymentInfo
    ): IamportPaymentResponseDto.GetPayment? {
        return paymentService.cancelPayment(impAccessToken, cancelPaymentInfo)
    }
}