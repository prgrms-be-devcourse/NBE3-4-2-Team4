package com.NBE3_4_2_Team4.global.security.oauth2.disconnectService.impl

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.security.oauth2.disconnectService.OAuth2DisconnectService
import com.NBE3_4_2_Team4.global.security.oauth2.tokenService.impl.GoogleTokenService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class GoogleDisconnectService(
    private val googleTokenService: GoogleTokenService,
    private val restTemplate: RestTemplate
) : OAuth2DisconnectService {

    private val log = LoggerFactory.getLogger(GoogleDisconnectService::class.java)

    override fun getProvider(): Member.OAuth2Provider = Member.OAuth2Provider.GOOGLE

    override fun disconnectSuccess(refreshToken: String?): Boolean {
        val googleDisconnectUrl = "https://oauth2.googleapis.com/revoke"

        val accessToken = googleTokenService.getFreshAccessToken(refreshToken) ?: return false

        val url = UriComponentsBuilder.fromUriString(googleDisconnectUrl)
            .queryParam("token", accessToken)
            .toUriString()

        val headers = HttpHeaders()
        val entity = HttpEntity<Void>(headers)

        return try {
            restTemplate.postForEntity(url, entity, String::class.java)
            true
        } catch (e: HttpClientErrorException) {
            log.error("Failed to disconnect for Google", e)
            false
        }
    }
}
