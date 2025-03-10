package com.NBE3_4_2_Team4.global.security.oauth2.disconnectService

import com.NBE3_4_2_Team4.domain.member.member.entity.Member

interface OAuth2DisconnectService {
    fun getProvider(): Member.OAuth2Provider?
    fun disconnectSuccess(refreshToken: String?): Boolean
}