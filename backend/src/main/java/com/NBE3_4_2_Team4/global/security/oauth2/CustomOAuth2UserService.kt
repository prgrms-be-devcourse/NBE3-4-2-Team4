package com.NBE3_4_2_Team4.global.security.oauth2

import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.service.OAuth2RefreshTokenService
import com.NBE3_4_2_Team4.domain.member.member.entity.Member.OAuth2Provider.Companion.getOAuth2ProviderByName
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService
import com.NBE3_4_2_Team4.global.security.user.customUser.CustomUser
import com.NBE3_4_2_Team4.global.security.user.tempUserBeforeSignUp.TempUserBeforeSignUpService
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CustomOAuth2UserService(
    private val memberService: MemberService,
    private val oAuth2RefreshTokenService: OAuth2RefreshTokenService,
    private val tempUserBeforeSignUpService: TempUserBeforeSignUpService,
    private val oAuth2Manager: OAuth2Manager
) : DefaultOAuth2UserService() {

    @Transactional
    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val refreshToken = userRequest.additionalParameters.getOrDefault("refresh_token", null) as String

        val oAuth2User = super.loadUser(userRequest)

        val providerTypeCode = userRequest
            .clientRegistration
            .registrationId
            .uppercase(Locale.getDefault())

        val oAuth2Provider = getOAuth2ProviderByName(providerTypeCode)


        val oAuth2UserInfoService = oAuth2Manager.getOAuth2UserInfoService(oAuth2Provider)
        val oAuth2UserInfo = oAuth2UserInfoService!!.getOAuth2UserInfo(oAuth2User)


        val oAuth2Id = oAuth2UserInfo.oAuth2Id
        val username = String.format("%s_%s", providerTypeCode, oAuth2Id)

        val optionalMember = Optional.ofNullable(memberService.findByUsername(username))
        if (optionalMember.isPresent) {
            val member = optionalMember.get()
            oAuth2RefreshTokenService.saveOrUpdateOAuth2RefreshToken(member, refreshToken, oAuth2Id)
            return CustomUser(member)
        }

        return tempUserBeforeSignUpService.getOrCreateTempUser(oAuth2UserInfo, providerTypeCode, refreshToken)
    }
}