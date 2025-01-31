package com.NBE3_4_2_Team4.domain.member.member.controller;

import com.NBE3_4_2_Team4.domain.member.dto.request.LoginRequestDto;
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService;
import com.NBE3_4_2_Team4.global.exceptions.InValidPasswordException;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.global.security.HttpManager;
import com.NBE3_4_2_Team4.standard.base.Empty;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final HttpManager httpManager;

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

    @PostMapping("/api/login")
    public RsData<String> login(
            @RequestBody LoginRequestDto loginRequestDto,
            HttpServletResponse resp) {
        String token = memberService.login(loginRequestDto);
        httpManager.setCookie(resp, "accessToken", token, 30);
        return new RsData<>("200-1", "OK", token);
    }

    @PostMapping("/api/logout")
    public RsData<Empty> logout(HttpServletResponse resp) {
        httpManager.deleteCookie(resp, "accessToken");
        return new RsData<>("204-1", "No Content");
    }

    @GetMapping("/api/test")
    public ResponseEntity<Void> test12(){
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/products/test")
    public ResponseEntity<Void> test22(){
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/questions/test")
    public ResponseEntity<Void> test32(){
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/answers/test")
    public ResponseEntity<Void> test42(){
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/admin/test")
    public ResponseEntity<Void> test52(){
        return ResponseEntity.ok().build();
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
}
