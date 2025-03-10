package com.NBE3_4_2_Team4.global.scheduler

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.config.AppConfig.Companion.log
import com.NBE3_4_2_Team4.global.security.oauth2.OAuth2Manager
import com.NBE3_4_2_Team4.global.security.user.tempUserBeforeSignUp.TempUserBeforeSignUp
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class OAuth2AutoDisconnectScheduler(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val oAuth2Manager: OAuth2Manager,
    private val objectMapper: ObjectMapper
) {
    @Scheduled(cron = "0 0 * * * *")
    fun autoDisconnect() {
        val keySet = redisTemplate.keys("*")

        val keySetToDisconnect: MutableSet<String> = HashSet()

        for (key in keySet) {
            val ttl = redisTemplate.getExpire(key)
            if (ttl in 1 until 3600) {
                keySetToDisconnect.add(key)
            }
        }

        for (key in keySetToDisconnect) {
            val tempUserBeforeSignUp = objectMapper.convertValue(
                redisTemplate.opsForValue()[key],
                TempUserBeforeSignUp::class.java
            )
            val oAuth2Provider = Member.OAuth2Provider.getOAuth2ProviderByName(tempUserBeforeSignUp.providerTypeCode)

            val oAuth2DisconnectService = oAuth2Manager.getOAuth2DisconnectService(oAuth2Provider)
            val refreshToken = tempUserBeforeSignUp.refreshToken

            if (oAuth2DisconnectService.disconnectSuccess(refreshToken)) {
                redisTemplate.delete(key)
            } else {
                log.error(
                    "OAuth2 연동 해제 실패. (연동 해제 요청이 실패했습니다.) 해당 서비스에 직접 연결 해제를 시도하세요. OAuth2Provider : {}, OAuth2Id : {}",
                    oAuth2Provider,
                    tempUserBeforeSignUp.oAuth2Id
                )
            }
        }
    }
}
