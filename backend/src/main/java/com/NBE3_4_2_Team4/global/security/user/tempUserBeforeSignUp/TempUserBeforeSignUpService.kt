package com.NBE3_4_2_Team4.global.security.user.tempUserBeforeSignUp

import com.NBE3_4_2_Team4.global.security.jwt.JwtManager
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfo
import com.NBE3_4_2_Team4.standard.constants.AuthConstants
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class TempUserBeforeSignUpService(
    val jwtManager: JwtManager,
    val redisTemplate: RedisTemplate<String, Any>,
    val objectMapper: ObjectMapper
) {
    private fun getTempUserFromRedis(key: String?): TempUserBeforeSignUp {
        return objectMapper.convertValue(redisTemplate.opsForValue()[key!!], TempUserBeforeSignUp::class.java)
    }

    fun getOrCreateTempUser(
        oAuth2UserInfo: OAuth2UserInfo,
        providerTypeCode: String?,
        refreshToken: String?
    ): TempUserBeforeSignUp {
        val oAuth2Id = oAuth2UserInfo.oAuth2Id

        val tempUserBeforeSignUp =
            if (redisTemplate.hasKey(oAuth2Id)) getTempUserFromRedis(oAuth2Id) else TempUserBeforeSignUp(
                oAuth2UserInfo,
                providerTypeCode!!, refreshToken!!
            )

        redisTemplate.opsForValue()[oAuth2Id, tempUserBeforeSignUp] = Duration.ofHours(2)

        return tempUserBeforeSignUp
    }

    fun getTempUserFromRedisWithJwt(tempToken: String?): TempUserBeforeSignUp {
        val claims = jwtManager.getClaims(tempToken)
        val oAuth2Id = claims!![AuthConstants.OAUTH2_ID] as String?
        return getTempUserFromRedis(oAuth2Id)
    }

    fun deleteTempUserFromRedis(tempToken: String?) {
        val claims = jwtManager.getClaims(tempToken)
        val oAuth2Id = claims!![AuthConstants.OAUTH2_ID] as String?
        redisTemplate.delete(oAuth2Id!!)
    }

    fun saveAuthCodeForMember(memberId: Long, authCode: String?) {
        val memberIdString = memberId.toString()
        redisTemplate.opsForValue()[memberIdString, authCode!!] = Duration.ofMinutes(10) // 10분 후 만료
    }

    fun isEmailVerified(memberId: Long, authCode: String): Boolean {
        val memberIdString = memberId.toString()
        val authCodeString = redisTemplate.opsForValue()[memberIdString] as String?
        return authCodeString.equals(authCode)
    }
}