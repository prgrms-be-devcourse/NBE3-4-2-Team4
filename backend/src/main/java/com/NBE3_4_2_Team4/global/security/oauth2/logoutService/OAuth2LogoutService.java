//package com.NBE3_4_2_Team4.global.security.oauth2.logoutService;
//
//
//import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
//import org.springframework.beans.factory.annotation.Value;
//
//public abstract class OAuth2LogoutService {
//    @Value("${custom.domain.backend}")
//    String backendDomain;
//
//    public static final String LOGOUT_COMPLETE_URL = "/api/logout/complete";
//
//    public abstract Member.OAuth2Provider getOAuth2Provider();
//
//    public String getLogoutRedirectUrl() {
//        return backendDomain + LOGOUT_COMPLETE_URL;
//    }
//
//    public String getLogoutUrl(){
//        return getLogoutRedirectUrl();
//    };
//}
