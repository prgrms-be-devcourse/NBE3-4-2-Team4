package com.NBE3_4_2_Team4.member.member.service;

import com.NBE3_4_2_Team4.global.jwt.JwtProvider;
import com.NBE3_4_2_Team4.member.dto.request.LoginRequestDto;
import com.NBE3_4_2_Team4.member.dto.request.SignUpRequestDto;
import com.NBE3_4_2_Team4.member.member.entity.Member;
import com.NBE3_4_2_Team4.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.member.memberCategory.entity.MemberCategory;
import com.NBE3_4_2_Team4.member.memberCategory.repository.MemberCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberCategoryRepository memberCategoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public String login(LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.email();
        String password = loginRequestDto.password();

        Member member = memberRepository.findByUsername(email).orElseThrow();
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new RuntimeException();
        }

        return jwtProvider.generateToken(member);
    }

    public Member signUp(String username, String password, String nickname, String memberCategoryName){
        memberRepository
                .findByUsername(username)
                .ifPresent(_ ->{
                    throw new RuntimeException();
                });

        MemberCategory memberCategory = memberCategoryRepository.findByName(memberCategoryName)
                .orElseThrow();

        return memberRepository.save(Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .memberCategory(memberCategory)
                .build());
    }

    public Member signUp(SignUpRequestDto signUpRequestDto) {
        return signUp(
                signUpRequestDto.username(),
                signUpRequestDto.password(),
                signUpRequestDto.nickname(),
                "COMMON");
    }

    public void modify(Member member, String nickname){
        member.setNickname(nickname);
    }

    public Member signUpOrModify(String username, String password, String nickname, String memberCategoryName) {
        Optional<Member> member = memberRepository.findByUsername(username);
        if (member.isPresent()) {
            Member memberToModify = member.get();
            modify(memberToModify, nickname);
            return memberToModify;
        }

        return signUp(username, password, nickname, memberCategoryName);
    }
}
