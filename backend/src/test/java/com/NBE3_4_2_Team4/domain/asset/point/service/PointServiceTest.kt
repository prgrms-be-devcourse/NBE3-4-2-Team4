package com.NBE3_4_2_Team4.domain.asset.point.service

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.NBE3_4_2_Team4.domain.asset.main.repository.AdminAssetCategoryRepository
import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetHistoryService
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Cash
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Point
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository
import com.NBE3_4_2_Team4.global.exceptions.PointClientException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime


@ActiveProfiles("test")
@SpringBootTest
@Transactional
class PointServiceTest {
    @Autowired
    private lateinit var pointService: PointService

    @Autowired
    private lateinit var assetHistoryRepository: AssetHistoryRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var assetHistoryService: AssetHistoryService

    @Autowired
    private lateinit var adminAssetCategoryRepository: AdminAssetCategoryRepository

    private lateinit var member1: Member
    private lateinit var member2: Member
    private var member1Id: Long? = null
    private var member2Id: Long? = null

    @BeforeEach
    fun setup() {
        member1 = Member(
            point = Point(300L),
            cash = Cash(300L),
            role = Member.Role.USER,
            oAuth2Provider = Member.OAuth2Provider.NONE,
            username = "m1",
            nickname = "n1",
            password = "1234"
        )

        member2 = Member(
            point = Point(0L),
            cash = Cash(0L),
            role = Member.Role.USER,
            oAuth2Provider = Member.OAuth2Provider.NONE,
            username = "m2",
            nickname = "n2",
            password = "1234"
        )

        memberRepository.save(member1)
        memberRepository.save(member2)

        assetHistoryService.createHistory(member1, null, 10, AssetCategory.ANSWER, null, AssetType.POINT, "a")
        assetHistoryService.createHistory(member1, null, 15, AssetCategory.ANSWER, null, AssetType.POINT, "b")
        assetHistoryService.createHistory(member1, null, 15, AssetCategory.PURCHASE, null, AssetType.POINT, "b")
        assetHistoryService.createHistory(member2, null, 10, AssetCategory.ANSWER, null, AssetType.POINT, "c")

        member1Id = member1.id
        member2Id = member2.id
    }

    @Test
    @DisplayName("transfer test")
    fun t1() {
        Assertions.assertEquals(300L, member1.point.amount)

        pointService.transfer(member1.username, member2.username, 150L, AssetCategory.TRANSFER)

        val updatedMember1 = memberRepository.findById(
            member1Id!!
        ).orElseThrow { java.lang.RuntimeException("Account not found") }
        val updatedMember2 = memberRepository.findById(
            member2Id!!
        ).orElseThrow { java.lang.RuntimeException("Account not found") }

        Assertions.assertEquals(150L, updatedMember1.point.amount)
        Assertions.assertEquals(150L, updatedMember2.point.amount)
    }

    @Test
    @DisplayName("accumulation test")
    fun t2() {
        pointService.accumulate(member1.username, 150L, AssetCategory.ANSWER)

        val updatedMember1 = memberRepository.findById(
            member1Id!!
        ).orElseThrow { java.lang.RuntimeException("Account not found") }

        Assertions.assertEquals(450, updatedMember1.point.amount)
    }

    @Test
    @DisplayName("deduction test")
    fun t3() {
        pointService.deduct(member1.username, 150L, AssetCategory.ANSWER)

        val updatedMember1 = memberRepository.findById(
            member1Id!!
        ).orElseThrow { java.lang.RuntimeException("Account not found") }

        Assertions.assertEquals(150, updatedMember1.point.amount)
    }

    @Test
    @DisplayName("history creation test")
    fun t4() {
        val adminAssetCategory =
            adminAssetCategoryRepository.findByName("ABUSE_RESPONSE").orElseThrow { RuntimeException() }

        val id1 =
            assetHistoryService.createHistory(member1, null, 10, AssetCategory.ANSWER, null, AssetType.POINT, "a")
        val id2 = assetHistoryService.createHistory(
            member1,
            null,
            10,
            AssetCategory.ADMIN,
            adminAssetCategory,
            AssetType.POINT,
            "a"
        )

        val assetHistory = assetHistoryRepository.findById(id1).orElseThrow {
            java.lang.RuntimeException(
                "히스토리 없음"
            )
        }
        Assertions.assertEquals(member1.id, assetHistory.member.id)

        val assetHistory2 = assetHistoryRepository.findById(id2).orElseThrow { RuntimeException() }
        Assertions.assertEquals("ABUSE_RESPONSE", assetHistory2.adminAssetCategory!!.name)

        val createdAt = LocalDateTime.now()
        Assertions.assertTrue(Duration.between(assetHistory.createdAt, createdAt).seconds < 5)
    }

    @Test
    @DisplayName("출석 테스트")
    fun t6() {
        pointService.attend(member1Id!!)
        val updatedMember1 = memberRepository.findById(
            member1Id!!
        ).orElseThrow { java.lang.RuntimeException("Account not found") }
        Assertions.assertEquals(310, updatedMember1.point.amount)
        Assertions.assertEquals(LocalDate.now(), updatedMember1.lastAttendanceDate)
    }

    @Test
    @DisplayName("출석 실패 테스트")
    fun t7() {
        member1.lastAttendanceDate = LocalDate.now()
        memberRepository.flush()

        Assertions.assertThrows(PointClientException::class.java) {
            pointService.attend(member1Id!!)
        }
    }

    @Test
    @DisplayName("adminAccumulate 테스트")
    fun t8() {
        val historyId = pointService.adminAccumulate(member1.username, 10, 1)
        val name = assetHistoryRepository
            .findById(historyId).get().adminAssetCategory!!.name
        Assertions.assertEquals("SYSTEM_COMPENSATION", name)
    }

    @Test
    @DisplayName("adminDeduct 서비스 테스트")
    fun t9() {
        val historyId = pointService.adminDeduct(member1.username, 10, 1)
        val name = assetHistoryRepository
            .findById(historyId).get().adminAssetCategory!!.name
        Assertions.assertEquals("SYSTEM_COMPENSATION", name)
    }
}
