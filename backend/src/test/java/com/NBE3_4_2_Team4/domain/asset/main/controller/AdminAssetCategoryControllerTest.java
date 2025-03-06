package com.NBE3_4_2_Team4.domain.asset.main.controller;

import com.NBE3_4_2_Team4.domain.asset.main.dto.AdminAssetCategoryRes;
import com.NBE3_4_2_Team4.domain.asset.main.service.AdminAssetCategoryService;
import com.NBE3_4_2_Team4.global.security.filter.CustomJwtFilter;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@WebMvcTest(AdminAssetCategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminAssetCategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminAssetCategoryService adminAssetCategoryService;

    @MockitoBean
    private CustomJwtFilter customJwtFilter;

    @Test
    public void t1() throws Exception{
        List<AdminAssetCategoryRes> categories = List.of(
                new AdminAssetCategoryRes(1L, "Category 1"),
                new AdminAssetCategoryRes(2L, "Category 2")
        );

        given(adminAssetCategoryService.findAll()).willReturn(categories);

        // 요청 수행
        mockMvc.perform(get("/api/admin/adminAssetCategory"))
                .andExpect(status().isOk())  // HTTP 상태 코드 확인
                .andDo(print())
                .andExpect(jsonPath("$.size()").value(3))  // 응답 데이터의 크기 확인
                .andExpect(jsonPath("$.data[0].name").value("Category 1"))  // 첫 번째 카테고리 이름 확인
                .andExpect(jsonPath("$.data[1].name").value("Category 2"));
    }
}
