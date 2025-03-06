package com.NBE3_4_2_Team4.domain.board.recommend.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class RecommendControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("3번 질문 추천")
    @WithUserDetails("test@test.com")
    void t1() throws Exception {
        ResultActions resultActions = mvc.perform(
                put("/api/questions/3/recommend")
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo(print());

        resultActions.andExpect(handler().handlerType(RecommendController.class))
                .andExpect(handler().methodName("recommend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result_code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("게시글 추천이 완료되었습니다."));
    }

    @Test
    @DisplayName("1번 질문 추천 취소")
    @WithUserDetails("test@test.com")
    void t2() throws Exception {
        ResultActions resultActions = mvc.perform(
                put("/api/questions/1/recommend")
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo(print());

        resultActions.andExpect(handler().handlerType(RecommendController.class))
                .andExpect(handler().methodName("recommend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result_code").value("200-2"))
                .andExpect(jsonPath("$.msg").value("게시글 추천을 취소하였습니다."));
    }

    @Test
    @DisplayName("본인 글 추천 방지")
    @WithUserDetails("admin@test.com")
    void t3() throws Exception {
        ResultActions resultActions = mvc.perform(
                put("/api/questions/1/recommend")
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo(print());

        resultActions.andExpect(handler().handlerType(RecommendController.class))
                .andExpect(handler().methodName("recommend"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result_code").value("400-2"))
                .andExpect(jsonPath("$.msg").value("자신의 게시글은 추천할 수 없습니다."));
    }
}
