package com.NBE3_4_2_Team4.global.security.jwt;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
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
    private final long jwtValidMinute;

    private final SecretKey key;

    public JwtManager(
            @Value("${custom.jwt.secretKey:key}") String jwtSecretKey,
            @Value("${jwt.secret.valid.minute:30}") long jwtValidMinute){
        try {
            byte[] keyBytes = Base64.getDecoder().decode(jwtSecretKey);
            this.key = Keys.hmacShaKeyFor(keyBytes);
            this.jwtValidMinute = jwtValidMinute;
        }catch (IllegalArgumentException e){
            throw new RuntimeException("키 값은 Base64로 디코딩 가능한 값이어야 합니다. yml 관련 파일을 확인해보세요.");
        }
    }

    public String generateToken(Member member) {
        return Jwts.builder()
                .claim("id", member.getId())
                .claim("nickname", member.getNickname())
                .claim("role", member.getRole().name())
                .claim("OAuth2Provider", member.getOAuth2Provider().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtValidMinute * 60 * 1000))
                .signWith(key)
                .compact();
    }

    public Map<String, Object> getClaims(String token) {
        if (token == null || token.isEmpty()) {
            throw new JwtException("Token is empty");
        }
        try {
            Claims claims = Jwts.parser()
                    .decryptWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return new HashMap<>(claims);
        }catch (ExpiredJwtException e){
            throw new JwtException("Token is expired");
        }catch (UnsupportedJwtException e){
            throw new JwtException("Token is empty");
        }catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            throw new JwtException("Token is empty2");
        }
        // 예외 처리는 추후 수정할 예정.
    }
}
