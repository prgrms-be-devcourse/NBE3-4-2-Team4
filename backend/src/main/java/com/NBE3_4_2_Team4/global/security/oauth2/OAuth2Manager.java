package com.NBE3_4_2_Team4.global.security.oauth2;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.security.oauth2.disconect.service.OAuth2DisconnectService;
import com.NBE3_4_2_Team4.global.security.oauth2.logout.service.OAuth2LogoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OAuth2Manager {
    private final Map<Member.OAuth2Provider, OAuth2LogoutService> oauth2LogoutServiceMap = new HashMap<>();
    private final Map<Member.OAuth2Provider, OAuth2DisconnectService> oauth2DisconnectServiceMap = new HashMap<>();

    @Autowired
    public OAuth2Manager(List<OAuth2LogoutService> oAuth2LogoutServiceList){
        for (OAuth2LogoutService oAuth2LogoutService : oAuth2LogoutServiceList) {
            oauth2LogoutServiceMap.put(oAuth2LogoutService.getOAuth2Provider(), oAuth2LogoutService);
        }
    }

    public OAuth2LogoutService getOAuth2LogoutService(Member.OAuth2Provider oAuth2Provider){
        return oauth2LogoutServiceMap.get(oAuth2Provider);
    }
}
