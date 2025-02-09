package com.NBE3_4_2_Team4.domain.point.initData;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.initData.MemberInitData;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService;
import com.NBE3_4_2_Team4.domain.point.entity.PointCategory;
import com.NBE3_4_2_Team4.domain.point.service.PointHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class PointHistoryInitData {
    private final PointHistoryService pointHistoryService;
    private final MemberRepository memberRepository;
    @Autowired
    @Lazy
    private PointHistoryInitData self;

    @Bean
    public ApplicationRunner pointInitDataApplicationRunner() {
        return args -> {
            if (memberRepository.count() <= 0) {

            }
            self.work();
        };
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void work() {
        if (pointHistoryService.count() > 0) return;

        Member admin = memberRepository.findByUsername("admin@test.com").get();
        Member member = memberRepository.findByUsername("test@test.com").get();


        pointHistoryService.createHistory(admin, null, 10, PointCategory.ANSWER, "");
        pointHistoryService.createHistory(admin, null, 10, PointCategory.ANSWER, "");
        pointHistoryService.createHistory(admin, member, 13, PointCategory.TRANSFER, "");
        pointHistoryService.createHistory(admin, null, 14, PointCategory.PURCHASE, "");
        pointHistoryService.createHistory(admin, null, 15, PointCategory.ATTENDANCE, "");
    }
}
