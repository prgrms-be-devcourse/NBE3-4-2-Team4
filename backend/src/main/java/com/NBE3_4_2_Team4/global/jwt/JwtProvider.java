package com.NBE3_4_2_Team4.global.jwt;

import com.NBE3_4_2_Team4.member.member.entity.Member;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtProvider {
    @Value("${jwt.secret.key : key}")
    private static String jwtSecretKey;

    @Value("${jwt.secret.valid.minute : 30}")
    private static long jwtValidMinute;

    private final Key key;

    public JwtProvider(){
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Member member) {
        return Jwts.builder()
                .claim("id", member.getId())
                .claim("nickname", member.getNickname())
                .claim("role", member.getMemberCategory().getName())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtValidMinute * 60 * 1000))
                .signWith(key)
                .compact();
    }
}
