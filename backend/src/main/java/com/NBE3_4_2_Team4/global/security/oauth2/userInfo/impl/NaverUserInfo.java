package com.NBE3_4_2_Team4.global.security.oauth2.userInfo.impl;

import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;
    private final Map<String, String> attributeProperties;

    public NaverUserInfo(OAuth2User oAuth2User) {
        attributes =  oAuth2User.getAttributes();
        attributeProperties = (Map<String, String>) attributes.get("response");
    }

    @Override
    public String getOAuth2Id() {
        return attributeProperties.get("id");
    }

    @Override
    public String getNickname() {
        return attributeProperties.get("nickname");
    }
}
