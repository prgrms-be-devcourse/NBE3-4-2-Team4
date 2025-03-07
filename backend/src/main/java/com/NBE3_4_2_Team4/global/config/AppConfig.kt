package com.NBE3_4_2_Team4.global.config

import com.NBE3_4_2_Team4.global.exceptions.InValidAccessException
import org.apache.tika.Tika
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class AppConfig {
    companion object {
        private lateinit var environment: Environment
        private lateinit var tika: Tika
        private lateinit var siteBackUrl: String
        private lateinit var genFileDirPath: String
        private lateinit var springServletMultipartMaxFileSize: String
        
        val log: Logger = LoggerFactory.getLogger(InValidAccessException::class.java)

        @JvmStatic
        fun isProd(): Boolean = environment.matchesProfiles("prod")

        @JvmStatic
        fun isDev(): Boolean = environment.matchesProfiles("dev")

        @JvmStatic
        fun isTest(): Boolean = environment.matchesProfiles("test")

        @JvmStatic
        fun getTika(): Tika = tika

        @JvmStatic
        fun getSiteBackUrl(): String = siteBackUrl

        @JvmStatic
        fun getGenFileDirPath(): String = genFileDirPath

        @JvmStatic
        fun getSpringServletMultipartMaxFileSize(): String = springServletMultipartMaxFileSize

        @JvmStatic
        fun getTempDirPath(): String = System.getProperty("java.io.tmpdir")
    }

    @Autowired
    fun setEnvironment(environment: Environment) {
        Companion.environment = environment
    }

    @Autowired
    fun setTika(tika: Tika) {
        Companion.tika = tika
    }

    @Value("\${custom.domain.backend}")
    fun setSiteBackUrl(siteBackUrl: String) {
        Companion.siteBackUrl = siteBackUrl
    }

    @Value("\${custom.genFile.dirPath}")
    fun setGenFileDirPath(genFileDirPath: String) {
        Companion.genFileDirPath = genFileDirPath
    }

    @Value("\${spring.servlet.multipart.max-file-size}")
    fun setSpringServletMultipartMaxFileSize(springServletMultipartMaxFileSize: String) {
        Companion.springServletMultipartMaxFileSize = springServletMultipartMaxFileSize
    }
}
