package com.NBE3_4_2_Team4.global.security.user;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import lombok.Getter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

@Getter
public class CustomUser extends User implements OAuth2User {
    private final Member member;

    public CustomUser(Member member){
        super(member.getUsername(), member.getPassword(), member.getAuthorities());
        this.member = member;
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
