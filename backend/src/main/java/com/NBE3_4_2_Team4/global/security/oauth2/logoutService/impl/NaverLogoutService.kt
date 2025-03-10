package com.NBE3_4_2_Team4.global.security.oauth2.logoutService.impl

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.OAuth2LogoutService
import org.springframework.stereotype.Service

@Service
class NaverLogoutService: OAuth2LogoutService() {
    override fun getOAuth2Provider(): Member.OAuth2Provider {
        return Member.OAuth2Provider.NAVER
    }
}