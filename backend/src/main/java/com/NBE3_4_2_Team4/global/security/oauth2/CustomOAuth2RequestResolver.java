package com.NBE3_4_2_Team4.global.security.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class CustomOAuth2RequestResolver implements OAuth2AuthorizationRequestResolver {
    private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;

    @Value("${custom.domain.frontend}")
    private String frontDomain;

    public CustomOAuth2RequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request);

        if (authorizationRequest == null) {
            log.warn("No authorization request found for request {} /n {}", request.getRequestURI(), request.getQueryString());
            Map<String, String[]> parameters = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                log.warn("key {} :  value {}", entry.getKey(), entry.getValue());
            };
            return null;
        }
//
        log.warn("authorization request found for request {} /n {}", authorizationRequest.getAuthorizationRequestUri(), authorizationRequest.getAuthorizationUri());

//        // clientRegistrationId가 이미 존재하면 중복 호출 방지
        if (authorizationRequest.getAttributes().containsKey("clientRegistrationId")) {
            log.info("client registration id found {}", authorizationRequest.getAttributes().get("clientRegistrationId"));
            return authorizationRequest;
        }

        return customizeAuthorizationRequest(authorizationRequest, request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request, clientRegistrationId);
        return customizeAuthorizationRequest(authorizationRequest, request);
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request) {
        if (authorizationRequest == null || request == null) {
            return null;
        }

        String redirectUrl = Objects.requireNonNullElse(request.getParameter("redirectUrl"), frontDomain);
        Map<String, Object> additionalParameters = new HashMap<>(authorizationRequest.getAdditionalParameters());
        if (!redirectUrl.isEmpty()) {
            //이거 주석 풀면 매번 카카오 계정 로그인 해야 함
//            String prompt = "login";
//            additionalParameters.put("prompt",prompt);
            additionalParameters.put("state", redirectUrl);
        }

        return OAuth2AuthorizationRequest.from(authorizationRequest)
                .additionalParameters(additionalParameters)
                .state(redirectUrl)
                .build();
    }
}
