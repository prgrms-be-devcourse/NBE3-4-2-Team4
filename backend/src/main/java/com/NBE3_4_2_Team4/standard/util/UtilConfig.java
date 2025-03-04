package com.NBE3_4_2_Team4.standard.util;

import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilConfig {
    @Bean
    public Tika tika() {
        return new Tika();
    }
}