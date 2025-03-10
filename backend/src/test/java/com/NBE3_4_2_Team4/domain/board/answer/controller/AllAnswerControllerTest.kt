package com.NBE3_4_2_Team4.domain.board.answer.controller

import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class AllAnswerControllerTest {
    @Autowired
    private lateinit var answerService: AnswerService

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun `전체 답변 조회`() {
        val resultActions = mvc
            .perform(MockMvcRequestBuilders.get("/api/answers"))
            .andDo(MockMvcResultHandlers.print())

        val answersPage = answerService
            .itemsAll(1, 10)

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(AllAnswerController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("items"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.current_page_number").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.page_size").value(10))
            .andExpect(MockMvcResultMatchers.jsonPath("$.total_pages").value(answersPage.totalPages))
            .andExpect(MockMvcResultMatchers.jsonPath("$.total_items").value(answersPage.totalElements))
            .andExpect(MockMvcResultMatchers.jsonPath("$.has_more").value(answersPage.hasNext()))

        val answers = answersPage.content

        for (i in answers.indices) {
            val answer = answers[i]

            resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].id").value(answer.id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].created_at").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].modified_at").exists())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.items[$i].question_id").value(answer.questionId)
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].author_id").value(answer.authorId))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.items[$i].author_name").value(answer.authorName)
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].content").value(answer.content))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[$i].selected").value(answer.selected))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.items[$i].selected_at").value(answer.selectedAt)
                )
        }
    }

    @Test
    fun `단건 조회`() {
        val resultActions = mvc
            .perform(MockMvcRequestBuilders.get("/api/answers/1"))
            .andDo(MockMvcResultHandlers.print())

        val answer = answerService.item(1)

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(AllAnswerController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("item"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(answer.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.created_at").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.modified_at").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.question_id").value(answer.questionId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.author_id").value(answer.authorId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.author_name").value(answer.authorName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(answer.content))
            .andExpect(MockMvcResultMatchers.jsonPath("$.selected").value(answer.selected))
            .andExpect(MockMvcResultMatchers.jsonPath("$.selected_at").value(answer.selectedAt))
    }

    @Test
    fun `존재하지 않는 11111111번 답변 조회`() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/answers/11111111")
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(AllAnswerController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("item"))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("404-2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("해당 답변은 존재하지 않습니다."))
    }

    @Test
    @WithUserDetails("test@test.com")
    fun `답변 수정`() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.patch("/api/answers/1")
                    .content(
                        """
                                {
                                    "content": "답변 내용 new"
                                }
                                
                                """.trimIndent()
                    )
                    .contentType(
                        MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        val answer = answerService.findById(1)

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(AllAnswerController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("modify"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("200-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("${answer.id}번 답변이 수정 되었습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(answer.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.created_at").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.modified_at").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.question_id").value(answer.question.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.content").value("답변 내용 new"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.selected").value(answer.selected))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.selected_at").value(answer.selectedAt))
    }

    @Test
    @WithUserDetails("test@test.com")
    fun `답변 수정, with no input`() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.patch("/api/answers/1")
                    .content(
                        """
                                {
                                    "content": ""
                                }
                                
                                """.trimIndent()
                    )
                    .contentType(
                        MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(AllAnswerController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("modify"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("400-1"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.msg").value(
                    """
                        content-NotBlank-must not be blank
                        content-Size-size must be between 2 and 2147483647
                    """.trimIndent()
                )
            )
    }

    @Test
    fun `답변 수정, with no actor`() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.patch("/api/answers/1")
                    .content(
                        """
                                {
                                    "content": "답변 내용 new"
                                }
                                
                                """.trimIndent()
                    )
                    .contentType(
                        MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("401-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("Unauthorized"))
    }

    @Test
    @WithUserDetails("admin@test.com")
    fun `답변 수정, with wrong actor`() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.patch("/api/answers/1")
                    .content(
                        """
                                {
                                    "content": "답변 내용 new"
                                }
                                
                                """.trimIndent()
                    )
                    .contentType(
                        MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(AllAnswerController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("modify"))
            .andExpect(MockMvcResultMatchers.status().isForbidden())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("403-2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("작성자만 답변을 수정할 수 있습니다."))
    }

    @Test
    @WithUserDetails("test@test.com")
    fun `존재하지 않는 11111111번 답변 수정`() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.patch("/api/answers/11111111")
                    .content(
                        """
                                {
                                    "content": "답변 내용 new"
                                }
                                
                                """.trimIndent()
                    )
                    .contentType(
                        MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(AllAnswerController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("modify"))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("404-2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("해당 답변은 존재하지 않습니다."))
    }

    @Test
    @WithUserDetails("test@test.com")
    fun `답변 삭제`() {
        val answer = answerService.findById(1)

        val resultActions = mvc
            .perform(MockMvcRequestBuilders.delete("/api/answers/1"))
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(AllAnswerController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("delete"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("200-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("${answer.id}번 답변이 삭제 되었습니다."))
    }

    @Test
    fun `답변 삭제, with no actor`() {
        val resultActions = mvc
            .perform(MockMvcRequestBuilders.delete("/api/answers/1"))
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("401-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("Unauthorized"))
    }

    @Test
    @WithUserDetails("test2@test.com")
    fun `답변 삭제, with wrong actor`() {
        val resultActions = mvc
            .perform(MockMvcRequestBuilders.delete("/api/answers/1"))
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(AllAnswerController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("delete"))
            .andExpect(MockMvcResultMatchers.status().isForbidden())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("403-2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("작성자만 답변을 삭제할 수 있습니다."))
    }

    @Test
    @WithUserDetails("test@test.com")
    fun `존재하지 않는 11111111번 답변 삭제`() {
        val resultActions = mvc
            .perform(MockMvcRequestBuilders.delete("/api/answers/11111111"))
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(AllAnswerController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("delete"))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("404-2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("해당 답변은 존재하지 않습니다."))
    }
}
