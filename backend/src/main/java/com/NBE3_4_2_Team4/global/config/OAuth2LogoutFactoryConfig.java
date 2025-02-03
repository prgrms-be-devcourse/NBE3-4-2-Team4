package com.NBE3_4_2_Team4.global.config;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.security.oauth2.logout.service.OAuth2LogoutService;
import com.NBE3_4_2_Team4.global.security.oauth2.logout.service.impl.DefaultLogoutService;
import com.NBE3_4_2_Team4.global.security.oauth2.logout.service.impl.KakaoLogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class OAuth2LogoutFactoryConfig {
    private final DefaultLogoutService defaultLogoutService;
    private final KakaoLogoutService kakaoLogoutService;
    public static final String LOGOUT_COMPLETE_URL = "/api/logout/complete";

    @Bean
    public Map<Member.OAuth2Provider, OAuth2LogoutService> oAuth2LogoutServiceFactory() {
        Map<Member.OAuth2Provider, OAuth2LogoutService> oAuth2LogoutServiceMap = new HashMap<>();
        oAuth2LogoutServiceMap.put(Member.OAuth2Provider.NONE, defaultLogoutService);
        oAuth2LogoutServiceMap.put(Member.OAuth2Provider.KAKAO, kakaoLogoutService);
        return oAuth2LogoutServiceMap;
    }
}
