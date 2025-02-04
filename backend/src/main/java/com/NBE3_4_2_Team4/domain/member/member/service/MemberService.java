package com.NBE3_4_2_Team4.domain.member.member.service;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.global.security.oauth2.logout.service.OAuth2LogoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final Map<Member.OAuth2Provider, OAuth2LogoutService> oAuth2LogoutServiceFactory;

    public long count(){
        return memberRepository.count();
    }



    public String getLogoutUrl(Member member){
        if (member != null) {
            Member.OAuth2Provider oAuthProvider = member.getOAuth2Provider();
            OAuth2LogoutService oAuth2LogoutService = oAuth2LogoutServiceFactory.get(oAuthProvider);
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
                .point(100L)
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
}
