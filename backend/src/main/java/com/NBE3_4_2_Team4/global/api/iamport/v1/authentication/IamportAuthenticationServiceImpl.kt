package com.NBE3_4_2_Team4.global.api.iamport.v1.authentication

import com.NBE3_4_2_Team4.global.api.iamport.v1.constants.IamportConstants.IAMPORT_GENERATE_TOKEN_URL
import com.NBE3_4_2_Team4.global.api.iamport.v1.constants.IamportConstants.IAMPORT_TOKEN_REDIS_KEY
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import java.time.Duration
import java.time.Instant

@Service
class IamportAuthenticationServiceImpl(

    private val restTemplate: RestTemplate,
    private val redisTemplate: RedisTemplate<String, Any>

    ) : IamportAuthenticationService {

    private val logger = KotlinLogging.logger {}

    @Value("\${custom.iamport.apiKey}")
    private lateinit var apiKey: String

    @Value("\${custom.iamport.apiSecret}")
    private lateinit var apiSecret: String

    override fun generateAccessToken(memberId: Long): String? {

        return runCatching {
            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
                accept = listOf(MediaType.APPLICATION_JSON)
            }

            val requestBody = mapOf(
                "imp_key" to apiKey,
                "imp_secret" to apiSecret
            )

            val request = HttpEntity(requestBody, headers)

            val response = restTemplate.exchange(
                IAMPORT_GENERATE_TOKEN_URL,
                HttpMethod.POST,
                request,
                Map::class.java
            )

            response.body?.get("response")?.let { it as? Map<*, *> }?.let { resData ->
                val accessToken = resData["access_token"] as? String
                val expiredAt = (resData["expired_at"] as? Number)?.toLong() ?: return null
                val expiresIn = expiredAt - Instant.now().epochSecond

                if (accessToken != null) {
                    val redisKey = "$IAMPORT_TOKEN_REDIS_KEY$memberId"
                    redisTemplate.opsForValue().set(redisKey, accessToken, Duration.ofSeconds(expiresIn))
                    logger.info { "Success: Iamport Access Token saved in Redis (Expires in: $expiresIn sec)" }
                }
                accessToken
            }

        }.onFailure { e ->
            when (e) {
                is HttpClientErrorException -> logger.error { "Client Error: ${e.message}" }
                is HttpServerErrorException -> logger.error { "Server Error: ${e.message}" }
                else -> logger.error { "Unexpected Error: ${e.message}" }
            }
        }.getOrNull()
    }

    override fun getAccessToken(memberId: Long): String? {

        val tokenKey = "$IAMPORT_TOKEN_REDIS_KEY$memberId"
        val remainTime = redisTemplate.getExpire(tokenKey)

        return if (remainTime == -2L) {
            logger.warn { "[$tokenKey] is expired. Please regenerate Access Token." }
            null
        } else {
            redisTemplate.opsForValue().get(tokenKey) as? String
        }
    }
}