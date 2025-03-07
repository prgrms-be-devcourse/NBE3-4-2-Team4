package com.NBE3_4_2_Team4.global.initData

import com.NBE3_4_2_Team4.standard.util.Ut
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile

@Profile("dev")
@Configuration
class DevInitData {
    @Autowired
    @Lazy
    private lateinit var self: DevInitData

    @Bean
    fun devInitDataApplicationRunner(): ApplicationRunner {
        return ApplicationRunner {
            Ut.file.downloadByHttp("http://localhost:8080/v3/api-docs", ".", false)

            val cmd =
                "yes | npx --package typescript --package openapi-typescript openapi-typescript api-docs.json -o ../frontend/src/lib/backend/apiV1/schema.d.ts"

            Ut.cmd.runAsync(cmd)
        }
    }
}
