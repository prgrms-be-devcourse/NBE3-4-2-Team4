package com.NBE3_4_2_Team4.global.security.oauth2.userInfo.service.impl

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfo
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.service.OAuth2UserInfoService
import org.springframework.security.oauth2.core.user.OAuth2User

class GoogleUserInfoService(): OAuth2UserInfoService {
    override fun getOAuth2Provider(): Member.OAuth2Provider {
        return Member.OAuth2Provider.GOOGLE
    }

    override fun getOAuth2UserInfo(oAuth2User: OAuth2User): OAuth2UserInfo {
        val attributes = oAuth2User.attributes
        val oAuth2Id = attributes["sub"].toString()
        val realName = attributes["family_name"].toString() + attributes["given_name"].toString()
        return OAuth2UserInfo(oAuth2Id, realName)
    }
}