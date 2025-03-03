package com.NBE3_4_2_Team4.domain.member.bankAccount.controller;

import com.NBE3_4_2_Team4.domain.member.bankAccount.dto.BankAccountRequestDto.GenerateBankAccount;
import com.NBE3_4_2_Team4.domain.member.bankAccount.dto.BankAccountRequestDto.UpdateBankAccount;
import com.NBE3_4_2_Team4.domain.member.bankAccount.dto.BankAccountResponseDto.GetBankAccount;
import com.NBE3_4_2_Team4.domain.member.bankAccount.dto.BankAccountResponseDto.GetBanks;
import com.NBE3_4_2_Team4.domain.member.bankAccount.service.BankAccountService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/banks")
@Tag(name = "은행 계좌 관리", description = "은행 계좌 API")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @GetMapping()
    @Operation(summary = "은행 코드 목록 조회", description = "은행 코드 목록을 조회합니다.")
    RsData<List<GetBanks>> getBankCodes() {

        List<GetBanks> banks = bankAccountService.getBanks();

        return new RsData<>(
                "200-1",
                "%d건의 은행이 조회되었습니다.".formatted(banks.size()),
                banks
        );
    }

    @GetMapping("/accounts")
    @Operation(summary = "은행 계좌 목록 조회", description = "해당 유저가 등록된 은행 계좌 목록을 조회합니다.")
    RsData<List<GetBankAccount>> getBankAccounts() {

        List<GetBankAccount> bankAccounts = bankAccountService.findAllBankAccount();

        return new RsData<>(
                "200-1",
                "%d건의 은행 계좌가 조회되었습니다.".formatted(bankAccounts.size()),
                bankAccounts
        );
    }

    @GetMapping("/accounts/{bank_account_id}")
    @Operation(summary = "은행 계좌 단건 조회", description = "은행 계좌를 단건 조회합니다.")
    RsData<GetBankAccount> getBankAccount(
            @PathVariable("bank_account_id") Long bankAccountId
    ) {

        GetBankAccount bankAccount = bankAccountService.getBankAccount(bankAccountId);

        return new RsData<>(
                "200-1",
                "%d번 은행 계좌가 조회되었습니다.".formatted(bankAccount.bankAccountId()),
                bankAccount
        );
    }

    @PostMapping("/accounts")
    @Operation(summary = "은행 계좌 인증 + 등록", description = "해당 유저의 은행 계좌를 인증 후 등록합니다.")
    RsData<GetBankAccount> generateBankAccount(
            @RequestBody @Validated GenerateBankAccount request
    ) {

        GetBankAccount bankAccount = bankAccountService.generateBankAccount(request);

        return new RsData<>(
                "200-1",
                "%d번 은행 계좌가 등록되었습니다.".formatted(bankAccount.bankAccountId()),
                bankAccount
        );
    }

    @PatchMapping("/accounts/{bank_account_id}")
    @Operation(summary = "은행 계좌 별칭 수정", description = "은행 계좌의 별칭을 수정합니다.")
    RsData<GetBankAccount> updateBankAccountNickname(
            @PathVariable("bank_account_id") Long bankAccountId,
            @RequestBody @Validated UpdateBankAccount request
    ) {

        GetBankAccount bankAccount = bankAccountService.updateBankAccountNickname(bankAccountId, request);

        return new RsData<>(
                "200-1",
                "%d번 은행 계좌 별명이 %s으로 수정되었습니다.".formatted(bankAccount.bankAccountId()),
                bankAccount
        );
    }

    @DeleteMapping("/accounts/{bank_account_id}")
    @Operation(summary = "은행 계좌 삭제", description = "은행 계좌를 삭제합니다.")
    RsData<GetBankAccount> deleteBankAccount(
            @PathVariable("bank_account_id") Long bankAccountId
    ) {

        bankAccountService.deleteBankAccount(bankAccountId);

        return new RsData<>(
                "200-1",
                "%d번 은행 계좌가 삭제되었습니다.".formatted(bankAccountId)
        );
    }
}