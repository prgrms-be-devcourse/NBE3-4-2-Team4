package com.NBE3_4_2_Team4.global.security.oauth2.disconect.service.impl;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.security.oauth2.disconect.service.OAuth2DisconnectService;
import org.springframework.stereotype.Service;

@Service
public class DefaultDisconnectService implements OAuth2DisconnectService {
    @Override
    public Member.OAuth2Provider getProvider() {
        return Member.OAuth2Provider.NONE;
    }

    @Override
    public boolean disconnect(String refreshToken) {
        return true;
    }
}
