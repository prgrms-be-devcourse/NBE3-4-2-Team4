package com.NBE3_4_2_Team4.domain.asset.main.dto

import com.NBE3_4_2_Team4.domain.asset.main.entity.AdminAssetCategory

class AdminAssetCategoryRes (
    val id: Long,
    val name: String
) {
    companion object {
        @JvmStatic
        fun from(entity: AdminAssetCategory): AdminAssetCategoryRes {
            return AdminAssetCategoryRes(entity.id!!, entity.name);
        }
    }
}
