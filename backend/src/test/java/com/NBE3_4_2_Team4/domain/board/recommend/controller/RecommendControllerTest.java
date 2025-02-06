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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @DisplayName("2번 질문 추천")
    @WithUserDetails("test@test.com")
    void t1() throws Exception {
        ResultActions resultActions = mvc.perform(
                post("/api/questions/2/recommend")
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
                delete("/api/questions/1/recommend")
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo(print());

        resultActions.andExpect(handler().handlerType(RecommendController.class))
                .andExpect(handler().methodName("cancelRecommend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result_code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("게시글 추천을 취소하었습니다."));
    }

    @Test
    @DisplayName("이미 추천한 1번 질문 중복 추천 방지")
    @WithUserDetails("test@test.com")
    void t3() throws Exception {
        ResultActions resultActions = mvc.perform(
                post("/api/questions/1/recommend")
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo(print());

        resultActions.andExpect(handler().handlerType(RecommendController.class))
                .andExpect(handler().methodName("recommend"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result_code").value("400-1"))
                .andExpect(jsonPath("$.msg").value("이미 추천한 게시글입니다."));
    }

    @Test
    @DisplayName("본인 글 추천 방지")
    @WithUserDetails("admin@test.com")
    void t4() throws Exception {
        ResultActions resultActions = mvc.perform(
                post("/api/questions/1/recommend")
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo(print());

        resultActions.andExpect(handler().handlerType(RecommendController.class))
                .andExpect(handler().methodName("recommend"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result_code").value("400-2"))
                .andExpect(jsonPath("$.msg").value("자신의 게시글은 추천할 수 없습니다."));
    }
}
