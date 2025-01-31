package com.NBE3_4_2_Team4.domain.product.product.initData;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class ProductInitData {

    @Autowired
    @Lazy
    private ProductInitData self;

    @Bean
    public ApplicationRunner productInitDataApplicationRunner() {
        return args -> {
            self.work1();
        };
    }

    @Transactional
    public void work1() {
        
        // TODO: 제품 생성 API 만든 후 작업 필요

    }
}
