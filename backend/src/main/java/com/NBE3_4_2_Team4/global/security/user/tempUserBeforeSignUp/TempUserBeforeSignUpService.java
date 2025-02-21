package com.NBE3_4_2_Team4.global.security.user.tempUserBeforeSignUp;

import com.NBE3_4_2_Team4.global.security.jwt.JwtManager;
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfo;
import com.NBE3_4_2_Team4.standard.constants.AuthConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TempUserBeforeSignUpService {
    private final JwtManager jwtManager;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;


    public TempUserBeforeSignUp getOrCreateTempUser(OAuth2UserInfo oAuth2UserInfo, String providerTypeCode, String refreshToken){
        String oAuth2Id = oAuth2UserInfo.getOAuth2Id();

        TempUserBeforeSignUp tempUserBeforeSignUp =  redisTemplate.hasKey(oAuth2Id) ?
                getTempUserFromRedis(oAuth2Id) :
                new TempUserBeforeSignUp(oAuth2UserInfo, providerTypeCode, refreshToken);

        redisTemplate.opsForValue().set(oAuth2Id, tempUserBeforeSignUp, Duration.ofHours(2));

        return tempUserBeforeSignUp;
    }

    private TempUserBeforeSignUp getTempUserFromRedis(String key) {
        return objectMapper.convertValue(redisTemplate.opsForValue().get(key), TempUserBeforeSignUp.class);
    }

    public TempUserBeforeSignUp getTempUserFromRedisWithJwt(String tempToken){
        Map<String, Object> claims = jwtManager.getClaims(tempToken);
        String oAuth2Id = (String) claims.get(AuthConstants.OAUTH2_ID);
        return getTempUserFromRedis(oAuth2Id);
    }

    public void deleteTempUserFromRedis(String tempToken){
        Map<String, Object> claims = jwtManager.getClaims(tempToken);
        String oAuth2Id = (String) claims.get(AuthConstants.OAUTH2_ID);
        redisTemplate.delete(oAuth2Id);
    }

    public void saveAuthCodeForMember(Long memberId, String authCode) {
        String memberIdString = String.valueOf(memberId);
        redisTemplate.opsForValue().set(memberIdString, authCode, Duration.ofMinutes(10)); // 10분 후 만료
    }

    public boolean isEmailVerified(Long memberId, String authCode) {
        String memberIdString = String.valueOf(memberId);
        String authCodeString = (String) redisTemplate.opsForValue().get(memberIdString);
        return authCodeString.equals(authCode);
    }
}
