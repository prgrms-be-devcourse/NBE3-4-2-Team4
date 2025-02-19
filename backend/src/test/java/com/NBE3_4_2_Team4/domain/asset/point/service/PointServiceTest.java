package com.NBE3_4_2_Team4.domain.asset.point.service;


import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetHistory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType;
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetHistoryService;
import com.NBE3_4_2_Team4.domain.asset.point.dto.AssetHistoryReq;
import com.NBE3_4_2_Team4.domain.asset.point.dto.AssetHistoryRes;
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Point;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository;
import com.NBE3_4_2_Team4.global.exceptions.PointClientException;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class PointServiceTest {
    @Autowired
    private PointService pointService;

    @Autowired
    private AssetHistoryRepository assetHistoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AssetHistoryService assetHistoryService;

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

        assetHistoryService.createHistory(member1, null, 10, AssetCategory.ANSWER, AssetType.POINT, "a");
        assetHistoryService.createHistory(member1, null, 15, AssetCategory.ANSWER, AssetType.POINT, "b");
        assetHistoryService.createHistory(member1, null, 15, AssetCategory.PURCHASE, AssetType.POINT, "b");
        assetHistoryService.createHistory(member2, null, 10, AssetCategory.ANSWER, AssetType.POINT, "c");

        member1Id = member1.getId();
        member2Id = member2.getId();
    }

    @Test
    @DisplayName("transfer test")
    void t1() {
        pointService.transfer(member1.getUsername(), member2.getUsername(), 150L, AssetCategory.TRANSFER);

        Member updatedMember1 = memberRepository.findById(member1Id).orElseThrow(() -> new RuntimeException("Account not found"));
        Member updatedMember2 = memberRepository.findById(member2Id).orElseThrow(() -> new RuntimeException("Account not found"));

        assertEquals(150L, updatedMember1.getPoint().getAmount());
        assertEquals(150L, updatedMember2.getPoint().getAmount());
    }

    @Test
    @DisplayName("accumulation test")
    void t2() {
        pointService.accumulate(member1.getUsername(), 150L, AssetCategory.ANSWER);

        Member updatedMember1 = memberRepository.findById(member1Id).orElseThrow(() -> new RuntimeException("Account not found"));

        assertEquals(450, updatedMember1.getPoint().getAmount());
    }

    @Test
    @DisplayName("deduction test")
    void t3() {
        pointService.deduct(member1.getUsername(), 150L, AssetCategory.ANSWER);

        Member updatedMember1 = memberRepository.findById(member1Id).orElseThrow(() -> new RuntimeException("Account not found"));

        assertEquals(150, updatedMember1.getPoint().getAmount());
    }

    @Test
    @DisplayName("history creation test")
    void t4() {
        long id = assetHistoryService.createHistory(member1, null, 10, AssetCategory.ANSWER, AssetType.POINT, "a");
        AssetHistory assetHistory = assetHistoryRepository.findById(id).orElseThrow(() -> new RuntimeException("히스토리 없음"));
        assertEquals(member1.getId(), assetHistory.getMember().getId());

        LocalDateTime createdAt = LocalDateTime.now();
        assertTrue(Duration.between(assetHistory.getCreatedAt(), createdAt).getSeconds() < 5);
    }

    @Test
    @DisplayName("history page")
    void t5() {
        LocalDate today = LocalDate.now();

        AssetHistoryReq dto = new AssetHistoryReq();
        dto.setPage(1);
        dto.setAssetCategory(AssetCategory.ANSWER);
        dto.setStartDate(today.minusDays(30));
        dto.setEndDate(today);

        PageDto<AssetHistoryRes> res = assetHistoryService.getHistoryPageWithFilter(member1, 10, dto);
        assertEquals(2, res.getTotalItems());
    }

    @Test
    @DisplayName("출석 테스트")
    void t6() {
        pointService.attend(member1Id);
        Member updatedMember1 = memberRepository.findById(member1Id).orElseThrow(() -> new RuntimeException("Account not found"));
        assertEquals(310, updatedMember1.getPoint().getAmount());
        assertEquals(LocalDate.now(), updatedMember1.getLastAttendanceDate());
    }

    @Test
    @DisplayName("출석 실패 테스트")
    void t7() {
        member1.setLastAttendanceDate(LocalDate.now());
        memberRepository.flush();

        assertThrows(PointClientException.class, () -> {
            pointService.attend(member1Id);
        });
    }
}
