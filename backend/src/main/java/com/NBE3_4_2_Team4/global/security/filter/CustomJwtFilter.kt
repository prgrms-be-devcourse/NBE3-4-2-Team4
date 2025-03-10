package com.NBE3_4_2_Team4.global.security.filter

import com.NBE3_4_2_Team4.global.security.AuthManager
import com.NBE3_4_2_Team4.global.security.HttpManager
import com.NBE3_4_2_Team4.global.security.jwt.JwtManager
import com.NBE3_4_2_Team4.global.security.jwt.JwtObjectMapper
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class CustomJwtFilter(
    private val jwtManager: JwtManager,
    private val authManager: AuthManager,
    private val jwtObjectMapper: JwtObjectMapper,
    private val httpManager: HttpManager
): OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Value("\${custom.domain.backend}")
    lateinit var backendDomain: String

    @Value("\${custom.jwt.accessToken.validMinute:30}")
    var accessTokenValidMinute: Int = 30

    private fun getAccessTokenFromCookie(request: HttpServletRequest): String? {
        val cookies = request.cookies
        if (cookies != null) {
            for (cookie in cookies) {
                if (cookie.name == "accessToken") {
                    return cookie.value
                }
            }
        }
        return null
    }

    private fun getRefreshToken(request: HttpServletRequest): String? {
        val cookies = request.cookies
        if (cookies != null) {
            for (cookie in cookies) {
                if (cookie.name == "refreshToken") {
                    return cookie.value
                }
            }
        }
        return null
    }

    @Throws(JwtException::class)
    private fun setAuthContextWithAccessToken(
        response: HttpServletResponse,
        accessToken: String
    ) {
        val claims = jwtManager.getClaims(accessToken)
        if (claims != null) {
            val member = jwtObjectMapper.getMemberByJwtClaims(claims)

            if (member != null) {
                authManager.setLogin(member)
                httpManager.setAccessTokenCookie(response, accessToken, accessTokenValidMinute)
            }
        }
    }

    private fun tryAgainWithRefreshToken(
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        try {
            val refreshToken = getRefreshToken(request)
            if (refreshToken.isNullOrBlank()) {
                return
            }
            val accessToken = jwtManager.getFreshAccessToken(refreshToken)

            setAuthContextWithAccessToken(response, accessToken)
        } catch (e: JwtException) {
            httpManager.expireJwtCookie(response)
            log.warn("trying with refresh token failed, msg : {}", e.message)
        }
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val accessToken = getAccessTokenFromCookie(request)

            if (accessToken.isNullOrBlank()) {
                tryAgainWithRefreshToken(request, response)
            } else {
                setAuthContextWithAccessToken(response, accessToken)
            }
        } catch (e: ExpiredJwtException) {
            tryAgainWithRefreshToken(request, response)
        } catch (e: JwtException) {
            httpManager.expireJwtCookie(response)
            log.warn("JWT token invalid. msg = {}", e.message)
        } finally {
            filterChain.doFilter(request, response)
        }
    }
}