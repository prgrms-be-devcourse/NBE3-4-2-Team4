package com.NBE3_4_2_Team4.global.api.iamport.v1.account

class IamportAccountRequestDto {

    data class BankAccountValidator(
        val bankCode: String,
        val bankAccountNum: String
    )
}