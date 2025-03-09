package com.NBE3_4_2_Team4.global.api.iamport.v1.account;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

import static com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountRequestDto.*;
import static com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountResponseDto.*;
import static com.NBE3_4_2_Team4.global.api.iamport.v1.constants.IamportConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class IamportAccountServiceImpl implements IamportAccountService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Override
    public Optional<String> validateBankAccount(String accessToken, BankAccountValidator bankAccount) {

        try {
            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            String url = UriComponentsBuilder.fromUriString(IAMPORT_VALIDATE_BANK_ACCOUNT_URL)
                    .queryParam("bank_code", bankAccount.bankCode())
                    .queryParam("bank_num", bankAccount.bankAccountNum())
                    .toUriString();

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    Map.class
            );

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

        } catch (HttpClientErrorException e) {
            log.error("Client Error: {}", e.getMessage());

        } catch (HttpServerErrorException e) {
            log.error("Server Error: {}", e.getMessage());

        } catch (Exception e) {
            log.error("Unexpected Error: {}", e.getMessage());

        }

        return Optional.empty();
    }

    @Override
    public List<BankInfo> getBankCodes(String accessToken) {

        try {
            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    IAMPORT_GET_BANK_CODES_URL,
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            return Optional.ofNullable(response.getBody())
                    .map(body ->
                            objectMapper.convertValue(
                                    body.get("response"),
                                    new TypeReference<List<BankInfo>>() {}))
                    .orElse(Collections.emptyList());

        } catch (HttpClientErrorException e) {
            log.error("Client Error: {}", e.getMessage());

        } catch (HttpServerErrorException e) {
            log.error("Server Error: {}", e.getMessage());

        } catch (Exception e) {
            log.error("Unexpected Error: {}", e.getMessage());

        }

        return Collections.emptyList();
    }

    @Override
    public Optional<BankInfo> findBankNameByBankCode(String accessToken, String bankCode) {

        try {
            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    IAMPORT_FIND_BANK_NAME_URL,
                    HttpMethod.GET,
                    request,
                    Map.class,
                    bankCode
            );

            return Optional.ofNullable(response.getBody())
                    .map(body -> (Map) body.get("response"))
                    .map(responseData ->
                            BankInfo.builder()
                                    .code((String) responseData.get("code"))
                                    .name((String) responseData.get("name"))
                                    .build()
                    );

        } catch (HttpClientErrorException e) {
            log.error("Client Error: {}", e.getMessage());

        } catch (HttpServerErrorException e) {
            log.error("Server Error: {}", e.getMessage());

        } catch (Exception e) {
            log.error("Unexpected Error: {}", e.getMessage());

        }

        return Optional.empty();
    }
}