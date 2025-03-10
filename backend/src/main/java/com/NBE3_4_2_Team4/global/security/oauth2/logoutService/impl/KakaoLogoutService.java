//package com.NBE3_4_2_Team4.global.security.oauth2.logoutService.impl;
//
//import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
//import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.OAuth2LogoutService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.util.UriComponentsBuilder;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class KakaoLogoutService extends OAuth2LogoutService {
//    @Value("${spring.security.oauth2.client.registration.kakao.clientId}")
//    private String clientId;
//
//    @Override
//    public Member.OAuth2Provider getOAuth2Provider(){
//        return Member.OAuth2Provider.KAKAO;
//    }
//
//    @Override
//    public String getLogoutUrl(){
//        String logoutUrl = "https://kauth.kakao.com/oauth/logout";
//        return UriComponentsBuilder.fromUriString(logoutUrl)
//                .queryParam("client_id", clientId)
//                .queryParam("logout_redirect_uri", getLogoutRedirectUrl())
//                .toUriString();
//    }
//}
