package com.NBE3_4_2_Team4.member.member.service;

import com.NBE3_4_2_Team4.global.jwt.JwtProvider;
import com.NBE3_4_2_Team4.member.dto.request.LoginRequestDto;
import com.NBE3_4_2_Team4.member.member.entity.Member;
import com.NBE3_4_2_Team4.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.member.memberCategory.entity.MemberCategory;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public String login(LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.email();
        String password = loginRequestDto.password();

        Member member = memberRepository.findByEmail(email).orElseThrow();
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new RuntimeException();
        }

        return jwtProvider.generateToken(member);
    }
}
