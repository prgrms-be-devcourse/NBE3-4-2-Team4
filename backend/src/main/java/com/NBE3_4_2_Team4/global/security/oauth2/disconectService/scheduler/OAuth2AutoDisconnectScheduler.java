package com.NBE3_4_2_Team4.global.security.oauth2.disconectService.scheduler;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.security.oauth2.OAuth2Manager;
import com.NBE3_4_2_Team4.global.security.oauth2.disconectService.OAuth2DisconnectService;
import com.NBE3_4_2_Team4.global.security.user.tempUserBeforeSignUp.TempUserBeforeSignUp;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AutoDisconnectScheduler {
    private final RedisTemplate<String, Object> redisTemplate;
    private final OAuth2Manager oAuth2Manager;
    private final ObjectMapper objectMapper;


    @Scheduled(cron = "0 0 * * * *")
    public void autoDisconnect() {
        Set<String> keySet = redisTemplate.keys("*");

        Set<String> keySetToDisconnect = new HashSet<>();

        for (String key : keySet) {
            long ttl = redisTemplate.getExpire(key);
            if (ttl > 0 && ttl < 3600) {
                keySetToDisconnect.add(key);
            }
        }

        for (String key : keySetToDisconnect) {
            TempUserBeforeSignUp tempUserBeforeSignUp = objectMapper.convertValue(redisTemplate.opsForValue().get(key), TempUserBeforeSignUp.class);
            Member.OAuth2Provider oAuth2Provider = Member.OAuth2Provider.getOAuth2ProviderByName(tempUserBeforeSignUp.getProviderTypeCode());

            OAuth2DisconnectService oAuth2DisconnectService = oAuth2Manager.getOAuth2DisconnectService(oAuth2Provider);
            String refreshToken = tempUserBeforeSignUp.getRefreshToken();

            if (oAuth2DisconnectService.disconnectSuccess(refreshToken)){
                redisTemplate.delete(key);
            }else {
                log.error("OAuth2 연동 해제 실패. (연동 해제 요청이 실패했습니다.) 해당 서비스에 직접 연결 해제를 시도하세요. OAuth2Provider : {}, OAuth2Id : {}", oAuth2Provider, tempUserBeforeSignUp.getOAuth2Id());
            }
        }
    }
}
