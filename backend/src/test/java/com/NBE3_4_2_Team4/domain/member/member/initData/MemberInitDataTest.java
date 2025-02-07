package com.NBE3_4_2_Team4.domain.member.member.initData;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class MemberInitDataTest {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("관리자 초기 데이터 생성으로 만들어진 데이터 테스트")
    void testFindInitAdminMember() throws Exception {
        // admin 계정을 가져옴
        Member admin = memberRepository.findByUsername("admin@test.com").orElseThrow();
        assertNotNull(admin);
        assertEquals("관리자", admin.getNickname());
        assertEquals(Member.Role.ADMIN, admin.getRole());
    }
}