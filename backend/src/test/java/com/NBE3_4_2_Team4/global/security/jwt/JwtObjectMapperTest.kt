package com.NBE3_4_2_Team4.global.security.jwt

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.standard.constants.AuthConstants
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.test.context.ActiveProfiles
import java.util.stream.Stream

@ActiveProfiles("test")
class JwtObjectMapperTest {
    private var jwtObjectMapper: JwtObjectMapper? = null

    private var claims: MutableMap<String, Any> = HashMap<String, Any>()

    @BeforeEach
    fun setUp() {
        jwtObjectMapper = JwtObjectMapper()
        claims = HashMap()
        claims[AuthConstants.ID] = 1
        claims[AuthConstants.USERNAME] = "testUser"
        claims[AuthConstants.NICKNAME] = "testNick"
        claims[AuthConstants.ROLE] = "USER"
        claims[AuthConstants.OAUTH2_PROVIDER] = "NONE"
        claims[AuthConstants.EMAIL_ADDRESS] = "testEmail"
        claims[AuthConstants.EMAIL_VERIFIED] = true
    }

    @AfterEach
    fun tearDown() {
        claims.clear()
    }

    @Test
    fun memberByJwtClaimsTest() {
        val member = jwtObjectMapper!!.getMemberByJwtClaims(
            claims
        )

        Assertions.assertNotNull(member)
        Assertions.assertEquals(1L, member.id)
        Assertions.assertEquals("testUser", member.username)
        Assertions.assertEquals("testNick", member.nickname)
        Assertions.assertEquals(Member.Role.USER, member.role)
        Assertions.assertEquals(Member.OAuth2Provider.NONE, member.oAuth2Provider)
        Assertions.assertTrue(member.emailVerified)
    }

    @Test
    fun memberByJwtClaims_NullId() {
        claims.remove(AuthConstants.ID)

        val exception: Exception = Assertions.assertThrows(
            RuntimeException::class.java
        ) { jwtObjectMapper!!.getMemberByJwtClaims(claims) }
        Assertions.assertEquals("Invalid claims", exception.message)
    }

    companion object {
        @JvmStatic
        fun provideInvalidClaims(): Stream<String> {
            return Stream.of(
                AuthConstants.USERNAME,
                AuthConstants.NICKNAME,
                AuthConstants.ROLE,
                AuthConstants.OAUTH2_PROVIDER
            )
        }
    }

    @ParameterizedTest
    @MethodSource("provideInvalidClaims")
    fun getMemberByJwtClaims_InvalidClaims_Null(invalidKey: String) {
        claims.remove(invalidKey)

        val exception: Exception = Assertions.assertThrows(
            RuntimeException::class.java
        ) { jwtObjectMapper!!.getMemberByJwtClaims(claims) }
        Assertions.assertEquals("Invalid claims", exception.message)
    }

    @ParameterizedTest
    @MethodSource("provideInvalidClaims")
    fun getMemberByJwtClaims_InvalidClaims_Blank(invalidKey: String) {
        claims[invalidKey] = ""

        val exception: Exception = Assertions.assertThrows(
            RuntimeException::class.java
        ) { jwtObjectMapper!!.getMemberByJwtClaims(claims) }
        Assertions.assertEquals("Invalid claims", exception.message)
    }
}