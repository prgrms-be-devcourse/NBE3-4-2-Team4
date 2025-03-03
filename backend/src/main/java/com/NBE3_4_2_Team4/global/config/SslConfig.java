package com.NBE3_4_2_Team4.global.config;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.Security;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class SslConfig {

    @Value("${custom.iamport.ssl.trustStore}")
    private String trustStore;

    @Value("${custom.iamport.ssl.trustStorePassword}")
    private String trustStorePassword;

    @PostConstruct
    public void configureSSL() {
        try {
            // Classpath에서 Iamport.jks 인증서 파일 로드
            InputStream trustStoreStream = new ClassPathResource(trustStore).getInputStream();
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(trustStoreStream, trustStorePassword.toCharArray());

            // 시스템 속성 적용
            System.setProperty("javax.net.ssl.trustStore", trustStore);
            System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
            Security.setProperty("jdk.tls.client.protocols", "TLSv1.3");

        } catch (Exception e) {
            throw new RuntimeException("SSL 설정 오류", e);
        }
    }
}
