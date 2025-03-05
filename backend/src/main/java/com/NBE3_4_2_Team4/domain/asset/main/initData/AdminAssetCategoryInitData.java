package com.NBE3_4_2_Team4.domain.asset.main.initData;

import com.NBE3_4_2_Team4.domain.asset.main.service.AdminAssetCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AdminAssetCategoryInitData {
    private final AdminAssetCategoryService adminAssetCategoryService;

    @Autowired
    @Lazy
    private AdminAssetCategoryInitData self;

    @Bean
    @Order(0)
    public ApplicationRunner AdminAssetCategoryInitDataApplicationRunner() {
        return _ -> {
            if (adminAssetCategoryService.count() <= 0)
                self.initData();
        };
    }

    @Transactional
    public void initData() {
        List<String> categories = List.of("SYSTEM_COMPENSATION", "CUSTOMER_SUPPORT", "ABUSE_RESPONSE", "삭제됨");

        for (String category : categories) {
            adminAssetCategoryService.create(category);
        }

        adminAssetCategoryService.disable(4);
    }

}
