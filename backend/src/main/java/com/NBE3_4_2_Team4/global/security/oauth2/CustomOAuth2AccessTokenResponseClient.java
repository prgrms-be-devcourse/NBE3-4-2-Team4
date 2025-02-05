package com.NBE3_4_2_Team4.global.security.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.endpoint.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;


@Slf4j
@Component
public class CustomOAuth2AccessTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {
    private final RestClientAuthorizationCodeTokenResponseClient tokenResponseClient;

    public CustomOAuth2AccessTokenResponseClient() {
        this.tokenResponseClient = new RestClientAuthorizationCodeTokenResponseClient();
    }


    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        OAuth2AccessTokenResponse tokenResponse =  tokenResponseClient.getTokenResponse(authorizationGrantRequest);

        if (tokenResponse.getRefreshToken() != null) {
            log.error("🔥 Received Refresh Token: {}", tokenResponse.getRefreshToken().getTokenValue());
        } else {
            log.error("❌ No Refresh Token received!");
        }
        String refreshTokenValue = tokenResponse.getRefreshToken() != null ? tokenResponse.getRefreshToken().getTokenValue() : null;
        Map<String, Object> additionalParameters = new HashMap<>(tokenResponse.getAdditionalParameters());

        if (refreshTokenValue != null) {
            additionalParameters.put("refresh_token", refreshTokenValue);
        }

        return OAuth2AccessTokenResponse.withToken(
                        tokenResponse.getAccessToken().getTokenValue())
                .tokenType(tokenResponse.getAccessToken().getTokenType())
                .expiresIn(tokenResponse.getAccessToken().getExpiresAt().toEpochMilli())
                .scopes(tokenResponse.getAccessToken().getScopes())
                .refreshToken(String.valueOf(tokenResponse.getRefreshToken())) // 원래 refresh_token 값 유지
                .additionalParameters(additionalParameters) // refresh_token 포함된 추가 파라미터
                .build();
    }
}