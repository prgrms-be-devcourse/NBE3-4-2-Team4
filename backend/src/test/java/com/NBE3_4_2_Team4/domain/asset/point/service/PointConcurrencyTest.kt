package com.NBE3_4_2_Team4.domain.asset.point.service

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory
import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Point
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository
import com.NBE3_4_2_Team4.standard.util.test.ConcurrencyTestUtil
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ActiveProfiles("test")
@SpringBootTest
class PointConcurrencyTest {
    @Autowired
    private lateinit var pointService: PointService

    @Autowired
    private lateinit var assetHistoryRepository: AssetHistoryRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    private lateinit var member1: Member
    private lateinit var member2: Member
    private var member1Id: Long? = null
    private var member2Id: Long? = null

    @BeforeEach
    fun setup() {
        member1 = Member.builder()
            .point(Point(300L))
            .role(Member.Role.USER)
            .oAuth2Provider(Member.OAuth2Provider.NONE)
            .username("point_test_" + UUID.randomUUID())
            .nickname("point_test_" + UUID.randomUUID())
            .password("1234")
            .build()

        member2 = Member.builder()
            .point(Point(0L))
            .role(Member.Role.USER)
            .oAuth2Provider(Member.OAuth2Provider.NONE)
            .username("point_test_" + UUID.randomUUID())
            .nickname("point_test_" + UUID.randomUUID())
            .password("1234")
            .build()

        memberRepository.save(member1)
        memberRepository.save(member2)

        member1Id = member1.getId()
        member2Id = member2.getId()
    }

    @AfterEach
    fun cleanUp() {
        val member1Histories = assetHistoryRepository.findByMemberId(member1Id!!)
        val member2Histories = assetHistoryRepository.findByMemberId(member2Id!!)

        assetHistoryRepository.deleteAll(member1Histories)
        assetHistoryRepository.deleteAll(member2Histories)

        //assetHistoryRepository.deleteAll();
        memberRepository.deleteById(member1Id!!)
        memberRepository.deleteById(member2Id!!)
    }

    @Test
    @DisplayName("동시성 테스트")
    fun t1() {
        ConcurrencyTestUtil.execute<Any?>(10) {
            pointService.transfer(member1.getUsername(), member2.getUsername(), 10, AssetCategory.TRANSFER)
            null
        }

        val updatedMember1 = memberRepository.findById(
            member1Id!!
        ).orElseThrow { RuntimeException("Account not found") }
        val updatedMember2 = memberRepository.findById(
            member2Id!!
        ).orElseThrow { RuntimeException("Account not found") }

        Assertions.assertEquals(200L, updatedMember1.getPoint().amount)
        Assertions.assertEquals(100L, updatedMember2.getPoint().amount)
    }
}
