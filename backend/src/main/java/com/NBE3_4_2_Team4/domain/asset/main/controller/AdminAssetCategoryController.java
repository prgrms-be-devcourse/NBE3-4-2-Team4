package com.NBE3_4_2_Team4.domain.asset.main.controller;

import com.NBE3_4_2_Team4.domain.asset.main.dto.AdminAssetCategoryCreateReq;
import com.NBE3_4_2_Team4.domain.asset.main.dto.AdminAssetCategoryUpdateReq;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AdminAssetCategory;
import com.NBE3_4_2_Team4.domain.asset.main.service.AdminAssetCategoryService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.base.Empty;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/adminAssetCategory")
@Tag(name = "adminAssetCategoryController", description = "asset 카테고리 관리 API")
public class AdminAssetCategoryController {
    private final AdminAssetCategoryService adminAssetCategoryService;

    @GetMapping
    public RsData<List<AdminAssetCategory>> getAdminAssetCategoryList() {

        return new RsData<>(
                "200-1",
                "",
                adminAssetCategoryService.findAll());
    }

    @PostMapping
    public RsData<Empty> create(@RequestBody @Valid AdminAssetCategoryCreateReq req) {
        adminAssetCategoryService.create(req.getName());
        return RsData.OK;
    }

    @PutMapping("/{categoryId}")
    public RsData<Empty> update(
            @PathVariable Long categoryId,
            @RequestBody @Valid AdminAssetCategoryUpdateReq req
    ) {
        adminAssetCategoryService.updateName(categoryId, req.getName());
        return RsData.OK;
    }

    @DeleteMapping("/{categoryId}")
    public RsData<Empty> delete(
            @PathVariable Long categoryId
    ) {
        adminAssetCategoryService.disable(categoryId);
        return RsData.OK;
    }
}
