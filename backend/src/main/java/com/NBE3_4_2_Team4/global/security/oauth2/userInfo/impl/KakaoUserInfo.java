package com.NBE3_4_2_Team4.global.security.oauth2.userInfo.impl;

import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {
    private final OAuth2User oAuth2User;
    private final Map<String, Object> attributes;
    private final Map<String, String> attributeProperties;

    public KakaoUserInfo(OAuth2User oAuth2User) {
        this.oAuth2User = oAuth2User;
        attributes =  oAuth2User.getAttributes();
        attributeProperties = (Map<String, String>) attributes.get("properties");
    }
    @Override
    public String getOAuth2Id() {
        return oAuth2User.getName();
    }

    @Override
    public String getNickname() {
        return attributeProperties.get("nickname");
    }
}
