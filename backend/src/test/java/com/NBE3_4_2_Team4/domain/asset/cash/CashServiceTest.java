package com.NBE3_4_2_Team4.domain.asset.cash;

import com.NBE3_4_2_Team4.domain.asset.cash.service.CashService;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType;
import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository;
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetHistoryService;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Cash;
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Point;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class CashServiceTest {
    @Autowired
    private CashService cashService;

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
                .point(new Point())
                .cash(new Cash(300L))
                .role(Member.Role.USER)
                .oAuth2Provider(Member.OAuth2Provider.NONE)
                .username("m1")
                .nickname("n1")
                .password("1234")
                .build();

        member2 = Member.builder()
                .point(new Point())
                .cash(new Cash(0L))
                .role(Member.Role.USER)
                .oAuth2Provider(Member.OAuth2Provider.NONE)
                .username("m2")
                .nickname("n2")
                .password("1234")
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);

        assetHistoryService.createHistory(member1, null, 10, AssetCategory.ANSWER, null, AssetType.CASH, "a");
        assetHistoryService.createHistory(member1, null, 15, AssetCategory.ANSWER, null, AssetType.CASH, "b");
        assetHistoryService.createHistory(member1, null, 15, AssetCategory.PURCHASE, null, AssetType.CASH, "b");
        assetHistoryService.createHistory(member2, null, 10, AssetCategory.ANSWER, null, AssetType.CASH, "c");

        member1Id = member1.getId();
        member2Id = member2.getId();
    }

    @Test
    @DisplayName("transfer test")
    void t1() {
        cashService.transfer(member1.getUsername(), member2.getUsername(), 150L, AssetCategory.TRANSFER);

        Member updatedMember1 = memberRepository.findById(member1Id).orElseThrow(() -> new RuntimeException("Account not found"));
        Member updatedMember2 = memberRepository.findById(member2Id).orElseThrow(() -> new RuntimeException("Account not found"));

        assertEquals(150L, updatedMember1.getCash().getAmount());
        assertEquals(150L, updatedMember2.getCash().getAmount());
    }

    @Test
    @DisplayName("accumulation test")
    void t2() {
        cashService.accumulate(member1.getUsername(), 150L, AssetCategory.ANSWER);

        Member updatedMember1 = memberRepository.findById(member1Id).orElseThrow(() -> new RuntimeException("Account not found"));

        assertEquals(450, updatedMember1.getCash().getAmount());
    }

    @Test
    @DisplayName("deduction test")
    void t3() {
        cashService.deduct(member1.getUsername(), 150L, AssetCategory.ANSWER);

        Member updatedMember1 = memberRepository.findById(member1Id).orElseThrow(() -> new RuntimeException("Account not found"));

        assertEquals(150, updatedMember1.getCash().getAmount());
    }

    @Test
    @DisplayName("adminAccumulate 테스트")
    void t4() {
        Long historyId = cashService.adminAccumulate(member1.getUsername(), 10, 1);
        String name = assetHistoryRepository
                .findById(historyId).get().getAdminAssetCategory().getName();
        assertEquals("SYSTEM_COMPENSATION", name);
    }

    @Test
    @DisplayName("adminDeduct 서비스 테스트")
    void t5() {
        Long historyId = cashService.adminDeduct(member1.getUsername(), 10, 1);
        String name = assetHistoryRepository
                .findById(historyId).get().getAdminAssetCategory().getName();
        assertEquals("SYSTEM_COMPENSATION", name);
    }
}
