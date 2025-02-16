package com.NBE3_4_2_Team4.global.security.user;

import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TempUser extends User implements OAuth2User {
    private final Map<String, Object> attributes = new HashMap<>();

    public TempUser(OAuth2UserInfo oAuth2UserInfo, String providerTypeCode, String refreshToken) {
        super(String.format("%s_%s", providerTypeCode, oAuth2UserInfo.getOAuth2Id()), "", new ArrayList<GrantedAuthority>());
        attributes.put("refreshToken", refreshToken);
        attributes.put("name", oAuth2UserInfo.getNickname());
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return getUsername();
    }

    public String getRefreshToken() {
        return attributes.get("refreshToken").toString();
    }

    public String getUsername() {
        return (String) attributes.get("name");
    }
}
