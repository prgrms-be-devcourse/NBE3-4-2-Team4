package com.NBE3_4_2_Team4.global.security.oauth2.disconect.service;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;

public interface OAuth2DisconnectService {
    Member.OAuth2Provider getProvider();
    boolean disconnect(String refreshToken);
}
