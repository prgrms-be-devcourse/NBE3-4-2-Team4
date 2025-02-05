package com.NBE3_4_2_Team4.domain.board.question.controller;

import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService;
import org.hamcrest.Matchers;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class QuestionControllerTest {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("전체 게시글 조회")
    void t1() {
        List<Question> questions = questionService.findAll();
        assertThat(questions).hasSize(20);
    }

    @Test
    @DisplayName("다건 조회 with paging")
    void t2() throws Exception {
        ResultActions resultActions = mvc.perform(get("/api/questions?page=3&pageSize=7"))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(QuestionController.class))
                .andExpect(handler().methodName("getQuestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page_number").value(3))
                .andExpect(jsonPath("$.page_size").value(7))
                .andExpect(jsonPath("$.total_items").value(20))
                .andExpect(jsonPath("$.has_more").value(false))
                .andExpect(jsonPath("$.items.length()").value(6));
    }

    @Test
    @DisplayName("1번 게시글 조회")
    void t3() throws Exception {
        ResultActions resultActions = mvc.perform(get("/api/questions/1"))
                .andDo(print());

        Question question = questionService.findById(1L).orElseThrow();

        resultActions.andExpect(handler().handlerType(QuestionController.class))
                .andExpect(handler().methodName("getQuestion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.created_at").value(Matchers.startsWith(question.getCreatedAt().toString().substring(0, 25))))
                .andExpect(jsonPath("$.modified_at").value(Matchers.startsWith(question.getModifiedAt().toString().substring(0, 25))))
                .andExpect(jsonPath("$.title").value("title1"))
                .andExpect(jsonPath("$.content").value("content1"))
                .andExpect(jsonPath("$.name").value("관리자"));
    }

    @Test
    @DisplayName("게시글 작성")
    @WithUserDetails("admin@test.com")
    void t4() throws Exception {
        ResultActions resultActions = mvc.perform(
                post("/api/questions")
                        .content("""
                                {
                                    "title": "title21",
                                    "content": "content21",
                                    "category_id": 1
                                }
                                """)
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo(print());

        Question question = questionService.findLatest().get();

        resultActions.andExpect(handler().handlerType(QuestionController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result_code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("21번 게시글 생성이 완료되었습니다."))
                .andExpect(jsonPath("$.data.item.id").value(21L))
                .andExpect(jsonPath("$.data.item.title").value("title21"))
                .andExpect(jsonPath("$.data.item.content").value("content21"))
                .andExpect(jsonPath("$.data.item.category_name").value("category1"))
                .andExpect(jsonPath("$.data.item.created_at").value(Matchers.startsWith(question.getCreatedAt().toString().substring(0, 25))))
                .andExpect(jsonPath("$.data.item.modified_at").value(Matchers.startsWith(question.getCreatedAt().toString().substring(0, 25))))
                .andExpect(jsonPath("$.data.total_count").value(21L));
    }

    @Test
    @DisplayName("1번 게시글 삭제")
    @WithUserDetails("admin@test.com")
    void t5() throws Exception {
        ResultActions resultActions = mvc.perform(
                delete("/api/questions/1")
        ).andDo(print());

        resultActions.andExpect(handler().handlerType(QuestionController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result_code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("게시글 삭제가 완료되었습니다."));
    }

    @Test
    @DisplayName("1번 게시글 수정")
    @WithUserDetails("admin@test.com")
    void t6() throws Exception {
        ResultActions resultActions = mvc.perform(
                put("/api/questions/1")
                        .content("""
                                {
                                    "title": "title1 수정",
                                    "content": "content1 수정",
                                    "category_id": 1
                                }
                                """)
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo(print());

        resultActions.andExpect(handler().handlerType(QuestionController.class))
                .andExpect(handler().methodName("update"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result_code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("1번 게시글 수정이 완료되었습니다."));
    }

    @Test
    @DisplayName("게시글 검색")
    void t7() throws Exception {
        ResultActions resultActions = mvc.perform(
                get("/api/questions?searchKeyword=0")
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(QuestionController.class))
                .andExpect(handler().methodName("getQuestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page_number").value(1))
                .andExpect(jsonPath("$.page_size").value(10))
                .andExpect(jsonPath("$.total_pages").value(1))
                .andExpect(jsonPath("$.total_items").value(2))
                .andExpect(jsonPath("$.has_more").value(false))
                .andExpect(jsonPath("$.items.length()").value(2));
    }
}
