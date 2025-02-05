package com.NBE3_4_2_Team4.global.security.oauth2.disconect.service.impl;

import com.NBE3_4_2_Team4.global.security.oauth2.disconect.service.OAuth2DisconnectService;
import com.NBE3_4_2_Team4.global.security.oauth2.token.service.impl.KakaoTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KaKaoDisconnectService implements OAuth2DisconnectService {
    private final KakaoTokenService kakaoTokenService;
    private final RestTemplate restTemplate;
    private static final String KAKAO_UNLINK_URL = "https://kapi.kakao.com/v1/user/unlink";

    public boolean disconnect(String refreshToken){
        String accessToken = kakaoTokenService.getFreshAccessToken(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", String.format("Bearer %s", accessToken));

        HttpEntity<String> requestEntity = new HttpEntity<>("{}",headers);

        try {
            restTemplate.postForEntity(KAKAO_UNLINK_URL, requestEntity, String.class);
            return true;
        }catch (HttpClientErrorException e){
            log.error("Failed to disconnect from Kakao");
            log.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }
}
