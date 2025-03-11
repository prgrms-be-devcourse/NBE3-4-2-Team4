package com.NBE3_4_2_Team4.domain.product.product.controller

import com.NBE3_4_2_Team4.domain.product.product.dto.ProductRequestDto
import com.NBE3_4_2_Team4.domain.product.product.dto.ProductRequestDto.WriteItem
import com.NBE3_4_2_Team4.domain.product.product.initData.ProductInitData.Companion.PRODUCT_LIST
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var mockMvc: MockMvc

    @DisplayName("전체 상품 조회 Test")
    @Test
    fun getProductsTest() {
        // given
        val url = "/api/products/all"

        // when
        val resultActions = mockMvc.perform(
            get(url)
        ).andDo(print())

        // then
        resultActions
            .andExpect(handler().handlerType(ProductController::class.java))
            .andExpect(handler().methodName("getAllProducts"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.result_code").value("200-1"))
            .andExpect(jsonPath("$.msg").value("15건의 상품이 조회되었습니다."))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].product_id").value(1))
            .andExpect(jsonPath("$.data[0].product_name").value("딸기라떼"))
            .andExpect(jsonPath("$.data[0].product_price").value(3800))
            .andExpect(jsonPath("$.data[0].product_description").value("빽다방 딸기라떼입니다."))
            .andExpect(jsonPath("$.data[0].product_category").value("카페/빽다방"))
    }

    @DisplayName("페이징 처리 된 전체 상품 조회 with 검색 Test")
    @Test
    fun getProductsByKeywordWithPagingTest() {

        // given
        val url = "/api/products?page=1&page_size=1&search_keyword_type=ALL"

        // when
        val resultActions = mockMvc.perform(
            get(url)
        ).andDo(print())

        val dataSize = PRODUCT_LIST.size
        val expectedProduct = PRODUCT_LIST[dataSize - 1]

        // then
        resultActions
            .andExpect(handler().handlerType(ProductController::class.java))
            .andExpect(handler().methodName("getAllProductsByKeywordWithPaging"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("result_code").value("200-1"))
            .andExpect(jsonPath("msg").value("1건의 상품이 조회되었습니다."))
            // 페이지 정보 정합성 체크
            .andExpect(jsonPath("$.data.current_page_number").value(1))
            .andExpect(jsonPath("$.data.page_size").value(1))

            // 데이터 정합성 체크
            .andExpect(jsonPath("$.data.items").isArray)
            .andExpect(jsonPath("$.data.items[0].product_id").value(dataSize))
            .andExpect(jsonPath("$.data.items[0].product_name").value(expectedProduct.productName))
            .andExpect(jsonPath("$.data.items[0].product_price").value(expectedProduct.productPrice))
            .andExpect(jsonPath("$.data.items[0].product_description").value(expectedProduct.productDescription))
            .andExpect(jsonPath("$.data.items[0].product_category").value(expectedProduct.productCategory))
            .andExpect(jsonPath("$.data.items[0].product_id").value(15))
            .andExpect(jsonPath("$.data.items[0].product_name").value("양념치킨+콜라1.25L"))
            .andExpect(jsonPath("$.data.items[0].product_price").value(24000))
            .andExpect(jsonPath("$.data.items[0].product_description").value("BBQ 양념치킨 + 콜라 1.25L 입니다."))
            .andExpect(jsonPath("$.data.items[0].product_category").value("치킨/BBQ"))
    }

    @DisplayName("카테고리별 상품 조회 Test")
    @Test
    fun getProductsByCategoryTest() {

        // given
        val url = "/api/products/categories/all?category_keyword=상품권"

        // when
        val resultActions = mockMvc.perform(
            get(url)
        ).andDo(print())

        // then
        resultActions
            .andExpect(handler().handlerType(ProductController::class.java))
            .andExpect(handler().methodName("getProductsByCategory"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("result_code").value("200-1"))
            .andExpect(jsonPath("msg").value("2건의 상품이 조회되었습니다."))
            .andExpect(jsonPath("$.data.keyword").value("상품권"))
            .andExpect(jsonPath("$.data.products").isArray)
            // 첫번째 data 정합성 체크
            .andExpect(jsonPath("$.data.products[0].product_id").value(3))
            .andExpect(jsonPath("$.data.products[0].product_name").value("올리브영 기프트카드 2만원권"))
            .andExpect(jsonPath("$.data.products[0].product_price").value(20000))
            .andExpect(jsonPath("$.data.products[0].product_description").value("올리브영 기프트카드 2만원권 입니다."))
            .andExpect(jsonPath("$.data.products[0].product_category").value("상품권/올리브영"))
            .andExpect(jsonPath("$.data.products[0].product_sale_state").value("AVAILABLE"))
            // 두번째 data 정합성 체크
            .andExpect(jsonPath("$.data.products[1].product_id").value(4))
            .andExpect(jsonPath("$.data.products[1].product_name").value("교보문고 기프트카드 3만원권"))
            .andExpect(jsonPath("$.data.products[1].product_price").value(30000))
            .andExpect(jsonPath("$.data.products[1].product_description").value("기프트카드 3만원권 입니다."))
            .andExpect(jsonPath("$.data.products[1].product_category").value("상품권/교보문고"))
            .andExpect(jsonPath("$.data.products[1].product_sale_state").value("AVAILABLE"))
    }

    @DisplayName("카테고리별 페이징 처리 된 상품 조회 Test")
    @Test
    fun getProductsByCategoryWithPagingTest() {

        // given
        val url = "/api/products/categories?category_keyword=상품권&page=1&page_size=1"

        // when
        val resultActions = mockMvc.perform(
            get(url)
        ).andDo(print())

        // then
        resultActions
            .andExpect(handler().handlerType(ProductController::class.java))
            .andExpect(handler().methodName("getProductsByCategoryWithPaging"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("result_code").value("200-1"))
            .andExpect(jsonPath("msg").value("1건의 상품이 조회되었습니다."))
            // 페이지 정보 정합성 체크
            .andExpect(jsonPath("$.data.keyword").value("상품권"))
            .andExpect(jsonPath("$.data.current_page_number").value(1))
            .andExpect(jsonPath("$.data.page_size").value(1))
            .andExpect(jsonPath("$.data.total_pages").value(2))
            .andExpect(jsonPath("$.data.total_items").value(2))
            .andExpect(jsonPath("$.data.has_more").value(true))
            // 데이터 정합성 체크
            .andExpect(jsonPath("$.data.items").isArray)
            .andExpect(jsonPath("$.data.items[0].product_id").value(4))
            .andExpect(jsonPath("$.data.items[0].product_name").value("교보문고 기프트카드 3만원권"))
            .andExpect(jsonPath("$.data.items[0].product_price").value(30000))
            .andExpect(jsonPath("$.data.items[0].product_description").value("기프트카드 3만원권 입니다."))
            .andExpect(jsonPath("$.data.items[0].product_category").value("상품권/교보문고"))
            .andExpect(jsonPath("$.data.items[0].product_sale_state").value("AVAILABLE"))
    }

    @DisplayName("판매 상태별 상품 조회 Test")
    @Test
    fun getProductsBySaleStateTest() {

        // given
        val url = "/api/products/states/all?sale_state_keyword=AVAILABLE"

        // when
        val resultActions = mockMvc.perform(
            get(url)
        ).andDo(print())

        // then
        resultActions
            .andExpect(handler().handlerType(ProductController::class.java))
            .andExpect(handler().methodName("getProductsBySaleState"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("result_code").value("200-1"))
    }

    @DisplayName("판매 상태별 페이징 처리 된 상품 조회 Test")
    @Test
    fun getProductsBySaleStateWithPagingTest() {

        // given
        val url = "/api/products/states?sale_state_keyword=AVAILABLE&page=1&page_size=1"

        // when
        val resultActions = mockMvc.perform(
            get(url)
        ).andDo(print())

        // then
        resultActions
            .andExpect(handler().handlerType(ProductController::class.java))
            .andExpect(handler().methodName("getProductsBySaleStateWithPaging"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("result_code").value("200-1"))
    }

    @DisplayName("단건 상품 조회 Test")
    @Test
    fun getProductTest() {

        // given
        val url = "/api/products/1"

        // when
        val resultActions = mockMvc.perform(
            get(url)
        ).andDo(print())

        // then
        resultActions
            .andExpect(handler().handlerType(ProductController::class.java))
            .andExpect(handler().methodName("getProduct"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("result_code").value("200-1"))
            .andExpect(jsonPath("msg").value("1번 상품이 조회되었습니다."))
            // 데이터 정합성 체크
            .andExpect(jsonPath("$.data.product_id").value(1))
            .andExpect(jsonPath("$.data.product_name").value("딸기라떼"))
            .andExpect(jsonPath("$.data.product_price").value(3800))
            .andExpect(jsonPath("$.data.product_description").value("빽다방 딸기라떼입니다."))
            .andExpect(jsonPath("$.data.product_category").value("카페/빽다방"))
    }

    @DisplayName("단건 상품 생성 Test")
    @WithUserDetails("admin@test.com")
    @Test
    fun writeProductTest() {

        // given
        val url = "/api/products"
        val saved = WriteItem(
            "테스트용 상품",
            1,
            "테스트용 상품입니다.",
            "http://example.com/path/test.jpg",
            "테스트",
            "UNAVAILABLE"
        )

        // when
        val resultActions = mockMvc.perform(
            post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(saved))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        resultActions
            .andExpect(handler().handlerType(ProductController::class.java))
            .andExpect(handler().methodName("writeProduct"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("result_code").value("200-1"))
            .andExpect(jsonPath("msg").value("16번 상품이 생성되었습니다."))
            // 데이터 정합성 체크
            .andExpect(jsonPath("$.data.product_id").value(16))
    }

    @DisplayName("단건 상품 수정 Test")
    @WithUserDetails("admin@test.com")
    @Test
    fun updateProductTest() {

        // given
        val url = "/api/products/5"
        val updated = ProductRequestDto.UpdateItem(
            "테스트용 상품 (수정)",
            2,
            "테스트용 상품 수정본 입니다.",
            "http://example.com/path/test-update.jpg",
            "테스트/수정",
            "UPCOMING"
        )

        // when
        val resultActions = mockMvc.perform(
            patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        resultActions
            .andExpect(handler().handlerType(ProductController::class.java))
            .andExpect(handler().methodName("updateProduct"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("result_code").value("200-1"))
            .andExpect(jsonPath("msg").value("5번 상품이 수정되었습니다."))
            // 데이터 정합성 체크
            .andExpect(jsonPath("$.data.product_id").value(5))
            .andExpect(jsonPath("$.data.product_name").value("테스트용 상품 (수정)"))
            .andExpect(jsonPath("$.data.product_price").value(2))
            .andExpect(jsonPath("$.data.product_description").value("테스트용 상품 수정본 입니다."))
            .andExpect(jsonPath("$.data.product_image_url").value("http://example.com/path/test-update.jpg"))
            .andExpect(jsonPath("$.data.product_category").value("테스트/수정"))
            .andExpect(jsonPath("$.data.product_sale_state").value("UPCOMING"))
    }

    @DisplayName("단건 상품 삭제 Test")
    @WithUserDetails("admin@test.com")
    @Test
    fun deleteProductTest() {

        // given
        val url = "/api/products/6"

        // when
        val resultActions = mockMvc.perform(
            delete(url)
        ).andDo(print())

        // then
        resultActions
            .andExpect(handler().handlerType(ProductController::class.java))
            .andExpect(handler().methodName("deleteProduct"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("result_code").value("200-1"))
            .andExpect(jsonPath("msg").value("6번 상품이 삭제되었습니다."))
    }

}