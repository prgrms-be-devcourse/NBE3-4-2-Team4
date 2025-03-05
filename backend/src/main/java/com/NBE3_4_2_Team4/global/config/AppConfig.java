package com.NBE3_4_2_Team4.global.config;

import lombok.Getter;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AppConfig {
    private static Environment environment;

    @Autowired
    public void setEnvironment(Environment environment) {
        AppConfig.environment = environment;
    }

    private static String siteBackUrl;

    public static String getSiteBackUrl() {
        return siteBackUrl;
    }

    @Value("${custom.domain.backend}")
    public void setSiteBackUrl(String siteBackUrl) {
        AppConfig.siteBackUrl = siteBackUrl;
    }

    public static boolean isProd() {
        return environment.matchesProfiles("prod");
    }

    public static boolean isDev() {
        return environment.matchesProfiles("dev");
    }

    public static boolean isTest() {
        return environment.matchesProfiles("test");
    }

    public static boolean isNotProd() {
        return !isProd();
    }

    @Getter
    private static Tika tika;

    @Autowired
    public void setTika(Tika tika) {
        AppConfig.tika = tika;
    }

    public static String genFileDirPath;

    public static String getGenFileDirPath() {
        return genFileDirPath;
    }

    @Value("${custom.genFile.dirPath}")
    public void setGenFileDirPath(String genFileDirPath) {
        this.genFileDirPath = genFileDirPath;
    }

    @Getter
    private static String springServletMultipartMaxFileSize;

    @Value("${spring.servlet.multipart.max-file-size}")
    public void setSpringServletMultipartMaxFileSize(String springServletMultipartMaxFileSize) {
        this.springServletMultipartMaxFileSize = springServletMultipartMaxFileSize;
    }

    @Getter
    private static String springServletMultipartMaxRequestSize;

    @Value("${spring.servlet.multipart.max-request-size}")
    public void setSpringServletMultipartMaxRequestSize(String springServletMultipartMaxRequestSize) {
        this.springServletMultipartMaxRequestSize = springServletMultipartMaxRequestSize;
    }

    public static String getTempDirPath() {
        return System.getProperty("java.io.tmpdir");
    }

}
