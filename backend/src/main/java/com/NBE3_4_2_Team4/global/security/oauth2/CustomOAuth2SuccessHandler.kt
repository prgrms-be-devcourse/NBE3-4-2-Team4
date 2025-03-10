package com.NBE3_4_2_Team4.global.security.oauth2

import com.NBE3_4_2_Team4.domain.asset.point.service.PointService
import com.NBE3_4_2_Team4.global.security.HttpManager
import com.NBE3_4_2_Team4.global.security.jwt.JwtManager
import com.NBE3_4_2_Team4.global.security.user.customUser.CustomUser
import com.NBE3_4_2_Team4.global.security.user.tempUserBeforeSignUp.TempUserBeforeSignUp
import com.NBE3_4_2_Team4.standard.constants.PointConstants
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class CustomOAuth2SuccessHandler (
    private val pointService: PointService,
    private val jwtManager: JwtManager,
    private val httpManager: HttpManager
): SavedRequestAwareAuthenticationSuccessHandler(){

    @Value("\${custom.domain.frontend}")
    lateinit var frontDomain: String

    @Value("\${custom.jwt.accessToken.validMinute:30}")
    var accessTokenValidMinute: Int = 30

    @Value("\${custom.jwt.refreshToken.validHour:24}")
    var refreshTokenValidHour: Int = 24

    @Transactional
    override fun onAuthenticationSuccess(req: HttpServletRequest, resp: HttpServletResponse, auth: Authentication) {
        if (auth.principal is CustomUser) {
            handleExistingMember(req, resp, auth)
            super.onAuthenticationSuccess(req, resp, auth)
        } else {
            handleNewMember(resp, auth)
        }
    }

    private fun handleExistingMember(req: HttpServletRequest, resp: HttpServletResponse, auth: Authentication) {
        val customUser = auth.principal as CustomUser
        val member = customUser.member

        val accessToken = jwtManager.generateAccessToken(member)
        val refreshToken = jwtManager.generateRefreshToken(member)
        httpManager.setJWTCookie(resp, accessToken, accessTokenValidMinute, refreshToken, refreshTokenValidHour)

        var targetUrl = req.getParameter("state")

        if (member.isFirstLoginToday()) {
            pointService.attend(member.id)

            targetUrl += String.format(
                "?attendanceMessage=%s",
                URLEncoder.encode(
                    String.format("출석 포인트 %dp 지급 되었습니다.", PointConstants.ATTENDANCE_POINT),
                    StandardCharsets.UTF_8
                )
            )
        }

        defaultTargetUrl = targetUrl
    }

    @Throws(IOException::class)
    private fun handleNewMember(resp: HttpServletResponse, auth: Authentication) {
        val tempUserBeforeSignUp = auth.principal as TempUserBeforeSignUp

        //TempUser 에는 OAuth2UserID (회원 가입용 아이디), 이름, 리프레시 토큰 있음.
        val tempTokenForSignUp = jwtManager.generateTempToken(tempUserBeforeSignUp)
        httpManager.setTempTokenForSignUpCookie(resp, tempTokenForSignUp, accessTokenValidMinute)

        resp.sendRedirect(String.format("%s/signup", frontDomain))
    }
}