package com.NBE3_4_2_Team4.global.api.iamport.v1.account;

import java.util.List;
import java.util.Optional;

import static com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountRequestDto.*;
import static com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountResponseDto.*;

public interface IamportAccountService {

    Optional<String> validateBankAccount(String accessToken, BankAccountValidator bankAccount);

    List<BankInfo> getBankCodes(String accessToken);

    Optional<BankInfo> findBankNameByBankCode(String accessToken, String bankCode);
}