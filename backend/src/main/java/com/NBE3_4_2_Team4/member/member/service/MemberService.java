package com.NBE3_4_2_Team4.member.member.service;

import com.NBE3_4_2_Team4.global.jwt.JwtProvider;
import com.NBE3_4_2_Team4.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
}
