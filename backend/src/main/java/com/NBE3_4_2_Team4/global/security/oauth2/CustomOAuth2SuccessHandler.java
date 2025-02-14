package com.NBE3_4_2_Team4.global.security.oauth2;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberQuerydsl;
import com.NBE3_4_2_Team4.domain.point.entity.PointCategory;
import com.NBE3_4_2_Team4.domain.point.entity.PointHistory;
import com.NBE3_4_2_Team4.domain.point.repository.PointHistoryRepository;
import com.NBE3_4_2_Team4.global.security.HttpManager;
import com.NBE3_4_2_Team4.global.security.jwt.JwtManager;
import com.NBE3_4_2_Team4.global.security.user.CustomUser;
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

    private final PointHistoryRepository pointHistoryRepository;
    private final MemberQuerydsl memberQuerydsl;

    @SneakyThrows
    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication auth) {
        CustomUser customUser = (CustomUser) auth.getPrincipal();
        Member member = customUser.getMember();

        String accessToken = jwtManager.generateAccessToken(member);
        String refreshToken = jwtManager.generateRefreshToken(member);
        httpManager.setJWTCookie(resp, accessToken, accessTokenValidMinute, refreshToken, refreshTokenValidHour);

        String targetUrl = req.getParameter("state");


        if(isFirstLoginToday(member)){
            rewardPointForFirstLoginOfDay(req, member);
            targetUrl += String.format("?attendanceMessage=%s",
                    URLEncoder.encode(
                            String.format("출석 포인트 %dp 지급 되었습니다.", PointConstants.ATTENDANCE_POINT),
                            StandardCharsets.UTF_8));
        }

        setDefaultTargetUrl(targetUrl);
        super.onAuthenticationSuccess(req, resp, auth);
    }

    private boolean isFirstLoginToday(Member member) {
        LocalDate today = LocalDate.now();
        LocalDate lastLoginDate = member.getLastAttendanceDate();
        return lastLoginDate == null || lastLoginDate.isBefore(today);
    }

    private void rewardPointForFirstLoginOfDay(HttpServletRequest req, Member member){
        req.getSession().setAttribute("attendanceMessage",
                String.format("출석 포인트 %dp 지급 되었습니다.", PointConstants.ATTENDANCE_POINT));

        LocalDate today = LocalDate.now();
        memberQuerydsl.updateLastLoginDate(member, today);

        pointHistoryRepository.save(PointHistory.builder()
                .member(member)
                .amount(PointConstants.ATTENDANCE_POINT)
                .pointCategory(PointCategory.ATTENDANCE)
                .correlationId("asdasdasdaff")
                .build());
    }
}
