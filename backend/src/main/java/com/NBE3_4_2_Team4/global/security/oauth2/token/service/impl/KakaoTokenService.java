package com.NBE3_4_2_Team4.global.security.oauth2.token.service.impl;

import com.NBE3_4_2_Team4.global.security.oauth2.token.service.OAuth2TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaoTokenService implements OAuth2TokenService {
    @Value("${spring.security.oauth2.client.registration.kakao.clientId}")
    private String clientId;
    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private final RestTemplate restTemplate;
    private ObjectMapper objectMapper = new ObjectMapper();

    public String getFreshAccessToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(KAKAO_TOKEN_URL, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                return objectMapper.readTree(response.getBody())
                        .get("access_token").asText();
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse access token", e);
            }
        }else {
            throw new RuntimeException("Failed to parse access token");
        }
    }
}
