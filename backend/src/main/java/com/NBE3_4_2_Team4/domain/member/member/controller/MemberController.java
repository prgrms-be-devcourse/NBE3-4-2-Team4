package com.NBE3_4_2_Team4.domain.member.member.controller;

import com.NBE3_4_2_Team4.domain.member.dto.request.LoginRequestDto;
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.global.security.HttpManager;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final HttpManager httpManager;

    @PostMapping("/api/login")
    public RsData<String> login(
            @RequestBody LoginRequestDto loginRequestDto,
            HttpServletResponse resp) {
        String token = memberService.login(loginRequestDto);
        httpManager.setCookie(resp, "accessToken", token, 30);
        return new RsData<>("200-1", "OK", token);
    }
}
