package com.NBE3_4_2_Team4.global.security.oauth2;

import com.NBE3_4_2_Team4.domain.asset.point.service.PointService;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.security.HttpManager;
import com.NBE3_4_2_Team4.global.security.jwt.JwtManager;
import com.NBE3_4_2_Team4.global.security.user.customUser.CustomUser;
import com.NBE3_4_2_Team4.global.security.user.tempUserBeforeSignUp.TempUserBeforeSignUp;
import com.NBE3_4_2_Team4.standard.constants.PointConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Value("${custom.domain.frontend}")
    private String frontDomain;

    @Value("${custom.jwt.accessToken.validMinute:30}")
    int accessTokenValidMinute;

    @Value("${custom.jwt.refreshToken.validHour:24}")
    int refreshTokenValidHour;

    private final JwtManager jwtManager;
    private final HttpManager httpManager;

    private final PointService pointService;

    @SneakyThrows
    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication auth) {
        if (auth.getPrincipal() instanceof CustomUser){
            handleExistingMember(req, resp, auth);
            super.onAuthenticationSuccess(req, resp, auth);
        }else {
            handleNewMember(resp, auth);
        }
    }

    private void handleExistingMember(HttpServletRequest req, HttpServletResponse resp, Authentication auth){
        CustomUser customUser = (CustomUser) auth.getPrincipal();
        Member member = customUser.getMember();

        String accessToken = jwtManager.generateAccessToken(member);
        String refreshToken = jwtManager.generateRefreshToken(member);
        httpManager.setJWTCookie(resp, accessToken, accessTokenValidMinute, refreshToken, refreshTokenValidHour);

        String targetUrl = req.getParameter("state");

        if(member.isFirstLoginToday()){
            pointService.attend(member.getId());

            targetUrl += String.format("?attendanceMessage=%s",
                    URLEncoder.encode(
                            String.format("출석 포인트 %dp 지급 되었습니다.", PointConstants.ATTENDANCE_POINT),
                            StandardCharsets.UTF_8));
        }

        setDefaultTargetUrl(targetUrl);
    }

    private void handleNewMember(HttpServletResponse resp, Authentication auth) throws IOException {
        TempUserBeforeSignUp tempUserBeforeSignUp = (TempUserBeforeSignUp) auth.getPrincipal();
        //TempUser 에는 OAuth2UserID (회원 가입용 아이디), 이름, 리프레시 토큰 있음.

        String tempTokenForSignUp = jwtManager.generateTempToken(tempUserBeforeSignUp);
        httpManager.setTempTokenForSignUpCookie(resp, tempTokenForSignUp, accessTokenValidMinute);

        resp.sendRedirect(String.format("%s/signup", frontDomain));
    }
}
