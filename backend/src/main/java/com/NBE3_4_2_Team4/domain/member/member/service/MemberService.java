package com.NBE3_4_2_Team4.domain.member.member.service;

import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.entity.OAuth2RefreshToken;
import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.repository.OAuth2RefreshTokenRepository;
import com.NBE3_4_2_Team4.domain.member.member.dto.NicknameUpdateRequestDto;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.global.security.oauth2.OAuth2Manager;
import com.NBE3_4_2_Team4.global.security.oauth2.disconect.service.OAuth2DisconnectService;
import com.NBE3_4_2_Team4.global.security.oauth2.logout.service.OAuth2LogoutService;
import com.NBE3_4_2_Team4.standard.constants.PointConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final OAuth2Manager oAuth2Manager;
    private final OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;

    @Transactional(readOnly = true)
    public long count(){
        return memberRepository.count();
    }

    public String getLogoutUrl(Member member){
        if (member != null) {
            Member.OAuth2Provider oAuthProvider = member.getOAuth2Provider();

            if (!oAuthProvider.equals(Member.OAuth2Provider.NONE)) {
                OAuth2LogoutService oAuth2LogoutService = oAuth2Manager.getOAuth2LogoutService(oAuthProvider);
                return oAuth2LogoutService.getLogoutUrl();
            }else {
                return OAuth2LogoutService.LOGOUT_COMPLETE_URL;
            }
        }
        throw new RuntimeException("no member logged in");
    }


    public Member signUp(
            String username,
            String password,
            String nickname,
            Member.Role role,
            Member.OAuth2Provider oAuth2Provider){
        if (memberRepository.existsByUsername(username)) {
            throw new RuntimeException("member already exists");
        }
        return memberRepository.save(Member.builder()
                .role(role)
                .oAuth2Provider(oAuth2Provider)
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .point(PointConstants.INITIAL_POINT)
                .build());
    }

    public Member userSignUp(
            String username,
            String password,
            String nickname,
            Member.OAuth2Provider oAuth2Provider){
        return signUp(username, password, nickname, Member.Role.USER, oAuth2Provider);
    }

    public void modify(Member member, NicknameUpdateRequestDto nicknameUpdateRequestDto){
        Member memberData = memberRepository
                .findById(member.getId())
                .orElseThrow(() -> new RuntimeException("member not found"));
        String newNickname = nicknameUpdateRequestDto.newNickname();
        memberData.setNickname(newNickname);
    }

    public Member signUpOrModify(String username, String password, String nickname, Member.OAuth2Provider oAuth2Provider) {
        Optional<Member> member = memberRepository.findByUsername(username);
        return member.orElseGet(() -> userSignUp(username, password, nickname, oAuth2Provider));

    }

    public void withdrawalMembership(Member member) {
        if (member != null) {
            Long memberId = member.getId();

            if (memberId == null || !memberRepository.existsById(memberId)) {
                throw new RuntimeException("no member found with id");
            }

            Member.OAuth2Provider oAuthProvider = member.getOAuth2Provider();

            if (!oAuthProvider.equals(Member.OAuth2Provider.NONE)) {
                OAuth2RefreshToken oAuth2RefreshToken = oAuth2RefreshTokenRepository
                        .findByMember(member)
                        .orElseThrow();
                String refreshToken = oAuth2RefreshToken.getRefreshToken();

                OAuth2DisconnectService oAuth2DisconnectService = oAuth2Manager.getOAuth2DisconnectService(oAuthProvider);

                if (!oAuth2DisconnectService.disconnect(refreshToken)) {
                    throw new RuntimeException("disconnect failed");
                }

                oAuth2RefreshTokenRepository.deleteByMember(member);
            }

            member.getQuestions().forEach(question -> question.setAuthor(null));
            member.getAnswers().forEach(answer -> answer.setAuthor(null));

            memberRepository.deleteById(memberId);
        }else {
            throw new RuntimeException("no member logged in");
        }
    }
}
