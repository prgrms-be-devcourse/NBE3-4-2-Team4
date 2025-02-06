package com.NBE3_4_2_Team4.global.security.oauth2;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.security.HttpManager;
import com.NBE3_4_2_Team4.global.security.jwt.JwtManager;
import com.NBE3_4_2_Team4.global.security.user.CustomUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final JwtManager jwtManager;
    private final HttpManager httpManager;

    @SneakyThrows
    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication auth) {
        CustomUser customUser = (CustomUser) auth.getPrincipal();
        Member member = customUser.getMember();
        String token = jwtManager.generateAccessToken(member);
        httpManager.setAccessTokenCookie(resp, token, 30);
        String targetUrl = req.getParameter("state");
        setDefaultTargetUrl(targetUrl);
        super.onAuthenticationSuccess(req, resp, auth);
    }
}
