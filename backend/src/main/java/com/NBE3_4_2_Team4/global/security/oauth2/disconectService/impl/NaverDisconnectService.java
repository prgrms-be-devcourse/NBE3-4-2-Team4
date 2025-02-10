package com.NBE3_4_2_Team4.global.security.oauth2.disconect.service.impl;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
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
    public Member.OAuth2Provider getProvider(){
        return Member.OAuth2Provider.NAVER;
    }

    @Override
    public boolean disconnect(String refreshToken) {
        String accessToken = naverTokenService.getFreshAccessToken(refreshToken);

        String url = UriComponentsBuilder.fromUriString(naverTokenUrl)
                .queryParam("grant_type", "delete")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("access_token", accessToken)
                .toUriString();
        try {
            restTemplate.getForEntity( url, String.class);
            return true;
        }catch (HttpClientErrorException e){
            log.error("Failed to disconnect for Naver");
            log.error(e.getLocalizedMessage());
            return false;
        }
    }
}
