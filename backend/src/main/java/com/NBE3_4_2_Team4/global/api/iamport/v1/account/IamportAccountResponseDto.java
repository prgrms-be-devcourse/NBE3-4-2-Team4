package com.NBE3_4_2_Team4.global.api.iamport.v1.account;

import lombok.Builder;
import lombok.Getter;

public class IamportAccountResponseDto {

    @Getter
    @Builder
    public static class BankInfo {

        private final String code;
        private final String name;
    }
}