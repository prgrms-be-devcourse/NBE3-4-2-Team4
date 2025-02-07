package com.NBE3_4_2_Team4.domain.member.member.service;


import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.entity.OAuth2RefreshToken;
import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.repository.OAuth2RefreshTokenRepository;
import com.NBE3_4_2_Team4.domain.member.member.dto.NicknameUpdateRequestDto;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.global.security.oauth2.OAuth2Manager;
import com.NBE3_4_2_Team4.global.security.oauth2.disconect.service.impl.GoogleDisconnectService;
import com.NBE3_4_2_Team4.global.security.oauth2.disconect.service.impl.KaKaoDisconnectService;
import com.NBE3_4_2_Team4.global.security.oauth2.disconect.service.impl.NaverDisconnectService;
import com.NBE3_4_2_Team4.global.security.oauth2.logout.service.OAuth2LogoutService;
import com.NBE3_4_2_Team4.global.security.oauth2.logout.service.impl.GoogleLogoutService;
import com.NBE3_4_2_Team4.global.security.oauth2.logout.service.impl.KakaoLogoutService;
import com.NBE3_4_2_Team4.global.security.oauth2.logout.service.impl.NaverLogoutService;
import com.NBE3_4_2_Team4.standard.constants.PointConstants;
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

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    private OAuth2Manager oAuth2Manager;

    @Mock
    private OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;

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
                .point(PointConstants.INITIAL_POINT)
                .questions(new ArrayList<>())
                .answers(new ArrayList<>())
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
        Member member = Member.builder()
                .oAuth2Provider(Member.OAuth2Provider.NONE)
                .build();

        String logoutUrl = memberService.getLogoutUrl(member);
        assertEquals(OAuth2LogoutService.LOGOUT_COMPLETE_URL, logoutUrl);
        verify(oAuth2Manager, times(0))
                .getOAuth2LogoutService(any());
    }

    @Test
    void getLogoutUrlTest2(){
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
    void getLogoutUrlTest3(){
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
    void getLogoutUrlTest4(){
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
    void getLogoutUrlTest5(){
        when(oAuth2Manager.getOAuth2LogoutService(Member.OAuth2Provider.GOOGLE))
                .thenReturn(null);

        Member member = Member.builder()
                .oAuth2Provider(Member.OAuth2Provider.GOOGLE)
                .build();

        assertThrows(RuntimeException.class, () -> memberService.getLogoutUrl(member));
        verify(oAuth2Manager, times(1))
                .getOAuth2LogoutService(Member.OAuth2Provider.GOOGLE);
    }

    @Test
    void signUpTest1(){
        when(memberRepository.existsByUsername(username))
                .thenReturn(true);

        assertThrows(RuntimeException.class, () ->
                memberService.signUp(username, password, nickname, role, oAuth2Provider));

        verify(memberRepository,times(1))
                .existsByUsername(username);
    }

    @Test
    void signUpTest2(){
        when(memberRepository.existsByUsername(username))
                .thenReturn(false);

        when(memberRepository.save(any()))
                .thenReturn(member);

        Member newMember =
                memberService.signUp(username, password, nickname, role, oAuth2Provider);

        assertEquals(username, newMember.getUsername());
        assertEquals(password, newMember.getPassword());
        assertEquals(nickname, newMember.getNickname());
        assertEquals(role, newMember.getRole());
        assertEquals(oAuth2Provider, newMember.getOAuth2Provider());
        assertEquals(PointConstants.INITIAL_POINT, newMember.getPoint());

        verify(memberRepository,times(1))
                .existsByUsername(username);
        verify(passwordEncoder,times(1))
                .encode(any());
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
        assertEquals(password, newMember.getPassword());
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
    void signUpTest4(){
        when(memberRepository.existsByUsername(username))
                .thenReturn(false);

        member.setRole(Member.Role.ADMIN);
        when(memberRepository.save(any()))
                .thenReturn(member);

        Member newMember = memberService
                .signUp(username, password, nickname, Member.Role.ADMIN, oAuth2Provider);

        assertEquals(username, newMember.getUsername());
        assertEquals(password, newMember.getPassword());
        assertEquals(nickname, newMember.getNickname());
        assertEquals(Member.Role.ADMIN, newMember.getRole());
        assertEquals(oAuth2Provider, newMember.getOAuth2Provider());
        assertEquals(PointConstants.INITIAL_POINT, newMember.getPoint());

        verify(memberRepository,times(1))
                .existsByUsername(username);
        verify(memberRepository, times(1))
                .save(any());
    }

    @Test
    void modifyTest1(){
        when(memberRepository.findById(any()))
                .thenReturn(Optional.empty());

        NicknameUpdateRequestDto nicknameUpdateRequestDto = new NicknameUpdateRequestDto("new nickname");
        assertThrows(RuntimeException.class, () ->
                memberService.modify(member, nicknameUpdateRequestDto));
    }

    @Test
    void modifyTest2(){
        when(memberRepository.findById(any()))
                .thenReturn(Optional.of(member));
        assertEquals("test nickname", member.getNickname());

        NicknameUpdateRequestDto nicknameUpdateRequestDto = new NicknameUpdateRequestDto("new nickname");
        memberService.modify(member, nicknameUpdateRequestDto);

        assertEquals("new nickname", member.getNickname());
    }

    @Test
    void signUpOrInTest1(){
        when(memberRepository.findByUsername(username))
                .thenReturn(Optional.of(member));

        Member signInMember =
                memberService.signUpOrIn(username, password, nickname, oAuth2Provider);

        assertEquals(member, signInMember);
        assertEquals(username, signInMember.getUsername());
        assertEquals(password, signInMember.getPassword());
        assertEquals(nickname, signInMember.getNickname());
        assertEquals(oAuth2Provider, signInMember.getOAuth2Provider());

        verify(memberRepository, times(0)).save(any());
    }

    @Test
    void signUpOrInTest2(){
        when(memberRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        when(memberRepository.save(any()))
                .thenReturn(member);

        Member signInMember =
                memberService.signUpOrIn(username, password, nickname, oAuth2Provider);

        assertEquals(member, signInMember);
        assertEquals(username, signInMember.getUsername());
        assertEquals(password, signInMember.getPassword());
        assertEquals(nickname, signInMember.getNickname());
        assertEquals(oAuth2Provider, signInMember.getOAuth2Provider());

        verify(memberRepository, times(1)).save(any());
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

        verify(memberRepository, times(1))
                .deleteById(any());
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
            case KAKAO -> kaKaoDisconnectService.disconnect(any());
            case NAVER -> naverDisconnectService.disconnect(any());
            case GOOGLE -> googleDisconnectService.disconnect(any());
        })
                .thenReturn(true);

        assertDoesNotThrow(() -> memberService.withdrawalMembership(testMember));

        verify(oAuth2RefreshTokenRepository, times(1))
                .findByMember(any());
        verify(oAuth2Manager, times(1))
                .getOAuth2DisconnectService(any());
        verify(memberRepository, times(1))
                .deleteById(any());
    }
}
