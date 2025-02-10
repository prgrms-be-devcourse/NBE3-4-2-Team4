package com.NBE3_4_2_Team4.global.security.jwt;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JwtObjectMapper {
    public Member getMemberByJwtClaims(Map<String, Object> claims) {
        Integer id = (Integer) claims.get("id");
        String username = (String) claims.get("username");
        String nickname = (String) claims.get("nickname");
        String roleName = (String) claims.get("role");
        String OAuth2ProviderName = (String) claims.get("OAuth2Provider");

        if (id == null || isNullOrBlank(nickname)|| isNullOrBlank(roleName) || isNullOrBlank(OAuth2ProviderName)) {
            throw new RuntimeException("Invalid claims");
        }
        return new Member(Long.valueOf(id), username, nickname, roleName, OAuth2ProviderName);
    }

    private boolean isNullOrBlank(String string) {
        return string == null || string.isBlank();
    }
}
