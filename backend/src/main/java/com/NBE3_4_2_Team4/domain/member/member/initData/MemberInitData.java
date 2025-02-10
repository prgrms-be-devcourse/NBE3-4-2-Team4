package com.NBE3_4_2_Team4.domain.member.member.initData;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MemberInitData {
    private final MemberService memberService;

    @Value("${custom.initData.member.admin.username}")
    private String adminUsername;

    @Value("${custom.initData.member.admin.password}")
    private String adminPassword;

    @Value("${custom.initData.member.admin.nickname}")
    private String adminNickname;


    @Value("${custom.initData.member.member1.username}")
    private String member1Username;

    @Value("${custom.initData.member.member1.password}")
    private String member1Password;

    @Value("${custom.initData.member.member1.nickname}")
    private String member1Nickname;


    @Value("${custom.initData.member.member2.username}")
    private String member2Username;

    @Value("${custom.initData.member.member2.password}")
    private String member2Password;

    @Value("${custom.initData.member.member2.nickname}")
    private String member2Nickname;


    @Autowired
    @Lazy
    private MemberInitData self;

    @Bean
    public ApplicationRunner memberInitDataApplicationRunner() {
        return _ -> {
            self.work();
        };
    }

    @Transactional
    public void work() {
        if (memberService.count() > 0) return;
        Member.OAuth2Provider oAuth2Provider = Member.OAuth2Provider.NONE;

        memberService.signUp(adminUsername, adminPassword, adminNickname, Member.Role.ADMIN, oAuth2Provider);
        memberService.signUp(member1Username, member1Password, member1Nickname, Member.Role.USER, oAuth2Provider);
        memberService.signUp(member2Username, member2Password, member2Nickname, Member.Role.USER, oAuth2Provider);
    }
}
