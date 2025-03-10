package com.NBE3_4_2_Team4.global.security.accessDeniedHandler

import com.NBE3_4_2_Team4.global.rsData.RsData
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import org.springframework.security.access.AccessDeniedException

@Component
class CustomAccessDeniedHandler(
    private val objectMapper: ObjectMapper
) : AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ){
        // RsData 형식의 JSON 응답 생성
        val responseData = RsData("403-1", "Forbidden", "권한이 부족합니다.")


        // 응답 설정
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpServletResponse.SC_FORBIDDEN


        // JSON 변환 후 응답에 작성
        val jsonResponse = objectMapper.writeValueAsString(responseData)
        response.writer.write(jsonResponse)
    }
}