package com.NBE3_4_2_Team4.global.security.user;

import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfo;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TempUserBeforeSignUp extends User implements OAuth2User {
    private Map<String, Object> attributes = new HashMap<>();

    @JsonCreator // Jackson이 역직렬화할 때 사용할 생성자 지정
    public TempUserBeforeSignUp(
            @JsonProperty("username") String username, // 부모 클래스(User)의 필드
            @JsonProperty("attributes") Map<String, Object> attributes
    ) {
        super(username, "", new ArrayList<GrantedAuthority>());
        this.attributes = attributes != null ? attributes : new HashMap<>();
    }

    public TempUserBeforeSignUp(OAuth2UserInfo oAuth2UserInfo, String providerTypeCode, String refreshToken) {
        super(String.format("%s_%s", providerTypeCode, oAuth2UserInfo.getOAuth2Id()), "", new ArrayList<GrantedAuthority>());
        attributes.put("providerTypeCode", providerTypeCode);
        attributes.put("refreshToken", refreshToken);
        attributes.put("realName", oAuth2UserInfo.getRealName());
        attributes.put("oAuth2Id", oAuth2UserInfo.getOAuth2Id());
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

    public String getRealName() {
        return (String) attributes.get("realName");
    }

    public String getProviderTypeCode() {
        return (String) attributes.get("providerTypeCode");
    }

    public String getOAuth2Id(){
        return (String) attributes.get("oAuth2Id");
    }
}
