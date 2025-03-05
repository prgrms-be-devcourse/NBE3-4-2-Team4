package com.NBE3_4_2_Team4.domain.asset.point.service;


import com.NBE3_4_2_Team4.domain.asset.main.entity.AdminAssetCategory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetHistory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType;
import com.NBE3_4_2_Team4.domain.asset.main.repository.AdminAssetCategoryRepository;
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetHistoryService;
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Point;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository;
import com.NBE3_4_2_Team4.global.exceptions.PointClientException;
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

    @Autowired
    private AdminAssetCategoryRepository adminAssetCategoryRepository;

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
                .nickname("n1")
                .password("1234")
                .build();

        member2 = Member.builder()
                .point(new Point(0L))
                .role(Member.Role.USER)
                .oAuth2Provider(Member.OAuth2Provider.NONE)
                .username("m2")
                .nickname("n2")
                .password("1234")
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);

        assetHistoryService.createHistory(member1, null, 10, AssetCategory.ANSWER, null, AssetType.POINT, "a");
        assetHistoryService.createHistory(member1, null, 15, AssetCategory.ANSWER, null, AssetType.POINT, "b");
        assetHistoryService.createHistory(member1, null, 15, AssetCategory.PURCHASE, null, AssetType.POINT, "b");
        assetHistoryService.createHistory(member2, null, 10, AssetCategory.ANSWER, null, AssetType.POINT, "c");

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
        AdminAssetCategory adminAssetCategory = adminAssetCategoryRepository.findByName("ABUSE_RESPONSE").orElseThrow(RuntimeException::new);

        long id1 = assetHistoryService.createHistory(member1, null, 10, AssetCategory.ANSWER, null, AssetType.POINT, "a");
        long id2 = assetHistoryService.createHistory(member1, null, 10, AssetCategory.ADMIN, adminAssetCategory, AssetType.POINT, "a");

        AssetHistory assetHistory = assetHistoryRepository.findById(id1).orElseThrow(() -> new RuntimeException("히스토리 없음"));
        assertEquals(member1.getId(), assetHistory.getMember().getId());

        AssetHistory assetHistory2 = assetHistoryRepository.findById(id2).orElseThrow(RuntimeException::new);
        assertEquals("ABUSE_RESPONSE", assetHistory2.getAdminAssetCategory().getName());

        LocalDateTime createdAt = LocalDateTime.now();
        assertTrue(Duration.between(assetHistory.getCreatedAt(), createdAt).getSeconds() < 5);

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

    @Test
    @DisplayName("adminAccumulate 테스트")
    void t8() {
        Long historyId = pointService.adminAccumulate(member1.getUsername(), 10, 1);
        String name = assetHistoryRepository
                .findById(historyId).get().getAdminAssetCategory().getName();
        assertEquals("SYSTEM_COMPENSATION", name);
    }

    @Test
    @DisplayName("adminDeduct 서비스 테스트")
    void t9() {
        Long historyId = pointService.adminDeduct(member1.getUsername(), 10, 1);
        String name = assetHistoryRepository
                .findById(historyId).get().getAdminAssetCategory().getName();
        assertEquals("SYSTEM_COMPENSATION", name);
    }
}
