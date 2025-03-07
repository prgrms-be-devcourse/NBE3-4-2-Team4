package com.NBE3_4_2_Team4.domain.asset.main.initData

import com.NBE3_4_2_Team4.domain.asset.main.service.AdminAssetCategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.core.annotation.Order
import org.springframework.transaction.annotation.Transactional

@Configuration
class AdminAssetCategoryInitData (
    private val adminAssetCategoryService: AdminAssetCategoryService
){
    @Autowired
    @Lazy
    private lateinit var self: AdminAssetCategoryInitData

    @Bean
    @Order(0)
    fun AdminAssetCategoryInitDataApplicationRunner(): ApplicationRunner {
        return ApplicationRunner {
            if (adminAssetCategoryService.count() <= 0) self.initData()
        }
    }

    @Transactional
    fun initData() {
        val categories = listOf("SYSTEM_COMPENSATION", "CUSTOMER_SUPPORT", "ABUSE_RESPONSE", "삭제됨")

        for (category in categories) {
            adminAssetCategoryService.create(category)
        }

        adminAssetCategoryService.disable(4)
    }
}
