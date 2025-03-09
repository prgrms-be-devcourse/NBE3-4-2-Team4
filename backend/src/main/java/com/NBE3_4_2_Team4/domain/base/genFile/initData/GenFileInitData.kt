package com.NBE3_4_2_Team4.domain.base.genFile.initData

import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFile
import com.NBE3_4_2_Team4.domain.board.answer.initData.AnswerInitData
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService
import com.NBE3_4_2_Team4.domain.board.question.initData.QuestionInitData
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService
import com.NBE3_4_2_Team4.domain.member.member.initData.MemberInitData
import com.NBE3_4_2_Team4.domain.product.product.initData.ProductInitData
import com.NBE3_4_2_Team4.domain.product.product.service.ProductService
import com.NBE3_4_2_Team4.global.config.AppConfig
import com.NBE3_4_2_Team4.standard.util.Ut
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.transaction.annotation.Transactional

@Configuration
class GenFileInitData(
    private val memberInitData: MemberInitData,
    private val productInitData: ProductInitData,
    private val questionInitData: QuestionInitData,
    private val answerInitData: AnswerInitData,
    private val questionService: QuestionService,
    private val answerService: AnswerService,
    private val productService: ProductService,
    private val em: EntityManager
) {
    private val GEN_FILE_TYPES = listOf(
        "AnswerGenFile",
        "QuestionGenFile",
        "ProductGenFile"
    )

    @Autowired
    @Lazy
    private lateinit var self: GenFileInitData

    @Bean
    fun genFileInitDataApplicationRunner(): ApplicationRunner {
        return ApplicationRunner {
            memberInitData.work()
            questionInitData.initData()
            answerInitData.initData()
            productInitData.createInitProducts()

            if (isAnyGenFileExists) return@ApplicationRunner
            if (AppConfig.isTest()) Ut.file.rm(AppConfig.getGenFileDirPath())

            self.initFiles()
        }
    }

    private val isAnyGenFileExists: Boolean
        get() = GEN_FILE_TYPES.stream()
            .anyMatch { this.hasGenFiles(it) }

    private fun hasGenFiles(genFileType: String): Boolean {
        val count = em.createQuery(
            "SELECT COUNT(g) FROM $genFileType g",
            Long::class.java
        ).singleResult

        return count > 0
    }

    @Transactional
    fun initFiles() {
        val question1 = questionService.findQuestionById(1)
        val answer3 = answerService.findById(3)
        val product1 = productService.findById(1L)

        val genFile1FilePath =
            Ut.file.downloadByHttp("https://picsum.photos/id/237/200/300", AppConfig.getTempDirPath())
        val genFile2FilePath =
            Ut.file.downloadByHttp("https://picsum.photos/id/238/200/300", AppConfig.getTempDirPath())
        val genFile3FilePath =
            Ut.file.downloadByHttp("https://picsum.photos/id/239/200/300", AppConfig.getTempDirPath())
        val genFile4FilePath =
            Ut.file.downloadByHttp("https://picsum.photos/id/240/200/300", AppConfig.getTempDirPath())
        val genFile5FilePath =
            Ut.file.downloadByHttp("https://picsum.photos/id/241/200/300", AppConfig.getTempDirPath())
        val genFile6FilePath =
            Ut.file.downloadByHttp("https://picsum.photos/id/242/200/300", AppConfig.getTempDirPath())
        val genFile7FilePath =
            Ut.file.downloadByHttp("https://picsum.photos/id/243/200/300", AppConfig.getTempDirPath())


        question1.addGenFile(GenFile.TypeCode.attachment, genFile4FilePath)
        question1.addGenFile(GenFile.TypeCode.body, genFile5FilePath)

        answer3.addGenFile(GenFile.TypeCode.attachment, genFile1FilePath)
        answer3.addGenFile(GenFile.TypeCode.attachment, genFile2FilePath)
        answer3.addGenFile(GenFile.TypeCode.body, genFile3FilePath)


        product1.addGenFile(GenFile.TypeCode.attachment, genFile6FilePath)
        product1.addGenFile(GenFile.TypeCode.body, genFile7FilePath)
    }
}
