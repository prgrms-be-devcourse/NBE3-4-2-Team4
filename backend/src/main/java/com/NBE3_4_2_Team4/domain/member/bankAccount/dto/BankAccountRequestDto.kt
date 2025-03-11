package com.NBE3_4_2_Team4.domain.member.bankAccount.dto

import com.NBE3_4_2_Team4.domain.member.bankAccount.entity.BankAccount
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

class BankAccountRequestDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class GenerateBankAccount(

        @field:NotBlank(
            message = "은행코드는 필수 입력 값입니다."
        )
        @field:Size(
            max = 3,
            message = "은행코드는 최대 3자리 숫자만 가능합니다."
        )
        @field:Pattern(
            regexp = "^[0-9]+$",
            message = "은행코드는 숫자만 입력 가능합니다."
        )
        val bankCode: String,

        @field:NotBlank(
            message = "계좌번호는 필수 입력 값입니다."
        )
        @field:Size(
            min = 10,
            max = 15,
            message = "계좌번호는 10~15자리여야 합니다."
        )
        @field:Pattern(
            regexp = "^[0-9]+$",
            message = "계좌번호는 숫자만 입력 가능합니다."
        )
        val accountNumber: String,

        @field:NotBlank(
            message = "예금주는 필수 입력 값입니다."
        )
        val accountHolder: String,

        val nickname: String? = null
    ) {

        fun toBankAccount(
            member: Member,
            maskAccountNumber: String,
            bankName: String
        ): BankAccount {

            return BankAccount(
                bankCode = this.bankCode,
                bankName = bankName,
                accountNumber = this.accountNumber,
                maskedAccountNumber = maskAccountNumber,
                accountHolder = this.accountHolder,
                nickname = this.nickname,
                member = member
            )
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class UpdateBankAccount(

        @field:Size(
            max = 20,
            message = "계좌 별칭은 최대 20자만 입력 가능합니다."
        )
        val nickname: String? = null
    )

    data class DuplicateCheckBankAccount(

        @field:NotBlank(
            message = "은행코드는 필수 입력 값입니다."
        )
        @field:Size(
            max = 3,
            message = "은행코드는 최대 3자리 숫자만 가능합니다."
        )
        @field:Pattern(
            regexp = "^[0-9]+$",
            message = "은행코드는 숫자만 입력 가능합니다."
        )
        val bankCode: String,

        @field:NotBlank(
            message = "계좌번호는 필수 입력 값입니다."
        )
        @field:Size(
            min = 10,
            max = 15,
            message = "계좌번호는 10~15자리여야 합니다."
        )
        @field:Pattern(
            regexp = "^[0-9]+$",
            message = "계좌번호는 숫자만 입력 가능합니다."
        )
        val accountNumber: String,

        @field:NotBlank(
            message = "예금주는 필수 입력 값입니다."
        )
        val accountHolder: String
    )
}