package com.NBE3_4_2_Team4.global.security.filter

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.NBE3_4_2_Team4.domain.asset.point.dto.PointTransferReq
import com.NBE3_4_2_Team4.domain.board.question.dto.request.QuestionWriteReqDto
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.rsData.RsData
import com.NBE3_4_2_Team4.global.security.jwt.JwtManager
import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.OAuth2LogoutService
import com.NBE3_4_2_Team4.standard.constants.AuthConstants
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.Cookie
import jakarta.transaction.Transactional
import lombok.extern.slf4j.Slf4j
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*
import javax.crypto.SecretKey

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class CustomJwtFilterTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoSpyBean
    private val jwtManager: JwtManager? = null

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private var member: Member? = null

    private var admin: Member? = null


    @Value("\${custom.jwt.accessToken.validMinute}")
    var accessTokenValidMinute : Int = 30

    @Value("\${custom.initData.member.admin.username}")
    lateinit var adminUsername: String

    @Value("\${custom.initData.member.admin.nickname}")
    lateinit var adminNickname: String

    @Value("\${custom.initData.member.admin.email}")
    lateinit var adminEmail: String


    @Value("\${custom.initData.member.member1.username}")
    lateinit var member1Username: String

    @Value("\${custom.initData.member.member1.nickname}")
    lateinit var member1Nickname: String

    @Value("\${custom.initData.member.member1.email}")
    lateinit var member1Email: String

    @Value("\${custom.domain.backend}")
    lateinit var backendDomain: String

    @Value("\${custom.jwt.secretKey:key}")
    lateinit var jwtSecretKey: String

    var key: SecretKey? = null

    @BeforeEach
    fun setUp() {
        member = Member(
            id = 1,
            username = member1Username,
            nickname = member1Nickname,
            role = Member.Role.USER,
            oAuth2Provider = Member.OAuth2Provider.NONE,
            emailAddress = member1Email)

        admin = Member(
            id = 2,
            username = adminUsername,
            nickname = adminNickname,
            role = Member.Role.ADMIN,
            oAuth2Provider = Member.OAuth2Provider.NONE,
            emailAddress = adminEmail)

        val keyBytes = Base64.getDecoder().decode(jwtSecretKey)
        key = Keys.hmacShaKeyFor(keyBytes)
    }

    @Test
    @DisplayName("필터 안 걸려있는 url 에 대한 get 테스트")
    @Throws(Exception::class)
    fun testCustomJwtFilter1() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/questions")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
    }

    @Test
    @DisplayName("필터 걸려있는 url - api/questions 에 대한 post 테스트 - 헤더에 JWT 없는 경우 (인증 실패)")
    @Throws(
        Exception::class
    )
    fun testCustomJwtFilter2() {
        val reqBody = QuestionWriteReqDto("test title", "test content", 1L, 100, AssetType.POINT)
        val body = objectMapper.writeValueAsString(reqBody)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/questions")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }

    @Test
    @DisplayName("필터 걸려있는 url - api/questions 에 대한 post 테스트 - 헤더에 JWT 있는 경우 (인증 성공)")
    @Throws(
        Exception::class
    )
    fun testCustomJwtFilter3() {
        val jwtToken = jwtManager!!.generateAccessToken(member!!)
        val accessToken = Cookie("accessToken", jwtToken)

        val reqBody = QuestionWriteReqDto("test title", "test content", 1L, 100, AssetType.POINT)
        val body = objectMapper.writeValueAsString(reqBody)


        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/questions")
                .header("Authorization", String.format("Bearer %s", jwtToken))
                .cookie(accessToken)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    @DisplayName("필터 걸려있는 url - admin/test 에 대한 post 테스트 - 헤더에 JWT 없는 경우 (인증 실패)")
    @Throws(
        Exception::class
    )
    fun testCustomJwtFilter6() {
        val responseData = RsData("401-1", "Unauthorized", "인증이 필요합니다.")

        val expectedJson = objectMapper.writeValueAsString(responseData)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/admin/products//accumulate")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.content().json(expectedJson))
    }

    @Test
    @DisplayName("필터 걸려있는 url - admin/test 에 대한 post 테스트 - 헤더에 일반 유저의 JWT 있는 경우 (인증 성공, 인가 실패)")
    @Throws(
        Exception::class
    )
    fun testCustomJwtFilter7() {
        val jwtToken = jwtManager!!.generateAccessToken(member!!)
        val accessToken = Cookie("accessToken", jwtToken)

        val responseData = RsData("403-1", "Forbidden", "권한이 부족합니다.")


        val expectedJson = objectMapper.writeValueAsString(responseData)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/admin/products//accumulate")
                .header("Authorization", String.format("Bearer %s", jwtToken))
                .cookie(accessToken)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isForbidden())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.content().json(expectedJson))
    }

    @Test
    @DisplayName("필터 걸려있는 url - api/point/products/accumulate 에 대한 post 테스트 - 헤더에 관리자의 JWT 있는 경우 (인증, 인가 성공)")
    @Throws(
        Exception::class
    )
    fun testCustomJwtFilter8() {
        val jwtToken = jwtManager!!.generateAccessToken(admin!!)
        val accessToken = Cookie("accessToken", jwtToken)

        val pointTransferReq = PointTransferReq("test@test.com", 1L)
        val body = objectMapper.writeValueAsString(pointTransferReq)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/admin/points/accumulate")
                .header("Authorization", String.format("Bearer %s", jwtToken))
                .cookie(accessToken)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
    }

    @Test
    @DisplayName("로그아웃 성공 테스트 - 헤더에 사용자의 JWT 있는 경우")
    @Throws(
        Exception::class
    )
    fun testCustomJwtFilter9() {
        val jwtToken = jwtManager!!.generateAccessToken(member!!)
        val accessToken = Cookie("accessToken", jwtToken)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/logout")
                .header("Authorization", String.format("Bearer %s", jwtToken))
                .cookie(accessToken)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data").value(backendDomain + OAuth2LogoutService.LOGOUT_COMPLETE_URL)
            ) // 응답 JSON의 data 필드 확인
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    @DisplayName("로그아웃 실패 테스트 - 헤더에 사용자의 JWT 없는 경우 (로그인 되어 있지 않은 경우)")
    @Throws(
        Exception::class
    )
    fun testCustomJwtFilter10() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/logout")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
    }


    @Test
    @DisplayName("만료된 액세스 토큰 테스트 - 리프레시 토큰 없을 때")
    @Throws(Exception::class)
    fun testCustomJwtFilter11() {
        Mockito.`when`<String>(jwtManager!!.generateAccessToken(member!!))
            .thenReturn(
                Jwts.builder()
                    .claim(AuthConstants.ID, member!!.id)
                    .claim(AuthConstants.USERNAME, member!!.username)
                    .claim(AuthConstants.NICKNAME, member!!.nickname)
                    .claim(AuthConstants.ROLE, member!!.role.name)
                    .claim(AuthConstants.OAUTH2_PROVIDER, member!!.oAuth2Provider.name)
                    .claim(AuthConstants.EMAIL_ADDRESS, member!!.emailAddress)
                    .claim(AuthConstants.EMAIL_VERIFIED, member!!.emailVerified)
                    .issuedAt(Date())
                    .expiration(Date(System.currentTimeMillis() - accessTokenValidMinute.toLong() * 2 * 50)) // 현재보다 과거
                    .signWith(key)
                    .compact()
            )

        val jwtToken = jwtManager.generateAccessToken(member!!)
        val accessToken = Cookie("accessToken", jwtToken)

        val reqBody = QuestionWriteReqDto("test title", "test content", 1L, 100, AssetType.POINT)
        val body = objectMapper.writeValueAsString(reqBody)


        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/questions")
                .header("Authorization", String.format("Bearer %s", jwtToken))
                .cookie(accessToken)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andExpect(MockMvcResultMatchers.cookie().doesNotExist("accessToken"))
            .andDo(MockMvcResultHandlers.print())

        Mockito.verify(jwtManager, Mockito.times(0)).getFreshAccessToken(ArgumentMatchers.any())
    }

    @Test
    @DisplayName("만료된 액세스 토큰 테스트 - 리프레시 토큰 있을 때")
    @Throws(Exception::class)
    fun testCustomJwtFilter12() {
        Mockito.`when`<String>(jwtManager!!.generateAccessToken(member!!))
            .thenReturn(
                Jwts.builder()
                    .claim(AuthConstants.ID, member!!.id)
                    .claim(AuthConstants.USERNAME, member!!.username)
                    .claim(AuthConstants.NICKNAME, member!!.nickname)
                    .claim(AuthConstants.ROLE, member!!.role.name)
                    .claim(AuthConstants.OAUTH2_PROVIDER, member!!.oAuth2Provider.name)
                    .claim(AuthConstants.EMAIL_ADDRESS, member!!.emailAddress)
                    .claim(AuthConstants.EMAIL_VERIFIED, member!!.emailVerified)
                    .issuedAt(Date())
                    .expiration(Date(System.currentTimeMillis() - accessTokenValidMinute.toLong() * 2 * 50)) // 현재보다 과거
                    .signWith(key)
                    .compact()
            )

        val accessToken = jwtManager.generateAccessToken(member!!)
        val accessTokenCookie = Cookie("accessToken", accessToken)

        val refreshToken = jwtManager.generateRefreshToken(member!!)
        val refreshTokenCookie = Cookie("refreshToken", refreshToken)

        Mockito.doReturn(
            Jwts.builder()
                .claim(AuthConstants.ID, member!!.id)
                .claim(AuthConstants.USERNAME, member!!.username)
                .claim(AuthConstants.NICKNAME, member!!.nickname)
                .claim(AuthConstants.ROLE, member!!.role.name)
                .claim(AuthConstants.OAUTH2_PROVIDER, member!!.oAuth2Provider.name)
                .claim(AuthConstants.EMAIL_ADDRESS, member!!.emailAddress)
                .claim(AuthConstants.EMAIL_VERIFIED, member!!.emailVerified)
                .issuedAt(Date())
                .expiration(Date(System.currentTimeMillis() + accessTokenValidMinute.toLong() * 2 * 50))
                .signWith(key)
                .compact()
        ).`when`<JwtManager?>(jwtManager).getFreshAccessToken(ArgumentMatchers.eq<String>(refreshToken))


        val reqBody = QuestionWriteReqDto("test title", "test content", 1L, 100, AssetType.POINT)
        val body = objectMapper.writeValueAsString(reqBody)


        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/questions")
                .header("Authorization", String.format("Bearer %s", accessToken))
                .cookie(accessTokenCookie, refreshTokenCookie)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.cookie().exists("accessToken"))
            .andDo(MockMvcResultHandlers.print())

        Mockito.verify(jwtManager, Mockito.times(1)).getFreshAccessToken(ArgumentMatchers.eq(refreshToken))
    }

    @Test
    @DisplayName("이메일 인증 필터링 테스트 - 헤더에 사용자의 JWT 없는 경우 (로그인 되어 있지 않은 경우) 401")
    @Throws(
        Exception::class
    )
    fun testFilterWithEmailVerified1() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/test")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    @DisplayName("이메일 인증 필터링 테스트 - 이메일이 인증되지 않았을 경우 403")
    @Throws(
        Exception::class
    )
    fun testFilterWithEmailVerified2() {
        val accessToken = jwtManager!!.generateAccessToken(member!!)
        val accessTokenCookie = Cookie("accessToken", accessToken)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/test")
                .header("Authorization", String.format("Bearer %s", accessToken))
                .cookie(accessTokenCookie)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isForbidden())
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    @DisplayName("이메일 인증 필터링 테스트 - 이메일이 인증된 경우 200")
    @Throws(
        Exception::class
    )
    fun testFilterWithEmailVerified3() {
        member!!.emailVerified = true
        val accessToken = jwtManager!!.generateAccessToken(member!!)
        val accessTokenCookie = Cookie("accessToken", accessToken)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/test")
                .header("Authorization", String.format("Bearer %s", accessToken))
                .cookie(accessTokenCookie)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print())
    }
}