package com.NBE3_4_2_Team4.global.security.oauth2.logoutService

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import org.springframework.beans.factory.annotation.Value

abstract class OAuth2LogoutService {
    @Value("\${custom.domain.backend}")
    lateinit var backendDomain: String

    companion object {
        const val LOGOUT_COMPLETE_URL: String = "/api/logout/complete"
    }

    abstract fun getOAuth2Provider(): Member.OAuth2Provider

    fun getLogoutRedirectUrl(): String {
        return backendDomain + LOGOUT_COMPLETE_URL
    }

    open fun getLogoutUrl(): String {
        return getLogoutRedirectUrl()
    };
}