package com.NBE3_4_2_Team4.domain.member.member.service

import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository
import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer
import com.NBE3_4_2_Team4.domain.board.question.entity.Question
import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.entity.OAuth2RefreshToken
import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.repository.OAuth2RefreshTokenRepository
import com.NBE3_4_2_Team4.domain.member.member.dto.AdminLoginRequestDto
import com.NBE3_4_2_Team4.domain.member.member.dto.NicknameUpdateRequestDto
import com.NBE3_4_2_Team4.domain.member.member.dto.SignupRequestDto
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Point
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberQuerydsl
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository
import com.NBE3_4_2_Team4.global.exceptions.InValidPasswordException
import com.NBE3_4_2_Team4.global.exceptions.MemberNotFoundException
import com.NBE3_4_2_Team4.global.mail.service.MailService
import com.NBE3_4_2_Team4.global.security.oauth2.OAuth2Manager
import com.NBE3_4_2_Team4.global.security.oauth2.disconnectService.impl.GoogleDisconnectService
import com.NBE3_4_2_Team4.global.security.oauth2.disconnectService.impl.KaKaoDisconnectService
import com.NBE3_4_2_Team4.global.security.oauth2.disconnectService.impl.NaverDisconnectService
import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.impl.DefaultLogoutService
import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.impl.GoogleLogoutService
import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.impl.KakaoLogoutService
import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.impl.NaverLogoutService
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfo
import com.NBE3_4_2_Team4.global.security.user.tempUserBeforeSignUp.TempUserBeforeSignUp
import com.NBE3_4_2_Team4.global.security.user.tempUserBeforeSignUp.TempUserBeforeSignUpService
import com.NBE3_4_2_Team4.standard.constants.PointConstants
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
class MemberServiceTest {
    private val memberRepository: MemberRepository = mock(MemberRepository::class.java)
    private val memberQuerydsl: MemberQuerydsl = mock(MemberQuerydsl::class.java)
    private val oAuth2Manager: OAuth2Manager = mock(OAuth2Manager::class.java)
    private val assetHistoryRepository: AssetHistoryRepository = mock(AssetHistoryRepository::class.java)
    private val passwordEncoder: PasswordEncoder = mock(PasswordEncoder::class.java)
    private val oAuth2RefreshTokenRepository: OAuth2RefreshTokenRepository = mock(OAuth2RefreshTokenRepository::class.java)
    private val tempUserBeforeSignUpService: TempUserBeforeSignUpService = mock(TempUserBeforeSignUpService::class.java)
    private val mailService: MailService = mock(MailService::class.java)


    private val memberService: MemberService = MemberService(
        memberRepository, memberQuerydsl,  assetHistoryRepository, passwordEncoder,oAuth2Manager, oAuth2RefreshTokenRepository, tempUserBeforeSignUpService, mailService)


    private val defaultLogoutService: DefaultLogoutService = mock(DefaultLogoutService::class.java)

    private val kakaoLogoutService: KakaoLogoutService = mock(KakaoLogoutService::class.java)

    private val naverLogoutService: NaverLogoutService = mock(NaverLogoutService::class.java)

    private val googleLogoutService: GoogleLogoutService = mock(GoogleLogoutService::class.java)

    private val kaKaoDisconnectService: KaKaoDisconnectService = mock(KaKaoDisconnectService::class.java)

    private val naverDisconnectService: NaverDisconnectService = mock(NaverDisconnectService::class.java)

    private val googleDisconnectService: GoogleDisconnectService = mock(GoogleDisconnectService::class.java)


    private val username = "test username"
    private val password = "test password"
    private val nickname = "test nickname"
    private val role = Member.Role.USER
    private val oAuth2Provider = Member.OAuth2Provider.NONE

    private var member: Member? = null

    @BeforeEach
    fun setUp() {
        member = Member(
            id = 1L,
            role = role,
            oAuth2Provider = oAuth2Provider,
            username = username,
            password = password,
            nickname = nickname,
            point = (Point(PointConstants.INITIAL_POINT)),
            questions = (ArrayList<Question>()),
            answers = ArrayList<Answer>())
    }


    @Test
    fun adminLoginTest1() {
        member!!.role = Member.Role.ADMIN

        val adminLoginRequestDto: AdminLoginRequestDto = AdminLoginRequestDto(
            adminUsername = username,
            password = password)

        Mockito.`when`(memberRepository.findByUsername(username))
            .thenReturn(member)

        Mockito.`when`(passwordEncoder.matches(ArgumentMatchers.any(), ArgumentMatchers.any()))
            .thenReturn(true)

        val member = memberService.adminLogin(adminLoginRequestDto)
        Assertions.assertNotNull(member)
        Assertions.assertEquals(member.username, username)
        Assertions.assertEquals(member.password, password)
        Mockito.verify(memberRepository, Mockito.times(1)).findByUsername(username)
    }


    @Test
    fun adminLoginTest2() {
        val adminLoginRequestDto: AdminLoginRequestDto = AdminLoginRequestDto(
            adminUsername = username,
            password = password)

        Mockito.`when`(memberRepository.findByUsername(username))
            .thenReturn(null)

        assertThrows(MemberNotFoundException::class.java) {
            memberService.adminLogin(
                adminLoginRequestDto
            )
        }
        Mockito.verify(memberRepository, Mockito.times(1)).findByUsername(username)
    }

    @Test
    fun adminLoginTest3() {
        val adminLoginRequestDto: AdminLoginRequestDto = AdminLoginRequestDto(
            adminUsername = username,
            password = password)

        Mockito.`when`(memberRepository.findByUsername(username))
            .thenReturn(member)

        Mockito.`when`(passwordEncoder.matches(ArgumentMatchers.any(), ArgumentMatchers.any()))
            .thenReturn(false)

        assertThrows(InValidPasswordException::class.java) {
            memberService.adminLogin(
                adminLoginRequestDto
            )
        }
        Mockito.verify(memberRepository, Mockito.times(1)).findByUsername(username)
    }

    @Test
    fun adminLoginTest4() {
        val adminLoginRequestDto: AdminLoginRequestDto = AdminLoginRequestDto(
            adminUsername = username,
            password = password)

        Mockito.`when`(memberRepository.findByUsername(username))
            .thenReturn(member)

        Mockito.`when`(passwordEncoder.matches(ArgumentMatchers.any(), ArgumentMatchers.any()))
            .thenReturn(true)

        assertThrows(
            RuntimeException::class.java
        ) { memberService.adminLogin(adminLoginRequestDto) }
        Mockito.verify(memberRepository, Mockito.times(1)).findByUsername(username)
    }


    @ParameterizedTest
    @EnumSource(value = Member.OAuth2Provider::class, names = ["NONE", "KAKAO", "GOOGLE", "NAVER"])
    fun getLogoutUrlTest2(provider: Member.OAuth2Provider) {
        member!!.oAuth2Provider = provider
        val testMember = member

        Mockito.`when`(memberRepository.existsById(1L)).thenReturn(true)

        Mockito.`when`(oAuth2Manager.getOAuth2LogoutService(provider)).thenReturn(
            when (provider) {
                Member.OAuth2Provider.NONE -> defaultLogoutService
                Member.OAuth2Provider.KAKAO -> kakaoLogoutService
                Member.OAuth2Provider.NAVER -> naverLogoutService
                Member.OAuth2Provider.GOOGLE -> googleLogoutService
            }
        )



        val logoutUrl = String.format("Logout url for %s", provider.name)

        Mockito.`when`<String>(
            when (provider) {
                Member.OAuth2Provider.NONE -> defaultLogoutService.getLogoutUrl()
                Member.OAuth2Provider.KAKAO -> kakaoLogoutService.getLogoutUrl()
                Member.OAuth2Provider.NAVER -> naverLogoutService.getLogoutUrl()
                Member.OAuth2Provider.GOOGLE -> googleLogoutService.getLogoutUrl()
            }
        )
            .thenReturn(logoutUrl)

        val actualLogoutUrl = memberService.getLogoutUrl((testMember)!!)
        Assertions.assertEquals(logoutUrl, actualLogoutUrl)
    }

    @ParameterizedTest
    @EnumSource(value = Member.OAuth2Provider::class, names = ["KAKAO", "GOOGLE", "NAVER"])
    fun getLogoutUrlTest3(provider: Member.OAuth2Provider?) {
        Mockito.`when`(memberRepository.existsById(1L)).thenReturn(true)

        Mockito.`when`(oAuth2Manager.getOAuth2LogoutService((provider)!!))
            .thenReturn(null)

        member!!.oAuth2Provider = (provider)
        val testMember = member

        assertThrows(
            RuntimeException::class.java
        ) { memberService.getLogoutUrl((testMember)!!) }
        Mockito.verify(oAuth2Manager, Mockito.times(1))
            .getOAuth2LogoutService((provider))
    }

    @Test
    fun updateNicknameTest1() {
        Mockito.`when`(memberRepository.existsById(1L)).thenReturn(true)

        Mockito.`when`(memberRepository.findById(ArgumentMatchers.any()))
            .thenReturn(Optional.empty())

        val nicknameUpdateRequestDto = NicknameUpdateRequestDto("new nickname")
        assertThrows(
            RuntimeException::class.java
        ) {
            memberService.updateNickname(
                (member)!!,
                nicknameUpdateRequestDto
            )
        }
    }

    @Test
    fun updateNicknameTest2() {
        Mockito.`when`(memberRepository.existsById(1L)).thenReturn(true)

        Mockito.`when`(memberRepository.findById(ArgumentMatchers.any()))
            .thenReturn(Optional.of((member)!!))
        Assertions.assertEquals("test nickname", member!!.nickname)

        val nicknameUpdateRequestDto = NicknameUpdateRequestDto("new nickname")
        memberService.updateNickname((member)!!, nicknameUpdateRequestDto)

        Assertions.assertEquals("new nickname", member!!.nickname)
    }


    @Test
    fun withdrawTest1() {
        Mockito.`when`(memberRepository.existsById(1L))
            .thenReturn(false)

        assertThrows(
            RuntimeException::class.java
        ) { memberService.withdrawalMembership((member)!!) }
    }

    @Test
    fun withdrawTest2() {
        Mockito.`when`(memberRepository.existsById(1L))
            .thenReturn(true)

        memberService.withdrawalMembership((member)!!)

        Mockito.verify(oAuth2RefreshTokenRepository, Mockito.times(0))
            .findByMember(ArgumentMatchers.any())

        Mockito.verify(oAuth2Manager, Mockito.times(0))
            .getOAuth2DisconnectService(ArgumentMatchers.any())

        Mockito.verify(memberQuerydsl, Mockito.times(1))
            .deleteMember(ArgumentMatchers.anyLong())
    }

    @ParameterizedTest
    @EnumSource(value = Member.OAuth2Provider::class, names = ["KAKAO", "GOOGLE", "NAVER"])
    fun withdrawTest(provider: Member.OAuth2Provider) {
        Mockito.`when`(memberRepository.existsById(1L))
            .thenReturn(true)

        member!!.oAuth2Provider = provider
        val testMember = member

        val oAuth2RefreshToken = OAuth2RefreshToken(
            1L,
            (testMember)!!,
            "asd",
            provider.name.lowercase(Locale.getDefault()) + " token"
        )

        Mockito.`when`(
            oAuth2RefreshTokenRepository.findByMember(
                (testMember)
            )
        )
            .thenReturn(oAuth2RefreshToken)

        Mockito.`when`(oAuth2Manager.getOAuth2DisconnectService(provider)).thenReturn(
            when (provider) {
                Member.OAuth2Provider.NONE -> null
                Member.OAuth2Provider.KAKAO -> kaKaoDisconnectService
                Member.OAuth2Provider.NAVER -> naverDisconnectService
                Member.OAuth2Provider.GOOGLE -> googleDisconnectService
            }
        )

        Mockito.`when`<Boolean>(
            when (provider) {
                Member.OAuth2Provider.NONE -> null
                Member.OAuth2Provider.KAKAO -> kaKaoDisconnectService.disconnectSuccess(ArgumentMatchers.any<String>())
                Member.OAuth2Provider.NAVER -> naverDisconnectService.disconnectSuccess(ArgumentMatchers.any<String>())
                Member.OAuth2Provider.GOOGLE -> googleDisconnectService.disconnectSuccess(ArgumentMatchers.any<String>())
            }
        )
            .thenReturn(true)

        Assertions.assertDoesNotThrow {
            memberService.withdrawalMembership(
                (testMember)
            )
        }

        Mockito.verify(oAuth2RefreshTokenRepository, Mockito.times(1))
            .findByMember(ArgumentMatchers.any())
        Mockito.verify(oAuth2Manager, Mockito.times(1))
            .getOAuth2DisconnectService(ArgumentMatchers.any())
        Mockito.verify(memberQuerydsl, Mockito.times(1))
            .deleteMember(ArgumentMatchers.anyLong())
    }

    @ParameterizedTest
    @EnumSource(value = Member.OAuth2Provider::class, names = ["KAKAO", "GOOGLE", "NAVER"])
    fun withdrawTest3(provider: Member.OAuth2Provider) {
        Mockito.`when`(memberRepository.existsById(1L))
            .thenReturn(true)

        member!!.oAuth2Provider = provider
        val testMember = member

        val oAuth2RefreshToken = OAuth2RefreshToken(
            1L,
            (testMember)!!,
            "asd",
            provider.name.lowercase(Locale.getDefault()) + " token"
        )

        Mockito.`when`(
            oAuth2RefreshTokenRepository.findByMember(
                (testMember)
            )
        )
            .thenReturn(oAuth2RefreshToken)

        Mockito.`when`(oAuth2Manager.getOAuth2DisconnectService(provider)).thenReturn(null)

        Assertions.assertDoesNotThrow {
            memberService.withdrawalMembership(
                (testMember)
            )
        }


        Mockito.verify(oAuth2RefreshTokenRepository, Mockito.times(1))
            .findByMember(ArgumentMatchers.any())
        Mockito.verify(oAuth2Manager, Mockito.times(1))
            .getOAuth2DisconnectService(ArgumentMatchers.any())
        Mockito.verify(memberRepository, Mockito.times(0))
            .deleteById(ArgumentMatchers.any())
    }

    @ParameterizedTest
    @EnumSource(value = Member.OAuth2Provider::class, names = ["KAKAO", "GOOGLE", "NAVER"])
    fun withdrawTest4(provider: Member.OAuth2Provider) {
        Mockito.`when`(memberRepository.existsById(1L))
            .thenReturn(true)

        member!!.oAuth2Provider = provider
        val testMember = member

        val oAuth2RefreshToken = OAuth2RefreshToken(
            1L,
            (testMember)!!,
            "asd",
            provider.name.lowercase(Locale.getDefault()) + " token"
        )

        Mockito.`when`(
            oAuth2RefreshTokenRepository.findByMember(
                (testMember)
            )
        )
            .thenReturn(oAuth2RefreshToken)

        Mockito.`when`(oAuth2Manager.getOAuth2DisconnectService(provider)).thenReturn(
        when (provider) {
            Member.OAuth2Provider.NONE -> null
            Member.OAuth2Provider.KAKAO -> kaKaoDisconnectService
            Member.OAuth2Provider.NAVER -> naverDisconnectService
            Member.OAuth2Provider.GOOGLE -> googleDisconnectService
        })

        Mockito.`when`<Boolean>(
            when (provider) {
                Member.OAuth2Provider.NONE -> null
                Member.OAuth2Provider.KAKAO -> kaKaoDisconnectService.disconnectSuccess(ArgumentMatchers.any<String>())
                Member.OAuth2Provider.NAVER -> naverDisconnectService.disconnectSuccess(ArgumentMatchers.any<String>())
                Member.OAuth2Provider.GOOGLE -> googleDisconnectService.disconnectSuccess(ArgumentMatchers.any<String>())
            }
        )
            .thenReturn(false)

        Assertions.assertDoesNotThrow {
            memberService.withdrawalMembership(
                (testMember)
            )
        }


        Mockito.verify(oAuth2RefreshTokenRepository, Mockito.times(1))
            .findByMember(ArgumentMatchers.any())
        Mockito.verify(oAuth2Manager, Mockito.times(1))
            .getOAuth2DisconnectService(ArgumentMatchers.any())
        Mockito.verify(memberRepository, Mockito.times(0))
            .deleteById(ArgumentMatchers.any())
    }

    @Test
    fun isNicknameDuplicateTest1() {
        Mockito.`when`(memberRepository.existsByUsername(member!!.username)).thenReturn(true)

        Assertions.assertFalse(memberService.isNicknameAvailable(member!!.username))
    }

    @Test
    fun isNicknameDuplicateTest2() {
        Mockito.`when`(memberRepository.existsByUsername(member!!.username)).thenReturn(false)

        Assertions.assertTrue(memberService.isNicknameAvailable(member!!.username))
    }

    @Test
    fun signUpTest1() {
        val tempToken = "tempToken"

        val oAuth2UserInfo = OAuth2UserInfo("oAuth2Id", "realName")
        val tempUserBeforeSignUp =
            TempUserBeforeSignUp(oAuth2UserInfo, Member.OAuth2Provider.KAKAO.name, "refreshToken")

        Mockito.`when`(tempUserBeforeSignUpService.getTempUserFromRedisWithJwt(tempToken))
            .thenReturn(tempUserBeforeSignUp)


        val signupRequestDto: SignupRequestDto = SignupRequestDto(
            email = "test@test.com",
            nickname = nickname
        )

        Mockito.`when`<Any?>(memberRepository.saveAndFlush(ArgumentMatchers.any())).thenReturn(member)
        Mockito.`when`(memberRepository.existsById(1L)).thenReturn(true)

        Assertions.assertDoesNotThrow {
            memberService.signUp(
                tempToken,
                signupRequestDto
            )
        }

        Mockito.verify(tempUserBeforeSignUpService, Mockito.times(1)).getTempUserFromRedisWithJwt(tempToken)
        Mockito.verify(tempUserBeforeSignUpService, Mockito.times(1)).deleteTempUserFromRedis(tempToken)
        Mockito.verify(tempUserBeforeSignUpService, Mockito.times(1))
            .saveAuthCodeForMember(ArgumentMatchers.eq(1L), ArgumentMatchers.any())

        Mockito.verify(memberRepository, Mockito.times(1)).saveAndFlush(ArgumentMatchers.any())
        Mockito.verify(memberRepository, Mockito.times(1)).existsById(1L)

        Mockito.verify(mailService, Mockito.times(1))
            .sendAuthenticationMail(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
    }
}
