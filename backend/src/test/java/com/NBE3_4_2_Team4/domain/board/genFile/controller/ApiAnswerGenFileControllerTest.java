package com.NBE3_4_2_Team4.domain.board.genFile.controller;

import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFile;
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService;
import com.NBE3_4_2_Team4.domain.board.genFile.entity.AnswerGenFile;
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService;
import com.NBE3_4_2_Team4.global.config.AppConfig;
import com.NBE3_4_2_Team4.standard.util.Ut;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ApiAnswerGenFileControllerTest {
    @Autowired
    private AnswerService answerService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("다건 조회")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/api/answers/3/genFiles"))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiAnswerGenFileController.class))
                .andExpect(handler().methodName("items"))
                .andExpect(status().isOk());

        List<AnswerGenFile> answerGenFiles = answerService
                .findById(3).getGenFiles();

        for (int i = 0; i < answerGenFiles.size(); i++) {
            AnswerGenFile answerGenFile = answerGenFiles.get(i);

            resultActions
                    .andExpect(jsonPath("$[%d].id".formatted(i)).value(answerGenFile.getId()))
                    .andExpect(jsonPath("$[%d].created_at".formatted(i)).exists())
                    .andExpect(jsonPath("$[%d].parent_id".formatted(i)).value(answerGenFile.getParent().getId()))
                    .andExpect(jsonPath("$[%d].type_code".formatted(i)).value(answerGenFile.getTypeCode().name()))
                    .andExpect(jsonPath("$[%d].file_ext_type_code".formatted(i)).value(answerGenFile.getFileExtTypeCode()))
                    .andExpect(jsonPath("$[%d].file_ext_type2_code".formatted(i)).value(answerGenFile.getFileExtType2Code()))
                    .andExpect(jsonPath("$[%d].file_size".formatted(i)).value(answerGenFile.getFileSize()))
                    .andExpect(jsonPath("$[%d].file_no".formatted(i)).value(answerGenFile.getFileNo()))
                    .andExpect(jsonPath("$[%d].file_ext".formatted(i)).value(answerGenFile.getFileExt()))
                    .andExpect(jsonPath("$[%d].file_date_dir".formatted(i)).value(answerGenFile.getFileDateDir()))
                    .andExpect(jsonPath("$[%d].original_file_name".formatted(i)).value(answerGenFile.getOriginalFileName()))
                    .andExpect(jsonPath("$[%d].download_url".formatted(i)).value(answerGenFile.getDownloadUrl()))
                    .andExpect(jsonPath("$[%d].public_url".formatted(i)).value(answerGenFile.getPublicUrl()))
                    .andExpect(jsonPath("$[%d].file_name".formatted(i)).value(answerGenFile.getFileName()));
        }
    }

    @Test
    @DisplayName("새 파일 등록")
    @WithUserDetails("test@test.com")
    void t2() throws Exception {
        String newFilePath = Ut.file.downloadByHttp("https://picsum.photos/id/239/500/500", AppConfig.getTempDirPath());

        ResultActions resultActions = mvc
                .perform(
                        multipart("/api/answers/3/genFiles/" + GenFile.TypeCode.attachment)
                                .file(new MockMultipartFile("files", "500.jpg", "jpg", new FileInputStream(newFilePath)))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiAnswerGenFileController.class))
                .andExpect(handler().methodName("makeNewItems"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result_code").value("201-1"))
                .andExpect(jsonPath("$.msg").value("1개의 파일이 생성되었습니다."))
                .andExpect(jsonPath("$.data[0].id").isNumber())
                .andExpect(jsonPath("$.data[0].created_at").isString())
                .andExpect(jsonPath("$.data[0].parent_id").value(3))
                .andExpect(jsonPath("$.data[0].type_code").value(GenFile.TypeCode.attachment.name()))
                .andExpect(jsonPath("$.data[0].file_ext_type_code").value("img"))
                .andExpect(jsonPath("$.data[0].file_ext_type2_code").value("jpg"))
                .andExpect(jsonPath("$.data[0].file_size").isNumber())
                .andExpect(jsonPath("$.data[0].file_no").value(4))
                .andExpect(jsonPath("$.data[0].file_ext").value("jpg"))
                .andExpect(jsonPath("$.data[0].file_date_dir").isString())
                .andExpect(jsonPath("$.data[0].original_file_name").value("500.jpg"))
                .andExpect(jsonPath("$.data[0].download_url").isString())
                .andExpect(jsonPath("$.data[0].public_url").isString())
                .andExpect(jsonPath("$.data[0].file_name").isString());

        Ut.file.rm(newFilePath);
    }

    @Test
    @DisplayName("단건 조회")
    void t3() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/answers/3/genFiles/1")
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiAnswerGenFileController.class))
                .andExpect(handler().methodName("item"))
                .andExpect(status().isOk());

        AnswerGenFile answerGenFile = answerService
                .findById(3).getGenFileById(1);

        resultActions
                .andExpect(jsonPath("$.id").value(answerGenFile.getId()))
                .andExpect(jsonPath("$.created_at").value(Matchers.startsWith(answerGenFile.getCreatedAt().toString().substring(0, 20))))
                .andExpect(jsonPath("$.parent_id").value(answerGenFile.getParent().getId()))
                .andExpect(jsonPath("$.type_code").value(answerGenFile.getTypeCode().name()))
                .andExpect(jsonPath("$.file_ext_type_code").value(answerGenFile.getFileExtTypeCode()))
                .andExpect(jsonPath("$.file_ext_type2_code").value(answerGenFile.getFileExtType2Code()))
                .andExpect(jsonPath("$.file_size").value(answerGenFile.getFileSize()))
                .andExpect(jsonPath("$.file_no").value(answerGenFile.getFileNo()))
                .andExpect(jsonPath("$.file_ext").value(answerGenFile.getFileExt()))
                .andExpect(jsonPath("$.file_date_dir").value(answerGenFile.getFileDateDir()))
                .andExpect(jsonPath("$.original_file_name").value(answerGenFile.getOriginalFileName()))
                .andExpect(jsonPath("$.download_url").value(answerGenFile.getDownloadUrl()))
                .andExpect(jsonPath("$.public_url").value(answerGenFile.getPublicUrl()))
                .andExpect(jsonPath("$.file_name").value(answerGenFile.getFileName()));
    }

    @Test
    @DisplayName("새 파일 등록(다건)")
    @WithUserDetails("test@test.com")
    void t4() throws Exception {
        String newFilePath1 = Ut.file.downloadByHttp("https://picsum.photos/id/239/500/500", AppConfig.getTempDirPath());
        String newFilePath2 = Ut.file.downloadByHttp("https://picsum.photos/id/240/500/500", AppConfig.getTempDirPath());

        ResultActions resultActions = mvc
                .perform(
                        multipart("/api/answers/3/genFiles/" + AnswerGenFile.TypeCode.attachment)
                                .file(new MockMultipartFile("files", "500.jpg", "jpg", new FileInputStream(newFilePath1)))
                                .file(new MockMultipartFile("files", "500.jpg", "jpg", new FileInputStream(newFilePath2)))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiAnswerGenFileController.class))
                .andExpect(handler().methodName("makeNewItems"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result_code").value("201-1"))
                .andExpect(jsonPath("$.msg").value("2개의 파일이 생성되었습니다."))
                .andExpect(jsonPath("$.data[0].id").isNumber())
                .andExpect(jsonPath("$.data[0].created_at").isString())
                .andExpect(jsonPath("$.data[0].parent_id").value(3))
                .andExpect(jsonPath("$.data[0].type_code").value(GenFile.TypeCode.attachment.name()))
                .andExpect(jsonPath("$.data[0].file_ext_type_code").value("img"))
                .andExpect(jsonPath("$.data[0].file_ext_type2_code").value("jpg"))
                .andExpect(jsonPath("$.data[0].file_size").isNumber())
                .andExpect(jsonPath("$.data[0].file_no").value(4))
                .andExpect(jsonPath("$.data[0].file_ext").value("jpg"))
                .andExpect(jsonPath("$.data[0].file_date_dir").isString())
                .andExpect(jsonPath("$.data[0].original_file_name").value("500.jpg"))
                .andExpect(jsonPath("$.data[0].download_url").isString())
                .andExpect(jsonPath("$.data[0].public_url").isString())
                .andExpect(jsonPath("$.data[0].file_name").isString())
                .andExpect(jsonPath("$.data[0].id").isNumber())
                .andExpect(jsonPath("$.data[1].created_at").isString())
                .andExpect(jsonPath("$.data[1].parent_id").value(3))
                .andExpect(jsonPath("$.data[1].type_code").value(GenFile.TypeCode.attachment.name()))
                .andExpect(jsonPath("$.data[1].file_ext_type_code").value("img"))
                .andExpect(jsonPath("$.data[1].file_ext_type2_code").value("jpg"))
                .andExpect(jsonPath("$.data[1].file_size").isNumber())
                .andExpect(jsonPath("$.data[1].file_no").value(5))
                .andExpect(jsonPath("$.data[1].file_ext").value("jpg"))
                .andExpect(jsonPath("$.data[1].file_date_dir").isString())
                .andExpect(jsonPath("$.data[1].original_file_name").value("500.jpg"))
                .andExpect(jsonPath("$.data[1].download_url").isString())
                .andExpect(jsonPath("$.data[1].public_url").isString())
                .andExpect(jsonPath("$.data[1].file_name").isString());

        Ut.file.rm(newFilePath1);
        Ut.file.rm(newFilePath2);
    }

    @Test
    @DisplayName("파일 삭제")
    @WithUserDetails("test@test.com")
    void t5() throws Exception {
        AnswerGenFile answerGenFile = answerService
                .findById(3).getGenFileById(1);

        String originFilePath = answerGenFile.getFilePath();
        String copyFilePath = AppConfig.getTempDirPath() + "/copy_" + answerGenFile.getFileName();

        Ut.file.copy(originFilePath, copyFilePath);

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/answers/3/genFiles/1")
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiAnswerGenFileController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result_code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("1번 파일이 삭제되었습니다."));

        Ut.file.mv(copyFilePath, originFilePath);
    }

    @Test
    @DisplayName("파일 수정")
    @WithUserDetails("test@test.com")
    void t6() throws Exception {
        AnswerGenFile answerGenFile = answerService
                .findById(3).getGenFileById(1);

        String originFilePath = answerGenFile.getFilePath();
        String copyFilePath = AppConfig.getTempDirPath() + "/copy_" + answerGenFile.getFileName();
        Ut.file.copy(originFilePath, copyFilePath);

        String newFilePath = Ut.file.downloadByHttp("https://picsum.photos/id/245/500/400", AppConfig.getTempDirPath());

        ResultActions resultActions = mvc
                .perform(
                        multipart("/api/answers/3/genFiles/1")
                                .file(new MockMultipartFile(
                                        "file",
                                        "400.jpg",
                                        "jpg",
                                        new FileInputStream(newFilePath))
                                )
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiAnswerGenFileController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result_code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("1번 파일이 수정되었습니다."))
                .andExpect(jsonPath("$.data.id").value(answerGenFile.getId()))
                .andExpect(jsonPath("$.data.created_at").isString())
                .andExpect(jsonPath("$.data.parent_id").value(3))
                .andExpect(jsonPath("$.data.type_code").value(answerGenFile.getTypeCode().name()))
                .andExpect(jsonPath("$.data.file_ext_type_code").value("img"))
                .andExpect(jsonPath("$.data.file_ext_type2_code").value("jpg"))
                .andExpect(jsonPath("$.data.file_size").isNumber())
                .andExpect(jsonPath("$.data.file_no").value(1))
                .andExpect(jsonPath("$.data.file_ext").value("jpg"))
                .andExpect(jsonPath("$.data.file_date_dir").isString())
                .andExpect(jsonPath("$.data.original_file_name").value("400.jpg"))
                .andExpect(jsonPath("$.data.download_url").isString())
                .andExpect(jsonPath("$.data.public_url").isString())
                .andExpect(jsonPath("$.data.file_name").isString());

        Ut.file.mv(copyFilePath, originFilePath);
    }
}
