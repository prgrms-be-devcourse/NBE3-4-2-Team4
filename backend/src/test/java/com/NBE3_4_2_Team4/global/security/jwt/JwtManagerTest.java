package com.NBE3_4_2_Team4.global.security.jwt;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.nimbusds.jose.util.Base64;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class JwtManagerTest {
    private JwtManager jwtManager;

    @Mock
    private MemberRepository memberRepository;

    private static String jwtKey;

    private static SecretKey secretKey;

    private Member member;

    @BeforeAll
    static void setup() {
        StringBuilder stringBuilder = new StringBuilder();
        String key = "test jwt secret key";
        stringBuilder.repeat(key, 20);
        String repeatedKey = stringBuilder.toString();
        jwtKey = String.valueOf(Base64.encode(repeatedKey));
        secretKey = Keys.hmacShaKeyFor(repeatedKey.getBytes());
    }

    @BeforeEach
    void setUp() {
        jwtManager = new JwtManager(jwtKey,30, 24, memberRepository);
        member = Member.builder()
                .id(1L)
                .username("test username")
                .nickname("test nickname")
                .role(Member.Role.USER)
                .oAuth2Provider(Member.OAuth2Provider.NONE)
                .build();
    }

    @Test
    void generateAccessTokenTest(){
        String accessToken = jwtManager.generateAccessToken(member);
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();

        assertEquals(member.getId(), ((Integer)claims.get("id")).longValue());
        assertEquals(member.getUsername(), claims.get("username"));
        assertEquals(member.getNickname(), claims.get("nickname"));
        assertEquals(member.getRole().name(), claims.get("role"));
        assertEquals(member.getOAuth2Provider().name(), claims.get("OAuth2Provider"));

        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void generateRefreshTokenTest(){
        String refreshToken = jwtManager.generateRefreshToken(member);
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();

        assertEquals(member.getId(), ((Integer)claims.get("id")).longValue());

        assertNull(claims.get("username"));
        assertNull(claims.get("nickname"));
        assertNull(claims.get("role"));
        assertNull(claims.get("OAuth2Provider"));

        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void getFreshAccessTokenTest(){
        String refreshToken = jwtManager.generateRefreshToken(member);

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(member));

        String freshAccessToken = jwtManager.getFreshAccessToken(refreshToken);

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(freshAccessToken)
                .getPayload();

        assertEquals(member.getId(), ((Integer)claims.get("id")).longValue());
        assertEquals(member.getUsername(), claims.get("username"));
        assertEquals(member.getNickname(), claims.get("nickname"));
        assertEquals(member.getRole().name(), claims.get("role"));
        assertEquals(member.getOAuth2Provider().name(), claims.get("OAuth2Provider"));

        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void getClaimsTest1(){
        assertThrows(JwtException.class, () -> jwtManager.getClaims(null));
    }

    @Test
    void getClaimsTest2(){
        assertThrows(JwtException.class, () -> jwtManager.getClaims("  "));
    }

    @Test
    void getClaimsTest3(){
        String invalidToken = String.format("%s salted", jwtManager.generateAccessToken(member));
        assertThrows(JwtException.class, () -> jwtManager.getClaims(invalidToken));
    }

    @Test
    void getClaimsTest4(){
        String expiredToken = Jwts.builder()
                .claim("id", member.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(secretKey)
                .compact();

        assertThrows(JwtException.class, () -> jwtManager.getClaims(expiredToken));
    }

    @Test
    void getClaimsTest5(){
        String malformedToken = "invalid token";

        assertThrows(JwtException.class, () -> jwtManager.getClaims(malformedToken));
    }
}
