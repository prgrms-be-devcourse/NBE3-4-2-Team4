package com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.service;

import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.repository.OAuth2RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2RefreshTokenService {
    private final OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;
}
