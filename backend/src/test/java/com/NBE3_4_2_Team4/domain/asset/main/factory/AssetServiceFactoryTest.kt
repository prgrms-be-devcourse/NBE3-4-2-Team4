package com.NBE3_4_2_Team4.domain.asset.main.factory

import com.NBE3_4_2_Team4.domain.asset.factory.AssetServiceFactory
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.NBE3_4_2_Team4.domain.asset.main.service.AssetService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AssetServiceFactoryTest {
    @Autowired
    private lateinit var assetServiceFactory: AssetServiceFactory

    @Autowired
    private lateinit var assetServices: List<AssetService>

    @Test
    @DisplayName("AssetServiceFactory 조회 테스트")
    fun t1() {
        val expectedSize = assetServices.size
        val actualSize = assetServiceFactory.size
        val className = assetServiceFactory.getService(AssetType.POINT).javaClass.superclass.simpleName

        Assertions.assertEquals(expectedSize, actualSize, "AssetServiceFactory의 서비스 개수가 일치해야 함")
        Assertions.assertEquals(className, "PointService")
    }
}

