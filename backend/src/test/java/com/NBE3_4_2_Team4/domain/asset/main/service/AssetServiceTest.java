package com.NBE3_4_2_Team4.domain.asset.main.service;

import com.NBE3_4_2_Team4.domain.asset.BaseAssetTestSetup;
import com.NBE3_4_2_Team4.domain.asset.main.dto.AssetHistoryReq;
import com.NBE3_4_2_Team4.domain.asset.main.dto.AssetHistoryRes;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetHistory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class AssetServiceTest extends BaseAssetTestSetup {
    @Test
    @DisplayName("Asset 히스토리 리스트 출력")
    void t1() {
        AssetHistoryReq assetHistoryReq = new AssetHistoryReq(1, AssetType.CASH, AssetCategory.PURCHASE, null, null);

        PageDto<AssetHistoryRes> res = assetHistoryService.getHistoryPageWithFilter(member1, 10, assetHistoryReq);
        Assertions.assertEquals(1, res.getTotalItems());
    }

    @Test
    @DisplayName("Asset 히스토리 생성")
    void t2() {
        long id = assetHistoryService.createHistory(member1, null, 10, AssetCategory.ANSWER, null, AssetType.POINT, "a");
        AssetHistory assetHistory = assetHistoryRepository.findById(id).orElseThrow(() -> new RuntimeException("히스토리 없음"));
        assertEquals(member1.getId(), assetHistory.getMember().getId());
        LocalDateTime createdAt = LocalDateTime.now();
        assertTrue(Duration.between(assetHistory.getCreatedAt(), createdAt).getSeconds() < 5);
    }
}
