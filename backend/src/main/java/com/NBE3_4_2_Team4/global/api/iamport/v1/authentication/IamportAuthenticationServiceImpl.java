package com.NBE3_4_2_Team4.global.api.iamport.v1.authentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.NBE3_4_2_Team4.global.api.iamport.v1.constants.IamportConstants.IAMPORT_TOKEN_REDIS_KEY;
import static com.NBE3_4_2_Team4.global.api.iamport.v1.constants.IamportConstants.IAMPORT_GENERATE_TOKEN_URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class IamportAuthenticationServiceImpl implements IamportAuthenticationService {

    @Value("${custom.iamport.apiKey}")
    private String apiKey;

    @Value("${custom.iamport.apiSecret}")
    private String apiSecret;

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Optional<String> generateAccessToken(Long memberId) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("imp_key", apiKey);
            requestBody.add("imp_secret", apiSecret);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    IAMPORT_GENERATE_TOKEN_URL,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            return Optional.ofNullable(response)
                    .filter(res -> res.getStatusCode() == HttpStatus.OK)
                    .map(ResponseEntity::getBody)
                    .map(body -> (Map<String, Object>) body.get("response"))
                    .map(resData -> {
                        String accessToken = (String) resData.get("access_token");
                        long expiredAt = ((Number) resData.get("expired_at")).longValue();
                        long currentTime = Instant.now().getEpochSecond();
                        long expiresIn = expiredAt - currentTime;

                        // Redis에 저장
                        // Key - iamport:access_token, Value - <액세스 토큰>, TTL - 30분
                        String redisKey = IAMPORT_TOKEN_REDIS_KEY + memberId;
                        redisTemplate.opsForValue().set(redisKey, accessToken, Duration.ofSeconds(expiresIn));
                        log.info("Success to save Import Access Token in Redis (Expired Time : {})", expiresIn);

                        return accessToken;
                    });

        } catch (HttpClientErrorException e) {
            log.error("Client Error: {}", e.getMessage());

        } catch (HttpServerErrorException e) {
            log.error("Server Error: {}", e.getMessage());

        } catch (Exception e) {
            log.error("Unexpected Error: {}", e.getMessage());

        }

        return Optional.empty();
    }

    @Override
    public Optional<String> getAccessToken(Long memberId) {

        String tokenKey = IAMPORT_TOKEN_REDIS_KEY + memberId;

        Long remainTime = redisTemplate.getExpire(tokenKey);

        // 토큰이 만료되었을 경우
        if (remainTime == -2) {
            log.warn("[{}] is expired. Please regenerate Access Token.", tokenKey);
            return Optional.empty();
        }

        return Optional.ofNullable(
                (String) redisTemplate.opsForValue().get(tokenKey)
        );
    }
}