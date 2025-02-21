package com.NBE3_4_2_Team4.global.security.user.tempUserBeforeSignUp;

import com.NBE3_4_2_Team4.global.security.jwt.JwtManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfo;
import com.NBE3_4_2_Team4.standard.constants.AuthConstants;
import org.junit.jupiter.api.BeforeEach;


import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TempUserBeforeSignUpServiceTest {
    @InjectMocks
    private TempUserBeforeSignUpService tempUserBeforeSignUpService;

    @Mock
    private JwtManager jwtManager;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ValueOperations<String, Object> valueOperations;

    private final String oAuth2Id = "testOAuth2Id";
    private final String tempToken = "testTempToken";
    private final String providerTypeCode = "google";
    private final String refreshToken = "testRefreshToken";

    private OAuth2UserInfo oAuth2UserInfo;
    private TempUserBeforeSignUp tempUserBeforeSignUp;

    @BeforeEach
    void setUp() {
        oAuth2UserInfo = mock(OAuth2UserInfo.class);
        when(oAuth2UserInfo.getOAuth2Id())
                .thenReturn(oAuth2Id);

        tempUserBeforeSignUp = new TempUserBeforeSignUp(oAuth2UserInfo, providerTypeCode, refreshToken);
    }

    @Test
    @DisplayName("getOrCreateTempUser - Redis에 사용자 정보가 존재하는 경우 기존 정보를 반환")
    void testGetOrCreateTempUser_WhenUserExistsInRedis() {
        when(redisTemplate.hasKey(oAuth2Id))
                .thenReturn(true);
        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);
        when(valueOperations.get(oAuth2Id))
                .thenReturn(tempUserBeforeSignUp);
        when(objectMapper.convertValue(tempUserBeforeSignUp, TempUserBeforeSignUp.class))
                .thenReturn(tempUserBeforeSignUp);


        TempUserBeforeSignUp result = tempUserBeforeSignUpService.getOrCreateTempUser(oAuth2UserInfo, providerTypeCode, refreshToken);


        assertThat(result).isEqualTo(tempUserBeforeSignUp);
        verify(objectMapper, times(1)).convertValue(tempUserBeforeSignUp, TempUserBeforeSignUp.class);
        verify(valueOperations, times(1)).set(eq(oAuth2Id), eq(tempUserBeforeSignUp), any());
    }

    @Test
    @DisplayName("getOrCreateTempUser - Redis에 사용자 정보가 없을 경우 새로 생성하여 저장")
    void testGetOrCreateTempUser_WhenUserNotInRedis() {
        when(redisTemplate.hasKey(oAuth2Id))
                .thenReturn(false);
        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);

        TempUserBeforeSignUp result = tempUserBeforeSignUpService.getOrCreateTempUser(oAuth2UserInfo, providerTypeCode, refreshToken);

        assertThat(result).isNotNull();
        assertThat(result.getOAuth2Id()).isEqualTo(oAuth2Id);
        verify(objectMapper, never()).convertValue(tempUserBeforeSignUp, TempUserBeforeSignUp.class);
        verify(valueOperations, times(1)).set(eq(oAuth2Id), eq(tempUserBeforeSignUp), any());
    }

    @Test
    @DisplayName("getTempUserFromRedisWithJwt - JWT에서 oAuth2Id를 추출하여 Redis에서 사용자 정보 조회")
    void testGetTempUserFromRedisWithJwt() {
        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);
        when(jwtManager.getClaims(tempToken))
                .thenReturn(Map.of(AuthConstants.OAUTH2_ID, oAuth2Id));
        when(valueOperations.get(oAuth2Id))
                .thenReturn(tempUserBeforeSignUp);
        when(objectMapper.convertValue(tempUserBeforeSignUp, TempUserBeforeSignUp.class))
                .thenReturn(tempUserBeforeSignUp);

        TempUserBeforeSignUp result = tempUserBeforeSignUpService.getTempUserFromRedisWithJwt(tempToken);


        assertThat(result).isEqualTo(tempUserBeforeSignUp);
        verify(jwtManager, times(1)).getClaims(tempToken);
        verify(objectMapper, times(1)).convertValue(tempUserBeforeSignUp, TempUserBeforeSignUp.class);
    }

    @Test
    @DisplayName("deleteTempUserFromRedis - JWT에서 oAuth2Id를 추출하여 Redis에서 사용자 정보 삭제")
    void testDeleteTempUserFromRedis() {
        when(jwtManager.getClaims(tempToken))
                .thenReturn(Map.of(AuthConstants.OAUTH2_ID, oAuth2Id));

        tempUserBeforeSignUpService.deleteTempUserFromRedis(tempToken);

        verify(redisTemplate, times(1)).delete(oAuth2Id);
    }
}
