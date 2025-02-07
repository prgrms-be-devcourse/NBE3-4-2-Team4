package com.NBE3_4_2_Team4.global.security.oauth2.userInfo;


import com.NBE3_4_2_Team4.domain.member.member.entity.Member;

public interface OAuth2UserInfo {
    Member.OAuth2Provider getOAuth2Provider();
    String getOAuth2Id();
    String getNickname();
}
