package com.NBE3_4_2_Team4.domain.asset;

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType;
import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository;
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetHistoryService;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Cash;
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Point;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest
public abstract class BaseAssetTestSetup {

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected AssetHistoryService assetHistoryService;

    @Autowired
    protected AssetHistoryRepository assetHistoryRepository;

    protected Member member1;
    protected Member member2;
    protected Long member1Id;
    protected Long member2Id;

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

        assetHistoryService.createHistory(member1, null, 10, AssetCategory.ANSWER, AssetType.POINT, "a");
        assetHistoryService.createHistory(member1, null, 15, AssetCategory.PURCHASE, AssetType.CASH, "b");
        assetHistoryService.createHistory(member1, null, 15, AssetCategory.PURCHASE, AssetType.POINT, "b");
        assetHistoryService.createHistory(member2, null, 10, AssetCategory.ANSWER, AssetType.CASH, "c");

        member1Id = member1.getId();
        member2Id = member2.getId();
    }
}
