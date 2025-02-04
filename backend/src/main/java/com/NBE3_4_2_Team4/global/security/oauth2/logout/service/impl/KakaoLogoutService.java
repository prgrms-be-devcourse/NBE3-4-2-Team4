package com.NBE3_4_2_Team4.global.security.oauth2.logout.service.impl;

import com.NBE3_4_2_Team4.global.config.OAuth2LogoutFactoryConfig;
import com.NBE3_4_2_Team4.global.security.oauth2.logout.service.OAuth2LogoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoLogoutService implements OAuth2LogoutService {
    @Value("${spring.security.oauth2.client.registration.kakao.clientId}")
    private String clientId;

    @Value("${custom.domain.backend}")
    private String backendDomain;

    @Override
    public String getLogoutUrl(){
        String logoutUrl = "https://kauth.kakao.com/oauth/logout";
        return UriComponentsBuilder.fromUriString(logoutUrl)
                .queryParam("client_id", clientId)
                .queryParam("logout_redirect_uri", backendDomain + OAuth2LogoutFactoryConfig.LOGOUT_COMPLETE_URL)
                .toUriString();
    }
}
