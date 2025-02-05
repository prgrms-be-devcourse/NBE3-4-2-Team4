package com.NBE3_4_2_Team4.global.security.oauth2.disconect.service.impl;

import com.NBE3_4_2_Team4.global.security.oauth2.disconect.service.OAuth2DisconnectService;
import com.NBE3_4_2_Team4.global.security.oauth2.token.service.impl.NaverTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverDisconnectService implements OAuth2DisconnectService {
    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String naverTokenUrl;

    private final NaverTokenService naverTokenService;
    private final RestTemplate restTemplate;

    @Override
    public boolean disconnect(String refreshToken) {
        String accessToken = naverTokenService.getFreshAccessToken(refreshToken);
        log.info("Access token for naver: {}", accessToken);

        String url = UriComponentsBuilder.fromUriString(naverTokenUrl)
                .queryParam("grant_type", "delete")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("access_token", accessToken)
                .toUriString();

        log.info("Trying Disconnect naver: {}", url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String tokenValidationUrl= "https://openapi.naver.com/v1/nid/me";
        try {
            //토근 유효성 검사 API
            ResponseEntity<String> response = restTemplate.exchange(tokenValidationUrl, HttpMethod.GET, entity, String.class);
            log.info("test response : {}", response.getBody());
        }catch (HttpClientErrorException e) {
            log.warn("test response fail : {}", e.getResponseBodyAsString());
        }
        try {
            HttpEntity<Void> entity2 = new HttpEntity<>(headers);
            //네이버 연결 끊기 API
            restTemplate.exchange(url, HttpMethod.GET, entity2, String.class);
            return true;
        }catch (HttpClientErrorException e){
            log.error("Failed to disconnect from Naver");
            log.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }
}
