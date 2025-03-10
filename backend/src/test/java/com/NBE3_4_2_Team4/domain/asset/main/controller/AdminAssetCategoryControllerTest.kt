package com.NBE3_4_2_Team4.domain.asset.main.controller

import com.NBE3_4_2_Team4.domain.asset.main.dto.AdminAssetCategoryRes
import com.NBE3_4_2_Team4.domain.asset.main.service.AdminAssetCategoryService
import com.NBE3_4_2_Team4.global.security.filter.CustomJwtFilter
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.List

@ActiveProfiles("test")
@WebMvcTest(AdminAssetCategoryController::class)
@AutoConfigureMockMvc(addFilters = false)
class AdminAssetCategoryControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var adminAssetCategoryService: AdminAssetCategoryService

    @MockitoBean
    private lateinit var customJwtFilter: CustomJwtFilter

    @Test
    @Throws(Exception::class)
    fun t1() {
        val categories = List.of(
            AdminAssetCategoryRes(1L, "Category 1"),
            AdminAssetCategoryRes(2L, "Category 2")
        )

        BDDMockito.given(adminAssetCategoryService.findAll()).willReturn(categories)

        // 요청 수행
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/adminAssetCategory"))
            .andExpect(MockMvcResultMatchers.status().isOk()) // HTTP 상태 코드 확인
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(3)) // 응답 데이터의 크기 확인
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].name").value("Category 1")) // 첫 번째 카테고리 이름 확인
            .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].name").value("Category 2"))
    }
}
