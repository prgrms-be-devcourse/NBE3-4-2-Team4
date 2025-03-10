package com.NBE3_4_2_Team4.global.security.oauth2.tokenService.impl

import com.NBE3_4_2_Team4.global.security.oauth2.tokenService.OAuth2TokenService
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Service
class KakaoTokenService(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
): OAuth2TokenService {
    private val log = LoggerFactory.getLogger(KakaoTokenService::class.java)
    @Value("\${spring.security.oauth2.client.registration.kakao.clientId}")
    lateinit var clientId: String

    @Value("\${spring.security.oauth2.client.provider.kakao.token-uri}")
    lateinit var kakaoTokenUri: String

    override fun getFreshAccessToken(refreshToken: String?): String? {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val body: MultiValueMap<String, String> = LinkedMultiValueMap()
        body.add("grant_type", "refresh_token")
        body.add("client_id", clientId)
        body.add("refresh_token", refreshToken)

        val requestEntity = HttpEntity(body, headers)
        try {
            val response: ResponseEntity<String> = restTemplate.postForEntity(
                kakaoTokenUri, requestEntity,
                String::class.java
            )
            return objectMapper.readTree(response.body)["access_token"].asText()
        }  catch (e: Exception) {
            log.error("error occurred while getting accessToken for google. msg: {}", e.message)
            return null
        }
    }
}