package com.NBE3_4_2_Team4.domain.asset.main.service;

import com.NBE3_4_2_Team4.domain.asset.main.dto.AdminAssetCategoryRes;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AdminAssetCategory;
import com.NBE3_4_2_Team4.domain.asset.main.repository.AdminAssetCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;


@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class AdminAssetCategoryServiceTest {

    @Autowired
    private AdminAssetCategoryService adminAssetCategoryService;

    @Autowired
    private AdminAssetCategoryRepository adminAssetCategoryRepository;



    @Test
    @DisplayName("카테고리 출력")
    void t1() {
        List<AdminAssetCategoryRes> adminAssetCategories = adminAssetCategoryService.findAll();
        assertEquals(3, adminAssetCategories.size());
    }

    @Test
    @DisplayName("카테고리 셍성")
    void t2() {
        AdminAssetCategory adminAssetCategory = adminAssetCategoryService.create("created");
        assertTrue(adminAssetCategoryRepository.existsByName("created"));
    }

    @Test
    @DisplayName("카테고리 변경")
    void t3() {
        AdminAssetCategory adminAssetCategory = adminAssetCategoryService.updateName(1, "updated");
        assertEquals("updated", adminAssetCategoryRepository.findById(1L).get().getName());
    }

    @Test
    @DisplayName("카테고리 삭제")
    void t4() {
        AdminAssetCategory adminAssetCategory = adminAssetCategoryService.disable(1);
        assertTrue(adminAssetCategoryRepository.findById(1L).get().isDisabled());
    }
}
