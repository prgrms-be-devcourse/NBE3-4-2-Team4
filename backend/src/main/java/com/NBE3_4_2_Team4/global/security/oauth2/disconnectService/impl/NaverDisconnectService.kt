package com.NBE3_4_2_Team4.global.security.oauth2.disconnectService.impl

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.security.oauth2.disconnectService.OAuth2DisconnectService
import com.NBE3_4_2_Team4.global.security.oauth2.tokenService.impl.NaverTokenService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class NaverDisconnectService (
    private val naverTokenService: NaverTokenService,
    private val restTemplate: RestTemplate
) : OAuth2DisconnectService {
    @Value("\${spring.security.oauth2.client.registration.naver.client-id}")
    lateinit var clientId: String

    @Value("\${spring.security.oauth2.client.registration.naver.client-secret}")
    lateinit var clientSecret: String

    @Value("\${spring.security.oauth2.client.provider.naver.token-uri}")
    lateinit var naverTokenUri: String

    private val log = LoggerFactory.getLogger(NaverDisconnectService::class.java)

    override fun getProvider(): Member.OAuth2Provider {
        return Member.OAuth2Provider.NAVER
    }

    override fun disconnectSuccess(refreshToken: String?): Boolean {
        val accessToken = naverTokenService.getFreshAccessToken(refreshToken) ?: return false

        val url = UriComponentsBuilder.fromUriString(naverTokenUri)
            .queryParam("grant_type", "delete")
            .queryParam("client_id", clientId)
            .queryParam("client_secret", clientSecret)
            .queryParam("access_token", accessToken)
            .toUriString()
        try {
            restTemplate.getForEntity(url, String::class.java)
            return true
        } catch (e: HttpClientErrorException) {
            log.error("Failed to disconnect for Naver")
            log.error(e.localizedMessage)
            return false
        }
    }
}