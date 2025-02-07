package com.NBE3_4_2_Team4.global.security.oauth2.userInfo.impl;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class GoogleUserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;

    public GoogleUserInfo(OAuth2User oAuth2User) {
        attributes =  oAuth2User.getAttributes();
    }

    @Override
    public Member.OAuth2Provider getOAuth2Provider() {
        return Member.OAuth2Provider.GOOGLE;
    }

    @Override
    public String getOAuth2Id() {
        return attributes.get("sub").toString();
    }

    @Override
    public String getNickname() {
        return attributes.get("family_name").toString() + attributes.get("given_name").toString();
    }
}
