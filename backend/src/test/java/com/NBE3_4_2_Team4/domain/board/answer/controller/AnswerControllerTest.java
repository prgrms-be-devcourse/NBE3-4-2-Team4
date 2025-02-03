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
                .andExpect(jsonPath("$.data.content").value(lastAnswer.getContent()));
    }

    @Test
    @DisplayName("전체 답변 조회")
    void t2() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/api/answers"))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(AllAnswerController.class))
                .andExpect(handler().methodName("items"))
                .andExpect(status().isOk());

        List<Answer> answers = answerService.findAll();

        for(int i = 0; i < answers.size(); i++) {
            Answer answer = answers.get(i);

            resultActions
                    .andExpect(jsonPath("$[%d].id".formatted(i)).value(answer.getId()))
                    .andExpect(jsonPath("$[%d].created_at".formatted(i)).exists())
                    .andExpect(jsonPath("$[%d].modified_at".formatted(i)).exists())
                    .andExpect(jsonPath("$[%d].question_id".formatted(i)).value(answer.getQuestion().getId()))
                    .andExpect(jsonPath("$[%d].content".formatted(i)).value(answer.getContent()));
        }
    }

    @Test
    @DisplayName("단건 조회")
    void t3() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/api/answers/1"))
                .andDo(print());

        Answer answer = answerService.findById(1).get();

        resultActions
                .andExpect(handler().handlerType(AllAnswerController.class))
                .andExpect(handler().methodName("item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(answer.getId()))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.modified_at").exists())
                .andExpect(jsonPath("$.question_id").value(answer.getQuestion().getId()))
                .andExpect(jsonPath("$.content").value(answer.getContent()));
    }

    @Test
    @DisplayName("답변 수정")
    @WithUserDetails("admin@test.com")
    void t4() throws Exception {
        ResultActions resultActions = mvc
                .perform(patch("/api/questions/1/answers/1")
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

        Answer answer = answerService.findById(1).get();

        resultActions
                .andExpect(handler().handlerType(AnswerController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result_code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 답변이 수정 되었습니다.".formatted(answer.getId())))
                .andExpect(jsonPath("$.data.id").value(answer.getId()))
                .andExpect(jsonPath("$.data.created_at").exists())
                .andExpect(jsonPath("$.data.modified_at").exists())
                .andExpect(jsonPath("$.data.question_id").value(answer.getQuestion().getId()))
                .andExpect(jsonPath("$.data.content").value(answer.getContent()));
    }

    @Test
    @DisplayName("답변 삭제")
    @WithUserDetails("admin@test.com")
    void t5() throws Exception {
        Answer answer = answerService.findById(1).get();

        ResultActions resultActions = mvc
                .perform(delete("/api/questions/1/answers/1"))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(AnswerController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result_code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 답변이 삭제 되었습니다.".formatted(answer.getId())));
    }

    @Test
    @DisplayName("특정 질문글 내 전체 답변 조회")
    void t6() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/api/questions/1/answers"))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(AnswerController.class))
                .andExpect(handler().methodName("items"))
                .andExpect(status().isOk());

        Question question = questionService.findById(1).get();
        List<Answer> answers = answerService.findByQuestionOrderByIdDesc(question);

        for(int i = 0; i < answers.size(); i++) {
            Answer answer = answers.get(i);

            resultActions
                    .andExpect(jsonPath("$[%d].id".formatted(i)).value(answer.getId()))
                    .andExpect(jsonPath("$[%d].created_at".formatted(i)).exists())
                    .andExpect(jsonPath("$[%d].modified_at".formatted(i)).exists())
                    .andExpect(jsonPath("$[%d].question_id".formatted(i)).value(answer.getQuestion().getId()))
                    .andExpect(jsonPath("$[%d].content".formatted(i)).value(answer.getContent()));
        }
    }
}
