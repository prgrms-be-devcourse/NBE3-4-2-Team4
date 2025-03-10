package com.NBE3_4_2_Team4.global.security.oauth2.logoutService.impl

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.OAuth2LogoutService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder

@Service
class KakaoLogoutService : OAuth2LogoutService() {
    @Value("\${spring.security.oauth2.client.registration.kakao.clientId}")
    lateinit var clientId: String

    override fun getOAuth2Provider(): Member.OAuth2Provider {
        return Member.OAuth2Provider.KAKAO
    }

    override fun getLogoutUrl(): String {
        val logoutUrl = "https://kauth.kakao.com/oauth/logout"
        return UriComponentsBuilder.fromUriString(logoutUrl)
            .queryParam("client_id", clientId)
            .queryParam("logout_redirect_uri", getLogoutRedirectUrl())
            .toUriString()
    }
}