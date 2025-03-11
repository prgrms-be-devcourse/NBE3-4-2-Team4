package com.NBE3_4_2_Team4.global.security.oauth2.tokenService.impl

import com.NBE3_4_2_Team4.global.security.oauth2.tokenService.OAuth2TokenService
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class NaverTokenService(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
): OAuth2TokenService {
    private val log = LoggerFactory.getLogger(NaverTokenService::class.java)

    @Value("\${spring.security.oauth2.client.registration.naver.client-id}")
    lateinit var clientId: String

    @Value("\${spring.security.oauth2.client.registration.naver.client-secret}")
    lateinit var clientSecret: String

    @Value("\${spring.security.oauth2.client.provider.naver.token-uri}")
    lateinit var naverTokenUri: String

    override fun getFreshAccessToken(refreshToken: String?): String? {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val url = UriComponentsBuilder.fromUriString(naverTokenUri)
            .queryParam("grant_type", "refresh_token")
            .queryParam("client_id", clientId)
            .queryParam("client_secret", clientSecret)
            .queryParam("refresh_token", refreshToken)
            .toUriString()

        val requestEntity = HttpEntity<Void>(headers)
        try {
            val response = restTemplate.postForEntity(url, requestEntity, String::class.java)
            return objectMapper.readTree(response.body)["access_token"].asText()
        } catch (e: Exception) {
            log.error("error occurred while getting accessToken for google. msg: {}", e.message)
            return null
        }
    }
}