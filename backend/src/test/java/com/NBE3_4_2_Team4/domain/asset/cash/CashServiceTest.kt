package com.NBE3_4_2_Team4.domain.asset.cash

import com.NBE3_4_2_Team4.domain.asset.cash.service.CashService
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetHistoryService
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Cash
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Point
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class CashServiceTest {
    @Autowired
    private lateinit var cashService: CashService

    @Autowired
    private lateinit var assetHistoryRepository: AssetHistoryRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var assetHistoryService: AssetHistoryService

    private lateinit var member1: Member
    private lateinit var member2: Member
    private var member1Id: Long ?= null
    private var member2Id: Long ?= null

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

        assetHistoryService.createHistory(member1, null, 10, AssetCategory.ANSWER, null, AssetType.CASH, "a")
        assetHistoryService.createHistory(member1, null, 15, AssetCategory.ANSWER, null, AssetType.CASH, "b")
        assetHistoryService.createHistory(member1, null, 15, AssetCategory.PURCHASE, null, AssetType.CASH, "b")
        assetHistoryService.createHistory(member2, null, 10, AssetCategory.ANSWER, null, AssetType.CASH, "c")

        member1Id = member1.getId()
        member2Id = member2.getId()
    }

    @Test
    @DisplayName("transfer test")
    fun t1() {
        cashService.transfer(member1.getUsername(), member2.getUsername(), 150L, AssetCategory.TRANSFER)

        val updatedMember1 = memberRepository.findById(
            member1Id!!
        ).orElseThrow { RuntimeException("Account not found") }
        val updatedMember2 = memberRepository.findById(
            member2Id!!
        ).orElseThrow { RuntimeException("Account not found") }

        Assertions.assertEquals(150L, updatedMember1.getCash().amount)
        Assertions.assertEquals(150L, updatedMember2.getCash().amount)
    }

    @Test
    @DisplayName("accumulation test")
    fun t2() {
        cashService.accumulate(member1.getUsername(), 150L, AssetCategory.ANSWER)

        val updatedMember1 = memberRepository.findById(
            member1Id!!
        ).orElseThrow { RuntimeException("Account not found") }

        Assertions.assertEquals(450, updatedMember1.getCash().amount)
    }

    @Test
    @DisplayName("deduction test")
    fun t3() {
        cashService.deduct(member1.getUsername(), 150L, AssetCategory.ANSWER)

        val updatedMember1 = memberRepository.findById(
            member1Id!!
        ).orElseThrow { RuntimeException("Account not found") }

        Assertions.assertEquals(150, updatedMember1.getCash().amount)
    }

    @Test
    @DisplayName("adminAccumulate 테스트")
    fun t4() {
        val historyId = cashService.adminAccumulate(member1.getUsername(), 10, 1)
        val name = assetHistoryRepository
            .findById(historyId).get().adminAssetCategory!!.name
        Assertions.assertEquals("SYSTEM_COMPENSATION", name)
    }

    @Test
    @DisplayName("adminDeduct 서비스 테스트")
    fun t5() {
        val historyId = cashService.adminDeduct(member1.getUsername(), 10, 1)
        val name = assetHistoryRepository
            .findById(historyId).get().adminAssetCategory!!.name
        Assertions.assertEquals("SYSTEM_COMPENSATION", name)
    }
}
