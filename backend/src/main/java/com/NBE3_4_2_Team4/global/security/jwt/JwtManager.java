package com.NBE3_4_2_Team4.global.security.jwt;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
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
    private final long accessTokenValidMinute;
    private final long refreshTokenValidHour;
    private final SecretKey key;
    private final MemberRepository memberRepository;

    public JwtManager(
            @Value("${custom.jwt.secretKey:key}") String jwtSecretKey,
            @Value("${custom.jwt.accessToken.validMinute:30}") long accessTokenValidMinute,
            @Value("${custom.jwt.refreshToken.validHour:24}") long refreshTokenValidHour,
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
                .claim("id", member.getId())
                .claim("username", member.getUsername())
                .claim("nickname", member.getNickname())
                .claim("role", member.getRole().name())
                .claim("OAuth2Provider", member.getOAuth2Provider().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenValidMinute * 60 * 1000))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(Member member) {
        return Jwts.builder()
                .claim("id", member.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenValidHour * 60 * 60 * 1000))
                .signWith(key)
                .compact();
    }

    public String getFreshAccessToken(String refreshToken){
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();

        Long id = ((Integer) claims.get("id")).longValue();
        Member member = memberRepository.findById(id)
                .orElseThrow();

        return generateAccessToken(member);
    }

    public Map<String, Object> getClaims(String accessToken)  throws ExpiredJwtException {
        if (accessToken == null || accessToken.isBlank()) {
            throw new JwtException("Token is empty");
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
            return new HashMap<>(claims);
        } catch (UnsupportedJwtException e){
            throw new JwtException("Token is empty");
        }catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            throw new JwtException("Token is empty2");
        }
        // 예외 처리는 추후 수정할 예정.
    }
}
