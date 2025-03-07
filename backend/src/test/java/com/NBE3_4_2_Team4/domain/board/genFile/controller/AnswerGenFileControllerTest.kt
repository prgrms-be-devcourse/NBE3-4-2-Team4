package com.NBE3_4_2_Team4.domain.board.genFile.controller

import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService
import com.NBE3_4_2_Team4.standard.util.Ut
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class AnswerGenFileControllerTest {
    @Autowired
    private lateinit var answerService: AnswerService

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun `다운로드 테스트`() {
        val answer3 = answerService.findById(3)
        val answerGenFile = answer3.genFiles.first()

        val downloadUrl = Ut.url.removeDomain(answerGenFile.downloadUrl)

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get(downloadUrl)
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(AnswerGenFileController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("download"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(
                MockMvcResultMatchers.header().string(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"${answerGenFile.originalFileName}\""
                )
            )
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.IMAGE_JPEG))
    }
}
