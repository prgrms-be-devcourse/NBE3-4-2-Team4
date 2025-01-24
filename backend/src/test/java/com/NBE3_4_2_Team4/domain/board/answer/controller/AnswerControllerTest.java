package com.NBE3_4_2_Team4.domain.board.answer.controller;

import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(post("/api/answers")
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
                .andExpect(jsonPath("$.rsCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("답변이 등록 되었습니다."))
                .andExpect(jsonPath("$.data.id").value(lastAnswer.getId()))
                //.andExpect(jsonPath("$.data.createDate").exists())
                //.andExpect(jsonPath("$.data.modifyDate").exists())
                .andExpect(jsonPath("$.data.content").value(lastAnswer.getContent()));
    }
}
