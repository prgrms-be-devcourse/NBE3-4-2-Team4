package com.NBE3_4_2_Team4.domain.product.genFile.controller;

import com.NBE3_4_2_Team4.domain.member.member.service.MemberService;
import com.NBE3_4_2_Team4.domain.product.genFile.entity.ProductGenFile;
import com.NBE3_4_2_Team4.domain.product.product.entity.Product;
import com.NBE3_4_2_Team4.domain.product.product.service.ProductService;
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
public class ProductGenFileControllerTest {
    @Autowired
    private ProductService productService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("다운로드 테스트")
    void t1() throws Exception {
        Product product1 = productService.findById(1L);
        ProductGenFile productGenFile = product1.getGenFiles().getFirst();

        String downloadUrl = Ut.url.removeDomain(productGenFile.getDownloadUrl());

        ResultActions resultActions = mvc
                .perform(
                        get(downloadUrl)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ProductGenFileController.class))
                .andExpect(handler().methodName("download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + productGenFile.getOriginalFileName() + "\""))
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }
}
