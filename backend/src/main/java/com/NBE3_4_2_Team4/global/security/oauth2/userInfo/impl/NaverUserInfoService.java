package com.NBE3_4_2_Team4.global.security.oauth2.userInfo.impl;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfoService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NaverUserInfoService implements OAuth2UserInfoService {
    @Override
    public Member.OAuth2Provider getOAuth2Provider() {
        return Member.OAuth2Provider.NAVER;
    }

    @Override
    public OAuth2UserInfoClass getOAuth2UserInfo(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, String> attributeProperties = (Map<String, String>) attributes.get("response");
        String oAuth2Id = attributeProperties.get("id");
        String nickName = attributeProperties.get("nickname");
        return new OAuth2UserInfoClass(oAuth2Id, nickName);
    }
}
