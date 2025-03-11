package com.NBE3_4_2_Team4.global.api.iamport.v1.account;

import lombok.Builder;

public class IamportAccountRequestDto {

    @Builder
    public record BankAccountValidator(String bankCode, String bankAccountNum) {
    }
}