//package com.NBE3_4_2_Team4.global.security.oauth2.userInfo.service.impl;
//
//import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
//import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfo;
//import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.service.OAuth2UserInfoService;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//import java.util.Map;
//
//@Service
//public class GoogleUserInfoService implements OAuth2UserInfoService {
//
//    @Override
//    public Member.OAuth2Provider getOAuth2Provider() {
//        return Member.OAuth2Provider.GOOGLE;
//    }
//
//    @Override
//    public OAuth2UserInfo getOAuth2UserInfo(OAuth2User oAuth2User) {
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//        String oAuth2Id = attributes.get("sub").toString();
//        String realName = attributes.get("family_name").toString() + attributes.get("given_name").toString();
//        return new OAuth2UserInfo(oAuth2Id, realName);
//    }
//}
