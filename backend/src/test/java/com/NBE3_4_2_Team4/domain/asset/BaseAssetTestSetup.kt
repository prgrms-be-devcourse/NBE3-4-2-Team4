package com.NBE3_4_2_Team4.domain.asset

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetHistoryService
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Cash
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Point
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@ExtendWith(
    SpringExtension::class
)
@SpringBootTest
abstract class BaseAssetTestSetup {
    @Autowired
    protected lateinit var memberRepository: MemberRepository

    @Autowired
    protected lateinit var assetHistoryService: AssetHistoryService

    @Autowired
    protected lateinit var assetHistoryRepository: AssetHistoryRepository

    protected lateinit var member1: Member
    protected lateinit var member2: Member
    protected var member1Id: Long ?= null
    protected var member2Id: Long ?= null

    @BeforeEach
    fun setup() {
        member1 = Member.builder()
            .point(Point())
            .cash(Cash(300L))
            .role(Member.Role.USER)
            .oAuth2Provider(Member.OAuth2Provider.NONE)
            .username("m1")
            .nickname("n1")
            .password("1234")
            .build()

        member2 = Member.builder()
            .point(Point())
            .cash(Cash(0L))
            .role(Member.Role.USER)
            .oAuth2Provider(Member.OAuth2Provider.NONE)
            .username("m2")
            .nickname("n2")
            .password("1234")
            .build()

        memberRepository.save(member1)
        memberRepository.save(member2)

        assetHistoryService.createHistory(member1, null, 10, AssetCategory.ANSWER, null, AssetType.POINT, "a")
        assetHistoryService.createHistory(member1, null, 15, AssetCategory.PURCHASE, null, AssetType.CASH, "b")
        assetHistoryService.createHistory(member1, null, 15, AssetCategory.PURCHASE, null, AssetType.POINT, "b")
        assetHistoryService.createHistory(member2, null, 10, AssetCategory.ANSWER, null, AssetType.CASH, "c")

        member1Id = member1.getId()
        member2Id = member2.getId()
    }
}
