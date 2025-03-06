package com.NBE3_4_2_Team4.domain.asset.main.dto

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetHistory
import lombok.Builder
import lombok.Getter
import java.time.LocalDateTime

@Getter
@Builder
class AssetHistoryRes (
    val amount: Long,
    val assetCategory: String,
    val assetType: String,
    val createdAt: LocalDateTime,
    val adminAssetCategory: String ?= null,
    val counterAccountUsername: String ?= null
){
    companion object {

        @JvmStatic
        fun from(assetHistory: AssetHistory): AssetHistoryRes {
            //TODO: 추후 수정 getUsername 안됨
            val counterMemberUsername = ""
            val adminAssetCategory = assetHistory.adminAssetCategory?.name ?: ""

            return AssetHistoryRes(
                assetHistory.amount,
                assetHistory.assetCategory.displayName,
                assetHistory.assetType.displayName,
                assetHistory.createdAt,
                adminAssetCategory,
                counterMemberUsername
                )
        }
    }
}
