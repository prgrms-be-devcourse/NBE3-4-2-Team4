package com.NBE3_4_2_Team4.global.api.iamport.v1;

import com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountRequestDto.BankAccountValidator;
import com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountResponseDto.BankInfo;
import com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountService;
import com.NBE3_4_2_Team4.global.api.iamport.v1.authentication.IamportAuthenticationService;
import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentRequestDto.CancelPaymentInfo;
import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentResponseDto.GetPayment;
import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IamportServiceImpl implements IamportService {

    private final IamportAuthenticationService authenticationService;
    private final IamportAccountService accountService;
    private final IamportPaymentService paymentService;

    @Override
    public Optional<String> generateAccessToken(Long memberId) {
        return authenticationService.generateAccessToken(memberId);
    }

    @Override
    public Optional<String> getAccessToken(Long memberId) {
        return authenticationService.getAccessToken(memberId);
    }

    @Override
    public Optional<String> validateBankAccount(String impAccessToken, BankAccountValidator bankAccount) {
        return accountService.validateBankAccount(impAccessToken, bankAccount);
    }

    @Override
    public List<BankInfo> getBankCodes(String impAccessToken) {
        return accountService.getBankCodes(impAccessToken);
    }

    @Override
    public Optional<BankInfo> findBankNameByBankCode(String impAccessToken, String bankCode) {
        return accountService.findBankNameByBankCode(impAccessToken, bankCode);
    }

    @Override
    public Optional<GetPayment> getPaymentHistory(String impAccessToken, String impUid) {
        return paymentService.getPaymentHistory(impAccessToken, impUid);
    }

    @Override
    public Optional<GetPayment> cancelPayment(String impAccessToken, CancelPaymentInfo cancelPaymentInfo) {
        return paymentService.cancelPayment(impAccessToken, cancelPaymentInfo);
    }
}