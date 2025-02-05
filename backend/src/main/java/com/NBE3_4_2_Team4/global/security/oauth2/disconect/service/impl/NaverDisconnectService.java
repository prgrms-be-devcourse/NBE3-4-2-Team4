package com.NBE3_4_2_Team4.global.security.oauth2.disconect.service.impl;

import com.NBE3_4_2_Team4.global.security.oauth2.disconect.service.OAuth2DisconnectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NaverDisconnectService implements OAuth2DisconnectService {

    @Override
    public boolean disconnect(String refreshToken) {
        return false;
    }
}
