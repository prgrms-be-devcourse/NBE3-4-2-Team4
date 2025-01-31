package com.NBE3_4_2_Team4.domain.member.member.controller;

import com.NBE3_4_2_Team4.domain.member.dto.request.LoginRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loginSuccessTest() throws Exception {
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("admin@test.com")
                .password("1234")
                .build();

        String requestBody = objectMapper.writeValueAsString(loginRequestDto);

        mockMvc.perform(post("/api/login")
                        .with(csrf())
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void loginFailureTest1() throws Exception {
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("wrong@test.com")
                .password("1234")
                .build();

        String requestBody = objectMapper.writeValueAsString(loginRequestDto);

        mockMvc.perform(post("/api/login")
                        .with(csrf())
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void loginFailureTest2() throws Exception {
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("admin@test.com")
                .password("wrong password")
                .build();

        String requestBody = objectMapper.writeValueAsString(loginRequestDto);

        mockMvc.perform(post("/api/login")
                        .with(csrf())
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
