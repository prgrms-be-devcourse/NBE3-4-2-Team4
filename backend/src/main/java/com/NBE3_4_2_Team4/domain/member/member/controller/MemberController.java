package com.NBE3_4_2_Team4.domain.member.member.controller;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService;
import com.NBE3_4_2_Team4.global.exceptions.InValidAccessException;
import com.NBE3_4_2_Team4.global.exceptions.InValidPasswordException;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.global.security.AuthManager;
import com.NBE3_4_2_Team4.global.security.HttpManager;
import com.NBE3_4_2_Team4.global.security.oauth2.logout.service.OAuth2LogoutService;
import com.NBE3_4_2_Team4.standard.base.Empty;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @GetMapping("/")
    public String home(HttpServletRequest request){
        String token = httpManager.getCookieValue(request, "accessToken");
        return StringUtils.isBlank(token) ?  "not logged in" : token;
    }


    @PostMapping("/api/logout")
    public ResponseEntity<RsData<Empty>> logout(HttpServletRequest req){
        Member member = AuthManager.getMemberFromContext();
        String redirectUrl = memberService.getLogoutUrl(member);

        req.getSession().setAttribute("logoutRequested", true);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", redirectUrl)
                .body(new RsData<>("302-1",
                        String.format("Trying to log out for %s",
                        Objects.requireNonNull(member).getOAuth2Provider().name())));
    }


    @GetMapping(OAuth2LogoutService.LOGOUT_COMPLETE_URL)
    public ResponseEntity<RsData<Empty>> logoutComplete(HttpServletRequest req, HttpServletResponse resp) {
        Boolean logoutRequested = (Boolean) req.getSession().getAttribute("logoutRequested");

        if (logoutRequested == null || !logoutRequested) {
            String remoteAddr = req.getRemoteAddr();
            throw new InValidAccessException(remoteAddr, OAuth2LogoutService.LOGOUT_COMPLETE_URL);
        }

        req.getSession().removeAttribute("logoutRequested");
        Member member = AuthManager.getMemberFromContext();
        memberService.logout(member);
        httpManager.expireJwtCookie(resp);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Location", frontDomain)
                .body(new RsData<>(
                        "302-1",
                        String.format("logout complete. redirecting to %s ", frontDomain)
                ));
    }

    @PostMapping("/api/test")
    public ResponseEntity<Void> test1(){
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/products/test")
    public ResponseEntity<Void> test2(){
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/questions/test")
    public ResponseEntity<Void> test3(){
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/answers/test")
    public ResponseEntity<Void> test4(){
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/admin/test")
    public ResponseEntity<Void> test5(){
        return ResponseEntity.ok().build();
    }

//    @DeleteMapping
    @GetMapping("/api/members/withdrawal")
    public RsData<Empty> withdrawalMembership(){
        Member member = AuthManager.getMemberFromContext();
        memberService.withdrawalMembership(member);
        return new RsData<>("204-1",
                "cancel membership done");
    }
}
