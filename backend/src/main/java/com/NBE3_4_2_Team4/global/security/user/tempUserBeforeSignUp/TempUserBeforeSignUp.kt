package com.NBE3_4_2_Team4.global.security.user.tempUserBeforeSignUp

import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfo
import com.NBE3_4_2_Team4.standard.constants.AuthConstants
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.oauth2.core.user.OAuth2User

class TempUserBeforeSignUp @JsonCreator constructor(
    @JsonProperty("username") username: String,
    @JsonProperty("attributes") private val attributes: MutableMap<String, Any> = mutableMapOf()
) : User(username, "", emptyList<GrantedAuthority>()), OAuth2User {

    constructor(oAuth2UserInfo: OAuth2UserInfo, providerTypeCode: String, refreshToken: String) : this(
        username = "${providerTypeCode}_${oAuth2UserInfo.oAuth2Id}"
    ) {
        attributes["providerTypeCode"] = providerTypeCode
        attributes["refreshToken"] = refreshToken
        attributes["realName"] = oAuth2UserInfo.realName
        attributes[AuthConstants.OAUTH2_ID] = oAuth2UserInfo.oAuth2Id
    }

    override fun getAttributes(): Map<String, Any> = attributes
    override fun getName(): String = username

    fun getOAuth2Id(): String? = attributes[AuthConstants.OAUTH2_ID] as? String
}