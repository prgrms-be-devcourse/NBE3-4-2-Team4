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
                .andExpect(jsonPath("$.data").isArray())
                // 첫번째 data 정합성 체크
                .andExpect(jsonPath("$.data[0].product_id").value(1))
                .andExpect(jsonPath("$.data[0].product_name").value("딸기라떼"))
                .andExpect(jsonPath("$.data[0].product_price").value(3800))
                .andExpect(jsonPath("$.data[0].product_description").value("빽다방 딸기라떼입니다."))
                .andExpect(jsonPath("$.data[0].product_image_url").value("http://example.com/path/pack1.jpg"))
                .andExpect(jsonPath("$.data[0].product_category").value("카페/빽다방"))
                .andExpect(jsonPath("$.data[0].product_sale_state").value("ONSALE"))
                // 두번째 data 정합성 체크
                .andExpect(jsonPath("$.data[1].product_id").value(2))
                .andExpect(jsonPath("$.data[1].product_name").value("오리지널 핫번 + 아메리카노(L)"))
                .andExpect(jsonPath("$.data[1].product_price").value(6100))
                .andExpect(jsonPath("$.data[1].product_description").value("이디야 오리지널 핫번 + 아메리카노(L) 세트입니다."))
                .andExpect(jsonPath("$.data[1].product_image_url").value("http://example.com/path/ediya1.jpg"))
                .andExpect(jsonPath("$.data[1].product_category").value("카페/이디야"))
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
                .andExpect(jsonPath("$.data.current_page_number").value(1))
                .andExpect(jsonPath("$.data.page_size").value(1))
                .andExpect(jsonPath("$.data.total_pages").value(5))
                .andExpect(jsonPath("$.data.total_items").value(5))
                .andExpect(jsonPath("$.data.has_more").value(true))
                // 데이터 정합성 체크
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].product_id").value(5))
                .andExpect(jsonPath("$.data.items[0].product_name").value("뿌링클콤보+콜라1.25L"))
                .andExpect(jsonPath("$.data.items[0].product_price").value(22500))
                .andExpect(jsonPath("$.data.items[0].product_description").value("BHC 뿌링클 콤보 + 콜라 1.25L 세트입니다."))
                .andExpect(jsonPath("$.data.items[0].product_image_url").value("http://example.com/path/chicken.jpg"))
                .andExpect(jsonPath("$.data.items[0].product_category").value("치킨/BHC"))
                .andExpect(jsonPath("$.data.items[0].product_sale_state").value("ONSALE"));
    }

    @DisplayName("카테고리별 상품 조회 Test")
    @Test
    void getProductsByCategoryTest() throws Exception {

        // given
        String url = "/api/products/category/all?category_keyword=상품권";

        // when
        ResultActions resultActions = mockMvc.perform(
                get(url)
        ).andDo(print());

        // then
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("getProductsByCategory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.keyword").value("상품권"))
                .andExpect(jsonPath("$.data.products").isArray())
                // 첫번째 data 정합성 체크
                .andExpect(jsonPath("$.data.products[0].product_id").value(3))
                .andExpect(jsonPath("$.data.products[0].product_name").value("올리브영 기프트카드 2만원권"))
                .andExpect(jsonPath("$.data.products[0].product_price").value(20000))
                .andExpect(jsonPath("$.data.products[0].product_description").value("올리브영 기프트카드 2만원권 입니다."))
                .andExpect(jsonPath("$.data.products[0].product_image_url").value("http://example.com/path/gift1.jpg"))
                .andExpect(jsonPath("$.data.products[0].product_category").value("상품권/올리브영"))
                .andExpect(jsonPath("$.data.products[0].product_sale_state").value("ONSALE"))
                // 두번째 data 정합성 체크
                .andExpect(jsonPath("$.data.products[1].product_id").value(4))
                .andExpect(jsonPath("$.data.products[1].product_name").value("교보문고 기프트카드 3만원권"))
                .andExpect(jsonPath("$.data.products[1].product_price").value(30000))
                .andExpect(jsonPath("$.data.products[1].product_description").value("기프트카드 3만원권 입니다."))
                .andExpect(jsonPath("$.data.products[1].product_image_url").value("http://example.com/path/gift2.jpg"))
                .andExpect(jsonPath("$.data.products[1].product_category").value("상품권/교보문고"))
                .andExpect(jsonPath("$.data.products[1].product_sale_state").value("ONSALE"));
    }

    @DisplayName("카테고리별 페이징 처리 된 상품 조회 Test")
    @Test
    void getProductsByCategoryWithPagingTest() throws Exception {

        // given
        String url = "/api/products/category?category_keyword=상품권&page=1&pageSize=1";

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
                .andExpect(jsonPath("$.data.keyword").value("상품권"))
                .andExpect(jsonPath("$.data.current_page_number").value(1))
                .andExpect(jsonPath("$.data.page_size").value(1))
                .andExpect(jsonPath("$.data.total_pages").value(2))
                .andExpect(jsonPath("$.data.total_items").value(2))
                .andExpect(jsonPath("$.data.has_more").value(true))
                // 데이터 정합성 체크
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].product_id").value(4))
                .andExpect(jsonPath("$.data.items[0].product_name").value("교보문고 기프트카드 3만원권"))
                .andExpect(jsonPath("$.data.items[0].product_price").value(30000))
                .andExpect(jsonPath("$.data.items[0].product_description").value("기프트카드 3만원권 입니다."))
                .andExpect(jsonPath("$.data.items[0].product_image_url").value("http://example.com/path/gift2.jpg"))
                .andExpect(jsonPath("$.data.items[0].product_category").value("상품권/교보문고"))
                .andExpect(jsonPath("$.data.items[0].product_sale_state").value("ONSALE"));
    }

    @DisplayName("판매 상태별 상품 조회 Test")
    @Test
    void getProductsBySaleStateTest() throws Exception {

        // given
        String url = "/api/products/state/all?sale_state_keyword=SOLDOUT";

        // when
        ResultActions resultActions = mockMvc.perform(
                get(url)
        ).andDo(print());

        // then
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("getProductsBySaleState"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.keyword").value("SOLDOUT"))
                .andExpect(jsonPath("$.data.products").isArray())
                // 첫번째 data 정합성 체크
                .andExpect(jsonPath("$.data.products[0].product_id").value(2))
                .andExpect(jsonPath("$.data.products[0].product_name").value("오리지널 핫번 + 아메리카노(L)"))
                .andExpect(jsonPath("$.data.products[0].product_price").value(6100))
                .andExpect(jsonPath("$.data.products[0].product_description").value("이디야 오리지널 핫번 + 아메리카노(L) 세트입니다."))
                .andExpect(jsonPath("$.data.products[0].product_image_url").value("http://example.com/path/ediya1.jpg"))
                .andExpect(jsonPath("$.data.products[0].product_category").value("카페/이디야"))
                .andExpect(jsonPath("$.data.products[0].product_sale_state").value("SOLDOUT"));
    }

    @DisplayName("판매 상태별 페이징 처리 된 상품 조회 Test")
    @Test
    void getProductsBySaleStateWithPagingTest() throws Exception {

        // given
        String url = "/api/products/state?sale_state_keyword=ONSALE&page=1&pageSize=1";

        // when
        ResultActions resultActions = mockMvc.perform(
                get(url)
        ).andDo(print());

        // then
        resultActions
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("getProductsBySaleStateWithPaging"))
                .andExpect(status().isOk())
                // 페이지 정보 정합성 체크
                .andExpect(jsonPath("$.data.keyword").value("ONSALE"))
                .andExpect(jsonPath("$.data.current_page_number").value(1))
                .andExpect(jsonPath("$.data.page_size").value(1))
                .andExpect(jsonPath("$.data.total_pages").value(4))
                .andExpect(jsonPath("$.data.total_items").value(4))
                .andExpect(jsonPath("$.data.has_more").value(true))
                // 데이터 정합성 체크
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].product_id").value(5))
                .andExpect(jsonPath("$.data.items[0].product_name").value("뿌링클콤보+콜라1.25L"))
                .andExpect(jsonPath("$.data.items[0].product_price").value(22500))
                .andExpect(jsonPath("$.data.items[0].product_description").value("BHC 뿌링클 콤보 + 콜라 1.25L 세트입니다."))
                .andExpect(jsonPath("$.data.items[0].product_image_url").value("http://example.com/path/chicken.jpg"))
                .andExpect(jsonPath("$.data.items[0].product_category").value("치킨/BHC"))
                .andExpect(jsonPath("$.data.items[0].product_sale_state").value("ONSALE"));
    }
}