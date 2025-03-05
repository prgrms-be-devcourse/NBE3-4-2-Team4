package com.NBE3_4_2_Team4.global.security.oauth2;

import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.service.OAuth2RefreshTokenService;
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.service.OAuth2UserInfoService;
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfo;
import com.NBE3_4_2_Team4.global.security.user.customUser.CustomUser;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService;
import com.NBE3_4_2_Team4.global.security.user.tempUserBeforeSignUp.TempUserBeforeSignUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberService memberService;
    private final OAuth2Manager oAuth2Manager;
    private final OAuth2RefreshTokenService oAuth2RefreshTokenService;
    private final TempUserBeforeSignUpService tempUserBeforeSignUpService;


    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String refreshToken = (String) userRequest.getAdditionalParameters().getOrDefault("refresh_token", null);

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String providerTypeCode = userRequest
                .getClientRegistration()
                .getRegistrationId()
                .toUpperCase();

        Member.OAuth2Provider oAuth2Provider = Member.OAuth2Provider.Companion.getOAuth2ProviderByName(providerTypeCode);


        OAuth2UserInfoService oAuth2UserInfoService = oAuth2Manager.getOAuth2UserInfoService(oAuth2Provider);
        OAuth2UserInfo oAuth2UserInfo = oAuth2UserInfoService.getOAuth2UserInfo(oAuth2User);


        String oAuth2Id = oAuth2UserInfo.getOAuth2Id();
        String username = String.format("%s_%s", providerTypeCode, oAuth2Id);

        Optional<Member> optionalMember = Optional.ofNullable(memberService.findByUsername(username));
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            oAuth2RefreshTokenService.saveOrUpdateOAuth2RefreshToken(member, refreshToken, oAuth2Id);
            return new CustomUser(member);
        }

        return tempUserBeforeSignUpService.getOrCreateTempUser(oAuth2UserInfo, providerTypeCode, refreshToken);
    }
}
