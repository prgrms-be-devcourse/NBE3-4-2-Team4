package com.NBE3_4_2_Team4.domain.member.member.service;


import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.repository.OAuth2RefreshTokenRepository;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.global.security.oauth2.OAuth2Manager;
import com.NBE3_4_2_Team4.global.security.oauth2.logout.service.OAuth2LogoutService;
import com.NBE3_4_2_Team4.global.security.oauth2.logout.service.impl.GoogleLogoutService;
import com.NBE3_4_2_Team4.global.security.oauth2.logout.service.impl.KakaoLogoutService;
import com.NBE3_4_2_Team4.global.security.oauth2.logout.service.impl.NaverLogoutService;
import com.NBE3_4_2_Team4.standard.constants.PointConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
public class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;

    @Spy
    private PasswordEncoder passwordEncoder;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private OAuth2Manager oAuth2Manager;

    @Mock
    private OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;

    @Mock
    private KakaoLogoutService kakaoLogoutService;

    @Mock
    private NaverLogoutService naverLogoutService;

    @Mock
    private GoogleLogoutService googleLogoutService;

    private final String username = "test username";
    private final String password = "test password";
    private final String nickname = "test nickname";
    private final Member.Role role = Member.Role.USER;
    private final Member.OAuth2Provider oAuth2Provider = Member.OAuth2Provider.NONE;

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .role(role)
                .oAuth2Provider(oAuth2Provider)
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .point(PointConstants.INITIAL_POINT)
                .build();
    }
    @Test
    @DisplayName("총 멤버 수 카운팅 테스트")
    void countTest() {
        Random random = new Random();
        long randomCount = Math.abs(random.nextLong());
        when(memberRepository.count()).thenReturn(randomCount);

        long count = memberService.count();
        assertEquals(count, randomCount);
        verify(memberRepository, times(1)).count();
    }

    @Test
    void getLogoutUrlTest1(){
        Throwable exception = assertThrows(RuntimeException.class, () -> {
            memberService.getLogoutUrl(null);
        });

        assertEquals("no member logged in", exception.getMessage());
    }

    @Test
    void getLogoutUrlTest2(){
        Member member = Member.builder()
                .oAuth2Provider(Member.OAuth2Provider.NONE)
                .build();

        String logoutUrl = memberService.getLogoutUrl(member);
        assertEquals(OAuth2LogoutService.LOGOUT_COMPLETE_URL, logoutUrl);
        verify(oAuth2Manager, times(0))
                .getOAuth2LogoutService(any());
    }

    @Test
    void getLogoutUrlTest3(){
        String testKakaoLogoutUrl = "test kakao logout url";

        when(oAuth2Manager.getOAuth2LogoutService(Member.OAuth2Provider.KAKAO))
                .thenReturn(kakaoLogoutService);
        when(kakaoLogoutService.getLogoutUrl())
                .thenReturn(testKakaoLogoutUrl);

        Member member = Member.builder()
                .oAuth2Provider(Member.OAuth2Provider.KAKAO)
                .build();

        String logoutUrl = memberService.getLogoutUrl(member);
        assertEquals(testKakaoLogoutUrl, logoutUrl);
        verify(oAuth2Manager, times(1)).getOAuth2LogoutService(Member.OAuth2Provider.KAKAO);
    }

    @Test
    void getLogoutUrlTest4(){
        String testNaverLogoutUrl = "test naver logout url";

        when(oAuth2Manager.getOAuth2LogoutService(Member.OAuth2Provider.NAVER))
                .thenReturn(naverLogoutService);
        when(naverLogoutService.getLogoutUrl())
                .thenReturn(testNaverLogoutUrl);

        Member member = Member.builder()
                .oAuth2Provider(Member.OAuth2Provider.NAVER)
                .build();

        String logoutUrl = memberService.getLogoutUrl(member);
        assertEquals(testNaverLogoutUrl, logoutUrl);
        verify(oAuth2Manager, times(1))
                .getOAuth2LogoutService(Member.OAuth2Provider.NAVER);
    }

    @Test
    void getLogoutUrlTest5(){
        String testGoogleLogoutUrl = "test google logout url";

        when(oAuth2Manager.getOAuth2LogoutService(Member.OAuth2Provider.GOOGLE))
                .thenReturn(googleLogoutService);
        when(googleLogoutService.getLogoutUrl())
                .thenReturn(testGoogleLogoutUrl);

        Member member = Member.builder()
                .oAuth2Provider(Member.OAuth2Provider.GOOGLE)
                .build();

        String logoutUrl = memberService.getLogoutUrl(member);
        assertEquals(testGoogleLogoutUrl, logoutUrl);
        verify(oAuth2Manager, times(1))
                .getOAuth2LogoutService(Member.OAuth2Provider.GOOGLE);
    }

    @Test
    void signUpTest1(){
        when(memberRepository.existsByUsername(username))
                .thenReturn(true);

        assertThrows(RuntimeException.class, () -> memberService
                .signUp(username, password, nickname, role, oAuth2Provider));

        verify(memberRepository,times(1))
                .existsByUsername(username);
    }

    @Test
    void signUpTest2(){
        when(memberRepository.existsByUsername(username))
                .thenReturn(false);

        when(memberRepository.save(any()))
                .thenReturn(member);

        Member newMember = memberService
                .signUp(username, password, nickname, role, oAuth2Provider);

        assertEquals(username, newMember.getUsername());
        assertNotEquals(password, newMember.getPassword());
        assertEquals(nickname, newMember.getNickname());
        assertEquals(role, newMember.getRole());
        assertEquals(oAuth2Provider, newMember.getOAuth2Provider());
        assertEquals(PointConstants.INITIAL_POINT, newMember.getPoint());

        verify(memberRepository,times(1))
                .existsByUsername(username);
        verify(memberRepository, times(1))
                .save(any());
    }

    @Test
    void signUpTest3(){
        when(memberRepository.existsByUsername(username))
                .thenReturn(false);

        when(memberRepository.save(any()))
                .thenReturn(member);

        Member newMember = memberService
                .userSignUp(username, password, nickname, oAuth2Provider);

        assertEquals(username, newMember.getUsername());
        assertNotEquals(password, newMember.getPassword());
        assertEquals(nickname, newMember.getNickname());
        assertEquals(role, newMember.getRole());
        assertEquals(oAuth2Provider, newMember.getOAuth2Provider());
        assertEquals(PointConstants.INITIAL_POINT, newMember.getPoint());

        verify(memberRepository,times(1))
                .existsByUsername(username);
        verify(memberRepository, times(1))
                .save(any());
    }

    @Test
    void modifyTest(){
        assertEquals("test nickname", member.getNickname());

        memberService.modify(member, "new nickname");

        assertEquals("new nickname", member.getNickname());
    }
}
