package com.NBE3_4_2_Team4.global.security.user.tempUserBeforeSignUp

import com.NBE3_4_2_Team4.global.security.jwt.JwtManager
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfo
import com.NBE3_4_2_Team4.standard.constants.AuthConstants
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration

class TempUserBeforeSignUpServiceTest {
    private val jwtManager: JwtManager = mock(JwtManager::class.java)

    private val redisTemplate: RedisTemplate<String, Any> = mock<RedisTemplate<String, Any>>()

    private val objectMapper: ObjectMapper = mock(ObjectMapper::class.java)

    private val valueOperations: ValueOperations<String, Any?> = mock<ValueOperations<String, Any?>>()

    private val tempUserBeforeSignUpService: TempUserBeforeSignUpService =
        TempUserBeforeSignUpService(
            jwtManager,
            redisTemplate,
            objectMapper
        )


    private val oAuth2Id = "testOAuth2Id"
    private val tempToken = "testTempToken"
    private val providerTypeCode = "google"
    private val refreshToken = "testRefreshToken"

    private var oAuth2UserInfo: OAuth2UserInfo? = null
    private var tempUserBeforeSignUp: TempUserBeforeSignUp? = null

    @BeforeEach
    fun setUp() {
        oAuth2UserInfo = Mockito.mock(OAuth2UserInfo::class.java)
        Mockito.`when`(oAuth2UserInfo!!.oAuth2Id)
            .thenReturn(oAuth2Id)

        tempUserBeforeSignUp = TempUserBeforeSignUp(oAuth2UserInfo!!, providerTypeCode, refreshToken)
    }

    @Test
    @DisplayName("getOrCreateTempUser - Redis에 사용자 정보가 존재하는 경우 기존 정보를 반환")
    fun testGetOrCreateTempUser_WhenUserExistsInRedis() {
        Mockito.`when`(redisTemplate.hasKey(oAuth2Id))
            .thenReturn(true)
        Mockito.`when`(redisTemplate.opsForValue())
            .thenReturn(valueOperations)
        Mockito.`when`(valueOperations[oAuth2Id])
            .thenReturn(tempUserBeforeSignUp)
        Mockito.`when`(
            objectMapper.convertValue(
                tempUserBeforeSignUp,
                TempUserBeforeSignUp::class.java
            )
        )
            .thenReturn(tempUserBeforeSignUp)


        val result = tempUserBeforeSignUpService.getOrCreateTempUser(oAuth2UserInfo!!, providerTypeCode, refreshToken)


        Assertions.assertThat(result).isEqualTo(tempUserBeforeSignUp)
        Mockito.verify(objectMapper, Mockito.times(1)).convertValue(
            tempUserBeforeSignUp,
            TempUserBeforeSignUp::class.java
        )
        Mockito.verify(valueOperations, Mockito.times(1))[ArgumentMatchers.eq(oAuth2Id), ArgumentMatchers.eq(
            tempUserBeforeSignUp
        )!!] =
            ArgumentMatchers.any<Duration>()
    }

    @Test
    @DisplayName("getOrCreateTempUser - Redis에 사용자 정보가 없을 경우 새로 생성하여 저장")
    fun testGetOrCreateTempUser_WhenUserNotInRedis() {
        Mockito.`when`(redisTemplate.hasKey(oAuth2Id))
            .thenReturn(false)
        Mockito.`when`(redisTemplate.opsForValue())
            .thenReturn(valueOperations)

        val result = tempUserBeforeSignUpService.getOrCreateTempUser(oAuth2UserInfo!!, providerTypeCode, refreshToken)

        Assertions.assertThat(result).isNotNull()
        Assertions.assertThat(result.getOAuth2Id()).isEqualTo(oAuth2Id)
        Mockito.verify(objectMapper, Mockito.never()).convertValue(
            tempUserBeforeSignUp,
            TempUserBeforeSignUp::class.java
        )
        Mockito.verify(valueOperations, Mockito.times(1))[ArgumentMatchers.eq(oAuth2Id), ArgumentMatchers.eq(
            tempUserBeforeSignUp
        )!!] =
            ArgumentMatchers.any<Duration>()
    }

    @Test
    @DisplayName("getTempUserFromRedisWithJwt - JWT에서 oAuth2Id를 추출하여 Redis에서 사용자 정보 조회")
    fun testGetTempUserFromRedisWithJwt() {
        Mockito.`when`(redisTemplate.opsForValue())
            .thenReturn(valueOperations)
        Mockito.`when`(jwtManager.getClaims(tempToken))
            .thenReturn(mapOf<String, Any>(AuthConstants.OAUTH2_ID to oAuth2Id))
        Mockito.`when`(valueOperations[oAuth2Id])
            .thenReturn(tempUserBeforeSignUp)
        Mockito.`when`(
            objectMapper.convertValue(
                tempUserBeforeSignUp,
                TempUserBeforeSignUp::class.java
            )
        )
            .thenReturn(tempUserBeforeSignUp)

        val result = tempUserBeforeSignUpService.getTempUserFromRedisWithJwt(tempToken)


        Assertions.assertThat(result).isEqualTo(tempUserBeforeSignUp)
        Mockito.verify(jwtManager, Mockito.times(1)).getClaims(tempToken)
        Mockito.verify(objectMapper, Mockito.times(1)).convertValue(
            tempUserBeforeSignUp,
            TempUserBeforeSignUp::class.java
        )
    }

    @Test
    @DisplayName("deleteTempUserFromRedis - JWT에서 oAuth2Id를 추출하여 Redis에서 사용자 정보 삭제")
    fun testDeleteTempUserFromRedis() {
        Mockito.`when`(jwtManager.getClaims(tempToken))
            .thenReturn(mapOf<String, Any>(AuthConstants.OAUTH2_ID to oAuth2Id))

        tempUserBeforeSignUpService.deleteTempUserFromRedis(tempToken)

        Mockito.verify(redisTemplate, Mockito.times(1)).delete(oAuth2Id)
    }
}