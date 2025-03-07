package com.NBE3_4_2_Team4.domain.asset.main.service

import com.NBE3_4_2_Team4.domain.asset.main.dto.AdminAssetCategoryRes
import com.NBE3_4_2_Team4.domain.asset.main.entity.AdminAssetCategory
import com.NBE3_4_2_Team4.domain.asset.main.repository.AdminAssetCategoryRepository
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import lombok.RequiredArgsConstructor
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminAssetCategoryService (
    private val adminAssetCategoryRepository: AdminAssetCategoryRepository
) {

    @Transactional
    fun create(name: String): AdminAssetCategory {
        if (adminAssetCategoryRepository.existsByName(name)) throw ServiceException("404-1", "이미 존재하는 이름입니다")

        val adminAssetCategory = AdminAssetCategory(name)
        return adminAssetCategoryRepository.save(adminAssetCategory)
    }

    @Transactional(readOnly = true)
    fun findAll(): List<AdminAssetCategoryRes> {
        return adminAssetCategoryRepository.findAllByIsDisabledFalse()
            .map(AdminAssetCategoryRes::from)
    }

    @Transactional
    fun updateName(id: Long, newName: String): AdminAssetCategory {
        val adminAssetCategory = adminAssetCategoryRepository.findByIdOrNull(id)
            ?: throw ServiceException("404-1", "존재하지 않는 id 입니다")

        adminAssetCategory.name = newName
        return adminAssetCategoryRepository.save(adminAssetCategory)
    }

    @Transactional
    fun disable(id: Long): AdminAssetCategory {
        val adminAssetCategory = adminAssetCategoryRepository.findByIdOrNull(id)
            ?: throw ServiceException("404-1", "존재하지 않는 id 입니다")

        adminAssetCategory.isDisabled = true
        return adminAssetCategoryRepository.save(adminAssetCategory)
    }

    fun count(): Long {
        return adminAssetCategoryRepository.count()
    }
}
