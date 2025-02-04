package com.NBE3_4_2_Team4.domain.member.member.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("로그아웃 실패 테스트 - 로그아웃 요청이 아닌 직접 로그아웃 완료 페이지로 접근 시")
    public void blockingAccessingLogoutCompleteDirectlyTest() throws Exception {
        mockMvc.perform(get("/api/logout/complete")
                        .with(csrf())
                )
                .andExpect(status().isBadRequest());
    }
}
