package com.NBE3_4_2_Team4.domain.asset;

import com.NBE3_4_2_Team4.domain.asset.factory.AssetServiceFactory;
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class AssetServiceFactoryTest {
    @Autowired
    private AssetServiceFactory assetServiceFactory;

    @Autowired
    private List<AssetService> assetServices;

    @Test
    @DisplayName("AssetServiceFactory 조회 테스트")
    void t1() {
        int expectedSize = assetServices.size();
        int actualSize = assetServiceFactory.getSize();
        String className = assetServiceFactory.getService("Point").getClass().getSuperclass().getSimpleName();

        assertEquals(expectedSize, actualSize, "AssetServiceFactory의 서비스 개수가 일치해야 함");
        assertEquals(className, "PointService");
    }
}

