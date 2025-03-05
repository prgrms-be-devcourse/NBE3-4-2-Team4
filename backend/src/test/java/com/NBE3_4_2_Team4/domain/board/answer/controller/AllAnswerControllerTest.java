package com.NBE3_4_2_Team4.domain.board.answer.controller;

import com.NBE3_4_2_Team4.domain.board.answer.dto.AnswerDto;
import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService;
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
public class AllAnswerControllerTest {
    @Autowired
    QuestionService questionService;
    @Autowired
    AnswerService answerService;
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("전체 답변 조회")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/api/answers"))
                .andDo(print());

        Page<AnswerDto> answersPage = answerService
                .itemsAll(1, 10);

        resultActions
                .andExpect(handler().handlerType(AllAnswerController.class))
                .andExpect(handler().methodName("items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page_number").value(1))
                .andExpect(jsonPath("$.page_size").value(10))
                .andExpect(jsonPath("$.total_pages").value(answersPage.getTotalPages()))
                .andExpect(jsonPath("$.total_items").value(answersPage.getTotalElements()))
                .andExpect(jsonPath("$.has_more").value(answersPage.hasNext()));

        List<AnswerDto> answers = answersPage.getContent();

        for(int i = 0; i < answers.size(); i++) {
            AnswerDto answer = answers.get(i);

            resultActions
                    .andExpect(jsonPath("$.items[%d].id".formatted(i)).value(answer.getId()))
                    .andExpect(jsonPath("$.items[%d].created_at".formatted(i)).exists())
                    .andExpect(jsonPath("$.items[%d].modified_at".formatted(i)).exists())
                    .andExpect(jsonPath("$.items[%d].question_id".formatted(i)).value(answer.getQuestionId()))
                    .andExpect(jsonPath("$.items[%d].author_id".formatted(i)).value(answer.getAuthorId()))
                    .andExpect(jsonPath("$.items[%d].author_name".formatted(i)).value(answer.getAuthorName()))
                    .andExpect(jsonPath("$.items[%d].content".formatted(i)).value(answer.getContent()))
                    .andExpect(jsonPath("$.items[%d].selected".formatted(i)).value(answer.getSelected()))
                    .andExpect(jsonPath("$.items[%d].selected_at".formatted(i)).value(answer.getSelectedAt()));
        }
    }

    @Test
    @DisplayName("단건 조회")
    void t2() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/api/answers/1"))
                .andDo(print());

        AnswerDto answer = answerService.item(1);

        resultActions
                .andExpect(handler().handlerType(AllAnswerController.class))
                .andExpect(handler().methodName("item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(answer.getId()))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.modified_at").exists())
                .andExpect(jsonPath("$.question_id").value(answer.getQuestionId()))
                .andExpect(jsonPath("$.author_id").value(answer.getAuthorId()))
                .andExpect(jsonPath("$.author_name").value(answer.getAuthorName()))
                .andExpect(jsonPath("$.content").value(answer.getContent()))
                .andExpect(jsonPath("$.selected").value(answer.getSelected()))
                .andExpect(jsonPath("$.selected_at").value(answer.getSelectedAt()));
    }

    @Test
    @DisplayName("존재하지 않는 11111111번 답변 조회")
    void t2_1() throws Exception {
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
    @WithUserDetails("test@test.com")
    void t3() throws Exception {
        ResultActions resultActions = mvc
                .perform(patch("/api/answers/1")
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

        Answer answer = answerService.findById(1);

        resultActions
                .andExpect(handler().handlerType(AllAnswerController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result_code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 답변이 수정 되었습니다.".formatted(answer.getId())))
                .andExpect(jsonPath("$.data.id").value(answer.getId()))
                .andExpect(jsonPath("$.data.created_at").exists())
                .andExpect(jsonPath("$.data.modified_at").exists())
                .andExpect(jsonPath("$.data.question_id").value(answer.getQuestion().getId()))
                .andExpect(jsonPath("$.data.content").value("답변 내용 new"))
                .andExpect(jsonPath("$.data.selected").value(answer.isSelected()))
                .andExpect(jsonPath("$.data.selected_at").value(answer.getSelectedAt()));
    }

    @Test
    @DisplayName("답변 수정, with no input")
    @WithUserDetails("test@test.com")
    void t3_1() throws Exception {
        ResultActions resultActions = mvc
                .perform(patch("/api/answers/1")
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
                .andExpect(handler().handlerType(AllAnswerController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result_code").value("400-1"))
                .andExpect(jsonPath("$.msg").value("""
                        content-NotBlank-must not be blank
                        content-Size-size must be between 2 and 2147483647
                        """.stripIndent().trim()));
    }

    @Test
    @DisplayName("답변 수정, with no actor")
    void t3_2() throws Exception {
        ResultActions resultActions = mvc
                .perform(patch("/api/answers/1")
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
    @WithUserDetails("admin@test.com")
    void t3_3() throws Exception {
        ResultActions resultActions = mvc
                .perform(patch("/api/answers/1")
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
                .andExpect(handler().handlerType(AllAnswerController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.result_code").value("403-2"))
                .andExpect(jsonPath("$.msg").value("작성자만 답변을 수정할 수 있습니다."));
    }

    @Test
    @DisplayName("존재하지 않는 11111111번 답변 수정")
    @WithUserDetails("test@test.com")
    void t3_4() throws Exception {
        ResultActions resultActions = mvc
                .perform(patch("/api/answers/11111111")
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
                .andExpect(handler().handlerType(AllAnswerController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result_code").value("404-2"))
                .andExpect(jsonPath("$.msg").value("해당 답변은 존재하지 않습니다."));
    }

    @Test
    @DisplayName("답변 삭제")
    @WithUserDetails("test@test.com")
    void t4() throws Exception {
        Answer answer = answerService.findById(1);

        ResultActions resultActions = mvc
                .perform(delete("/api/answers/1"))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(AllAnswerController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result_code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 답변이 삭제 되었습니다.".formatted(answer.getId())));
    }

    @Test
    @DisplayName("답변 삭제, with no actor")
    void t4_1() throws Exception {
        ResultActions resultActions = mvc
                .perform(delete("/api/answers/1"))
                .andDo(print());

        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.result_code").value("401-1"))
                .andExpect(jsonPath("$.msg").value("Unauthorized"));
    }

    @Test
    @DisplayName("답변 삭제, with wrong actor")
    @WithUserDetails("test2@test.com")
    void t4_2() throws Exception {
        ResultActions resultActions = mvc
                .perform(delete("/api/answers/1"))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(AllAnswerController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.result_code").value("403-2"))
                .andExpect(jsonPath("$.msg").value("작성자만 답변을 삭제할 수 있습니다."));
    }

    @Test
    @DisplayName("존재하지 않는 11111111번 답변 삭제")
    @WithUserDetails("test@test.com")
    void t4_3() throws Exception {
        ResultActions resultActions = mvc
                .perform(delete("/api/answers/11111111"))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(AllAnswerController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result_code").value("404-2"))
                .andExpect(jsonPath("$.msg").value("해당 답변은 존재하지 않습니다."));
    }
}
