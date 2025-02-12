package com.NBE3_4_2_Team4.global.security.oauth2;

import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.entity.OAuth2RefreshToken;
import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.repository.OAuth2RefreshTokenRepository;
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.service.OAuth2UserInfoService;
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfo;
import com.NBE3_4_2_Team4.global.security.user.CustomUser;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberService memberService;
    private final OAuth2Manager oAuth2Manager;
    private final OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String refreshToken = (String) userRequest.getAdditionalParameters().getOrDefault("refresh_token", null);

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String providerTypeCode = userRequest
                .getClientRegistration()
                .getRegistrationId()
                .toUpperCase();

        Member.OAuth2Provider oAuth2Provider = Member.OAuth2Provider.getOAuth2ProviderByName(providerTypeCode);

        OAuth2UserInfoService oAuth2UserInfoService = oAuth2Manager.getOAuth2UserInfoService(oAuth2Provider);

        OAuth2UserInfo oAuth2UserInfo = oAuth2UserInfoService.getOAuth2UserInfo(oAuth2User);

        String oAuth2Id = oAuth2UserInfo.getOAuth2Id();
        String nickname = oAuth2UserInfo.getNickname();
        String username = String.format("%s_%s", providerTypeCode, oAuth2Id);

        Member member = memberService.signUpOrIn(username, "", nickname, oAuth2Provider);

        if (refreshToken != null && !refreshToken.isBlank()) {
            OAuth2RefreshToken oAuth2RefreshToken = oAuth2RefreshTokenRepository.findByMember(member)
                    .orElse(null); // 먼저 찾기만 함
            if (oAuth2RefreshToken != null) {
                // 이미 존재하는 경우 업데이트
                oAuth2RefreshToken.setRefreshToken(refreshToken);
                oAuth2RefreshTokenRepository.save(oAuth2RefreshToken); // 업데이트
            } else {
                // 없으면 새로 저장
                oAuth2RefreshTokenRepository.save(OAuth2RefreshToken.builder()
                        .member(member)
                        .oAuth2Id(oAuth2Id)
                        .refreshToken(refreshToken)
                        .build());
            }
        }

        return new CustomUser(member);
    }
}
