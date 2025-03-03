package com.NBE3_4_2_Team4.domain.member.bankAccount.dto;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.bankAccount.entity.BankAccount;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

public class BankAccountRequestDto {

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GenerateBankAccount {

        @NotBlank(message = "은행코드는 필수 입력 값입니다.")
        @Size(max = 3, message = "은행코드는 최대 3자리 숫자만 가능합니다.")
        @Pattern(regexp = "^[0-9]+$", message = "은행코드는 숫자만 입력 가능합니다.")
        private String bankCode;

        @NotBlank(message = "계좌번호는 필수 입력 값입니다.")
        @Size(min = 10, max = 15, message = "계좌번호는 10~15자리여야 합니다.")
        @Pattern(regexp = "^[0-9]+$", message = "계좌번호는 숫자만 입력 가능합니다.")
        private String accountNumber;

        @NotBlank(message = "예금주는 필수 입력 값입니다.")
        private String accountHolder;

        private String nickname;

        public BankAccount toBankAccount(Member member, String maskAccountNumber, String bankName) {
            return BankAccount.builder()
                    .bankCode(this.bankCode)
                    .bankName(bankName)
                    .accountNumber(this.accountNumber)
                    .maskedAccountNumber(maskAccountNumber)
                    .accountHolder(this.accountHolder)
                    .nickname(this.nickname)
                    .member(member)
                    .build();
        }
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UpdateBankAccount {

        @Size(max = 10, message = "계좌 별칭은 최대 10자만 가능합니다.")
        private String nickname;
    }
}