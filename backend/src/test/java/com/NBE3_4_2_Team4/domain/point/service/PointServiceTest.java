package com.NBE3_4_2_Team4.domain.point.service;


import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.domain.point.entity.PointCategory;
import com.NBE3_4_2_Team4.domain.point.entity.PointHistory;
import com.NBE3_4_2_Team4.domain.point.repository.PointHistoryRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class PointServiceTest {
    @Autowired
    private PointService pointService;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member member1;
    private Member member2;
    private Long member1Id;
    private Long member2Id;

    @BeforeEach
    void setup() {
        member1 = Member.builder()
                .point(300L)
                .role(Member.Role.USER)
                .oAuth2Provider(Member.OAuth2Provider.NONE)
                .username("member1")
                .password("1234")
                .build();

        member2 = Member.builder()
                .point(0L)
                .role(Member.Role.USER)
                .oAuth2Provider(Member.OAuth2Provider.NONE)
                .username("member2")
                .password("1234")
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);

        pointService.createHistory(member1, null, 10, PointCategory.ANSWER, "a");
        pointService.createHistory(member2, null, 10, PointCategory.ANSWER, "b");

        member1Id = member1.getId();
        member2Id = member2.getId();
    }

    @Test
    @DisplayName("transfer test")
    void t1() {
        pointService.transfer(member1.getUsername(), member2.getUsername(), 150L, PointCategory.TRANSFER);

        Member updatedMember1 = memberRepository.findById(member1Id).orElseThrow(() -> new RuntimeException("Account not found"));
        Member updatedMember2 = memberRepository.findById(member2Id).orElseThrow(() -> new RuntimeException("Account not found"));

        assertEquals(150L, updatedMember1.getPoint());
        assertEquals(150L, updatedMember2.getPoint());
    }

    @Test
    @DisplayName("accumulation test")
    void t2() {
        pointService.accumulatePoints(member1.getUsername(), 150L, PointCategory.ANSWER);

        Member updatedMember1 = memberRepository.findById(member1Id).orElseThrow(() -> new RuntimeException("Account not found"));

        assertEquals(450, updatedMember1.getPoint());
    }

    @Test
    @DisplayName("deduction test")
    void t3() {
        pointService.deductPoints(member1.getUsername(), 150L, PointCategory.ANSWER);

        Member updatedMember1 = memberRepository.findById(member1Id).orElseThrow(() -> new RuntimeException("Account not found"));

        assertEquals(150, updatedMember1.getPoint());
    }

    @Test
    @DisplayName("history creation test")
    void t4() {
        long id = pointService.createHistory(member1, null, 10, PointCategory.ANSWER, "a");
        PointHistory pointHistory = pointHistoryRepository.findById(id).orElseThrow(() -> new RuntimeException("히스토리 없음"));
        assertEquals(member1.getId(), pointHistory.getMember().getId());

        LocalDateTime createdAt = LocalDateTime.now();
        assertTrue(Duration.between(pointHistory.getCreatedAt(), createdAt).getSeconds() < 5);
    }

//    @Test
//    @DisplayName("history page")
//    void t5() {
//        pointService.getHistoryPage()
//    }
}
