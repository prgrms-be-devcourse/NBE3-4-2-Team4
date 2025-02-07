package com.NBE3_4_2_Team4.global.security.filter;

import com.NBE3_4_2_Team4.domain.board.question.dto.request.QuestionWriteReqDto;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.point.dto.PointTransferReq;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.global.security.jwt.JwtManager;
import com.NBE3_4_2_Team4.global.security.oauth2.logout.service.OAuth2LogoutService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class CustomJwtFilterTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtManager jwtManager;

    @Autowired
    private ObjectMapper objectMapper;

    private Member member;

    private Member admin;


    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .username("testUser")
                .nickname("nickname")
                .role(Member.Role.USER)
                .oAuth2Provider(Member.OAuth2Provider.NONE)
                .build();

        admin = Member.builder()
                .id(2L)
                .username("testAdmin")
                .nickname("nickname")
                .role(Member.Role.ADMIN)
                .oAuth2Provider(Member.OAuth2Provider.NONE)
                .build();
    }

    @Test
    @DisplayName("필터 안 걸려있는 url 에 대한 get 테스트")
    public void testCustomJwtFilter1() throws Exception {
        mockMvc.perform(get("/api/questions")
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("필터 걸려있는 url - api/questions 에 대한 post 테스트 - 헤더에 JWT 없는 경우 (인증 실패)")
    public void testCustomJwtFilter2() throws Exception {
        QuestionWriteReqDto reqBody = new QuestionWriteReqDto("test title", "test content", 1L);
        String body = objectMapper.writeValueAsString(reqBody);

        mockMvc.perform(post("/api/questions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("필터 걸려있는 url - api/questions 에 대한 post 테스트 - 헤더에 JWT 있는 경우 (인증 성공)")
    public void testCustomJwtFilter3() throws Exception {
        String jwtToken = jwtManager.generateAccessToken(member);

        QuestionWriteReqDto reqBody = new QuestionWriteReqDto("test title", "test content", 1L);
        String body = objectMapper.writeValueAsString(reqBody);

        mockMvc.perform(post("/api/questions")
                        .header("Authorization", String.format("Bearer %s", jwtToken))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("필터 걸려있는 url - admin/test 에 대한 post 테스트 - 헤더에 JWT 없는 경우 (인증 실패)")
    public void testCustomJwtFilter6() throws Exception {
        RsData<String> responseData = new RsData<>("401-1", "Unauthorized", "인증이 필요합니다.");

        String expectedJson = objectMapper.writeValueAsString(responseData);
        mockMvc.perform(put("/api/admin/products//accumulate")
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("필터 걸려있는 url - admin/test 에 대한 post 테스트 - 헤더에 일반 유저의 JWT 있는 경우 (인증 성공, 인가 실패)")
    public void testCustomJwtFilter7() throws Exception {
        String jwtToken = jwtManager.generateAccessToken(member);
        RsData<String> responseData = new RsData<>("403-1", "Forbidden", "권한이 부족합니다.");

        String expectedJson = objectMapper.writeValueAsString(responseData);
        mockMvc.perform(put("/api/admin/products//accumulate")
                        .header("Authorization", String.format("Bearer %s", jwtToken))
                        .with(csrf())
                )
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("필터 걸려있는 url - api/point/products/accumulate 에 대한 post 테스트 - 헤더에 관리자의 JWT 있는 경우 (인증, 인가 성공)")
    public void testCustomJwtFilter8() throws Exception {
        String jwtToken = jwtManager.generateAccessToken(admin);

        PointTransferReq pointTransferReq = new PointTransferReq("test@test.com", 1L);
        String body = objectMapper.writeValueAsString(pointTransferReq);

        mockMvc.perform(put("/api/admin/products/accumulate")
                        .header("Authorization", String.format("Bearer %s", jwtToken))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그아웃 성공 테스트 - 헤더에 사용자의 JWT 있는 경우")
    public void testCustomJwtFilter9() throws Exception {
        String jwtToken = jwtManager.generateAccessToken(member);

        mockMvc.perform(post("/api/logout")
                        .header("Authorization", String.format("Bearer %s", jwtToken))
                        .with(csrf())
                )
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, OAuth2LogoutService.LOGOUT_COMPLETE_URL))
                .andDo(print());
    }

    @Test
    @DisplayName("로그아웃 실패 테스트 - 헤더에 사용자의 JWT 없는 경우 (로그인 되어 있지 않은 경우)")
    public void testCustomJwtFilter10() throws Exception {
        mockMvc.perform(post("/api/logout")
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }
}
