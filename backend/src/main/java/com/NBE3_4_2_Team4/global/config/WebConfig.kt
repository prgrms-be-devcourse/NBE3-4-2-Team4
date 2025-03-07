package com.NBE3_4_2_Team4.global.config

import com.NBE3_4_2_Team4.global.config.AppConfig.Companion.getGenFileDirPath
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/gen/**")
            .addResourceLocations("file:///${getGenFileDirPath()}/")
    }
}
