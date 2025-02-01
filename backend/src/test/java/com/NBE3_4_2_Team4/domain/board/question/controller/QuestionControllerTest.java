package com.NBE3_4_2_Team4.domain.board.question.controller;

import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.entity.QuestionCategory;
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

        resultActions.andExpect(handler().handlerType(QuestionController.class))
                .andExpect(handler().methodName("getQuestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6));
    }

    @Test
    @DisplayName("1번 게시글 조회")
    void t3() throws Exception {
        ResultActions resultActions = mvc.perform(get("/api/questions/1"))
                .andDo(print());

        resultActions.andExpect(handler().handlerType(QuestionController.class))
                .andExpect(handler().methodName("getQuestion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title1"))
                .andExpect(jsonPath("$.content").value("content1"));
    }

    @Test
    @DisplayName("게시글 작성")
    void t4() {
        QuestionCategory category = questionService.createCategory("category1");
        Question question = questionService.write("title4", "content4", category.getId());

        assertThat(question.getId()).isEqualTo(4);
        assertThat(question.getTitle()).isEqualTo("title4");
        assertThat(question.getContent()).isEqualTo("content4");
    }

    @Test
    @DisplayName("1번 게시글 삭제")
    void t5() {
        questionService.delete(1L);
        List<Question> questions = questionService.findAll();

        assertThat(questions).hasSize(2);
    }

    @Test
    @DisplayName("1번 게시글 수정")
    void t6() {
        Question question = questionService.findById(1L);
        questionService.update(question, "title1 수정", "content1 수정");

        assertThat(question.getId()).isEqualTo(1);
        assertThat(question.getTitle()).isEqualTo("title1 수정");
        assertThat(question.getContent()).isEqualTo("content1 수정");
    }
}
