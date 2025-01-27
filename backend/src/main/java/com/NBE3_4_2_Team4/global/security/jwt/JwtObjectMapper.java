package com.NBE3_4_2_Team4.global.security.jwt;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JwtObjectMapper {
    public Member getMemberByJwtClaims(Map<String, Object> claims) {
        Long id = (Long) claims.get("id");
        String nickname = (String) claims.get("nickname");
        String roleName = (String) claims.get("role");
        String OAuth2ProviderName = (String) claims.get("OAuth2Provider");

        if (id == null || nickname == null || roleName == null || OAuth2ProviderName == null) {
            throw new RuntimeException("Invalid claims");
        }
        return new Member(id, nickname, roleName, OAuth2ProviderName);
    }
}
