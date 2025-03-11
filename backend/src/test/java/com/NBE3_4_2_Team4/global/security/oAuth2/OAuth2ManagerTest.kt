package com.NBE3_4_2_Team4.global.security.oAuth2

import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.security.oauth2.OAuth2Manager
import com.NBE3_4_2_Team4.global.security.oauth2.disconnectService.OAuth2DisconnectService
import com.NBE3_4_2_Team4.global.security.oauth2.disconnectService.impl.GoogleDisconnectService
import com.NBE3_4_2_Team4.global.security.oauth2.disconnectService.impl.KaKaoDisconnectService
import com.NBE3_4_2_Team4.global.security.oauth2.disconnectService.impl.NaverDisconnectService
import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.impl.DefaultLogoutService
import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.impl.GoogleLogoutService
import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.impl.KakaoLogoutService
import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.impl.NaverLogoutService
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.service.OAuth2UserInfoService
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.service.impl.GoogleUserInfoService
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.service.impl.KakaoUserInfoService
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.service.impl.NaverUserInfoService
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OAuth2ManagerTest {
    @Autowired
    lateinit var oAuth2Manager: OAuth2Manager

    @Autowired
    lateinit var defaultLogoutService: DefaultLogoutService

    @Autowired
    lateinit var kaKaoDisconnectService: KaKaoDisconnectService

    @Autowired
    lateinit var kakaoLogoutService: KakaoLogoutService

    @Autowired
    lateinit var kakaoUserInfoService: KakaoUserInfoService


    @Autowired
    lateinit var naverDisconnectService: NaverDisconnectService

    @Autowired
    lateinit var naverLogoutService: NaverLogoutService

    @Autowired
    lateinit var naverUserInfoService: NaverUserInfoService

    @Autowired
    lateinit var googleDisconnectService: GoogleDisconnectService

    @Autowired
    lateinit var googleLogoutService: GoogleLogoutService

    @Autowired
    lateinit var googleUserInfoService: GoogleUserInfoService

    @ParameterizedTest
    @EnumSource(value = Member.OAuth2Provider::class, names = ["KAKAO", "NAVER", "GOOGLE"])
    fun test1(provider: Member.OAuth2Provider?) {
        val oAuth2DisconnectService = oAuth2Manager.getOAuth2DisconnectService(provider!!)

        val expectedService: OAuth2DisconnectService? = when (provider) {
            Member.OAuth2Provider.NONE -> null
            Member.OAuth2Provider.KAKAO -> kaKaoDisconnectService
            Member.OAuth2Provider.NAVER -> naverDisconnectService
            Member.OAuth2Provider.GOOGLE -> googleDisconnectService
        }

        Assertions.assertEquals(expectedService, oAuth2DisconnectService)
    }

    @ParameterizedTest
    @EnumSource(value = Member.OAuth2Provider::class, names = ["NONE", "KAKAO", "NAVER", "GOOGLE"])
    fun test2(provider: Member.OAuth2Provider?) {
        val oAuth2LogoutService = oAuth2Manager.getOAuth2LogoutService(provider!!)

        val expectedService = when (provider) {
            Member.OAuth2Provider.NONE -> defaultLogoutService
            Member.OAuth2Provider.KAKAO -> kakaoLogoutService
            Member.OAuth2Provider.NAVER -> naverLogoutService
            Member.OAuth2Provider.GOOGLE -> googleLogoutService
        }

        Assertions.assertEquals(expectedService, oAuth2LogoutService)
    }

    @ParameterizedTest
    @EnumSource(value = Member.OAuth2Provider::class, names = ["KAKAO", "NAVER", "GOOGLE"])
    fun test3(provider: Member.OAuth2Provider?) {
        val oAuth2UserInfoService = oAuth2Manager.getOAuth2UserInfoService(provider!!)

        val expectedService: OAuth2UserInfoService? = when (provider) {
            Member.OAuth2Provider.NONE -> null
            Member.OAuth2Provider.KAKAO -> kakaoUserInfoService
            Member.OAuth2Provider.NAVER -> naverUserInfoService
            Member.OAuth2Provider.GOOGLE -> googleUserInfoService
        }

        Assertions.assertEquals(expectedService, oAuth2UserInfoService)
    }
}
