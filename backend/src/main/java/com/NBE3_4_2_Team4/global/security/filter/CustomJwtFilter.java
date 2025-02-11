package com.NBE3_4_2_Team4.global.security.filter;

import com.NBE3_4_2_Team4.global.security.AuthManager;
import com.NBE3_4_2_Team4.global.security.HttpManager;
import com.NBE3_4_2_Team4.global.security.jwt.JwtManager;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.security.jwt.JwtObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomJwtFilter extends OncePerRequestFilter {
    private final JwtManager jwtManager;
    private final AuthManager authManager;
    private final JwtObjectMapper jwtObjectMapper;
    private final HttpManager httpManager;

    @Value("${custom.jwt.accessToken.validMinute:30}")
    int accessTokenValidMinute;

    private String getAccessTokenFromHeader(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    private String getAccessTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void setAuthContextWithAccessToken(@NonNull HttpServletResponse response,
                                               String accessToken) throws JwtException {
        Map<String, Object> claims = jwtManager.getClaims(accessToken);
        if (claims != null) {
            Member member = jwtObjectMapper.getMemberByJwtClaims(claims);

            if (member != null) {
                authManager.setLogin(member);
                httpManager.setAccessTokenCookie(response, accessToken, accessTokenValidMinute);
            }
        }
    }

    private void tryAgainWithRefreshToken(@NonNull HttpServletRequest request,
                                          @NonNull HttpServletResponse response){
        try{
            String refreshToken = getRefreshToken(request);
            if (refreshToken == null || refreshToken.isBlank()) { return;}
            String accessToken = jwtManager.getFreshAccessToken(refreshToken);

            setAuthContextWithAccessToken(response, accessToken);
        }catch (JwtException e){
            httpManager.expireJwtCookie(response);
            log.warn("trying with refresh token failed, msg : {}", e.getMessage());
        }
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        try{
            String accessToken = getAccessTokenFromCookie(request);

            if (accessToken == null || accessToken.isBlank()) {
                tryAgainWithRefreshToken(request, response);
            }
            setAuthContextWithAccessToken(response, accessToken);
        }catch (ExpiredJwtException e){
            tryAgainWithRefreshToken(request, response);
        } catch (JwtException e){
            httpManager.expireJwtCookie(response);
            log.warn("JWT token invalid. msg = {}", e.getMessage());
        }finally {
            filterChain.doFilter(request, response);
        }
    }
}
