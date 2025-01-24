package com.NBE3_4_2_Team4.global.security.user;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomUser extends User implements OAuth2User {
    private final long id;

    private final String nickname;

    public CustomUser(
            long id,
            String username,
            String password,
            String nickname,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.nickname = nickname;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public String getName() {
        return getUsername();
    }
}
