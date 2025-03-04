package com.NBE3_4_2_Team4.global.security;

import com.NBE3_4_2_Team4.global.security.user.customUser.CustomUser;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthManager {
    public void setLogin(Member member){
        UserDetails userDetails = new CustomUser(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static Member getMemberFromContext(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof CustomUser){
            return ((CustomUser) authentication.getPrincipal()).getMember();
        }else {
            return null;
        }
    }

    public static Member getNonNullMember() {
        Member member = getMemberFromContext();
        if (member == null) throw new AuthenticationCredentialsNotFoundException("로그인이 필요합니다.");
        return member;
    }
}
