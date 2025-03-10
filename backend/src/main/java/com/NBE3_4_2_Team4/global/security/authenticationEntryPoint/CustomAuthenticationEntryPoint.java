//package com.NBE3_4_2_Team4.global.security.authenticationEntryPoint;
//
//import com.NBE3_4_2_Team4.global.rsData.RsData;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
//    private final ObjectMapper objectMapper;
//
//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//        // RsData 형식의 JSON 응답 생성
//        RsData<String> responseData = new RsData<>("401-1", "Unauthorized", "인증이 필요합니다.");
//
//        // 응답 설정
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//
//        // JSON 변환 후 응답에 작성
//        String jsonResponse = objectMapper.writeValueAsString(responseData);
//        response.getWriter().write(jsonResponse);
//    }
//}
