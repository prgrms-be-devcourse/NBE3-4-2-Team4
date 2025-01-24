package com.NBE3_4_2_Team4.global.jwt;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;

@Component
public class JwtProvider {
    @Value("${jwt.secret.key : key}")
    private static String jwtSecretKey;

    @Value("${jwt.secret.valid.minute : 30}")
    private static int jwtValidMinute;

    private final Key key;

    public JwtProvider(){
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
}
