package com.NBE3_4_2_Team4.global.api.iamport.v1.payment;

import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentRequestDto.CancelPaymentInfo;
import com.NBE3_4_2_Team4.global.api.iamport.v1.payment.IamportPaymentResponseDto.GetPayment;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.NBE3_4_2_Team4.global.api.iamport.v1.constants.IamportConstants.IAMPORT_CANCEL_PAYMENT_URL;
import static com.NBE3_4_2_Team4.global.api.iamport.v1.constants.IamportConstants.IAMPORT_GET_PAYMENT_HISTORY_URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class IamportPaymentServiceImpl implements IamportPaymentService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<GetPayment> getPaymentHistory(String impAccessToken, String impUid) {

        try {
            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + impAccessToken);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    IAMPORT_GET_PAYMENT_HISTORY_URL,
                    HttpMethod.GET,
                    request,
                    Map.class,
                    impUid
            );

            return Optional.ofNullable(response.getBody())
                    .map(body ->
                            objectMapper.convertValue(
                                    body.get("response"),
                                    GetPayment.class
                            ))
                    .or(Optional::empty);

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
    public Optional<GetPayment> cancelPayment(String impAccessToken, CancelPaymentInfo cancelPaymentInfo) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            headers.set("Authorization", "Bearer " + impAccessToken);

            MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("imp_uid", cancelPaymentInfo.impUid());
            requestBody.add("merchant_uid", cancelPaymentInfo.merchantUid());
            requestBody.add("amount", cancelPaymentInfo.amount());
            requestBody.add("reason", cancelPaymentInfo.reason());

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    IAMPORT_CANCEL_PAYMENT_URL,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            return Optional.ofNullable(response.getBody())
                    .map(body ->
                            objectMapper.convertValue(
                                    body.get("response"),
                                    GetPayment.class
                            ))
                    .or(Optional::empty);

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