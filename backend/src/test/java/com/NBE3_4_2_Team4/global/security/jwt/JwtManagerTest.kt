package com.NBE3_4_2_Team4.global.security.jwt

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository
import com.NBE3_4_2_Team4.standard.constants.AuthConstants
import com.nimbusds.jose.util.Base64
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import java.util.*
import javax.crypto.SecretKey

class JwtManagerTest {
    companion object {
        private var jwtKey: String? = null

        private var secretKey: SecretKey? = null

        @JvmStatic
        @BeforeAll
        fun setup(): Unit {
            val stringBuilder = StringBuilder()
            val key = "test jwt secret key"
            stringBuilder.repeat(key, 20)
            val repeatedKey = stringBuilder.toString()
            jwtKey = Base64.encode(repeatedKey).toString()
            secretKey = Keys.hmacShaKeyFor(repeatedKey.toByteArray())
        }
    }

    private var jwtManager: JwtManager? = null

    @Mock
    private val memberRepository: MemberRepository = mock(MemberRepository::class.java)

    private var member: Member? = null

    @BeforeEach
    fun setUp() {
        jwtManager = JwtManager(jwtKey!!, 30, 24, memberRepository)
        member = Member(
            id = 1,
            username = "test username",
            nickname = "test nickname",
            role = Member.Role.USER,
            oAuth2Provider = Member.OAuth2Provider.NONE
        )
    }

    @Test
    fun generateAccessTokenTest() {
        val accessToken = jwtManager!!.generateAccessToken(member!!)
        val claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(accessToken)
            .payload

        Assertions.assertEquals(
            member!!.id,
            (claims[AuthConstants.ID] as Int?)!!.toLong()
        )
        Assertions.assertEquals(member!!.username, claims[AuthConstants.USERNAME])
        Assertions.assertEquals(member!!.nickname, claims[AuthConstants.NICKNAME])
        Assertions.assertEquals(member!!.role.name, claims[AuthConstants.ROLE])
        Assertions.assertEquals(
            member!!.oAuth2Provider.name,
            claims[AuthConstants.OAUTH2_PROVIDER]
        )

        Assertions.assertNotNull(claims.issuedAt)
        Assertions.assertNotNull(claims.expiration)
    }

    @Test
    fun generateRefreshTokenTest() {
        val refreshToken = jwtManager!!.generateRefreshToken(member!!)
        val claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(refreshToken)
            .payload

        Assertions.assertEquals(
            member!!.id,
            (claims[AuthConstants.ID] as Int?)!!.toLong()
        )

        Assertions.assertNull(claims[AuthConstants.USERNAME])
        Assertions.assertNull(claims[AuthConstants.NICKNAME])
        Assertions.assertNull(claims[AuthConstants.ROLE])
        Assertions.assertNull(claims[AuthConstants.OAUTH2_PROVIDER])

        Assertions.assertNotNull(claims.issuedAt)
        Assertions.assertNotNull(claims.expiration)
    }

    @Test
    fun freshAccessTokenTest(){
            val refreshToken = jwtManager!!.generateRefreshToken(member!!)

            Mockito.`when`(memberRepository.findById(1L))
                .thenReturn(Optional.of(member!!))

            val freshAccessToken = jwtManager!!.getFreshAccessToken(refreshToken)

            val claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(freshAccessToken)
                .payload

            Assertions.assertEquals(
                member!!.id,
                (claims[AuthConstants.ID] as Int?)!!.toLong()
            )
            Assertions.assertEquals(member!!.username, claims[AuthConstants.USERNAME])
            Assertions.assertEquals(member!!.nickname, claims[AuthConstants.NICKNAME])
            Assertions.assertEquals(member!!.role.name, claims[AuthConstants.ROLE])
            Assertions.assertEquals(
                member!!.oAuth2Provider.name,
                claims[AuthConstants.OAUTH2_PROVIDER]
            )

            Assertions.assertNotNull(claims.issuedAt)
            Assertions.assertNotNull(claims.expiration)
        }

    @Test
    fun claimsTest1() {
        Assertions.assertNull(jwtManager!!.getClaims(null))
    }

    @Test
    fun claimsTest2() {
        Assertions.assertNull(jwtManager!!.getClaims("  "))
    }

    @Test
    fun claimsTest3() {
        val invalidToken = String.format("%s salted", jwtManager!!.generateAccessToken(member!!))
        Assertions.assertThrows(
            JwtException::class.java
        ) { jwtManager!!.getClaims(invalidToken) }
    }

    @Test
    fun claimsTest4() {
        val expiredToken = Jwts.builder()
            .claim(AuthConstants.ID, member!!.id)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() - 1000))
            .signWith(secretKey)
            .compact()

        Assertions.assertThrows(
            JwtException::class.java
        ) { jwtManager!!.getClaims(expiredToken) }
    }

    @Test
    fun claimsTest5() {
        val malformedToken = "invalid token"

        Assertions.assertThrows(
            JwtException::class.java
        ) { jwtManager!!.getClaims(malformedToken) }
    }
}