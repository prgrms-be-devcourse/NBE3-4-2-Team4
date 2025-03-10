package com.NBE3_4_2_Team4.global.security.jwt

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.standard.constants.AuthConstants
import org.springframework.stereotype.Component

@Component
class JwtObjectMapper {
    fun getMemberByJwtClaims(claims: Map<String?, Any?>): Member {
        val id = claims[AuthConstants.ID] as Int?
        val username = claims[AuthConstants.USERNAME] as String?
        val nickname = claims[AuthConstants.NICKNAME] as String?
        val roleName = claims[AuthConstants.ROLE] as String?
        val oAuth2ProviderName = claims[AuthConstants.OAUTH2_PROVIDER] as String?
        val emailAddress = claims[AuthConstants.EMAIL_ADDRESS] as String?
        val emailVerified = claims[AuthConstants.EMAIL_VERIFIED] as Boolean?

        if ((id == null || username.isNullOrBlank()
                    || nickname.isNullOrBlank()
                    || roleName.isNullOrBlank()
                    || oAuth2ProviderName.isNullOrBlank()
                    || emailAddress.isNullOrBlank()
                    || emailVerified == null)) {
            throw RuntimeException("Invalid claims")
        }

        return Member.from(
            id = id.toLong(),
            username = username,
            nickname = nickname,
            roleName = roleName,
            oAuth2ProviderName = oAuth2ProviderName,
            emailAddress = emailAddress,
            emailVerified = emailVerified
        )
    }
}