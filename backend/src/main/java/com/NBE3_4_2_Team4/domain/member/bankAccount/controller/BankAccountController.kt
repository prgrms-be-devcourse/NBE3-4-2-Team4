package com.NBE3_4_2_Team4.domain.member.bankAccount.controller

import com.NBE3_4_2_Team4.domain.member.bankAccount.dto.BankAccountRequestDto.*
import com.NBE3_4_2_Team4.domain.member.bankAccount.dto.BankAccountResponseDto.GetBankAccount
import com.NBE3_4_2_Team4.domain.member.bankAccount.dto.BankAccountResponseDto.GetBanks
import com.NBE3_4_2_Team4.domain.member.bankAccount.service.BankAccountService
import com.NBE3_4_2_Team4.global.rsData.RsData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/banks")
@Tag(name = "은행 계좌 관리", description = "은행 계좌 API")
class BankAccountController(

    private val bankAccountService: BankAccountService,
) {

    @GetMapping
    @Operation(summary = "은행 코드 목록 조회", description = "은행 코드 목록을 조회합니다.")
    fun getBankCodes(
    ): RsData<List<GetBanks>> {

        val banks = bankAccountService.getBanks()

        return RsData(
            "200-1",
            "${banks.size}건의 은행이 조회되었습니다.",
            banks
        )
    }

    @GetMapping("/accounts")
    @Operation(summary = "은행 계좌 목록 조회", description = "해당 유저가 등록된 은행 계좌 목록을 조회합니다.")
    fun getBankAccounts(
    ): RsData<List<GetBankAccount>> {

        val bankAccounts = bankAccountService.findAllBankAccount()

        return RsData(
            "200-1",
            "${bankAccounts.size}건의 은행 계좌가 조회되었습니다.",
            bankAccounts
        )
    }

    @GetMapping("/accounts/{bank_account_id}")
    @Operation(summary = "은행 계좌 단건 조회", description = "은행 계좌를 단건 조회합니다.")
    fun getBankAccount(
        @PathVariable("bank_account_id") bankAccountId: Long
    ): RsData<GetBankAccount> {

        val bankAccount = bankAccountService.getBankAccount(bankAccountId)

        return RsData(
            "200-1",
            "${bankAccount.bankAccountId}번 은행 계좌가 조회되었습니다.",
            bankAccount
        )
    }

    @PostMapping("/accounts")
    @Operation(summary = "은행 계좌 인증 + 등록", description = "해당 유저의 은행 계좌를 인증 후 등록합니다.")
    fun generateBankAccount(
        @RequestBody @Valid request: GenerateBankAccount
    ): RsData<GetBankAccount> {

        val bankAccount = bankAccountService.generateBankAccount(request)

        return RsData(
            "200-1",
            "${bankAccount.bankAccountId}번 은행 계좌가 등록되었습니다.",
            bankAccount
        )
    }

    @PatchMapping("/accounts/{bank_account_id}")
    @Operation(summary = "은행 계좌 별칭 수정", description = "은행 계좌의 별칭을 수정합니다.")
    fun updateBankAccountNickname(
        @PathVariable("bank_account_id") bankAccountId: Long,
        @RequestBody @Valid request: UpdateBankAccount
    ): RsData<GetBankAccount> {

        val bankAccount = bankAccountService.updateBankAccountNickname(bankAccountId, request)

        return RsData(
            "200-1",
            "${bankAccount.bankAccountId}번 은행 계좌 별명이 ${bankAccount.nickname}으로 수정되었습니다.",
            bankAccount
        )
    }

    @DeleteMapping("/accounts/{bank_account_id}")
    @Operation(summary = "은행 계좌 삭제", description = "은행 계좌를 삭제합니다.")
    fun deleteBankAccount(
        @PathVariable("bank_account_id") bankAccountId: Long
    ): RsData<Unit> {

        bankAccountService.deleteBankAccount(bankAccountId)

        return RsData(
            "200-1",
            "${bankAccountId}번 은행 계좌가 삭제되었습니다."
        )
    }

    @PostMapping("/accounts/duplicate")
    @Operation(summary = "은행 계좌 중복 체크", description = "이미 등록된 은행 계좌인지 체크합니다.")
    fun duplicateCheckBankAccount(
        @RequestBody request: DuplicateCheckBankAccount
    ): RsData<Unit> {

        bankAccountService.checkBankAccountDuplicated(request)

        return RsData(
            "200-1",
            "[(${request.bankCode}) ${request.accountNumber}] 은행 계좌는 등록되지 않았습니다."
        )
    }



}