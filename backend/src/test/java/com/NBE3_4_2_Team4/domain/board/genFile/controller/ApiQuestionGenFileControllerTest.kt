package com.NBE3_4_2_Team4.domain.board.genFile.controller

import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFile
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService
import com.NBE3_4_2_Team4.global.config.AppConfig
import com.NBE3_4_2_Team4.standard.util.Ut
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import java.io.FileInputStream

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class ApiQuestionGenFileControllerTest {
    @Autowired
    private lateinit var questionService: QuestionService

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun `다건 조회`() {
        val resultActions = mvc
            .perform(MockMvcRequestBuilders.get("/api/questions/1/genFiles"))
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiQuestionGenFileController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("items"))
            .andExpect(MockMvcResultMatchers.status().isOk())

        val questionGenFiles = questionService
            .findQuestionById(1).genFiles

        for (i in questionGenFiles.indices) {
            val questionGenFile = questionGenFiles[i]

            resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$[$i].id").value(questionGenFile.id))
                .andExpect(MockMvcResultMatchers.jsonPath("$[$i].created_at").exists())
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$[$i].parent_id").value(questionGenFile.parent.id)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$[$i].type_code").value(questionGenFile.typeCode.name)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$[$i].file_ext_type_code")
                        .value(questionGenFile.fileExtTypeCode)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$[$i].file_ext_type2_code")
                        .value(questionGenFile.fileExtType2Code)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$[$i].file_size").value(questionGenFile.fileSize)
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$[$i].file_no").value(questionGenFile.fileNo))
                .andExpect(MockMvcResultMatchers.jsonPath("$[$i].file_ext").value(questionGenFile.fileExt))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$[$i].file_date_dir")
                        .value(questionGenFile.fileDateDir)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$[$i].original_file_name")
                        .value(questionGenFile.originalFileName)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$[$i].download_url").value(questionGenFile.downloadUrl)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$[$i].public_url").value(questionGenFile.publicUrl)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$[$i].file_name").value(questionGenFile.fileName)
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$[$i].file_type").value("question"))
        }
    }

    @Test
    @WithUserDetails("admin@test.com")
    fun `새 파일 등록`() {
        val newFilePath = Ut.file.downloadByHttp("https://picsum.photos/id/239/500/500", AppConfig.getTempDirPath())

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.multipart("/api/questions/1/genFiles/${GenFile.TypeCode.attachment}")
                    .file(MockMultipartFile("files", "500.jpg", "jpg", FileInputStream(newFilePath)))
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiQuestionGenFileController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("makeNewItems"))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("201-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("1개의 파일이 생성되었습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].created_at").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].parent_id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].type_code").value(GenFile.TypeCode.attachment.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].file_ext_type_code").value("img"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].file_ext_type2_code").value("jpg"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].file_size").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].file_no").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].file_ext").value("jpg"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].file_date_dir").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].original_file_name").value("500.jpg"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].download_url").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].public_url").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].file_name").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].file_type").value("question"))

        Ut.file.rm(newFilePath)
    }

    @Test
    fun `단건 조회`() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/questions/1/genFiles/1")
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiQuestionGenFileController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("item"))
            .andExpect(MockMvcResultMatchers.status().isOk())

        val questionGenFile = questionService
            .findQuestionById(1).getGenFileById(1)

        resultActions
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(questionGenFile.id))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.created_at")
                    .value(Matchers.startsWith(questionGenFile.createdAt.toString().substring(0, 20)))
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.parent_id").value(questionGenFile.parent.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.type_code").value(questionGenFile.typeCode.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.file_ext_type_code").value(questionGenFile.fileExtTypeCode))
            .andExpect(MockMvcResultMatchers.jsonPath("$.file_ext_type2_code").value(questionGenFile.fileExtType2Code))
            .andExpect(MockMvcResultMatchers.jsonPath("$.file_size").value(questionGenFile.fileSize))
            .andExpect(MockMvcResultMatchers.jsonPath("$.file_no").value(questionGenFile.fileNo))
            .andExpect(MockMvcResultMatchers.jsonPath("$.file_ext").value(questionGenFile.fileExt))
            .andExpect(MockMvcResultMatchers.jsonPath("$.file_date_dir").value(questionGenFile.fileDateDir))
            .andExpect(MockMvcResultMatchers.jsonPath("$.original_file_name").value(questionGenFile.originalFileName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.download_url").value(questionGenFile.downloadUrl))
            .andExpect(MockMvcResultMatchers.jsonPath("$.public_url").value(questionGenFile.publicUrl))
            .andExpect(MockMvcResultMatchers.jsonPath("$.file_name").value(questionGenFile.fileName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.file_type").value("question"))
    }

    @Test
    @WithUserDetails("admin@test.com")
    fun `새 파일 등록(다건)`() {
        val newFilePath1 = Ut.file.downloadByHttp("https://picsum.photos/id/239/500/500", AppConfig.getTempDirPath())
        val newFilePath2 = Ut.file.downloadByHttp("https://picsum.photos/id/240/500/500", AppConfig.getTempDirPath())

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.multipart("/api/questions/1/genFiles/${GenFile.TypeCode.attachment}")
                    .file(MockMultipartFile("files", "500.jpg", "jpg", FileInputStream(newFilePath1)))
                    .file(MockMultipartFile("files", "500.jpg", "jpg", FileInputStream(newFilePath2)))
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiQuestionGenFileController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("makeNewItems"))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("201-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("2개의 파일이 생성되었습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].created_at").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].parent_id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].type_code").value(GenFile.TypeCode.attachment.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].file_ext_type_code").value("img"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].file_ext_type2_code").value("jpg"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].file_size").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].file_no").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].file_ext").value("jpg"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].file_date_dir").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].original_file_name").value("500.jpg"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].download_url").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].public_url").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].file_name").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].file_type").value("question"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].created_at").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].parent_id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].type_code").value(GenFile.TypeCode.attachment.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].file_ext_type_code").value("img"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].file_ext_type2_code").value("jpg"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].file_size").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].file_no").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].file_ext").value("jpg"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].file_date_dir").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].original_file_name").value("500.jpg"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].download_url").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].public_url").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].file_name").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].file_type").value("question"))

        Ut.file.rm(newFilePath1)
        Ut.file.rm(newFilePath2)
    }

    @Test
    @WithUserDetails("test@test.com")
    fun `파일 삭제`() {
        val questionGenFile = questionService
            .findQuestionById(1).getGenFileById(1)

        val originFilePath = questionGenFile.filePath
        val copyFilePath = "${AppConfig.getTempDirPath()}/copy_${questionGenFile.fileName}"

        Ut.file.copy(originFilePath, copyFilePath)

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.delete("/api/questions/1/genFiles/1")
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiQuestionGenFileController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("delete"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("200-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("1번 파일이 삭제되었습니다."))

        Ut.file.mv(copyFilePath, originFilePath)
    }

    @Test
    @WithUserDetails("test@test.com")
    fun `파일 수정`() {
        val questionGenFile = questionService
            .findQuestionById(1).getGenFileById(1)

        val originFilePath = questionGenFile.filePath
        val copyFilePath = "${AppConfig.getTempDirPath()}/copy_${questionGenFile.fileName}"

        Ut.file.copy(originFilePath, copyFilePath)

        val newFilePath = Ut.file.downloadByHttp("https://picsum.photos/id/245/500/400", AppConfig.getTempDirPath())

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.multipart("/api/questions/1/genFiles/1")
                    .file(
                        MockMultipartFile(
                            "file",
                            "400.jpg",
                            "jpg",
                            FileInputStream(newFilePath)
                        )
                    )
                    .with { request: MockHttpServletRequest ->
                        request.method = "PUT"
                        request
                    }
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ApiQuestionGenFileController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("modify"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.result_code").value("200-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("1번 파일이 수정되었습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(questionGenFile.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.created_at").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.parent_id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.type_code").value(questionGenFile.typeCode.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.file_ext_type_code").value("img"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.file_ext_type2_code").value("jpg"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.file_size").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.file_no").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.file_ext").value("jpg"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.file_date_dir").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.original_file_name").value("400.jpg"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.download_url").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.public_url").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.file_name").isString())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.file_type").value("question"))

        Ut.file.mv(copyFilePath, originFilePath)
    }
}
