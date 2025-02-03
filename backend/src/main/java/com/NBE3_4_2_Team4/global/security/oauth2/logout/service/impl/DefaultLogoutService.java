package com.NBE3_4_2_Team4.global.security.oauth2.logout.service.impl;

import com.NBE3_4_2_Team4.global.config.OAuth2LogoutFactoryConfig;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.global.security.oauth2.logout.service.OAuth2LogoutService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class DefaultLogoutService implements OAuth2LogoutService {
    @Value("${custom.domain.backend}")
    private String backendDomain;

    @Override
    public String getLogoutUrl() {
        return backendDomain + OAuth2LogoutFactoryConfig.LOGOUT_COMPLETE_URL;
    }
}
