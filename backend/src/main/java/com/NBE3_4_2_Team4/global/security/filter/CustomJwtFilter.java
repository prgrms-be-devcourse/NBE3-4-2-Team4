package com.NBE3_4_2_Team4.global.security.filter;

import com.NBE3_4_2_Team4.global.security.AuthHandler;
import com.NBE3_4_2_Team4.global.security.jwt.JwtManager;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomJwtFilter extends OncePerRequestFilter {
    private final JwtManager jwtManager;
    private final AuthHandler authHandler;
    private final MemberService memberService;

    private String getJwtToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String jwtToken = getJwtToken(request);

        if (jwtToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Map<String, Object> claims = jwtManager.getClaims(jwtToken);
        Member member = memberService.getMemberByJwtClaims(claims);

        if (member != null) {
            authHandler.setLogin(member);
        }

        filterChain.doFilter(request, response);
    }
}
