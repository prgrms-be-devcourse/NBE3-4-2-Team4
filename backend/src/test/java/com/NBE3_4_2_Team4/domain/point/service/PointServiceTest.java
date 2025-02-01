package com.NBE3_4_2_Team4.domain.point.service;


import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.domain.point.entity.PointCategory;
import com.NBE3_4_2_Team4.domain.point.repository.PointHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        member1Id = member1.getId();
        member2Id = member2.getId();
    }

    @Test
    void transferTest() {
        pointService.transfer(member1.getUsername(), member2.getUsername(), 150L, PointCategory.TRANSFER);

        Member updatedMember1 = memberRepository.findById(member1Id).orElseThrow(() -> new RuntimeException("Account not found"));
        Member updatedMember2 = memberRepository.findById(member2Id).orElseThrow(() -> new RuntimeException("Account not found"));

        assertEquals(150L, updatedMember1.getPoint(), "Account1 balance should be 150 less");
        assertEquals(150L, updatedMember2.getPoint(), "Account2 balance should be 150 more");

        assertEquals(2, pointHistoryRepository.count());
        assertEquals(1, pointService.getHistoryPage(member1, 0, 10).getItems().size());
    }


}
