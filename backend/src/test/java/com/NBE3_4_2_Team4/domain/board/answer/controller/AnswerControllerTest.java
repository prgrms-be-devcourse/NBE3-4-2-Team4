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
    @DisplayName("존재하지 않는 11111111번 질문글에 답변 등록")
    @WithUserDetails("admin@test.com")
    void t1_3() throws Exception {
        ResultActions resultActions = mvc
                .perform(post("/api/questions/11111111/answers")
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
                .andExpect(handler().handlerType(AnswerController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result_code").value("404-1"))
                .andExpect(jsonPath("$.msg").value("해당 질문글이 존재하지 않습니다."));
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
                    .andExpect(jsonPath("$[%d].author_id".formatted(i)).value(answer.getAuthor().getId()))
                    .andExpect(jsonPath("$[%d].author_name".formatted(i)).value(answer.getAuthor().getNickname()))
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
                .andExpect(jsonPath("$.author_id").value(answer.getAuthor().getId()))
                .andExpect(jsonPath("$.author_name").value(answer.getAuthor().getNickname()))
                .andExpect(jsonPath("$.content").value(answer.getContent()));
    }

    @Test
    @DisplayName("존재하지 않는 11111111번 답변 조회")
    void t3_1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/answers/11111111")
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(AllAnswerController.class))
                .andExpect(handler().methodName("item"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result_code").value("404-2"))
                .andExpect(jsonPath("$.msg").value("해당 답변은 존재하지 않습니다."));
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
    @DisplayName("답변 수정, with no input")
    @WithUserDetails("admin@test.com")
    void t4_1() throws Exception {
        ResultActions resultActions = mvc
                .perform(patch("/api/questions/1/answers/1")
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
                .andExpect(handler().methodName("modify"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result_code").value("400-1"))
                .andExpect(jsonPath("$.msg").value("""
                        content-Length-length must be between 2 and 2147483647
                        content-NotBlank-must not be blank
                        """.stripIndent().trim()));
    }

    @Test
    @DisplayName("답변 수정, with no actor")
    void t4_2() throws Exception {
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

        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.result_code").value("401-1"))
                .andExpect(jsonPath("$.msg").value("Unauthorized"));
    }

    @Test
    @DisplayName("답변 수정, with wrong actor")
    @WithUserDetails("test@test.com")
    void t4_3() throws Exception {
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

        resultActions
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.result_code").value("403-2"))
                .andExpect(jsonPath("$.msg").value("작성자만 답변을 수정할 수 있습니다."));
    }

    @Test
    @DisplayName("존재하지 않는 11111111번 답변 수정")
    @WithUserDetails("test@test.com")
    void t4_4() throws Exception {
        ResultActions resultActions = mvc
                .perform(patch("/api/questions/1/answers/11111111")
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
                .andExpect(handler().handlerType(AnswerController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result_code").value("404-2"))
                .andExpect(jsonPath("$.msg").value("해당 답변은 존재하지 않습니다."));
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
    @DisplayName("답변 삭제, with no actor")
    void t5_1() throws Exception {
        ResultActions resultActions = mvc
                .perform(delete("/api/questions/1/answers/1"))
                .andDo(print());

        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.result_code").value("401-1"))
                .andExpect(jsonPath("$.msg").value("Unauthorized"));
    }

    @Test
    @DisplayName("답변 삭제, with wrong actor")
    @WithUserDetails("test@test.com")
    void t5_2() throws Exception {
        ResultActions resultActions = mvc
                .perform(delete("/api/questions/1/answers/1"))
                .andDo(print());

        resultActions
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.result_code").value("403-2"))
                .andExpect(jsonPath("$.msg").value("작성자만 답변을 삭제할 수 있습니다."));
    }

    @Test
    @DisplayName("존재하지 않는 11111111번 답변 삭제")
    @WithUserDetails("test@test.com")
    void t5_3() throws Exception {
        ResultActions resultActions = mvc
                .perform(delete("/api/questions/1/answers/11111111"))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(AnswerController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result_code").value("404-2"))
                .andExpect(jsonPath("$.msg").value("해당 답변은 존재하지 않습니다."));
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
                    .andExpect(jsonPath("$[%d].author_id".formatted(i)).value(answer.getAuthor().getId()))
                    .andExpect(jsonPath("$[%d].author_name".formatted(i)).value(answer.getAuthor().getNickname()))
                    .andExpect(jsonPath("$[%d].content".formatted(i)).value(answer.getContent()));
        }
    }

    @Test
    @DisplayName("존재하지 않는 11111111번 질문글 내 전체 답변 조회")
    void t6_1() throws Exception {
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
