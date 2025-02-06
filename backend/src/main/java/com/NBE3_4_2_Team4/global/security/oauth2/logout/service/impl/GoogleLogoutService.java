package com.NBE3_4_2_Team4.global.security.oauth2.logout.service.impl;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.security.oauth2.logout.service.OAuth2LogoutService;
import org.springframework.stereotype.Component;

@Component
public class GoogleLogoutService extends OAuth2LogoutService {
    @Override
    public Member.OAuth2Provider getOAuth2Provider() {
        return Member.OAuth2Provider.GOOGLE;
    }
}
