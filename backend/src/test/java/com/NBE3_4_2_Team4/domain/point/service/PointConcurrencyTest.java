package com.NBE3_4_2_Team4.domain.point.service;

import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Point;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.domain.point.entity.PointCategory;
import com.NBE3_4_2_Team4.domain.point.repository.PointHistoryRepository;
import com.NBE3_4_2_Team4.standard.util.test.ConcurrencyTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
public class PointConcurrencyTest {
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
                .point(new Point(300L))
                .role(Member.Role.USER)
                .oAuth2Provider(Member.OAuth2Provider.NONE)
                .username("m1")
                .password("1234")
                .build();

        member2 = Member.builder()
                .point(new Point(0L))
                .role(Member.Role.USER)
                .oAuth2Provider(Member.OAuth2Provider.NONE)
                .username("m2")
                .password("1234")
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);

        member1Id = member1.getId();
        member2Id = member2.getId();
    }

    @AfterEach
    void cleanUp() {
        pointHistoryRepository.deleteAll();
        memberRepository.deleteById(member1Id);
        memberRepository.deleteById(member2Id);
    }

    @Test
    @DisplayName("동시성 테스트")
    void t1() {
        ConcurrencyTestUtil.execute(10, () -> {
            pointService.transfer(member1.getUsername(), member2.getUsername(), 10, PointCategory.TRANSFER);
            return null;
        });

        Member updatedMember1 = memberRepository.findById(member1Id).orElseThrow(() -> new RuntimeException("Account not found"));
        Member updatedMember2 = memberRepository.findById(member2Id).orElseThrow(() -> new RuntimeException("Account not found"));

        assertEquals(200L, updatedMember1.getPoint().getAmount());
        assertEquals(100L, updatedMember2.getPoint().getAmount());
    }

}
