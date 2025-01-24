package com.NBE3_4_2_Team4.global.security.oauth2;

import com.NBE3_4_2_Team4.global.security.user.CustomUser;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final MemberService memberService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String oAuth2Id = oAuth2User.getName();
        String providerTypeCode = userRequest
                .getClientRegistration()
                .getRegistrationId()
                .toUpperCase();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, String> attributeProperties = (Map<String, String>) attributes.get("properties");
        String nickname = attributeProperties.get("nickname");
        String username = String.format("%s_%s", providerTypeCode, oAuth2Id);

        Member member = memberService.signUpOrModify(username, "", nickname, providerTypeCode);

        return new CustomUser(member);
    }
}
