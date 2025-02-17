package com.NBE3_4_2_Team4.global.security.oauth2.userInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class OAuth2UserInfo {
    private String oAuth2Id;
    private String nickname;
}
