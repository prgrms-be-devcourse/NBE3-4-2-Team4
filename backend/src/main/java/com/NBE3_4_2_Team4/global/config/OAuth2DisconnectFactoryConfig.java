package com.NBE3_4_2_Team4.global.config;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.security.oauth2.disconect.service.OAuth2DisconnectService;
import com.NBE3_4_2_Team4.global.security.oauth2.disconect.service.impl.KaKaoDisconnectService;
import com.NBE3_4_2_Team4.global.security.oauth2.disconect.service.impl.NaverDisconnectService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class OAuth2DisconnectFactoryConfig {
    private final KaKaoDisconnectService kaKaoDisconnectService;
    private final NaverDisconnectService naverDisconnectService;

    @Bean
    public Map<Member.OAuth2Provider, OAuth2DisconnectService> oAuth2DisconnectServiceMap() {
        Map<Member.OAuth2Provider, OAuth2DisconnectService> oAuth2DisconnectServiceMap = new HashMap<>();
        oAuth2DisconnectServiceMap.put(Member.OAuth2Provider.NONE, null);
        oAuth2DisconnectServiceMap.put(Member.OAuth2Provider.KAKAO, kaKaoDisconnectService);
        oAuth2DisconnectServiceMap.put(Member.OAuth2Provider.NAVER, naverDisconnectService);
        oAuth2DisconnectServiceMap.put(Member.OAuth2Provider.GOOGLE, null);
        return oAuth2DisconnectServiceMap;
    }
}
