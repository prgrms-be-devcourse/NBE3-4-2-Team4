package com.NBE3_4_2_Team4.domain.member.member.controller

import com.NBE3_4_2_Team4.domain.member.member.dto.*
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService
import com.NBE3_4_2_Team4.global.exceptions.EmailAlreadyVerifiedException
import com.NBE3_4_2_Team4.global.exceptions.InValidAccessException
import com.NBE3_4_2_Team4.global.exceptions.InValidPasswordException
import com.NBE3_4_2_Team4.global.rsData.RsData
import com.NBE3_4_2_Team4.global.security.AuthManager
import com.NBE3_4_2_Team4.global.security.HttpManager
import com.NBE3_4_2_Team4.global.security.jwt.JwtManager
import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.OAuth2LogoutService
import com.NBE3_4_2_Team4.standard.base.Empty
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Tag(name = "Member", description = "Member API")
class MemberController (
    private val memberService: MemberService,
    private val httpManager: HttpManager,
    private val jwtManager: JwtManager
){
    private val log = LoggerFactory.getLogger(MemberController::class.java)

    @Value("\${custom.domain.frontend}")
    private lateinit var frontDomain: String

    @ExceptionHandler(InValidPasswordException::class)
    fun handleInValidPasswordException(e: InValidPasswordException): ResponseEntity<RsData<Empty>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                RsData(
                    "400-2",
                    e.message!!
                )
            )
    }

    @ExceptionHandler(EmailAlreadyVerifiedException::class)
    fun handleEmailAlreadyVerifiedException(): ResponseEntity<RsData<Empty>> {
        val location = String.format("%s/verify-email/%s", frontDomain, "already-verified")

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .header("Location", location)
            .body(
                RsData(
                    "302-1",
                    String.format("already verified email. redirecting to %s ", location)
                )
            )
    }


    @PostMapping("/api/test")
    @Operation(summary = "test", description = "이메일 인증 여부에 따른 필터링 테스틀 위한 간이 매서드입니다. 추후 삭제할 예정.")
    fun test(): ResponseEntity<Void> {
        return ResponseEntity.ok().build()
    }


    @GetMapping("/api/members")
    @Operation(summary = "check if nickname exists", description = "회원가입 페이지에서 입력한 닉네임이 존재하는 지 사전 검사합니다.")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "닉네임의 중복 여부.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(description = "닉네임 사용 가능 (중복 없음)의 경우 true, 불가능 (중복 닉네임 존재)의 경우 false")
            )]
        )]
    )
    fun checkNicknameIsAvailable(
        @RequestParam(name = "nickname") nickname: String
    ): RsData<Boolean> {
        return RsData<Boolean>("200-1", "", memberService.isNicknameAvailable(nickname))
    }


    @GetMapping("/api/auth/temp-token")
    @Operation(
        summary = "check temp token exists",
        description = "회원가입 페이지에 접근 시, 임시 토큰이 존재하는 지 확인합니다. 임시 토큰이 없는 상태로 회원가입 페이지에 접근 시, 홈페이지로 리다이렉트 시키기 위해 호출됩니다."
    )
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "임시 토큰의 존재 여부.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(description = "임시 토큰이 존재할 경우 true, 존재하지 않을 경우 false")
            )]
        )]
    )
    fun tempTokenCheck(
        @CookieValue(name = "tempToken", required = false) tempToken: String?
    ): RsData<Boolean> {
        val tempTokenExists = !tempToken.isNullOrBlank()
        return RsData<Boolean>("200-1", "tempToken exists?", tempTokenExists)
    }


    @PostMapping("/api/members")
    @Operation(summary = "signup", description = "회원가입 요청을 처리합니다.")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "201",
            description = "회원 가입 성공."
        ), ApiResponse(responseCode = "409", description = "이미 존재하는 닉네임으로 회원가입을 시도하였을 때.")]
    )
    fun signup(
        @CookieValue(name = "tempToken") tempToken: String,
        @RequestBody signupRequestDto: @Valid SignupRequestDto,
        resp: HttpServletResponse
    ): RsData<Empty> {
        memberService.signUp(tempToken, signupRequestDto)
        httpManager.deleteCookie(resp, "tempToken")
        return RsData("201-1", "sign up complete")
    }


    @PostMapping("/api/members/verify-email")
    @Operation(summary = "verify email", description = "이메일 인증 요청을 처리합니다.")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "302",
            description = "이메일 인증 결과에 따른 리다이렉트.",
            headers = [Header(
                name = "Location",
                description = "성공한 경우 성공 페이지, 인증 실패(인증 코드 유효 기간 만료 등)의 경우 실패 페이지, 이미 인증된 경우 이미 인증됐다고 표시하는 페이지."
            )]
        )]
    )
    fun verifyEmail(
        @RequestParam("memberId") memberId: Long,
        @RequestParam("authCode") authCode: String,
        resp: HttpServletResponse
    ): ResponseEntity<RsData<Empty>> {
        val isEmailVerified = memberService.verifyEmail(memberId, authCode)
        val result = if (isEmailVerified) "success" else "fail"
        val location = String.format("%s/verify-email/%s", frontDomain, result)

        httpManager.expireJwtCookie(resp)

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .header("Location", location)
            .body(
                RsData(
                    "302-1",
                    String.format("email verifying complete. redirecting to %s ", location)
                )
            )
    }


    @PostMapping("/api/members/resend-verification-email")
    @Operation(summary = "verify email", description = "인증 이메일을 재전송합니다.")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "이메일 인증 결과에 따른 리다이렉트."
        ), ApiResponse(
            responseCode = "401",
            description = "인증 없는 회원. (JWT 필터에 걸림)"
        )]
    )
    fun resendVerificationEmail(): RsData<Empty> {
        val member = AuthManager.getNonNullMember()
        val memberId = member.id!!
        val emailAddress = member.emailAddress!!
        memberService.sendAuthenticationMail(memberId, emailAddress)

        return RsData("200-1", "resend verification email complete")
    }


    @PostMapping("/api/admin/login")
    @Operation(summary = "login with admin role", description = "관리자 회원의 로그인 요청을 처리합니다")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "괸라자 로그인 성공"
        ), ApiResponse(responseCode = "400", description = "비밀번호 불일치"), ApiResponse(
            responseCode = "403",
            description = "관리자 권한이 아닌 계정의 로그인 시도"
        )]
    )
    fun adminLogin(
        @RequestBody adminLoginRequestDto: @Valid AdminLoginRequestDto,
        resp: HttpServletResponse
    ): RsData<MemberThumbnailInfoResponseDto> {
        val member = memberService.adminLogin(adminLoginRequestDto)
        val accessToken = jwtManager.generateAccessToken(member)
        val accessTokenValidMinute = jwtManager.accessTokenValidMinute

        val refreshToken = jwtManager.generateRefreshToken(member)
        val refreshTokenValidHour = jwtManager.refreshTokenValidHour
        httpManager.setJWTCookie(resp, accessToken, accessTokenValidMinute, refreshToken, refreshTokenValidHour)

        val responseDto = MemberThumbnailInfoResponseDto(member.id!!, member.role, member.nickname)

        return RsData(
            "200-1",
            "admin login complete", responseDto
        )
    }


    @GetMapping("/api/members/thumbnail")
    @Operation(summary = "get member's simple info", description = "멤버의 간단한 정보 (현재는 닉네임만)를 조회합니다. 프론트의 헤더에서 사용합니다.")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "204",
            description = "로그인 되어 있지 않은 경우"
        ), ApiResponse(
            responseCode = "200",
            description = "로그인 되어 있는 경우.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = MemberThumbnailInfoResponseDto::class)
            )]
        )]
    )
    fun getMemberThumbnailInfo(): RsData<*> {
        val member = AuthManager.getMemberFromContext()
        if (member == null) {
            return RsData<Empty>("204-1", "User not logged in") // 로그인되지 않음
        } else {
            val responseDto = MemberThumbnailInfoResponseDto(member.id!!, member.role, member.nickname)
            return RsData("200-1", "find member", responseDto)
        }
    }


    @GetMapping("/api/members/details")
    @Operation(
        summary = "get member's detail info",
        description = "멤버의 자세한 정보 (포인트 작성 질문/답변 수, 닉네임)를 조회합니다. 마이 페이지에서 사용합니다."
    )
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "로그인 되어 있는 경우.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = MemberDetailInfoResponseDto::class)
            )]
        ), ApiResponse(responseCode = "401", description = "인증 없는 회원. (JWT 필터에 걸림)")]
    )
    fun getMemberDetailInfo(): RsData<MemberDetailInfoResponseDto> {
        val member = AuthManager.getNonNullMember()
        val responseDto = memberService.getMemberDetailInfo(member)
        return RsData("200-1", "member found", responseDto)
    }


    @PostMapping("/api/logout")
    @Operation(summary = "request for logout", description = "로그아웃을 요청합니다. 연동된 OAuth2 서비스에 따라 다른 url 이 반환됩니다.")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "로그인 되어 있는 경우.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(
                    type = "string",
                    description = "로그아웃 후 리다이렉트 될 URL. 302 사용 시 fetch 에서 오류가 발생해 200으로 설정."
                )
            )]
        ), ApiResponse(responseCode = "401", description = "인증 없는 회원. (JWT 필터에 걸림)")]
    )
    fun logout(req: HttpServletRequest): RsData<String> {
        val member = AuthManager.getNonNullMember()
        val redirectUrl = memberService.getLogoutUrl(member)

        req.session.setAttribute("logoutRequested", true)

        return RsData(
            "200-3", String.format(
                "Trying to log out for %s",
                Objects.requireNonNull(member).oAuth2Provider.name
            ), redirectUrl
        )
    }


    @GetMapping(OAuth2LogoutService.LOGOUT_COMPLETE_URL)
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "302",
            description = "로그아웃 성공적으로 처리된 경우.",
            headers = [Header(name = "Location", description = "로그아웃 후 리다이렉트 될 URL. 기본적으로 http://localhost:3000")]
        )]
    )
    @Operation(
        summary = "logout complete",
        description = "로그아웃 요청이 성공적으로 실행되었을 때 도착합니다. Cookie 에 담긴 JWT 를 파기하고 프론트의 메인 페이지로 이동합니다."
    )
    fun logoutComplete(req: HttpServletRequest, resp: HttpServletResponse): ResponseEntity<RsData<Empty>> {
        val logoutRequested = req.session.getAttribute("logoutRequested") as Boolean?

        if (logoutRequested == null || !logoutRequested) {
            val remoteAddr = req.remoteAddr
            throw InValidAccessException(remoteAddr, OAuth2LogoutService.LOGOUT_COMPLETE_URL)
        }

        req.session.removeAttribute("logoutRequested")
        httpManager.expireJwtCookie(resp)

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .header("Location", frontDomain)
            .body(
                RsData(
                    "302-1",
                    String.format("logout complete. redirecting to %s ", frontDomain)
                )
            )
    }


    @PatchMapping("/api/members/nickname")
    @Operation(summary = "update member nickname", description = "회원의 닉네임을 변경합니다.")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "회원 닉네임 변경 성공"
        ), ApiResponse(responseCode = "401", description = "인증 없는 회원. (JWT 필터에 걸림)"), ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 회원. (JWT 필드에 있는 id에 해당하는 회원이 존재하지 않음)"
        )]
    )
    fun updateMembersNickname(
        @RequestBody nicknameUpdateRequestDto: @Valid NicknameUpdateRequestDto?
    ): RsData<Empty> {
        val member = AuthManager.getNonNullMember()
        memberService.updateNickname(member, nicknameUpdateRequestDto!!)
        return RsData(
            "200-1",
            "nickname updated"
        )
    }


    @DeleteMapping("/api/members")
    @Operation(summary = "withdrawal membership", description = "회원 탈퇴를 요청합니다. 성공 시 연동된 OAuth 서비스와의 연결도 해제됩니다.")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "204",
            description = "회원 탈퇴 성공"
        ), ApiResponse(responseCode = "401", description = "인증 없는 회원. (JWT 필터에 걸림)"), ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 회원. (JWT 필드에 있는 id에 해당하는 회원이 존재하지 않음)"
        )]
    )
    fun withdrawalMembership(resp: HttpServletResponse): RsData<Empty> {
        val member = AuthManager.getNonNullMember()
        memberService.withdrawalMembership(member)
        httpManager.expireJwtCookie(resp)
        return RsData(
            "204-1",
            "withdrawal membership done"
        )
    }
}