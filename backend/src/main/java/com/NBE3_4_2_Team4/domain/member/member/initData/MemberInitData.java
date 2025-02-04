package com.NBE3_4_2_Team4.domain.member.member.initData;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class MemberInitData {
    private final MemberService memberService;

    @Autowired
    @Lazy
    private MemberInitData self;

    @Bean
    public ApplicationRunner memberInitDataApplicationRunner() {
        return args -> {
            self.work();
        };
    }

    @Transactional
    public void work() {
        if (memberService.count() > 0) return;
        String username = "admin@test.com";
        String password = "1234";
        String nickname = "관리자";
        String oAuth2ProviderName = "NONE";
        memberService.signUp(username, password, nickname, Member.Role.ADMIN, oAuth2ProviderName);

        memberService.signUp("test@test.com", password, "테스트 유저", Member.Role.USER, "NONE");
    }
}
