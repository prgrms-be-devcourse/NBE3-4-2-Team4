package com.NBE3_4_2_Team4.global.security.oauth2.tokenService.impl;

import com.NBE3_4_2_Team4.global.security.oauth2.token.service.OAuth2TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoTokenService implements OAuth2TokenService {
    @Value("${spring.security.oauth2.client.registration.kakao.clientId}")
    private String clientId;
    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getFreshAccessToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(kakaoTokenUrl, requestEntity, String.class);
            return objectMapper.readTree(response.getBody())
                    .get("access_token").asText();
        }catch (RestClientException | JsonProcessingException e) {
            log.error("error occurred while get accessToken for kakao. msg : {}",e.getMessage());
            return null;
        }
    }
}
