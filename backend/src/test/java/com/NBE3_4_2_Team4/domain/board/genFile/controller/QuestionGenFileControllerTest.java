package com.NBE3_4_2_Team4.domain.board.genFile.controller;

import com.NBE3_4_2_Team4.domain.board.genFile.entity.QuestionGenFile;
import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.board.question.service.QuestionService;
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService;
import com.NBE3_4_2_Team4.standard.util.Ut;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class QuestionGenFileControllerTest {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("다운로드 테스트")
    void t1() throws Exception {
        Question question1 = questionService.findQuestionById(1);
        QuestionGenFile questionGenFile = question1.getGenFiles().getFirst();

        String downloadUrl = Ut.url.removeDomain(questionGenFile.getDownloadUrl());

        ResultActions resultActions = mvc
                .perform(
                        get(downloadUrl)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(QuestionGenFileController.class))
                .andExpect(handler().methodName("download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + questionGenFile.getOriginalFileName() + "\""))
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }
}
