package com.NBE3_4_2_Team4.domain.board.answer.controller;

import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService;
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
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("답변이 등록 되었습니다."))
                .andExpect(jsonPath("$.data.id").value(lastAnswer.getId()))
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.modifiedAt").exists())
                .andExpect(jsonPath("$.data.questionId").value(lastAnswer.getQuestion().getId()))
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
                    .andExpect(jsonPath("$[%d].createdAt".formatted(i)).exists())
                    .andExpect(jsonPath("$[%d].modifiedAt".formatted(i)).exists())
                    .andExpect(jsonPath("$[%d].questionId".formatted(i)).value(answer.getQuestion().getId()))
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
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.modifiedAt").exists())
                .andExpect(jsonPath("$.questionId").value(answer.getQuestion().getId()))
                .andExpect(jsonPath("$.content").value(answer.getContent()));
    }

    @Test
    @DisplayName("답변 수정")
    @WithUserDetails("admin@test.com")
    void t4() throws Exception {
        ResultActions resultActions = mvc
                .perform(put("/api/questions/1/answers/1")
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
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("답변이 수정 되었습니다."))
                .andExpect(jsonPath("$.data.id").value(answer.getId()))
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.modifiedAt").exists())
                .andExpect(jsonPath("$.data.questionId").value(answer.getQuestion().getId()))
                .andExpect(jsonPath("$.data.content").value(answer.getContent()));
    }
}
