package com.NBE3_4_2_Team4.global.security.oauth2;

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberQuerydsl;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetHistory;
import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository;
import com.NBE3_4_2_Team4.global.security.HttpManager;
import com.NBE3_4_2_Team4.global.security.jwt.JwtManager;
import com.NBE3_4_2_Team4.global.security.user.CustomUser;
import com.NBE3_4_2_Team4.global.security.user.TempUser;
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
import java.time.LocalDate;


@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Value("${custom.jwt.accessToken.validMinute:30}")
    int accessTokenValidMinute;

    @Value("${custom.jwt.refreshToken.validHour:24}")
    int refreshTokenValidHour;

    private final JwtManager jwtManager;
    private final HttpManager httpManager;

    private final AssetHistoryRepository assetHistoryRepository;
    private final MemberQuerydsl memberQuerydsl;

    @SneakyThrows
    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication auth) {
        if (auth instanceof CustomUser){
            handleExistingMember(req, resp, auth);
            super.onAuthenticationSuccess(req, resp, auth);
        }else {
            handleNewMember(req, resp, auth);
        }
//        CustomUser customUser = (CustomUser) auth.getPrincipal();
//        Member member = customUser.getMember();
//
//        String accessToken = jwtManager.generateAccessToken(member);
//        String refreshToken = jwtManager.generateRefreshToken(member);
//        httpManager.setJWTCookie(resp, accessToken, accessTokenValidMinute, refreshToken, refreshTokenValidHour);
//
//        String targetUrl = req.getParameter("state");
//
//        if(isFirstLoginToday(member)){
//            rewardPointForFirstLoginOfDay(member);
//
//            targetUrl += String.format("?attendanceMessage=%s",
//                    URLEncoder.encode(
//                            String.format("출석 포인트 %dp 지급 되었습니다.", PointConstants.ATTENDANCE_POINT),
//                            StandardCharsets.UTF_8));
//        }
//
//        setDefaultTargetUrl(targetUrl);
//        super.onAuthenticationSuccess(req, resp, auth);
    }

    private void handleExistingMember(HttpServletRequest req, HttpServletResponse resp, Authentication auth){
        CustomUser customUser = (CustomUser) auth.getPrincipal();
        Member member = customUser.getMember();

        String accessToken = jwtManager.generateAccessToken(member);
        String refreshToken = jwtManager.generateRefreshToken(member);
        httpManager.setJWTCookie(resp, accessToken, accessTokenValidMinute, refreshToken, refreshTokenValidHour);

        String targetUrl = req.getParameter("state");

        if(isFirstLoginToday(member)){
            rewardPointForFirstLoginOfDay(member);

            targetUrl += String.format("?attendanceMessage=%s",
                    URLEncoder.encode(
                            String.format("출석 포인트 %dp 지급 되었습니다.", PointConstants.ATTENDANCE_POINT),
                            StandardCharsets.UTF_8));
        }

        setDefaultTargetUrl(targetUrl);
    }

    private void handleNewMember(HttpServletRequest req, HttpServletResponse resp, Authentication auth) throws IOException {
        TempUser tempUser = (TempUser) auth.getPrincipal();
        //TempUser 에는 OAuth2UserID (회원 가입용 아이디), 이름, 리프레시 토큰 있음.

        resp.sendRedirect("/signup");
    }

    private boolean isFirstLoginToday(Member member) {
        LocalDate today = LocalDate.now();
        LocalDate lastLoginDate = member.getLastAttendanceDate();
        return lastLoginDate == null || lastLoginDate.isBefore(today);
    }

    private void rewardPointForFirstLoginOfDay(Member member){
        LocalDate today = LocalDate.now();
        memberQuerydsl.updateLastLoginDate(member, today);

        assetHistoryRepository.save(AssetHistory.builder()
                .member(member)
                .amount(PointConstants.ATTENDANCE_POINT)
                .assetCategory(AssetCategory.ATTENDANCE)
                .correlationId("asdasdasdaff")
                .build());
    }
}
