package com.NBE3_4_2_Team4.domain.member.member.service;

import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.entity.OAuth2RefreshToken;
import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.repository.OAuth2RefreshTokenRepository;
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

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final OAuth2Manager oAuth2Manager;
    private final OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;

    public long count(){
        return memberRepository.count();
    }

    public String getLogoutUrl(Member member){
        if (member != null) {
            Member.OAuth2Provider oAuthProvider = member.getOAuth2Provider();
            OAuth2LogoutService oAuth2LogoutService = oAuth2Manager.getOAuth2LogoutService(oAuthProvider);
            if (oAuth2LogoutService != null) {
                return oAuth2LogoutService.getLogoutUrl();
            }
            throw new RuntimeException("no OAuth provider found");
        }
        throw new RuntimeException("no member logged in");
    }


    public Member signUp(
            String username,
            String password,
            String nickname,
            Member.Role role,
            Member.OAuth2Provider oAuth2Provider){
        memberRepository
                .findByUsername(username)
                .ifPresent(_ ->{
                    throw new RuntimeException();
                });
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

    public void modify(Member member, String nickname){
        member.setNickname(nickname);
    }

    public Member signUpOrModify(String username, String password, String nickname, Member.OAuth2Provider oAuth2Provider) {
        Optional<Member> member = memberRepository.findByUsername(username);
        if (member.isPresent()) {
            Member memberToModify = member.get();
            modify(memberToModify, nickname);
            return memberToModify;
        }

        return userSignUp(username, password, nickname, oAuth2Provider);
    }

    public void withdrawalMembership(Member member) {
        if (member != null) {
            Member.OAuth2Provider oAuthProvider = member.getOAuth2Provider();

            if (!oAuthProvider.equals(Member.OAuth2Provider.NONE)) {
                OAuth2RefreshToken oAuth2RefreshToken = oAuth2RefreshTokenRepository
                        .findByMember(member)
                        .orElseThrow();
                String refreshToken = oAuth2RefreshToken.getRefreshToken();

                OAuth2DisconnectService oAuth2DisconnectService = oAuth2Manager.getOAuth2DisconnectService(oAuthProvider);

                if (!oAuth2DisconnectService.disconnect(refreshToken)) {
                    throw new RuntimeException();
                }

                oAuth2RefreshTokenRepository.deleteByMember(member);
            }

            member.getQuestions().forEach(question -> question.setAuthor(null));
            member.getAnswers().forEach(answer -> answer.setAuthor(null));

            memberRepository.delete(member);
        }else {
            throw new RuntimeException("no member logged in");
        }
    }
}
