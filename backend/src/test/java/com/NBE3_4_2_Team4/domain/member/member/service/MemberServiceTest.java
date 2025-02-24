package com.NBE3_4_2_Team4.domain.member.member.service;


import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetHistory;
import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository;
import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.entity.OAuth2RefreshToken;
import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.repository.OAuth2RefreshTokenRepository;
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Point;
import com.NBE3_4_2_Team4.domain.member.member.dto.AdminLoginRequestDto;
import com.NBE3_4_2_Team4.domain.member.member.dto.NicknameUpdateRequestDto;
import com.NBE3_4_2_Team4.domain.member.member.dto.SignupRequestDto;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberQuerydsl;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.global.exceptions.InValidPasswordException;
import com.NBE3_4_2_Team4.global.exceptions.MemberNotFoundException;
import com.NBE3_4_2_Team4.global.security.oauth2.OAuth2Manager;
import com.NBE3_4_2_Team4.global.security.oauth2.disconectService.impl.GoogleDisconnectService;
import com.NBE3_4_2_Team4.global.security.oauth2.disconectService.impl.KaKaoDisconnectService;
import com.NBE3_4_2_Team4.global.security.oauth2.disconectService.impl.NaverDisconnectService;
import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.impl.DefaultLogoutService;
import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.impl.GoogleLogoutService;
import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.impl.KakaoLogoutService;
import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.impl.NaverLogoutService;
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfo;
import com.NBE3_4_2_Team4.global.security.user.tempUserBeforeSignUp.TempUserBeforeSignUp;
import com.NBE3_4_2_Team4.global.security.user.tempUserBeforeSignUp.TempUserBeforeSignUpService;
import com.NBE3_4_2_Team4.standard.constants.PointConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@Transactional
public class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberQuerydsl memberQuerydsl;

    @Mock
    private OAuth2Manager oAuth2Manager;

    @Mock
    private OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;

    @Mock
    private AssetHistoryRepository pointHistoryRepository;

    @Mock
    private DefaultLogoutService defaultLogoutService;

    @Mock
    private KakaoLogoutService kakaoLogoutService;

    @Mock
    private NaverLogoutService naverLogoutService;

    @Mock
    private GoogleLogoutService googleLogoutService;

    @Mock
    private KaKaoDisconnectService kaKaoDisconnectService;

    @Mock
    private NaverDisconnectService naverDisconnectService;

    @Mock
    private GoogleDisconnectService googleDisconnectService;

    @Mock
    private TempUserBeforeSignUpService tempUserBeforeSignUpService;

    private final String username = "test username";
    private final String password = "test password";
    private final String nickname = "test nickname";
    private final Member.Role role = Member.Role.USER;
    private final Member.OAuth2Provider oAuth2Provider = Member.OAuth2Provider.NONE;

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .role(role)
                .oAuth2Provider(oAuth2Provider)
                .username(username)
                .password(password)
                .nickname(nickname)
                .point(new Point(PointConstants.INITIAL_POINT))
                .questions(new ArrayList<>())
                .answers(new ArrayList<>())
                .build();
    }


    @Test
    void adminLoginTest1(){
        member.setRole(Member.Role.ADMIN);

        AdminLoginRequestDto adminLoginRequestDto = AdminLoginRequestDto.builder()
                .adminUsername(username)
                .password(password)
                .build();

        when(memberRepository.findByUsername(username))
                .thenReturn(Optional.of(member));

        when(passwordEncoder.matches(any(), any()))
                .thenReturn(true);

        Member member = memberService.adminLogin(adminLoginRequestDto);
        assertNotNull(member);
        assertEquals(member.getUsername(), username);
        assertEquals(member.getPassword(), password);
        verify(memberRepository, times(1)).findByUsername(username);
    }


    @Test
    void adminLoginTest2(){
        AdminLoginRequestDto adminLoginRequestDto = AdminLoginRequestDto.builder()
                .adminUsername(username)
                .password(password)
                .build();

        when(memberRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.adminLogin(adminLoginRequestDto));
        verify(memberRepository, times(1)).findByUsername(username);
    }

    @Test
    void adminLoginTest3(){
        AdminLoginRequestDto adminLoginRequestDto = AdminLoginRequestDto.builder()
                .adminUsername(username)
                .password(password)
                .build();

        when(memberRepository.findByUsername(username))
                .thenReturn(Optional.of(member));

        when(passwordEncoder.matches(any(), any()))
                .thenReturn(false);

        assertThrows(InValidPasswordException.class, () -> memberService.adminLogin(adminLoginRequestDto));
        verify(memberRepository, times(1)).findByUsername(username);
    }

    @Test
    void adminLoginTest4(){
        AdminLoginRequestDto adminLoginRequestDto = AdminLoginRequestDto.builder()
                .adminUsername(username)
                .password(password)
                .build();

        when(memberRepository.findByUsername(username))
                .thenReturn(Optional.of(member));

        when(passwordEncoder.matches(any(), any()))
                .thenReturn(true);

        assertThrows(RuntimeException.class, () -> memberService.adminLogin(adminLoginRequestDto));
        verify(memberRepository, times(1)).findByUsername(username);
    }


    @ParameterizedTest
    @EnumSource(value = Member.OAuth2Provider.class, names = {"NONE", "KAKAO", "GOOGLE", "NAVER"})
    void getLogoutUrlTest2(Member.OAuth2Provider provider) {
        member.setOAuth2Provider(provider);
        Member testMember = member;

        when(memberRepository.existsById(1L)).thenReturn(true);

        when(oAuth2Manager.getOAuth2LogoutService(provider)).
                thenReturn(switch (provider) {
                    case NONE -> defaultLogoutService;
                    case KAKAO -> kakaoLogoutService;
                    case NAVER -> naverLogoutService;
                    case GOOGLE -> googleLogoutService;
                });


        String logoutUrl = String.format("Logout url for %s", provider.name());

        when(switch (provider) {
            case NONE -> defaultLogoutService.getLogoutUrl();
            case KAKAO -> kakaoLogoutService.getLogoutUrl();
            case NAVER -> naverLogoutService.getLogoutUrl();
            case GOOGLE -> googleLogoutService.getLogoutUrl();
        })
                .thenReturn(logoutUrl);

        String actualLogoutUrl = memberService.getLogoutUrl(testMember);
        assertEquals(logoutUrl, actualLogoutUrl);
    }

    @ParameterizedTest
    @EnumSource(value = Member.OAuth2Provider.class, names = {"KAKAO", "GOOGLE", "NAVER"}) // NONE 제외
    void getLogoutUrlTest3(Member.OAuth2Provider provider){
        when(memberRepository.existsById(1L)).thenReturn(true);

        when(oAuth2Manager.getOAuth2LogoutService(provider))
                .thenReturn(null);

        member.setOAuth2Provider(provider);
        Member testMember = member;

        assertThrows(RuntimeException.class, () -> memberService.getLogoutUrl(testMember));
        verify(oAuth2Manager, times(1))
                .getOAuth2LogoutService(provider);
    }

    @Test
    void updateNicknameTest1(){
        when(memberRepository.existsById(1L)).thenReturn(true);

        when(memberRepository.findById(any()))
                .thenReturn(Optional.empty());

        NicknameUpdateRequestDto nicknameUpdateRequestDto = new NicknameUpdateRequestDto("new nickname");
        assertThrows(RuntimeException.class, () ->
                memberService.updateNickname(member, nicknameUpdateRequestDto));
    }

    @Test
    void updateNicknameTest2(){
        when(memberRepository.existsById(1L)).thenReturn(true);

        when(memberRepository.findById(any()))
                .thenReturn(Optional.of(member));
        assertEquals("test nickname", member.getNickname());

        NicknameUpdateRequestDto nicknameUpdateRequestDto = new NicknameUpdateRequestDto("new nickname");
        memberService.updateNickname(member, nicknameUpdateRequestDto);

        assertEquals("new nickname", member.getNickname());
    }


    @Test
    void withdrawTest1(){
        when(memberRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(RuntimeException.class, () -> memberService.withdrawalMembership(member));
    }

    @Test
    void withdrawTest2(){
        when(memberRepository.existsById(1L))
                .thenReturn(true);

        memberService.withdrawalMembership(member);

        verify(oAuth2RefreshTokenRepository, times(0))
                .findByMember(any());

        verify(oAuth2Manager, times(0))
                .getOAuth2DisconnectService(any());

        verify(memberQuerydsl, times(1))
                .deleteMember(anyLong());
    }

    @ParameterizedTest
    @EnumSource(value = Member.OAuth2Provider.class, names = {"KAKAO", "GOOGLE", "NAVER"}) // NONE 제외
    void withdrawTest(Member.OAuth2Provider provider) {
        when(memberRepository.existsById(1L))
                .thenReturn(true);

        member.setOAuth2Provider(provider);
        Member testMember = member;

        OAuth2RefreshToken oAuth2RefreshToken = new OAuth2RefreshToken(
                1L,
                testMember,
                "asd",
                provider.name().toLowerCase() + " token"
        );

        when(oAuth2RefreshTokenRepository.findByMember(testMember))
                .thenReturn(Optional.of(oAuth2RefreshToken));

        when(oAuth2Manager.getOAuth2DisconnectService(provider)).
            thenReturn(switch (provider) {
                case NONE -> null;
                case KAKAO -> kaKaoDisconnectService;
                case NAVER -> naverDisconnectService;
                case GOOGLE -> googleDisconnectService;
            });

        when(switch (provider) {
            case NONE -> null;
            case KAKAO -> kaKaoDisconnectService.disconnectSuccess(any());
            case NAVER -> naverDisconnectService.disconnectSuccess(any());
            case GOOGLE -> googleDisconnectService.disconnectSuccess(any());
        })
                .thenReturn(true);

        assertDoesNotThrow(() -> memberService.withdrawalMembership(testMember));

        verify(oAuth2RefreshTokenRepository, times(1))
                .findByMember(any());
        verify(oAuth2Manager, times(1))
                .getOAuth2DisconnectService(any());
        verify(memberQuerydsl, times(1))
                .deleteMember(anyLong());
    }

    @ParameterizedTest
    @EnumSource(value = Member.OAuth2Provider.class, names = {"KAKAO", "GOOGLE", "NAVER"}) // NONE 제외
    void withdrawTest3(Member.OAuth2Provider provider) {
        when(memberRepository.existsById(1L))
                .thenReturn(true);

        member.setOAuth2Provider(provider);
        Member testMember = member;

        OAuth2RefreshToken oAuth2RefreshToken = new OAuth2RefreshToken(
                1L,
                testMember,
                "asd",
                provider.name().toLowerCase() + " token"
        );

        when(oAuth2RefreshTokenRepository.findByMember(testMember))
                .thenReturn(Optional.of(oAuth2RefreshToken));

        when(oAuth2Manager.getOAuth2DisconnectService(provider)).
                thenReturn(null);

        assertDoesNotThrow(() -> memberService.withdrawalMembership(testMember));


        verify(oAuth2RefreshTokenRepository, times(1))
                .findByMember(any());
        verify(oAuth2Manager, times(1))
                .getOAuth2DisconnectService(any());
        verify(memberRepository, times(0))
                .deleteById(any());
    }

    @ParameterizedTest
    @EnumSource(value = Member.OAuth2Provider.class, names = {"KAKAO", "GOOGLE", "NAVER"}) // NONE 제외
    void withdrawTest4(Member.OAuth2Provider provider) {
        when(memberRepository.existsById(1L))
                .thenReturn(true);

        member.setOAuth2Provider(provider);
        Member testMember = member;

        OAuth2RefreshToken oAuth2RefreshToken = new OAuth2RefreshToken(
                1L,
                testMember,
                "asd",
                provider.name().toLowerCase() + " token"
        );

        when(oAuth2RefreshTokenRepository.findByMember(testMember))
                .thenReturn(Optional.of(oAuth2RefreshToken));

        when(oAuth2Manager.getOAuth2DisconnectService(provider)).
                thenReturn(switch (provider) {
                    case NONE -> null;
                    case KAKAO -> kaKaoDisconnectService;
                    case NAVER -> naverDisconnectService;
                    case GOOGLE -> googleDisconnectService;
                });

        when(switch (provider) {
            case NONE -> null;
            case KAKAO -> kaKaoDisconnectService.disconnectSuccess(any());
            case NAVER -> naverDisconnectService.disconnectSuccess(any());
            case GOOGLE -> googleDisconnectService.disconnectSuccess(any());
        })
                .thenReturn(false);

        assertDoesNotThrow(() -> memberService.withdrawalMembership(testMember));


        verify(oAuth2RefreshTokenRepository, times(1))
                .findByMember(any());
        verify(oAuth2Manager, times(1))
                .getOAuth2DisconnectService(any());
        verify(memberRepository, times(0))
                .deleteById(any());
    }
}
