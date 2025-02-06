package com.NBE3_4_2_Team4.global.security.oauth2.token.service.impl;

import com.NBE3_4_2_Team4.global.security.oauth2.token.service.OAuth2TokenService;
import org.springframework.stereotype.Service;

@Service
public class GoogleTokenService implements OAuth2TokenService {
    @Override
    public String getFreshAccessToken(String refreshToken) {
        return "";
    }
}
