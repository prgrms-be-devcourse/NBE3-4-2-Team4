package com.NBE3_4_2_Team4.domain.member.member.initData


import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import jakarta.transaction.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class MemberInitDataTest {

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Value("\${custom.initData.member.admin.username}")
    private lateinit var adminUsername: String

    @Value("\${custom.initData.member.admin.nickname}")
    private lateinit var adminNickname: String

    @Test
    @DisplayName("관리자 초기 데이터 생성으로 만들어진 데이터 테스트")
    fun testFindInitAdminMember() {
        // admin 계정을 가져옴
        val admin = memberRepository.findByUsername(adminUsername)
        assertNotNull(admin)
        assertEquals(adminNickname, admin.nickname)
        assertEquals(Member.Role.ADMIN, admin.role)
    }
}
