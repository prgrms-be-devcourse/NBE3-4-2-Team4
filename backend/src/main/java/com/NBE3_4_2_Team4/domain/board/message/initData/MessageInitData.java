package com.NBE3_4_2_Team4.domain.board.message.initData;

import com.NBE3_4_2_Team4.domain.board.message.service.MessageService;
import com.NBE3_4_2_Team4.domain.member.member.initData.MemberInitData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class MessageInitData {
    private final MessageService messageService;
    private final MemberInitData memberInitData;

    @Value("${custom.initData.member.admin.username}")
    private String adminUsername;

    @Value("${custom.initData.member.member1.username}")
    private String member1Username;

    @Autowired
    @Lazy
    private MessageInitData self;

    @Bean
    public ApplicationRunner messageInitDataApplicationRunner() {
        return _ -> {
            memberInitData.work();
            self.work1();
        };
    }

    @Transactional
    public void work1() {
        if (messageService.count() > 0) return;

        messageService.write(member1Username, adminUsername, "안녕하세요.", "안녕하세요. 반갑습니다!");
        messageService.write(adminUsername, member1Username, "질문 관련 문의 드립니다.", "중요한 안내사항입니다!");
    }
}
