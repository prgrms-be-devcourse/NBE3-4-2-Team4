package com.NBE3_4_2_Team4.global.security.oauth2

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.stereotype.Component
import java.util.*

@Component
class CustomOAuth2RequestResolver(
    clientRegistrationRepository: ClientRegistrationRepository
): OAuth2AuthorizationRequestResolver{
    private val defaultResolver = DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization")

    @Value("\${custom.domain.frontend}")
    lateinit var frontDomain: String

    override fun resolve(request: HttpServletRequest?): OAuth2AuthorizationRequest? {
        val authorizationRequest = defaultResolver.resolve(request) ?: return null

        //        // clientRegistrationId가 이미 존재하면 중복 호출 방지
        if (authorizationRequest.attributes.containsKey("clientRegistrationId")) {
            return authorizationRequest
        }

        return customizeAuthorizationRequest(authorizationRequest, request)
    }

    override fun resolve(request: HttpServletRequest?, clientRegistrationId: String?): OAuth2AuthorizationRequest? {
        val authorizationRequest = defaultResolver.resolve(request, clientRegistrationId)
        return customizeAuthorizationRequest(authorizationRequest, request)
    }

    private fun customizeAuthorizationRequest(
        authorizationRequest: OAuth2AuthorizationRequest?,
        request: HttpServletRequest?
    ): OAuth2AuthorizationRequest? {
        if (authorizationRequest == null || request == null) {
            return null
        }

        val redirectUrl = Objects.requireNonNullElse(request.getParameter("redirectUrl"), frontDomain)
        val additionalParameters: MutableMap<String, Any> = HashMap(authorizationRequest.additionalParameters)
        if (!redirectUrl.isEmpty()) {
            //이거 주석 풀면 매번 카카오 계정 로그인 해야 함
//            String prompt = "login";
//            additionalParameters.put("prompt",prompt);
            val accessType = "offline"
            additionalParameters["access_type"] = accessType
            additionalParameters["state"] = redirectUrl
        }

        return OAuth2AuthorizationRequest.from(authorizationRequest)
            .additionalParameters(additionalParameters)
            .state(redirectUrl)
            .build()
    }
}