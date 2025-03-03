package com.NBE3_4_2_Team4.global.api.iamport.v1;

import com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountRequestDto.BankAccountValidator;
import com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountResponseDto.BankInfo;
import com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountService;
import com.NBE3_4_2_Team4.global.api.iamport.v1.authentication.IamportAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IamportServiceImpl implements IamportService {

    private final IamportAuthenticationService authenticationService;
    private final IamportAccountService accountService;

    @Override
    public Optional<String> generateAccessToken(Long memberId) {
        return authenticationService.generateAccessToken(memberId);
    }

    @Override
    public Optional<String> getAccessToken(Long memberId) {
        return authenticationService.getAccessToken(memberId);
    }

    @Override
    public Optional<String> validateBankAccount(String accessToken, BankAccountValidator bankAccount) {
        return accountService.validateBankAccount(accessToken, bankAccount);
    }

    @Override
    public List<BankInfo> getBankCodes(String accessToken) {
        return accountService.getBankCodes(accessToken);
    }

    @Override
    public Optional<BankInfo> findBankNameByBankCode(String accessToken, String bankCode) {
        return accountService.findBankNameByBankCode(accessToken, bankCode);
    }
}