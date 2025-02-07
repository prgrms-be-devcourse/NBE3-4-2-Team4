package com.NBE3_4_2_Team4.domain.member.member.controller;

import com.NBE3_4_2_Team4.domain.member.member.dto.NicknameUpdateRequestDto;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService;
import com.NBE3_4_2_Team4.global.exceptions.InValidAccessException;
import com.NBE3_4_2_Team4.global.exceptions.InValidPasswordException;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.global.security.AuthManager;
import com.NBE3_4_2_Team4.global.security.HttpManager;
import com.NBE3_4_2_Team4.global.security.oauth2.logout.service.OAuth2LogoutService;
import com.NBE3_4_2_Team4.standard.base.Empty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Member", description = "Member API")
public class MemberController {
    private final MemberService memberService;
    private final HttpManager httpManager;

    @Value("${custom.domain.frontend}")
    private String frontDomain;

    @ExceptionHandler(InValidPasswordException.class)
    public ResponseEntity<RsData<Empty>> handleInValidPasswordException(InValidPasswordException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new RsData<>(
                        "400-2",
                        e.getMessage()
                ));
    }


    @PostMapping("/api/logout")
    @Operation(summary = "request for logout", description = "로그아웃을 요청합니다. 연동된 OAuth2 서비스에 따라 다른 url 이 반환됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "", headers = {
                    @Header(name = "Location", description = "로그아웃 후 리다이렉트 될 URL (OAuth2 서비스에 따라 다름)")
            }),
            @ApiResponse(responseCode = "401", description = "인증 없는 회원. (JWT 필터에 걸림)")
    })
    public ResponseEntity<RsData<Empty>> logout(HttpServletRequest req){
        Member member = AuthManager.getNonNullMember();
        String redirectUrl = memberService.getLogoutUrl(member);

        req.getSession().setAttribute("logoutRequested", true);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", redirectUrl)
                .body(new RsData<>("302-1",
                        String.format("Trying to log out for %s",
                        Objects.requireNonNull(member).getOAuth2Provider().name())));
    }


    @GetMapping(OAuth2LogoutService.LOGOUT_COMPLETE_URL)
    @Operation(summary = "logout complete", description = "로그아웃 요청이 성공적으로 실행되었을 때 도착합니다. Cookie 에 담긴 JWT 를 파기하고 프론트의 메인 페이지로 이동합니다.")
    public ResponseEntity<RsData<Empty>> logoutComplete(HttpServletRequest req, HttpServletResponse resp) {
        Boolean logoutRequested = (Boolean) req.getSession().getAttribute("logoutRequested");

        if (logoutRequested == null || !logoutRequested) {
            String remoteAddr = req.getRemoteAddr();
            throw new InValidAccessException(remoteAddr, OAuth2LogoutService.LOGOUT_COMPLETE_URL);
        }

        req.getSession().removeAttribute("logoutRequested");
        httpManager.expireJwtCookie(resp);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Location", frontDomain)
                .body(new RsData<>(
                        "302-1",
                        String.format("logout complete. redirecting to %s ", frontDomain)
                ));
    }

    @DeleteMapping("/api/members")
    @Operation(summary = "withdrawal membership", description = "회원 탈퇴를 요청합니다. 성공 시 연동된 OAuth 서비스와의 연결도 해제됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = "인증 없는 회원. (JWT 필터에 걸림)"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원. (JWT 필드에 있는 id에 해당하는 회원이 존재하지 않음)")
    })
    public RsData<Empty> withdrawalMembership(){
        Member member = AuthManager.getNonNullMember();
        memberService.withdrawalMembership(member);
        return new RsData<>("204-1",
                "withdrawal membership done");
    }

    @PatchMapping("/api/members/nickname")
    public RsData<Empty> updateMembers(
            @RequestBody @Valid NicknameUpdateRequestDto nicknameUpdateRequestDto){
        Member member = AuthManager.getNonNullMember();
        memberService.modify(member, nicknameUpdateRequestDto);
        return new RsData<>("200-1",
                "nickname updated");
    }
}
