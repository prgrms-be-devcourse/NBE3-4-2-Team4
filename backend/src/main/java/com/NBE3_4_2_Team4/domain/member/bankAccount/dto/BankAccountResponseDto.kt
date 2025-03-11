package com.NBE3_4_2_Team4.domain.member.bankAccount.dto

class BankAccountResponseDto {

    data class GetBankAccount(

        val bankAccountId: Long,
        val bankName: String,
        val maskedAccountNumber: String,
        val accountHolder: String,
        val nickname: String?
    )

    data class GetBanks(

        val bankCode: String,
        val bankName: String
    )
}