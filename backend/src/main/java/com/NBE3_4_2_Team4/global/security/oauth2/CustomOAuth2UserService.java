package com.NBE3_4_2_Team4.global.security.oauth2;

import com.NBE3_4_2_Team4.global.config.OAuth2UserInfoFactory;
import com.NBE3_4_2_Team4.global.security.oauth2.userInfo.OAuth2UserInfo;
import com.NBE3_4_2_Team4.global.security.user.CustomUser;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberService memberService;
    private final OAuth2UserInfoFactory oAuth2UserInfoFactory;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String providerTypeCode = userRequest
                .getClientRegistration()
                .getRegistrationId()
                .toUpperCase();
//        log.warn("providerTypeCode: {}", providerTypeCode);
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//        log.warn("attributes: {}", attributes);
//        Map<String, String> attributeProperties;
//        switch (providerTypeCode) {
//            case "KAKAO":
//                attributeProperties = (Map<String, String>) attributes.get("properties");
//                oAuth2Id = oAuth2User.getName();
//                break;
//            case "NAVER":
//                attributeProperties = (Map<String, String>) attributes.get("response");
//                oAuth2Id = attributeProperties.get("id");
//                break;
//            default:
//                throw new RuntimeException("Unknown providerTypeCode: " + providerTypeCode);
//        }
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
