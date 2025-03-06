package com.NBE3_4_2_Team4.domain.asset.main.controller

import com.NBE3_4_2_Team4.domain.asset.main.dto.AdminAssetCategoryCreateReq
import com.NBE3_4_2_Team4.domain.asset.main.dto.AdminAssetCategoryRes
import com.NBE3_4_2_Team4.domain.asset.main.dto.AdminAssetCategoryUpdateReq
import com.NBE3_4_2_Team4.domain.asset.main.service.AdminAssetCategoryService
import com.NBE3_4_2_Team4.global.rsData.RsData
import com.NBE3_4_2_Team4.standard.base.Empty
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.web.bind.annotation.*

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/adminAssetCategory")
@Tag(name = "adminAssetCategoryController", description = "asset 카테고리 관리 API")
class AdminAssetCategoryController (
    private val adminAssetCategoryService: AdminAssetCategoryService
){
    @GetMapping
    fun getAdminAssetCategoryList(): RsData<List<AdminAssetCategoryRes>> {
        return RsData(
            "200-1",
            "",
            adminAssetCategoryService.findAll()
        )
    }

    @PostMapping
    fun create(@RequestBody req: @Valid AdminAssetCategoryCreateReq): RsData<Empty> {
        adminAssetCategoryService.create(req.name)
        return RsData.OK
    }

    @PutMapping("/{categoryId}")
    fun update(
        @PathVariable categoryId: Long,
        @RequestBody req: @Valid AdminAssetCategoryUpdateReq
    ): RsData<Empty> {
        adminAssetCategoryService.updateName(categoryId, req.name)
        return RsData.OK
    }

    @DeleteMapping("/{categoryId}")
    fun delete(
        @PathVariable categoryId: Long
    ): RsData<Empty> {
        adminAssetCategoryService.disable(categoryId)
        return RsData.OK
    }
}
