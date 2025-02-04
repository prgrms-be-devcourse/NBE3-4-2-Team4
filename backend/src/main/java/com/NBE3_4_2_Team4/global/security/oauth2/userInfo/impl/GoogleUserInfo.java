package com.NBE3_4_2_Team4.global.security.oauth2.userInfo.impl;

import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class GoogleUserInfo implements OAuth2UserInfo {
    private final OAuth2User oAuth2User;

    public GoogleUserInfo(OAuth2User oAuth2User) {
        this.oAuth2User = oAuth2User;
    }

    @Override
    public String getOAuth2Id() {
        return "";
    }

    @Override
    public String getNickname() {
        return "";
    }
}
