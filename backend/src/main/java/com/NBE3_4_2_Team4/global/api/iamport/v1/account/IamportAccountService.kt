package com.NBE3_4_2_Team4.global.api.iamport.v1.account

import com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountRequestDto.BankAccountValidator
import com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountResponseDto.BankInfo

interface IamportAccountService {

    fun validateBankAccount(
        accessToken: String,
        bankAccount: BankAccountValidator
    ) : String?

    fun getBankCodes(
        accessToken: String
    ) : List<BankInfo>

    fun findBankNameByBankCode(
        accessToken: String,
        bankCode: String
    ) : BankInfo?
}