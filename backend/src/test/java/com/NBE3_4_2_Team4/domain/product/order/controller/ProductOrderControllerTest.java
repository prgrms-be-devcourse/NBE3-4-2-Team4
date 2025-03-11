package com.NBE3_4_2_Team4.domain.product.order.controller;

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.NBE3_4_2_Team4.domain.product.order.dto.ProductOrderRequestDto.PurchaseDetails;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ProductOrderControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("상품의 구매를 확정하고 상품을 구매한 유저의 포인트를 착감하는 Test")
    @Test
    void createOrderWithPointTest() throws Exception {

        // given
        String url = "/api/order/1";
        PurchaseDetails purchaseDetails = new PurchaseDetails(
                "test2@test.com",
                1L,
                AssetType.POINT,
                AssetCategory.PURCHASE
        );

        // when
        ResultActions resultActions = mockMvc.perform(
                put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(purchaseDetails))
        ).andDo(print());

        // then
        resultActions
                .andExpect(handler().handlerType(ProductOrderController.class))
                .andExpect(handler().methodName("confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result_code").value("200-1"))
                .andExpect(jsonPath("msg")
                        .value("상품 구매가 완료되었습니다. test2@test.com의 포인트가 1번 상품 가격 (1) 만큼 착감되었습니다."));
    }

    @DisplayName("상품의 구매를 확정하고 상품을 구매한 유저의 캐시를 착감하는 Test")
    @Test
    void createOrderWithCashTest() throws Exception {

        // given
        String url = "/api/order/1";
        PurchaseDetails purchaseDetails = new PurchaseDetails(
                "test2@test.com",
                1L,
                AssetType.CASH,
                AssetCategory.PURCHASE
        );

        // when
        ResultActions resultActions = mockMvc.perform(
                put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(purchaseDetails))
        ).andDo(print());

        // then
        resultActions
                .andExpect(handler().handlerType(ProductOrderController.class))
                .andExpect(handler().methodName("confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result_code").value("200-1"))
                .andExpect(jsonPath("msg")
                        .value("상품 구매가 완료되었습니다. test2@test.com의 캐시가 1번 상품 가격 (1) 만큼 착감되었습니다."));
    }
}
