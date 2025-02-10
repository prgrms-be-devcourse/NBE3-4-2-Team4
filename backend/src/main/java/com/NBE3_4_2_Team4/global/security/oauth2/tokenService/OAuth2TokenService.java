package com.NBE3_4_2_Team4.global.security.oauth2.token.service;

public interface OAuth2TokenService {
    String getFreshAccessToken(String refreshToken);
}
