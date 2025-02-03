package com.NBE3_4_2_Team4.domain.board.answer.controller;

import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService;
import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class AnswerControllerTest {
    @Autowired
    QuestionService questionService;
    @Autowired
    AnswerService answerService;
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("답변 등록")
    @WithUserDetails("admin@test.com")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(post("/api/questions/1/answers")
                        .content("""
                                {
                                    "content": "답변 내용 new"
                                }
                                """.stripIndent())
                        .contentType(
                                new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                        )
                )
                .andDo(print());


        Answer lastAnswer = answerService.findLatest().get();

        resultActions
                .andExpect(handler().handlerType(AnswerController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result_code").value("201-1"))
                .andExpect(jsonPath("$.msg").value("%d번 답변이 등록 되었습니다.".formatted(lastAnswer.getId())))
                .andExpect(jsonPath("$.data.id").value(lastAnswer.getId()))
                .andExpect(jsonPath("$.data.created_at").exists())
                .andExpect(jsonPath("$.data.modified_at").exists())
                .andExpect(jsonPath("$.data.question_id").value(lastAnswer.getQuestion().getId()))
                .andExpect(jsonPath("$.data.author_id").value(lastAnswer.getAuthor().getId()))
                .andExpect(jsonPath("$.data.author_name").value(lastAnswer.getAuthor().getNickname()))
                .andExpect(jsonPath("$.data.content").value(lastAnswer.getContent()));
    }

    @Test
    @DisplayName("답변 등록, with no input")
    @WithUserDetails("admin@test.com")
    void t1_1() throws Exception {
        ResultActions resultActions = mvc
                .perform(post("/api/questions/1/answers")
                        .content("""
                                {
                                    "content": ""
                                }
                                """.stripIndent())
                        .contentType(
                                new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                        )
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(AnswerController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result_code").value("400-1"))
                .andExpect(jsonPath("$.msg").value("""
                        content-Length-length must be between 2 and 2147483647
                        content-NotBlank-must not be blank
                        """.stripIndent().trim()));
    }

    @Test
    @DisplayName("답변 등록, with no actor")
    void t1_2() throws Exception {
        ResultActions resultActions = mvc
                .perform(post("/api/questions/1/answers")
                        .content("""
                                {
                                    "content": "답변 내용 new"
                                }
                                """.stripIndent())
                        .contentType(
                                new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                        )
                )
                .andDo(print());

        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.result_code").value("401-1"))
                .andExpect(jsonPath("$.msg").value("Unauthorized"));
    }

    @Test
    @DisplayName("특정 질문글 내 전체 답변 조회")
    void t2() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/api/questions/1/answers"))
                .andDo(print());

        Question question = questionService.findById(1).get();
        Page<Answer> answersPage = answerService
                .findByQuestion(question, 1, 10);

        resultActions
                .andExpect(handler().handlerType(AnswerController.class))
                .andExpect(handler().methodName("items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page_number").value(1))
                .andExpect(jsonPath("$.page_size").value(10))
                .andExpect(jsonPath("$.total_pages").value(answersPage.getTotalPages()))
                .andExpect(jsonPath("$.total_items").value(answersPage.getTotalElements()))
                .andExpect(jsonPath("$.has_more").value(answersPage.hasNext()));


        List<Answer> answers = answersPage.getContent();

        for(int i = 0; i < answers.size(); i++) {
            Answer answer = answers.get(i);

            resultActions
                    .andExpect(jsonPath("$.items[%d].id".formatted(i)).value(answer.getId()))
                    .andExpect(jsonPath("$.items[%d].created_at".formatted(i)).exists())
                    .andExpect(jsonPath("$.items[%d].modified_at".formatted(i)).exists())
                    .andExpect(jsonPath("$.items[%d].question_id".formatted(i)).value(answer.getQuestion().getId()))
                    .andExpect(jsonPath("$.items[%d].author_id".formatted(i)).value(answer.getAuthor().getId()))
                    .andExpect(jsonPath("$.items[%d].author_name".formatted(i)).value(answer.getAuthor().getNickname()))
                    .andExpect(jsonPath("$.items[%d].content".formatted(i)).value(answer.getContent()));
        }
    }

    @Test
    @DisplayName("존재하지 않는 11111111번 질문글 내 전체 답변 조회")
    void t2_1() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/api/questions/11111111/answers"))
                .andDo(print());

    resultActions
            .andExpect(handler().handlerType(AnswerController.class))
            .andExpect(handler().methodName("items"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.result_code").value("404-1"))
            .andExpect(jsonPath("$.msg").value("해당 질문글이 존재하지 않습니다."));
    }
}
