package com.NBE3_4_2_Team4.global.security.oauth2.logout.service;


import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.config.OAuth2LogoutFactoryConfig;
import org.springframework.beans.factory.annotation.Value;

public abstract class OAuth2LogoutService {
    @Value("${custom.domain.backend}")
    String backendDomain;

    public abstract Member.OAuth2Provider getOAuth2Provider();

    public String getLogoutRedirectUrl() {
        return backendDomain + OAuth2LogoutFactoryConfig.LOGOUT_COMPLETE_URL;
    }

    public String getLogoutUrl(){
        return getLogoutRedirectUrl();
    };
}
