package com.NBE3_4_2_Team4.global.security.oauth2.disconect.service.impl;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.security.oauth2.disconect.service.OAuth2DisconnectService;
import com.NBE3_4_2_Team4.global.security.oauth2.token.service.impl.GoogleTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleDisconnectService implements OAuth2DisconnectService {
    private final GoogleTokenService googleTokenService;

    @Override
    public Member.OAuth2Provider getProvider() {
        return Member.OAuth2Provider.GOOGLE;
    }

    @Override
    public boolean disconnect(String refreshToken) {
        String accessToken = googleTokenService.getFreshAccessToken(refreshToken);
        return true;
    }
}
