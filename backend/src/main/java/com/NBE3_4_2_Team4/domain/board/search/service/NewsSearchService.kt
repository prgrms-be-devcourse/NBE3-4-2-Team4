package com.NBE3_4_2_Team4.domain.board.search.service

import com.NBE3_4_2_Team4.domain.board.search.entity.NewsSearchResult
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Service
class NewsSearchService(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
) {
    @Value("\${custom.naver.api.client-id}")
    private lateinit var clientId: String

    @Value("\${custom.naver.api.client-secret}")
    private lateinit var clientSecret: String

    @Value("\${custom.naver.api.trend-url}")
    private lateinit var trendUrl: String

    fun getSearchResults(query: String, display: Int, start: Int): List<NewsSearchResult> {
        val url = "$trendUrl?display=$display&start=$start&query=$query"
        // 요청 헤더 설정
        val headers = HttpHeaders().apply {
            set("X-Naver-Client-Id", clientId)
            set("X-Naver-Client-Secret", clientSecret)
            contentType = MediaType.APPLICATION_JSON
        }

        return try {
            val response = restTemplate.exchange(url, HttpMethod.GET, HttpEntity<Any>(headers), String::class.java)
            parseSearchResults(response.body!!)
        } catch (e: HttpClientErrorException.TooManyRequests) {
            Thread.sleep(1000) // 1초 대기 후 재시도
            val response = restTemplate.exchange(url, HttpMethod.GET, HttpEntity<Any>(headers), String::class.java)
            parseSearchResults(response.body!!)
        }
    }

    private fun parseSearchResults(responseBody: String): List<NewsSearchResult> {
        return try {
            val root = objectMapper.readTree(responseBody)
            val items = root.path("items") // "items" 배열 가져오기

            items.map {
                NewsSearchResult(
                    title = it.path("title").asText(),
                    link = it.path("link").asText(),
                    description = it.path("description").asText()
                )
            }
        } catch (e: Exception) {
            throw RuntimeException("네이버 검색 API 응답 파싱 실패", e)
        }
    }
}
