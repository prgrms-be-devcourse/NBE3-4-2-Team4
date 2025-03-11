package com.NBE3_4_2_Team4.global.security.oauth2

import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
import org.springframework.stereotype.Component

@Component
class CustomOAuth2AccessTokenResponseClient(
): OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>{
    private val tokenResponseClient: RestClientAuthorizationCodeTokenResponseClient
    = RestClientAuthorizationCodeTokenResponseClient()

    private val log = LoggerFactory.getLogger(CustomOAuth2AccessTokenResponseClient::class.java)

    override fun getTokenResponse(authorizationGrantRequest: OAuth2AuthorizationCodeGrantRequest?): OAuth2AccessTokenResponse {
        val tokenResponse = tokenResponseClient.getTokenResponse(authorizationGrantRequest)

        val refreshToken = tokenResponse.refreshToken
        if (refreshToken == null) {
            log.info(
                "❌ 리프레시 토큰 값을 받지 못했습니다. 리프레시 토큰을 지급하지 않는 서비스일 수 있습니다. 현재 서비스 : {}",
                authorizationGrantRequest!!.clientRegistration.clientName
            )
        }

        val refreshTokenValue = refreshToken?.tokenValue
        val additionalParameters: MutableMap<String, Any> = HashMap(tokenResponse.additionalParameters)

        if (refreshTokenValue != null) {
            additionalParameters["refresh_token"] = refreshTokenValue
        }

        return OAuth2AccessTokenResponse.withToken(
            tokenResponse.accessToken.tokenValue
        )
            .tokenType(tokenResponse.accessToken.tokenType)
            .expiresIn(tokenResponse.accessToken.expiresAt!!.toEpochMilli())
            .scopes(tokenResponse.accessToken.scopes)
            .refreshToken(tokenResponse.refreshToken.toString()) // 원래 refresh_token 값 유지
            .additionalParameters(additionalParameters) // refresh_token 포함된 추가 파라미터
            .build()
    }
}