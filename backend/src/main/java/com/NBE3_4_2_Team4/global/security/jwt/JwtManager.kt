package com.NBE3_4_2_Team4.global.security.jwt

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository
import com.NBE3_4_2_Team4.global.security.user.tempUserBeforeSignUp.TempUserBeforeSignUp
import com.NBE3_4_2_Team4.standard.constants.AuthConstants
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtManager(
    @Value("\${custom.jwt.secretKey:key}") jwtSecretKey: String,
    @Value("\${custom.jwt.accessToken.validMinute:30}") val accessTokenValidMinute: Int,
    @Value("\${custom.jwt.refreshToken.validHour:24}") val refreshTokenValidHour: Int,
    private val memberRepository: MemberRepository
) {
    private final var key: SecretKey

    init {
        try {
            val keyBytes = Base64.getDecoder().decode(jwtSecretKey)
            this.key = Keys.hmacShaKeyFor(keyBytes)
        } catch (e: IllegalArgumentException) {
            throw RuntimeException("키 값은 Base64로 디코딩 가능한 값이어야 합니다. yml 관련 파일을 확인해보세요.")
        }
    }

    fun generateAccessToken(member: Member): String {
        return Jwts.builder()
            .claim(AuthConstants.ID, member.id)
            .claim(AuthConstants.USERNAME, member.username)
            .claim(AuthConstants.NICKNAME, member.nickname)
            .claim(AuthConstants.ROLE, member.role.name)
            .claim(AuthConstants.OAUTH2_PROVIDER, member.oAuth2Provider.name)
            .claim(AuthConstants.EMAIL_ADDRESS, member.emailAddress)
            .claim(AuthConstants.EMAIL_VERIFIED, member.emailVerified)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + accessTokenValidMinute.toLong() * 2 * 50))
            .signWith(key)
            .compact()
    }

    fun generateRefreshToken(member: Member): String {
        return Jwts.builder()
            .claim(AuthConstants.ID, member.id)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + refreshTokenValidHour.toLong() * 60 * 60 * 1000))
            .signWith(key)
            .compact()
    }

    fun getFreshAccessToken(refreshToken: String?): String {
        val claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(refreshToken)
            .payload

        val id = (claims[AuthConstants.ID] as Int?)!!.toLong()
        val member = memberRepository.findById(id)
            .orElseThrow()

        return generateAccessToken(member)
    }

    fun getClaims(accessToken: String?): Map<String, Any>? {
        if (accessToken.isNullOrBlank()) {
            return null
        }
        try {
            val claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(accessToken)
                .payload
            return HashMap(claims)
        } catch (e: UnsupportedJwtException) {
            throw JwtException(e.localizedMessage)
        } catch (e: MalformedJwtException) {
            throw JwtException(e.localizedMessage)
        }
    }


    fun generateTempToken(tempUserBeforeSignUp: TempUserBeforeSignUp): String {
        return Jwts.builder()
            .claim(AuthConstants.OAUTH2_ID, tempUserBeforeSignUp.oAuth2Id)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + accessTokenValidMinute.toLong() * 60 * 1000))
            .signWith(key)
            .compact()
    }
}
