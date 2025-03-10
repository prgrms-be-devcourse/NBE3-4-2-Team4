//package com.NBE3_4_2_Team4.global.security.oauth2.tokenService.impl;
//
//import com.NBE3_4_2_Team4.global.security.oauth2.tokenService.OAuth2TokenService;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class NaverTokenService implements OAuth2TokenService {
//    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
//    private String clientId;
//
//    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
//    private String clientSecret;
//
//    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
//    private String naverTokenUrl;
//
//    private final RestTemplate restTemplate;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Override
//    public String getFreshAccessToken(String refreshToken) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        String url = UriComponentsBuilder.fromUriString(naverTokenUrl)
//                .queryParam("grant_type", "refresh_token")
//                .queryParam("client_id", clientId)
//                .queryParam("client_secret", clientSecret)
//                .queryParam("refresh_token", refreshToken)
//                .toUriString();
//
//        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
//        try {
//            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
//            return objectMapper.readTree(response.getBody())
//                    .get("access_token").asText();
//        }catch (HttpClientErrorException | JsonProcessingException e) {
//            log.error("error occurred while get accessToken for naver. msg : {}",e.getMessage());
//            return null;
//        }
//    }
//}
