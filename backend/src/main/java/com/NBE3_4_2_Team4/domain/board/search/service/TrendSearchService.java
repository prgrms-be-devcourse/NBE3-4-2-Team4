package com.NBE3_4_2_Team4.domain.board.search.service;

import com.NBE3_4_2_Team4.domain.board.search.entity.SearchResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrendSearchService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${custom.naver.api.client-id}")
    private String clientId;

    @Value("${custom.naver.api.client-secret}")
    private String clientSecret;

    @Value("${custom.naver.api.trend-url}")
    private String trendUrl;

    public TrendSearchService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String getSearch(String query, int display, int start) {
        String url = "https://openapi.naver.com/v1/search/news.json?display=3&start=1&sort=sim&query=" + query;
        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        return response.getBody();  // JSON 응답 반환
    }

    public List<SearchResult> getSearchResults(String query, int display, int start) {
        String url = "%s?display=%d&start=%d&query=%s".formatted(trendUrl, display, start, query);
        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        return parseSearchResults(response.getBody());
    }

    private List<SearchResult> parseSearchResults(String responseBody) {
        List<SearchResult> results = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode items = root.path("items"); // "items" 배열 가져오기

            for (JsonNode item : items) {
                String title = item.path("title").asText();
                String link = item.path("link").asText();
                String description = item.path("description").asText();

                results.add(new SearchResult(title, link, description));
            }
        } catch (Exception e) {
            throw new RuntimeException("네이버 검색 API 응답 파싱 실패", e);
        }
        return results;
    }
}
