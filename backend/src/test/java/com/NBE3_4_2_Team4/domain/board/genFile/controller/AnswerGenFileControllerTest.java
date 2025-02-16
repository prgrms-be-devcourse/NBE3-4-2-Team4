package com.NBE3_4_2_Team4.domain.board.genFile.controller;

import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.answer.service.AnswerService;
import com.NBE3_4_2_Team4.domain.board.genFile.entity.AnswerGenFile;
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
public class AnswerGenFileControllerTest {
    @Autowired
    private AnswerService answerService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("다운로드 테스트")
    void t1() throws Exception {
        Answer answer3 = answerService.findById(3);
        AnswerGenFile answerGenFile1 = answer3.getGenFiles().getFirst();

        String downloadUrl = Ut.url.removeDomain(answerGenFile1.getDownloadUrl());

        ResultActions resultActions = mvc
                .perform(
                        get(downloadUrl)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(AnswerGenFileController.class))
                .andExpect(handler().methodName("download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + answerGenFile1.getOriginalFileName() + "\""))
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }
}
