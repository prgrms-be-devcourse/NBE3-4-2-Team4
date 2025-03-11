package com.NBE3_4_2_Team4.global.security.authenticationEntryPoint

import com.NBE3_4_2_Team4.global.rsData.RsData
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ){
        // RsData 형식의 JSON 응답 생성
        val responseData = RsData("401-1", "Unauthorized", "인증이 필요합니다.")

        // 응답 설정
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        // JSON 변환 후 응답에 작성
        val jsonResponse = objectMapper.writeValueAsString(responseData)
        response.writer.write(jsonResponse)
    }
}