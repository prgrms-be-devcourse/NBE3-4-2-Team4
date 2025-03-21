package com.NBE3_4_2_Team4.global.security.jwt;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.global.security.user.tempUserBeforeSignUp.TempUserBeforeSignUp;
import com.NBE3_4_2_Team4.standard.constants.AuthConstants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Slf4j
@Component
public class JwtManager {
    @Getter
    private final int accessTokenValidMinute;
    @Getter
    private final int refreshTokenValidHour;
    private final SecretKey key;
    private final MemberRepository memberRepository;

    public JwtManager(
            @Value("${custom.jwt.secretKey:key}") String jwtSecretKey,
            @Value("${custom.jwt.accessToken.validMinute:30}") int accessTokenValidMinute,
            @Value("${custom.jwt.refreshToken.validHour:24}") int refreshTokenValidHour,
            MemberRepository memberRepository
            ){
        try {
            byte[] keyBytes = Base64.getDecoder().decode(jwtSecretKey);
            this.key = Keys.hmacShaKeyFor(keyBytes);
            this.accessTokenValidMinute = accessTokenValidMinute;
            this.refreshTokenValidHour = refreshTokenValidHour;
            this.memberRepository = memberRepository;
        }catch (IllegalArgumentException e){
            throw new RuntimeException("키 값은 Base64로 디코딩 가능한 값이어야 합니다. yml 관련 파일을 확인해보세요.");
        }
    }

    public String generateAccessToken(Member member) {
        return Jwts.builder()
                .claim(AuthConstants.ID, member.getId())
                .claim(AuthConstants.USERNAME, member.getUsername())
                .claim(AuthConstants.NICKNAME, member.getNickname())
                .claim(AuthConstants.ROLE, member.getRole().name())
                .claim(AuthConstants.OAUTH2_PROVIDER, member.getOAuth2Provider().name())
                .claim(AuthConstants.EMAIL_ADDRESS, member.getEmailAddress())
                .claim(AuthConstants.EMAIL_VERIFIED, member.isEmailVerified())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (long) accessTokenValidMinute * 2 * 50))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(Member member) {
        return Jwts.builder()
                .claim(AuthConstants.ID, member.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (long) refreshTokenValidHour * 60 * 60 * 1000))
                .signWith(key)
                .compact();
    }

    public String getFreshAccessToken(String refreshToken){
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();

        Long id = ((Integer) claims.get(AuthConstants.ID)).longValue();
        Member member = memberRepository.findById(id)
                .orElseThrow();

        return generateAccessToken(member);
    }

    public Map<String, Object> getClaims(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            return null;
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
            return new HashMap<>(claims);
        }catch (UnsupportedJwtException | MalformedJwtException e){
            throw new JwtException(e.getLocalizedMessage());
        }
    }


    public String generateTempToken(TempUserBeforeSignUp tempUserBeforeSignUp){
        return Jwts.builder()
                .claim(AuthConstants.OAUTH2_ID, tempUserBeforeSignUp.getOAuth2Id())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (long) accessTokenValidMinute * 60 * 1000))
                .signWith(key)
                .compact();
    }
}
