package com.NBE3_4_2_Team4.global.security.oauth2.userInfo;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.impl.KakaoUserInfo;
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.impl.NaverUserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;


@Component
public class OAuth2UserInfoFactory {
    public OAuth2UserInfo getOAuth2UserInfo(Member.OAuth2Provider oAuth2Provider, OAuth2User oAuth2User) {
        return switch (oAuth2Provider) {
            case KAKAO -> new KakaoUserInfo(oAuth2User);
            case NAVER -> new NaverUserInfo(oAuth2User);
            default -> throw new IllegalArgumentException("Unsupported providerType");
        };
    }
}