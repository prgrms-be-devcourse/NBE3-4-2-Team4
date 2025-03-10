package com.NBE3_4_2_Team4.global.security.oauth2.disconnectService.impl

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.security.oauth2.disconnectService.OAuth2DisconnectService
import com.NBE3_4_2_Team4.global.security.oauth2.tokenService.impl.KakaoTokenService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Service
class KaKaoDisconnectService (
    private val kakaoTokenService: KakaoTokenService,
    private val restTemplate: RestTemplate
) : OAuth2DisconnectService {
    private val log = LoggerFactory.getLogger(javaClass)
    private final val KAKAO_UNLINK_URL: String = "https://kapi.kakao.com/v1/user/unlink"

    override fun getProvider(): Member.OAuth2Provider {
        return Member.OAuth2Provider.KAKAO
    }

    override fun disconnectSuccess(refreshToken: String?): Boolean {
        val accessToken = kakaoTokenService.getFreshAccessToken(refreshToken) ?: return false

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.add("Authorization", String.format("Bearer %s", accessToken))

        val entity = HttpEntity("{}", headers)

        try {
            restTemplate.postForEntity(
                KAKAO_UNLINK_URL, entity,
                String::class.java
            )
            return true
        } catch (e: HttpClientErrorException) {
            log.error("Failed to disconnect from Kakao")
            log.error(e.localizedMessage)
            return false
        }
    }
}