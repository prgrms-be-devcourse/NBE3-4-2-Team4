package com.NBE3_4_2_Team4.global.security.oauth2;

import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfoFactory;
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


@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberService memberService;
    private final OAuth2UserInfoFactory oAuth2UserInfoFactory;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.error("Client Registration: {}", userRequest.getClientRegistration());

        // accessToken 정보 출력
        log.error("Access Token: {}", userRequest.getAccessToken().getTokenValue());

        // 기타 필요한 정보 출력
        log.error("Access Token Expiry: {}", userRequest.getAccessToken().getExpiresAt());
        log.error("Scope: {}", userRequest.getAccessToken().getScopes());
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.error("Loading user {}", oAuth2User);
        String providerTypeCode = userRequest
                .getClientRegistration()
                .getRegistrationId()
                .toUpperCase();

        Member.OAuth2Provider oAuth2Provider = Member.OAuth2Provider.getOAuth2ProviderByName(providerTypeCode);

        OAuth2UserInfo oAuth2UserInfo = oAuth2UserInfoFactory
                .getOAuth2UserInfo
                        (oAuth2Provider, oAuth2User);

        String oAuth2Id = oAuth2UserInfo.getOAuth2Id();
        String nickname = oAuth2UserInfo.getNickname();
        String username = String.format("%s_%s", providerTypeCode, oAuth2Id);

        Member member = memberService.signUpOrModify(username, "", nickname, oAuth2Provider);
        return new CustomUser(member);
    }
}
