package com.NBE3_4_2_Team4.domain.product.product.controller;

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
        String url = "/api/products/all";

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

    @DisplayName("페이징 처리 된 전체 상품 조회 Test")
    @Test
    void getProductsWithPagingTest() throws Exception {

        // given
        String url = "/api/products?page=1&pageSize=1";

        // when
        ResultActions resultActions = mockMvc.perform(
                get(url)
        ).andDo(print());

        // then
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("getAllProductsWithPaging"))
                .andExpect(status().isOk())
                // 페이지 정보 정합성 체크
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.current_page_number").value(1))
                .andExpect(jsonPath("$.data.page_size").value(1))
                .andExpect(jsonPath("$.data.total_pages").value(2))
                .andExpect(jsonPath("$.data.total_items").value(2))
                .andExpect(jsonPath("$.data.has_more").value(true))
                // 데이터 정합성 체크
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].product_id").value(2))
                .andExpect(jsonPath("$.data.items[0].product_name").value("스타벅스 세트2"))
                .andExpect(jsonPath("$.data.items[0].product_price").value(15000))
                .andExpect(jsonPath("$.data.items[0].product_description").value("스타벅스 쿠폰2입니다."))
                .andExpect(jsonPath("$.data.items[0].product_image_url").value("http://example.com/path/image2.jpg"))
                .andExpect(jsonPath("$.data.items[0].product_categories").value("기프티콘/음료/커피"))
                .andExpect(jsonPath("$.data.items[0].product_sale_state").value("SOLDOUT"));
    }

    @DisplayName("카테고리별 상품 조회 Test")
    @Test
    void getProductsByCategoryTest() throws Exception {

        // given
        String url = "/api/products/category/all?category_keyword=기프티콘";

        // when
        ResultActions resultActions = mockMvc.perform(
                get(url)
        ).andDo(print());

        // then
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("getProductsByCategory"))
                .andExpect(status().isOk())
                // 첫번째 data 정합성 체크
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.keyword").value("기프티콘"))
                .andExpect(jsonPath("$.data.products").isArray())
                .andExpect(jsonPath("$.data.products[0].product_id").value(1))
                .andExpect(jsonPath("$.data.products[0].product_name").value("스타벅스 세트1"))
                .andExpect(jsonPath("$.data.products[0].product_price").value(10000))
                .andExpect(jsonPath("$.data.products[0].product_description").value("스타벅스 쿠폰1입니다."))
                .andExpect(jsonPath("$.data.products[0].product_image_url").value("http://example.com/path/image1.jpg"))
                .andExpect(jsonPath("$.data.products[0].product_categories").value("기프티콘/음료/커피"))
                .andExpect(jsonPath("$.data.products[0].product_sale_state").value("SOLDOUT"))
                // 두번째 data 정합성 체크
                .andExpect(jsonPath("$.data.products[1].product_id").value(2))
                .andExpect(jsonPath("$.data.products[1].product_name").value("스타벅스 세트2"))
                .andExpect(jsonPath("$.data.products[1].product_price").value(15000))
                .andExpect(jsonPath("$.data.products[1].product_description").value("스타벅스 쿠폰2입니다."))
                .andExpect(jsonPath("$.data.products[1].product_image_url").value("http://example.com/path/image2.jpg"))
                .andExpect(jsonPath("$.data.products[1].product_categories").value("기프티콘/음료/커피"))
                .andExpect(jsonPath("$.data.products[1].product_sale_state").value("SOLDOUT"));
    }

    @DisplayName("카테고리별 페이징 처리 된 상품 조회 Test")
    @Test
    void getProductsByCategoryWithPagingTest() throws Exception {

        // given
        String url = "/api/products/category?category_keyword=기프티콘&page=1&pageSize=1";

        // when
        ResultActions resultActions = mockMvc.perform(
                get(url)
        ).andDo(print());

        // then
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("getProductsByCategoryWithPaging"))
                .andExpect(status().isOk())
                // 페이지 정보 정합성 체크
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.keyword").value("기프티콘"))
                .andExpect(jsonPath("$.data.current_page_number").value(1))
                .andExpect(jsonPath("$.data.page_size").value(1))
                .andExpect(jsonPath("$.data.total_pages").value(2))
                .andExpect(jsonPath("$.data.total_items").value(2))
                .andExpect(jsonPath("$.data.has_more").value(true))
                // 데이터 정합성 체크
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].product_id").value(2))
                .andExpect(jsonPath("$.data.items[0].product_name").value("스타벅스 세트2"))
                .andExpect(jsonPath("$.data.items[0].product_price").value(15000))
                .andExpect(jsonPath("$.data.items[0].product_description").value("스타벅스 쿠폰2입니다."))
                .andExpect(jsonPath("$.data.items[0].product_image_url").value("http://example.com/path/image2.jpg"))
                .andExpect(jsonPath("$.data.items[0].product_categories").value("기프티콘/음료/커피"))
                .andExpect(jsonPath("$.data.items[0].product_sale_state").value("SOLDOUT"));
    }
}