package com.NBE3_4_2_Team4.domain.member.bankAccount.service;

import com.NBE3_4_2_Team4.domain.member.bankAccount.dto.BankAccountRequestDto;
import com.NBE3_4_2_Team4.domain.member.bankAccount.dto.BankAccountRequestDto.DuplicateCheckBankAccount;
import com.NBE3_4_2_Team4.domain.member.bankAccount.dto.BankAccountResponseDto.GetBanks;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.bankAccount.entity.BankAccount;
import com.NBE3_4_2_Team4.domain.member.bankAccount.repository.BankAccountRepository;
import com.NBE3_4_2_Team4.domain.member.bankAccount.dto.BankAccountRequestDto.GenerateBankAccount;
import com.NBE3_4_2_Team4.domain.member.bankAccount.dto.BankAccountRequestDto.UpdateBankAccount;
import com.NBE3_4_2_Team4.domain.member.bankAccount.dto.BankAccountResponseDto.GetBankAccount;
import com.NBE3_4_2_Team4.global.api.iamport.v1.IamportService;
import com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountRequestDto.BankAccountValidator;
import com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountResponseDto.BankInfo;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.NBE3_4_2_Team4.global.security.AuthManager.getNonNullMember;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final IamportService iamportService;

    @Transactional(readOnly = true)
    public List<GetBankAccount> findAllBankAccount() {

        // 회원 확인
        Member member = getNonNullMember();

        // 회원의 모든 계좌 조회
        List<BankAccount> bankAccounts = bankAccountRepository.findAllByMember(member);

        log.info("[{}] BankAccounts are found.", bankAccounts.size());

        return bankAccounts.stream()
                .map(bankAccount -> GetBankAccount.builder()
                            .bankAccountId(bankAccount.getId())
                            .bankName(bankAccount.getBankName())
                            .maskedAccountNumber(bankAccount.getMaskedAccountNumber())
                            .accountHolder(bankAccount.getAccountHolder())
                            .nickname(bankAccount.getNickname())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GetBanks> getBanks() {

        // 회원 확인
        Member member = getNonNullMember();

        // Iamport 액세스 토큰 발급
        String accessToken = iamportService.getAccessToken(member.getId())
                .orElse(iamportService.generateAccessToken(member.getId())
                        .orElseThrow(() -> new ServiceException("500-1", "Iamport 액세스 토큰 발급을 실패했습니다."))
        );

        // 모든 은행 목록 조회
        List<BankInfo> bankCodes = iamportService.getBankCodes(accessToken);

        log.info("[{}] Banks are found.", bankCodes.size());

        return bankCodes.stream()
                .map(bankCode ->
                    GetBanks.builder()
                                .bankCode(bankCode.getCode())
                                .bankName(bankCode.getName())
                            .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public GetBankAccount getBankAccount(Long bankAccountId) {
        
        // 은행 계좌 조회
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new ServiceException("404-1", "해당 은행 계좌가 존재하지 않습니다."));

        log.info("BankAccount Id [{}] is found.", bankAccountId);

        return GetBankAccount.builder()
                    .bankAccountId(bankAccount.getId())
                    .bankName(bankAccount.getBankName())
                    .maskedAccountNumber(bankAccount.getMaskedAccountNumber())
                    .accountHolder(bankAccount.getAccountHolder())
                    .nickname(bankAccount.getNickname())
                .build();
    }

    @Transactional
    public GetBankAccount generateBankAccount(GenerateBankAccount accountInfo) {

        // 회원 확인
        Member member = getNonNullMember();

        // Iamport 액세스 토큰 발급
        String accessToken = iamportService.getAccessToken(member.getId())
                .orElse(iamportService.generateAccessToken(member.getId())
                        .orElseThrow(() -> new ServiceException("500-1", "Iamport 액세스 토큰 발급을 실패했습니다."))
                );

        // 은행 계좌 인증
        verifyBankAccount(accessToken,
                accountInfo.getBankCode(),
                accountInfo.getAccountNumber(),
                accountInfo.getAccountHolder()
        );

        // 은행코드로 은행명 조회
        BankInfo bankInfo = iamportService.findBankNameByBankCode(accessToken, accountInfo.getBankCode())
                .orElseThrow(() -> new ServiceException("404-1", "은행 코드에 해당하는 은행이 존재하지 않습니다."));

        // 은행 계좌 저장
        BankAccount bankAccount = bankAccountRepository.save(
                accountInfo.toBankAccount(
                        member,
                        maskAccountNumber(accountInfo.getAccountNumber()),
                        bankInfo.getName()
                )
        );

        log.info("BankAccount Id [{}] is saved.", bankAccount.getId());

        return GetBankAccount.builder()
                    .bankAccountId(bankAccount.getId())
                    .bankName(bankAccount.getBankName())
                    .maskedAccountNumber(bankAccount.getMaskedAccountNumber())
                    .accountHolder(bankAccount.getAccountHolder())
                    .nickname(bankAccount.getNickname())
                .build();
    }

    @Transactional
    public GetBankAccount updateBankAccountNickname(Long bankAccountId, UpdateBankAccount updateAccountInfo) {

        // 은행 계좌 조회
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new ServiceException("404-1", "해당 은행 계좌가 존재하지 않습니다."));

        // 계좌 별칭 수정
        bankAccount.updateNickname(updateAccountInfo.getNickname());

        BankAccount updatedBankAccount = bankAccountRepository.save(bankAccount);

        log.info("BankAccount Nickname is update [{}] to [{}].",
                bankAccount.getNickname(), updatedBankAccount.getNickname());

        return GetBankAccount.builder()
                .bankAccountId(bankAccount.getId())
                .bankName(bankAccount.getBankName())
                .maskedAccountNumber(updatedBankAccount.getMaskedAccountNumber())
                .accountHolder(updatedBankAccount.getAccountHolder())
                .nickname(updatedBankAccount.getNickname())
                .build();
    }

    @Transactional
    public void deleteBankAccount(Long bankAccountId) {

        // 은행 계좌 조회
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new ServiceException("404-1", "해당 은행 계좌가 존재하지 않습니다."));

        // 은행 계좌 삭제
        bankAccountRepository.delete(bankAccount);

        log.info("BankAccount Id [{}] is deleted", bankAccount.getId());
    }

    @Transactional(readOnly = true)
    public boolean checkBankAccountDuplicated(DuplicateCheckBankAccount checkBankAccount) {

        boolean isExist = bankAccountRepository.existsByBankCodeAndAccountNumberAndAccountHolder(
                checkBankAccount.getBankCode(),
                checkBankAccount.getAccountNumber(),
                checkBankAccount.getAccountHolder()
        );

        if (isExist) {
            throw new ServiceException("409-1", "해당 은행 계좌는 이미 등록되었습니다.");
        }

        return isExist;
    }

    private boolean verifyBankAccount(
            String accessToken,
            String bankCode,
            String accountNumber,
            String accountHolder
    ) {

        // 은행 계좌 유효성 체크
        String holder = iamportService.validateBankAccount(
                accessToken,
                BankAccountValidator.builder()
                        .bankCode(bankCode)
                        .bankAccountNum(accountNumber)
                        .build()
        ).orElseThrow(() -> new ServiceException("500-1", "해당 계좌의 유효성을 체크하던 중에 오류가 발생했습니다."));

        // 예금주명 체크
        if (!accountHolder.equals(holder)) {
            throw new ServiceException("400-1", "해당 계좌의 예금주가 다릅니다. [기대값: %s, 결과값: %s]"
                    .formatted(holder, accountHolder));
        }

        return true;
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null) {
            log.warn("AccountNumber is Null, so we set Default masked Account Number. [****]");
            return "****";
        }

        return accountNumber.replaceAll("(?<=\\d{3})\\d(?=\\d{3})", "*");
    }
}