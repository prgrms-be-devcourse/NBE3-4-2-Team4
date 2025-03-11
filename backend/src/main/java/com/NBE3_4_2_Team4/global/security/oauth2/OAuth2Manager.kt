package com.NBE3_4_2_Team4.global.security.oauth2

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.security.oauth2.disconnectService.OAuth2DisconnectService
import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.OAuth2LogoutService
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.service.OAuth2UserInfoService
import org.springframework.stereotype.Component

@Component
class OAuth2Manager (
    oAuth2LogoutServiceList : List<OAuth2LogoutService>,
    oAuth2UserInfoServiceList: List<OAuth2UserInfoService>,
    oAuth2DisconnectServiceList : List<OAuth2DisconnectService>
){
    private val oauth2LogoutServiceMap: Map<Member.OAuth2Provider, OAuth2LogoutService> =
        oAuth2LogoutServiceList.associateBy { it.getOAuth2Provider() }

    private val oauth2DisconnectServiceMap: Map<Member.OAuth2Provider, OAuth2DisconnectService> =
        oAuth2DisconnectServiceList.associateBy { it.getProvider() }

    private val oauth2UserInfoServiceMap: Map<Member.OAuth2Provider, OAuth2UserInfoService> =
        oAuth2UserInfoServiceList.associateBy { it.getOAuth2Provider() }

    fun getOAuth2LogoutService(oAuth2Provider: Member.OAuth2Provider): OAuth2LogoutService? =
        oauth2LogoutServiceMap[oAuth2Provider]

    fun getOAuth2DisconnectService(oAuth2Provider: Member.OAuth2Provider): OAuth2DisconnectService? =
        oauth2DisconnectServiceMap[oAuth2Provider]

    fun getOAuth2UserInfoService(oAuth2Provider: Member.OAuth2Provider): OAuth2UserInfoService? =
        oauth2UserInfoServiceMap[oAuth2Provider]
}