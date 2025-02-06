package com.NBE3_4_2_Team4.global.security.oauth2.disconect.service.impl;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.security.oauth2.disconect.service.OAuth2DisconnectService;
import com.NBE3_4_2_Team4.global.security.oauth2.token.service.impl.GoogleTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleDisconnectService implements OAuth2DisconnectService {
    private final GoogleTokenService googleTokenService;
    private final RestTemplate restTemplate;

    @Override
    public Member.OAuth2Provider getProvider() {
        return Member.OAuth2Provider.GOOGLE;
    }

    @Override
    public boolean disconnect(String refreshToken) {
        String googleDisconnectUrl = "https://oauth2.googleapis.com/revoke";

        String accessToken = googleTokenService.getFreshAccessToken(refreshToken);

        String url = UriComponentsBuilder.fromUriString(googleDisconnectUrl)
                .queryParam("token", accessToken)
                .toUriString();
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return true;
        }catch (HttpClientErrorException e){
            log.error("Failed to disconnect for Naver");
            log.error(e.getLocalizedMessage());
            return false;
        }
    }
}
