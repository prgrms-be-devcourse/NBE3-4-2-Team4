package com.NBE3_4_2_Team4.domain.product.product.controller;

import com.NBE3_4_2_Team4.domain.product.product.dto.ProductResponseDto;
import com.NBE3_4_2_Team4.domain.product.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("전체 상품 조회 Test")
    @Test
    void getProductsTest() throws Exception {

        // given
        String url = "/api/products";

        // when
        ResultActions resultActions = mockMvc.perform(
                get(url)
        ).andDo(print());

        // then
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("getAllProducts"))
                .andExpect(status().isOk())
                // 첫번째 data 정합성 체크
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].product_id").value(1))
                .andExpect(jsonPath("$.data[0].product_name").value("스타벅스 세트1"))
                .andExpect(jsonPath("$.data[0].product_price").value(10000))
                .andExpect(jsonPath("$.data[0].product_description").value("스타벅스 쿠폰1입니다."))
                .andExpect(jsonPath("$.data[0].product_image_url").value("http://example.com/path/image1.jpg"))
                .andExpect(jsonPath("$.data[0].product_categories").value("기프티콘/음료/커피"))
                .andExpect(jsonPath("$.data[0].product_sale_state").value("SOLDOUT"))
                // 두번째 data 정합성 체크
                .andExpect(jsonPath("$.data[1].product_id").value(2))
                .andExpect(jsonPath("$.data[1].product_name").value("스타벅스 세트2"))
                .andExpect(jsonPath("$.data[1].product_price").value(15000))
                .andExpect(jsonPath("$.data[1].product_description").value("스타벅스 쿠폰2입니다."))
                .andExpect(jsonPath("$.data[1].product_image_url").value("http://example.com/path/image2.jpg"))
                .andExpect(jsonPath("$.data[1].product_categories").value("기프티콘/음료/커피"))
                .andExpect(jsonPath("$.data[1].product_sale_state").value("SOLDOUT"));
    }
}