package com.NBE3_4_2_Team4.domain.member.member.service;

import com.NBE3_4_2_Team4.global.exceptions.InValidPasswordException;
import com.NBE3_4_2_Team4.global.security.jwt.JwtManager;
import com.NBE3_4_2_Team4.domain.member.dto.request.LoginRequestDto;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtManager jwtManager;

    public long count(){
        return memberRepository.count();
    }

    public String login(LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.email();
        String password = loginRequestDto.password();

        Member member = memberRepository.findByUsername(email).orElseThrow();
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new InValidPasswordException();
        }

        return jwtManager.generateToken(member);
    }

    public Member signUp(
            String username,
            String password,
            String nickname,
            Member.Role role,
            String oAuth2ProviderName){
        memberRepository
                .findByUsername(username)
                .ifPresent(_ ->{
                    throw new RuntimeException();
                });
        return memberRepository.save(Member.builder()
                .role(role)
                .oAuth2Provider(Member.OAuth2Provider.getOAuth2ProviderByName(oAuth2ProviderName))
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .build());
    }

    public Member userSignUp(
            String username,
            String password,
            String nickname,
            String oAuth2ProviderName){
        return signUp(username, password, nickname, Member.Role.USER, oAuth2ProviderName);
    }


    public void modify(Member member, String nickname){
        member.setNickname(nickname);
    }

    public Member signUpOrModify(String username, String password, String nickname, String oAuth2ProviderName) {
        Optional<Member> member = memberRepository.findByUsername(username);
        if (member.isPresent()) {
            Member memberToModify = member.get();
            modify(memberToModify, nickname);
            return memberToModify;
        }

        return userSignUp(username, password, nickname, oAuth2ProviderName);
    }

    public Member getMemberByJwtClaims(Map<String, Object> claims) {
        Long id = (Long) claims.get("id");
        String nickname = (String) claims.get("nickname");
        String roleName = (String) claims.get("role");
        String OAuth2ProviderName = (String) claims.get("OAuth2Provider");

        if (id == null || nickname == null || roleName == null || OAuth2ProviderName == null) {
            throw new RuntimeException("Invalid claims");
        }

        return new Member(id, nickname, roleName, OAuth2ProviderName);
    }
}
