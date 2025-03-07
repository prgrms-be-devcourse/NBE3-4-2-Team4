package com.NBE3_4_2_Team4.domain.asset.main.service

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory
import org.springframework.stereotype.Service

@Service
interface AssetService {
    fun transfer(fromUsername: String, toUsername: String, amount: Long, assetCategory: AssetCategory)
    fun deduct(from: String, amount: Long, assetCategory: AssetCategory): Long
    fun accumulate(to: String, amount: Long, assetCategory: AssetCategory): Long
    fun adminDeduct(from: String, amount: Long, admAstCategoryId: Long): Long
    fun adminAccumulate(to: String, amount: Long, admAstCategoryId: Long): Long
}
