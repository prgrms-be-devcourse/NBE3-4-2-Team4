package com.NBE3_4_2_Team4.global.security.oauth2.userInfo.service

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfo
import org.springframework.security.oauth2.core.user.OAuth2User

interface OAuth2UserInfoService {
    fun getOAuth2Provider(): Member.OAuth2Provider
    fun getOAuth2UserInfo(oAuth2User: OAuth2User?): OAuth2UserInfo
}