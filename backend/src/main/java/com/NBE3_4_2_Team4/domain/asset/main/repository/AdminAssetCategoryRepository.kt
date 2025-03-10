package com.NBE3_4_2_Team4.domain.asset.main.repository

import com.NBE3_4_2_Team4.domain.asset.main.entity.AdminAssetCategory
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

//TODO: optional 제거
interface AdminAssetCategoryRepository : JpaRepository<AdminAssetCategory, Long> {
    fun existsByName(name: String): Boolean
    fun findAllByIsDisabledFalse(): List<AdminAssetCategory>
    fun findByName(name: String): Optional<AdminAssetCategory>
}
