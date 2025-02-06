package com.NBE3_4_2_Team4.global.security.oauth2.token.service.impl;

import com.NBE3_4_2_Team4.global.security.oauth2.token.service.OAuth2TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class GoogleTokenService implements OAuth2TokenService {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getFreshAccessToken(String refreshToken) {
        String googleTokenUrl = "https://oauth2.googleapis.com/token";

        String url = UriComponentsBuilder.fromUriString(googleTokenUrl)
                .queryParam("grant_type", "refresh_token")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("refresh_token", refreshToken)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
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
