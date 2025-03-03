package com.NBE3_4_2_Team4.global.api.iamport.v1.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountRequestDto.*;
import static com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountResponseDto.*;
import static com.NBE3_4_2_Team4.global.api.iamport.v1.constants.IamportConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class IamportAccountServiceImpl implements IamportAccountService {

    private final RestTemplate restTemplate;

    @Override
    public Optional<String> validateBankAccount(String accessToken, BankAccountValidator bankAccount) {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("bank_code", bankAccount.bankCode());
        requestBody.put("bank_num", bankAccount.bankAccountNum());

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                IAMPORT_VALIDATE_BANK_ACCOUNT_URL,
                HttpMethod.GET,
                request,
                Map.class
        );

        // 200 성공일 경우
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return Optional.ofNullable(response.getBody())
                    .filter(body -> body.containsKey("response"))
                    .map(body -> (Map) body.get("response"))
                    .filter(responseData -> responseData.containsKey("bank_holder"))
                    .map(responseData -> (String) responseData.get("bank_holder"))
                    .map(name -> {
                       log.info("Bank Account is validated: [{}, {} -> {}]",
                               bankAccount.bankCode(),
                               bankAccount.bankAccountNum(),
                               name);
                       return name;
                    });
        }

        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            // 401 에러일 경우
            log.error("[{}] Access Token is Invalid.", accessToken);

        } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
            // 400 에러일 경우
            log.error("Bank Code or Bank Account Num is Invalid: [{}, {}]",
                    bankAccount.bankCode(), bankAccount.bankAccountNum());

        } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            // 404 에러일 경우
            log.error("Bank Account is Not Exist: [{}, {}]",
                    bankAccount.bankCode(), bankAccount.bankAccountNum());

        } else {
            // 알 수 없는 에러일 경우
            log.error("Failed to validate Bank Account: [Unknown Error]");
        }

        return Optional.empty();
    }

    @Override
    public List<BankInfo> getBankCodes(String accessToken) {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                IAMPORT_GET_BANK_CODES_URL,
                HttpMethod.GET,
                request,
                Map.class
        );

        // 200 성공일 경우
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return Optional.ofNullable(response.getBody())
                    .map(body -> (List<BankInfo>) body.get("response"))
                    .orElse(Collections.emptyList());
        }

        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            // 401 에러일 경우
            log.error("[{}] Access Token is Invalid.", accessToken);

        } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            // 404 에러일 경우
            log.error("Failed to get Bank Info");

        } else {
            // 알 수 없는 에러일 경우
            log.error("Failed to get Bank Info: [Unknown Error]");
        }

        return Collections.emptyList();
    }

    @Override
    public Optional<BankInfo> findBankNameByBankCode(String accessToken, String bankCode) {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                IAMPORT_FIND_BANK_NAME_URL,
                HttpMethod.GET,
                request,
                Map.class,
                bankCode
        );

        // 200 성공일 경우
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return Optional.ofNullable(response.getBody())
                    .map(body -> (Map) body.get("response"))
                    .map(responseData ->
                            BankInfo.builder()
                            .bankCode((String) responseData.get("code"))
                            .bankName((String) responseData.get("name"))
                            .build()
                    );
        }

        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            // 401 에러일 경우
            log.error("[{}] Access Token is Invalid.", accessToken);

        } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            // 404 에러일 경우
            log.error("Failed to get Bank Name: {}", bankCode);

        } else {
            // 알 수 없는 에러일 경우
            log.error("Failed to get Bank Info: [Unknown Error]");
        }

        return Optional.empty();
    }
}