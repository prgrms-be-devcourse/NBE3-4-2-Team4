package com.NBE3_4_2_Team4.domain.member.bankAccount.dto;

import lombok.Builder;

public class BankAccountResponseDto {

    @Builder
    public record GetBankAccount(
            Long bankAccountId,
            String bankName,
            String maskedAccountNumber,
            String accountHolder,
            String nickname
    ) {
    }

    @Builder
    public record GetBanks(
            String bankCode,
            String bankName
    ) {
    }
}