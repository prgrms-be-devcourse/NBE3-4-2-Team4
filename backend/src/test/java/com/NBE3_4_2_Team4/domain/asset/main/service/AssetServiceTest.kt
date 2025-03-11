package com.NBE3_4_2_Team4.domain.asset.main.service

import com.NBE3_4_2_Team4.domain.asset.BaseAssetTestSetup
import com.NBE3_4_2_Team4.domain.asset.main.dto.AssetHistoryReq
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AssetServiceTest : BaseAssetTestSetup() {
    @Test
    @DisplayName("Asset 히스토리 리스트 출력")
    fun t1() {
        val assetHistoryReq = AssetHistoryReq(1, AssetType.CASH, AssetCategory.PURCHASE, null, null)

        val res = assetHistoryService.getHistoryPageWithFilter(member1, 10, assetHistoryReq)
        Assertions.assertEquals(1, res.totalItems)
    }

    @Test
    @DisplayName("Asset 히스토리 생성")
    fun t2() {
        val id = assetHistoryService.createHistory(member1, null, 10, AssetCategory.ANSWER, null, AssetType.POINT, "a")
        val assetHistory = assetHistoryRepository.findById(id).orElseThrow {
            RuntimeException(
                "히스토리 없음"
            )
        }
        Assertions.assertEquals(member1.id, assetHistory.member.id)
        val createdAt = LocalDateTime.now()
        Assertions.assertTrue(Duration.between(assetHistory.createdAt, createdAt).seconds < 5)
    }
}
