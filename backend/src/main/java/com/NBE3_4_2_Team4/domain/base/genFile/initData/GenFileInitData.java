package com.NBE3_4_2_Team4.domain.base.genFile.initData;

import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFile;
import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.initData.AnswerInitData;
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService;
import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.initData.QuestionInitData;
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService;
import com.NBE3_4_2_Team4.domain.member.member.initData.MemberInitData;
import com.NBE3_4_2_Team4.domain.product.product.entity.Product;
import com.NBE3_4_2_Team4.domain.product.product.initData.ProductInitData;
import com.NBE3_4_2_Team4.domain.product.product.service.ProductService;
import com.NBE3_4_2_Team4.global.config.AppConfig;
import com.NBE3_4_2_Team4.standard.util.Ut;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class GenFileInitData {
    private final MemberInitData memberInitData;
    private final ProductInitData productInitData;
    private final QuestionInitData questionInitData;
    private final AnswerInitData answerInitData;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final ProductService productService;
    private final EntityManager em;
    private final List<String> GEN_FILE_TYPES = List.of(
            "AnswerGenFile",
            "QuestionGenFile",
            "ProductGenFile"
    );

    @Autowired
    @Lazy
    private GenFileInitData self;

    @Bean
    public ApplicationRunner genFileInitDataApplicationRunner() {
        return args -> {
            memberInitData.work();
            questionInitData.initData();
            answerInitData.initData();
            productInitData.createInitProducts();

            if (isAnyGenFileExists()) return;

            if (AppConfig.isTest()) Ut.file.rm(AppConfig.getGenFileDirPath());

            self.initFiles();
        };
    }

    private boolean isAnyGenFileExists() {
        return GEN_FILE_TYPES.stream()
                .anyMatch(this::hasGenFiles);
    }

    private boolean hasGenFiles(String genFileType) {
        Long count = em.createQuery(
                "SELECT COUNT(g) FROM %s g".formatted(genFileType),
                Long.class
        ).getSingleResult();

        return count > 0;
    }

    @Transactional
    public void initFiles() {
        Question question1 = questionService.findQuestionById(1);
        Answer answer3 = answerService.findById(3);
        Product product1 = productService.findById(1L);

        String genFile1FilePath = Ut.file.downloadByHttp("https://picsum.photos/id/237/200/300", AppConfig.getTempDirPath());
        String genFile2FilePath = Ut.file.downloadByHttp("https://picsum.photos/id/238/200/300", AppConfig.getTempDirPath());
        String genFile3FilePath = Ut.file.downloadByHttp("https://picsum.photos/id/239/200/300", AppConfig.getTempDirPath());
        String genFile4FilePath = Ut.file.downloadByHttp("https://picsum.photos/id/240/200/300", AppConfig.getTempDirPath());
        String genFile5FilePath = Ut.file.downloadByHttp("https://picsum.photos/id/241/200/300", AppConfig.getTempDirPath());
        String genFile6FilePath = Ut.file.downloadByHttp("https://picsum.photos/id/242/200/300", AppConfig.getTempDirPath());
        String genFile7FilePath = Ut.file.downloadByHttp("https://picsum.photos/id/243/200/300", AppConfig.getTempDirPath());


        question1.addGenFile(GenFile.TypeCode.attachment, genFile4FilePath);
        question1.addGenFile(GenFile.TypeCode.body, genFile5FilePath);

        answer3.addGenFile(GenFile.TypeCode.attachment, genFile1FilePath);
        answer3.addGenFile(GenFile.TypeCode.attachment, genFile2FilePath);
        answer3.addGenFile(GenFile.TypeCode.body, genFile3FilePath);


        product1.addGenFile(GenFile.TypeCode.attachment, genFile6FilePath);
        product1.addGenFile(GenFile.TypeCode.body, genFile7FilePath);
    }
}
