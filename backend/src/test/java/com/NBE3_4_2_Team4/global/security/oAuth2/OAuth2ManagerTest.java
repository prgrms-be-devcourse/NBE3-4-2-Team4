//package com.NBE3_4_2_Team4.global.security.oAuth2;
//
//import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
//import com.NBE3_4_2_Team4.global.security.oauth2.OAuth2Manager;
//import com.NBE3_4_2_Team4.global.security.oauth2.disconnectService.OAuth2DisconnectService;
//import com.NBE3_4_2_Team4.global.security.oauth2.disconnectService.impl.GoogleDisconnectService;
//import com.NBE3_4_2_Team4.global.security.oauth2.disconnectService.impl.KaKaoDisconnectService;
//import com.NBE3_4_2_Team4.global.security.oauth2.disconnectService.impl.NaverDisconnectService;
//import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.OAuth2LogoutService;
//import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.impl.DefaultLogoutService;
//import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.impl.GoogleLogoutService;
//import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.impl.KakaoLogoutService;
//import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.impl.NaverLogoutService;
//import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.service.OAuth2UserInfoService;
//import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.service.impl.GoogleUserInfoService;
//import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.service.impl.KakaoUserInfoService;
//import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.service.impl.NaverUserInfoService;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.EnumSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@Transactional
//public class OAuth2ManagerTest {
//    @Autowired
//    private OAuth2Manager oAuth2Manager;
//
//    @Autowired
//    private DefaultLogoutService defaultLogoutService;
//
//    @Autowired
//    private KaKaoDisconnectService kaKaoDisconnectService;
//
//    @Autowired
//    private KakaoLogoutService kakaoLogoutService;
//
//    @Autowired
//    private KakaoUserInfoService kakaoUserInfoService;
//
//
//    @Autowired
//    private NaverDisconnectService naverDisconnectService;
//
//    @Autowired
//    private NaverLogoutService naverLogoutService;
//
//    @Autowired
//    private NaverUserInfoService naverUserInfoService;
//
//    @Autowired
//    private GoogleDisconnectService googleDisconnectService;
//
//    @Autowired
//    private GoogleLogoutService googleLogoutService;
//
//    @Autowired
//    private GoogleUserInfoService googleUserInfoService;
//
//    @ParameterizedTest
//    @EnumSource(value = Member.OAuth2Provider.class, names = {"KAKAO", "NAVER", "GOOGLE"})
//    void test1(Member.OAuth2Provider provider) {
//        OAuth2DisconnectService oAuth2DisconnectService = oAuth2Manager.getOAuth2DisconnectService(provider);
//
//        OAuth2DisconnectService expectedService = switch (provider) {
//            case NONE -> null;
//            case KAKAO -> kaKaoDisconnectService;
//            case NAVER -> naverDisconnectService;
//            case GOOGLE -> googleDisconnectService;
//        };
//
//        assertEquals(expectedService, oAuth2DisconnectService);
//    }
//
//    @ParameterizedTest
//    @EnumSource(value = Member.OAuth2Provider.class, names = {"NONE", "KAKAO", "NAVER", "GOOGLE"})
//    void test2(Member.OAuth2Provider provider) {
//        OAuth2LogoutService oAuth2LogoutService = oAuth2Manager.getOAuth2LogoutService(provider);
//
//        OAuth2LogoutService expectedService = switch (provider) {
//            case NONE -> defaultLogoutService;
//            case KAKAO -> kakaoLogoutService;
//            case NAVER -> naverLogoutService;
//            case GOOGLE -> googleLogoutService;
//        };
//
//        assertEquals(expectedService, oAuth2LogoutService);
//    }
//
//    @ParameterizedTest
//    @EnumSource(value = Member.OAuth2Provider.class, names = {"KAKAO", "NAVER", "GOOGLE"})
//    void test3(Member.OAuth2Provider provider) {
//        OAuth2UserInfoService oAuth2UserInfoService = oAuth2Manager.getOAuth2UserInfoService(provider);
//
//        OAuth2UserInfoService expectedService = switch (provider) {
//            case NONE -> null;
//            case KAKAO -> kakaoUserInfoService;
//            case NAVER -> naverUserInfoService;
//            case GOOGLE -> googleUserInfoService;
//        };
//
//        assertEquals(expectedService, oAuth2UserInfoService);
//    }
//}
