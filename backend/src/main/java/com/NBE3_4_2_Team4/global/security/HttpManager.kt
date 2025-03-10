package com.NBE3_4_2_Team4.global.security

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component

@Component
class HttpManager {
    fun setCookie(
        resp: HttpServletResponse, name: String, value: String, minute: Int
    ) {
        val cookie = Cookie(name, value)
        cookie.path = "/"
        cookie.domain = "localhost"
        //        cookie.setSecure(true);
        cookie.isHttpOnly = true
        cookie.maxAge = minute * 60 // 쿠키 만료 시간 (초 단위)
        resp.addCookie(cookie) // 응답에 쿠키 추가
    }

    fun setAccessTokenCookie(resp: HttpServletResponse, accessToken: String, minute: Int) {
        this.setCookie(resp, "accessToken", accessToken, minute)
    }

    fun setRefreshTokenCookie(resp: HttpServletResponse, refreshToken: String, hour: Int) {
        this.setCookie(resp, "refreshToken", refreshToken, hour * 60)
    }

    fun setJWTCookie(
        resp: HttpServletResponse, accessToken: String, minute: Int, refreshToken: String, hour: Int
    ) {
        this.setAccessTokenCookie(resp, accessToken, minute)
        this.setRefreshTokenCookie(resp, refreshToken, hour)
    }


    fun deleteCookie(
        resp: HttpServletResponse, name: String
    ) {
        val cookie = Cookie(name, null)
        cookie.path = "/"
        cookie.domain = "localhost"
        //        cookie.setSecure(true);
        cookie.isHttpOnly = true
        cookie.maxAge = 0 // 쿠키 만료 시간 (초 단위)
        resp.addCookie(cookie) // 응답에 쿠키 추가
    }

    fun expireJwtCookie(
        resp: HttpServletResponse
    ) {
        deleteCookie(resp, "accessToken")
        deleteCookie(resp, "refreshToken")
    }


    fun setTempTokenForSignUpCookie(resp: HttpServletResponse, tempToken: String, minute: Int) {
        this.setCookie(resp, "tempToken", tempToken, minute)
    }
}