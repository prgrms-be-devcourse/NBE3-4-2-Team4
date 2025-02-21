package com.NBE3_4_2_Team4.global.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;


@Component
public class HttpManager {

    public void setCookie(
            HttpServletResponse resp, String name, String value, int minute) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setDomain("localhost");
//        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(minute * 60); // 쿠키 만료 시간 (초 단위)
        resp.addCookie(cookie); // 응답에 쿠키 추가
    }

    public void setAccessTokenCookie(HttpServletResponse resp, String accessToken, int minute){
        this.setCookie(resp, "accessToken", accessToken, minute);
    }

    public void setRefreshTokenCookie(HttpServletResponse resp, String refreshToken, int hour){
        this.setCookie(resp, "refreshToken", refreshToken, hour * 60);
    }

    public void setJWTCookie(
            HttpServletResponse resp, String accessToken, int minute, String refreshToken, int hour){
        this.setAccessTokenCookie(resp, accessToken, minute);
        this.setRefreshTokenCookie(resp, refreshToken, hour);
    }


    public void deleteCookie(
            HttpServletResponse resp, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setDomain("localhost");
//        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // 쿠키 만료 시간 (초 단위)
        resp.addCookie(cookie); // 응답에 쿠키 추가
    }

    public void expireJwtCookie(
            HttpServletResponse resp){
        deleteCookie(resp, "accessToken");
        deleteCookie(resp, "refreshToken");
    }


    public void setTempTokenForSignUpCookie(HttpServletResponse resp, String tempToken, int minute){
        this.setCookie(resp, "tempToken", tempToken, minute);
    }
}
