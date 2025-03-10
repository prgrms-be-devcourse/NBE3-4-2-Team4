package com.NBE3_4_2_Team4.global.security.oauth2.userInfo.service.impl

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfo
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.service.OAuth2UserInfoService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class KakaoUserInfoService: OAuth2UserInfoService {
    override fun getOAuth2Provider(): Member.OAuth2Provider {
        return Member.OAuth2Provider.KAKAO
    }

    override fun getOAuth2UserInfo(oAuth2User: OAuth2User): OAuth2UserInfo {
        val oAuth2Id = oAuth2User.name
        val attributes = oAuth2User.attributes
        val attributeProperties = attributes["properties"] as Map<*, *>
        val realName = attributeProperties["nickname"]
        return OAuth2UserInfo(oAuth2Id, realName!!.toString())
    }
}