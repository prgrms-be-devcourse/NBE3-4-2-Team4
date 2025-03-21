package com.NBE3_4_2_Team4.global.security.oauth2.userInfo.service;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2UserInfoService {
    Member.OAuth2Provider getOAuth2Provider();
    OAuth2UserInfo getOAuth2UserInfo(OAuth2User oAuth2User);
}
