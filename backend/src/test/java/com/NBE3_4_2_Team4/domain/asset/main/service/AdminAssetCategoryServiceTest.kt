package com.NBE3_4_2_Team4.domain.asset.main.service

import com.NBE3_4_2_Team4.domain.asset.main.repository.AdminAssetCategoryRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AdminAssetCategoryServiceTest {
    @Autowired
    private lateinit var adminAssetCategoryService: AdminAssetCategoryService

    @Autowired
    private lateinit var adminAssetCategoryRepository: AdminAssetCategoryRepository


    @Test
    @DisplayName("카테고리 출력")
    fun t1() {
        val adminAssetCategories = adminAssetCategoryService.findAll()
        Assertions.assertEquals(3, adminAssetCategories.size)
    }

    @Test
    @DisplayName("카테고리 셍성")
    fun t2() {
        val adminAssetCategory = adminAssetCategoryService.create("created")
        Assertions.assertTrue(adminAssetCategoryRepository.existsByName("created"))
    }

    @Test
    @DisplayName("카테고리 변경")
    fun t3() {
        val adminAssetCategory = adminAssetCategoryService.updateName(1, "updated")
        Assertions.assertEquals("updated", adminAssetCategoryRepository.findById(1L).get().name)
    }

    @Test
    @DisplayName("카테고리 삭제")
    fun t4() {
        val adminAssetCategory = adminAssetCategoryService.disable(1)
        Assertions.assertTrue(adminAssetCategoryRepository.findById(1L).get().isDisabled)
    }
}
