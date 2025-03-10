package com.NBE3_4_2_Team4.domain.member.member.controller

import com.NBE3_4_2_Team4.domain.member.member.dto.AdminLoginRequestDto
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class MemberControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun adminLoginTest() {
        val adminLoginRequestDto = AdminLoginRequestDto(
            adminUsername = "admin@test.com",
            password = "1234"
        )

        val requestBody = objectMapper.writeValueAsString(adminLoginRequestDto)

        mockMvc.perform(post("/api/admin/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk)
            .andDo(print())
    }

    @Test
    @DisplayName("로그아웃 실패 테스트 - 로그아웃 요청이 아닌 직접 로그아웃 완료 페이지로 접근 시")
    fun blockingAccessingLogoutCompleteDirectlyTest() {
        mockMvc.perform(get("/api/logout/complete")
            .with(csrf()))
            .andExpect(status().isBadRequest)
    }
}
